package com.proyecto;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.TransactionContext;
import static org.neo4j.driver.Values.parameters;

/**
 * Clase que maneja la conexión con Neo4j
 * realiza operaciones sobre el grafo, crea nodos y relaciones.
 */
public class Neo4jManager implements AutoCloseable {

    private final Driver driver;

    // Lista de formatos de fecha que se intentan al parsear fechas del CSV
    private static final List<DateTimeFormatter> DATE_FORMATS = new ArrayList<>();

    static {
        DATE_FORMATS.add(DateTimeFormatter.ISO_LOCAL_DATE);
        DATE_FORMATS.add(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        DATE_FORMATS.add(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    /**
     * Constructor que inicializa la conexión con Neo4j.
     * 
     * @param uri dirección de conexión
     * @param user usuario de la base de datos
     * @param password contraseña de la base de datos
     */
    public Neo4jManager(String uri, String user, String password) {
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    /**
     * Cierra la conexión con la base de datos.
     */
    @Override
    public void close() {
        driver.close();
    }

    /**
     * Importa los datos desde un archivo CSV y los inserta en Neo4j.
     * 
     * @param rutaArchivo ruta del archivo CSV
     */
    public void importarDatos(String rutaArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo));
             Session session = driver.session()) {

            String linea;
            String[] headers = null;
            int contador = 0;

            while ((linea = br.readLine()) != null) {

                if (headers == null) {
                    headers = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    continue;
                }

                String[] datos = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                if (datos.length >= 11) {
                    Map<String, String> fila = mapearDatos(headers, datos);

                    session.executeWrite(tx -> {
                        crearNodosYRelaciones(tx, fila);
                        return null;
                    });

                    contador++;

                    if (contador % 100 == 0) {
                        System.out.println("Importados " + contador + " registros...");
                    }
                }
            }

            System.out.println("Importación completada. Total: " + contador);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Crea los nodos y relaciones básicas en el grafo a partir de una fila del CSV.
     */
    private void crearNodosYRelaciones(TransactionContext tx, Map<String, String> fila) {

        String trackId = fila.getOrDefault("track_id", "");
        String trackName = limpiar(fila.get("track_name"));
        String artist = limpiar(fila.get("track_artist"));
        String playlistName = limpiar(fila.get("playlist_name"));
        String genre = limpiar(fila.get("playlist_genre"));
        String albumName = limpiar(fila.get("track_album_name"));
        String releaseDate = fila.getOrDefault("track_album_release_date", "");
        String popularity = fila.getOrDefault("track_popularity", "0");
        String danceability = fila.getOrDefault("danceability", "0");
        String energy = fila.getOrDefault("energy", "0");

        if (!trackId.isEmpty() && !artist.isEmpty()) {

            // Nodo Track
            tx.run(
                "MERGE (t:Track {id: $id}) " +
                "SET t.name = $name, t.popularity = $pop, t.danceability = $dance, " +
                "t.energy = $energy, t.album = $album, t.releaseDate = $releaseDate",
                parameters(
                    "id", trackId,
                    "name", trackName,
                    "pop", parseDouble(popularity),
                    "dance", parseDouble(danceability),
                    "energy", parseDouble(energy),
                    "album", albumName,
                    "releaseDate", parseDate(releaseDate)
                )
            );

            // Relación con Artist
            tx.run(
                "MERGE (a:Artist {name: $name}) " +
                "WITH a MATCH (t:Track {id: $trackId}) " +
                "MERGE (t)-[:PERFORMED_BY]->(a)",
                parameters("name", artist, "trackId", trackId)
            );

            // Relación con Playlist
            if (!playlistName.isEmpty()) {
                tx.run(
                    "MERGE (p:Playlist {name: $name}) " +
                    "WITH p MATCH (t:Track {id: $trackId}) " +
                    "MERGE (t)-[:PART_OF]->(p)",
                    parameters("name", playlistName, "trackId", trackId)
                );
            }

            // Relación con Genre
            if (!genre.isEmpty()) {
                tx.run(
                    "MERGE (g:Genre {name: $name}) " +
                    "WITH g MATCH (t:Track {id: $trackId}) " +
                    "MERGE (t)-[:HAS_GENRE]->(g)",
                    parameters("name", genre, "trackId", trackId)
                );
            }
        }
    }

    /**
     * Crea relaciones de similitud entre canciones basadas en sus características.
     * Se asigna un peso a cada relación.
     */
    public void crearAristasSimilitud() {

        try (Session session = driver.session()) {

            // Crear índices
            session.executeWrite(tx -> {
                tx.run("CREATE INDEX track_id IF NOT EXISTS FOR (t:Track) ON (t.id)");
                tx.run("CREATE INDEX genre_name IF NOT EXISTS FOR (g:Genre) ON (g.name)");
                return null;
            });

            // Crear relaciones SIMILAR_TO con peso
            session.executeWrite(tx -> {

                tx.run(
                    "MATCH (g:Genre)<-[:HAS_GENRE]-(t1:Track) " +
                    "MATCH (g)<-[:HAS_GENRE]-(t2:Track) " +
                    "WHERE t1.id < t2.id " +
                    "WITH t1, t2, " +
                    "sqrt( (t1.danceability - t2.danceability)^2 + " +
                    "(t1.energy - t2.energy)^2 + " +
                    "((t1.popularity - t2.popularity)/100.0)^2 ) AS w " +
                    "WHERE w < 0.25 " +
                    "MERGE (t1)-[r:SIMILAR_TO]->(t2) " +
                    "SET r.weight = w"
                );

                return null;
            });
        }
    }

    /**
     * Convierte los datos del CSV en un mapa de clave-valor.
     */
    private Map<String, String> mapearDatos(String[] headers, String[] datos) {
        Map<String, String> fila = new HashMap<>();
        int limite = Math.min(headers.length, datos.length);

        for (int i = 0; i < limite; i++) {
            fila.put(headers[i].trim(), datos[i].trim());
        }

        return fila;
    }

    /**
     * Limpia strings eliminando comillas y espacios.
     */
    private String limpiar(String valor) {
        return valor == null ? "" : valor.replace("\"", "").trim();
    }

    /**
     * Convierte un string a double.
     */
    private double parseDouble(String valor) {
        try {
            return Double.parseDouble(limpiar(valor));
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Parsea la fecha
     */
    private String parseDate(String valor) {
        String limpio = limpiar(valor);

        if (limpio.isEmpty()) return "";

        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                return LocalDate.parse(limpio, formatter).toString();
            } catch (DateTimeParseException ignored) {}
        }

        return limpio;
    }
}