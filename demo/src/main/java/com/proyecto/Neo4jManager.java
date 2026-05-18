package com.proyecto;

import org.neo4j.driver.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static org.neo4j.driver.Values.parameters;

public class Neo4jManager implements AutoCloseable {

    private final Driver driver;

    public Neo4jManager(String uri, String user, String password) {

        this.driver = GraphDatabase.driver(
                uri,
                AuthTokens.basic(user, password)
        );

        // Verifica conexión REAL
        driver.verifyConnectivity();

        System.out.println("Conectado correctamente a Neo4j Aura");
    }

    @Override
    public void close() {
        driver.close();
    }

    public void importarDatos(String rutaArchivo) {

        try (
                BufferedReader br = new BufferedReader(new FileReader(rutaArchivo));
                Session session = driver.session(
                        SessionConfig.forDatabase("6b0e96ad")
                )
        ) {

            String linea;
            String[] headers = null;

            int contador = 0;

            while ((linea = br.readLine()) != null) {

                // Leer encabezados
                if (headers == null) {
                    headers = linea.split(",");
                    continue;
                }

                String[] datos = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                if (datos.length < headers.length) {
                    continue;
                }

                Map<String, String> fila = mapearDatos(headers, datos);

                String trackId = limpiar(fila.get("track_id"));
                String trackName = limpiar(fila.get("track_name"));
                String artist = limpiar(fila.get("artists"));
                String album = limpiar(fila.get("album_name"));
                String genre = limpiar(fila.get("track_genre"));

                double popularity = parseDouble(fila.get("popularity"));
                double danceability = parseDouble(fila.get("danceability"));
                double energy = parseDouble(fila.get("energy"));

                // Validar datos mínimos
                if (trackId.isEmpty() || artist.isEmpty()) {
                    continue;
                }

                session.executeWrite(tx -> {

                    // Nodo canción
                    tx.run(
                            """
                            MERGE (t:Track {id: $id})
                            SET t.name = $name,
                                t.album = $album,
                                t.popularity = $popularity,
                                t.danceability = $danceability,
                                t.energy = $energy
                            """,
                            parameters(
                                    "id", trackId,
                                    "name", trackName,
                                    "album", album,
                                    "popularity", popularity,
                                    "danceability", danceability,
                                    "energy", energy
                            )
                    );

                    // Nodo artista
                    tx.run(
                            """
                            MERGE (a:Artist {name: $artist})
                            """,
                            parameters("artist", artist)
                    );

                    // Relación artista
                    tx.run(
                            """
                            MATCH (t:Track {id: $trackId})
                            MATCH (a:Artist {name: $artist})
                            MERGE (t)-[:PERFORMED_BY]->(a)
                            """,
                            parameters(
                                    "trackId", trackId,
                                    "artist", artist
                            )
                    );

                    // Nodo género
                    if (!genre.isEmpty()) {

                        tx.run(
                                """
                                MERGE (g:Genre {name: $genre})
                                """,
                                parameters("genre", genre)
                        );

                        // Relación género
                        tx.run(
                                """
                                MATCH (t:Track {id: $trackId})
                                MATCH (g:Genre {name: $genre})
                                MERGE (t)-[:HAS_GENRE]->(g)
                                """,
                                parameters(
                                        "trackId", trackId,
                                        "genre", genre
                                )
                        );
                    }

                    return null;
                });

                contador++;

                if (contador % 100 == 0) {
                    System.out.println("Importados " + contador + " registros...");
                }
            }

            System.out.println("Importación completada. Total: " + contador);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void crearAristasSimilitud() {

        try (
                Session session = driver.session(
                        SessionConfig.forDatabase("6b0e96ad")
                )
        ) {

            System.out.println("Creando relaciones de similitud...");

            // Índices
            session.executeWrite(tx -> {

                tx.run("""
                    CREATE INDEX track_id IF NOT EXISTS
                    FOR (t:Track)
                    ON (t.id)
                """);

                tx.run("""
                    CREATE INDEX artist_name IF NOT EXISTS
                    FOR (a:Artist)
                    ON (a.name)
                """);

                tx.run("""
                    CREATE INDEX genre_name IF NOT EXISTS
                    FOR (g:Genre)
                    ON (g.name)
                """);

                return null;
            });

            // Relaciones SIMILAR_TO
            session.executeWrite(tx -> {

                tx.run("""
                    MATCH (g:Genre)<-[:HAS_GENRE]-(t1:Track)
                    MATCH (g)<-[:HAS_GENRE]-(t2:Track)

                    WHERE t1.id < t2.id

                    WITH t1, t2,

                    sqrt(
                        (t1.danceability - t2.danceability)^2 +
                        (t1.energy - t2.energy)^2 +
                        ((t1.popularity - t2.popularity)/100.0)^2
                    ) AS distancia

                    WHERE distancia < 0.25

                    MERGE (t1)-[r:SIMILAR_TO]->(t2)

                    SET r.weight = distancia
                """);

                return null;
            });

            System.out.println("Relaciones creadas correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> mapearDatos(String[] headers, String[] datos) {

        Map<String, String> fila = new HashMap<>();

        int limite = Math.min(headers.length, datos.length);

        for (int i = 0; i < limite; i++) {

            fila.put(
                    headers[i].trim(),
                    datos[i].trim()
            );
        }

        return fila;
    }

    private String limpiar(String valor) {

        if (valor == null) {
            return "";
        }

        return valor
                .replace("\"", "")
                .trim();
    }

    private double parseDouble(String valor) {

        try {
            return Double.parseDouble(
                    limpiar(valor)
            );

        } catch (Exception e) {

            return 0.0;
        }
    }
}