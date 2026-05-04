package com.proyecto;

import org.neo4j.driver.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static org.neo4j.driver.Values.parameters;

public class Neo4jManager implements AutoCloseable {

    private final Driver driver;

    // Formatos de fecha
    private static final List<DateTimeFormatter> DATE_FORMATS = new ArrayList<>();

    static {
        DATE_FORMATS.add(DateTimeFormatter.ISO_LOCAL_DATE);
        DATE_FORMATS.add(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        DATE_FORMATS.add(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    // Constructor
    public Neo4jManager(String uri, String user, String password) {
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    // Cerrar conexión
    @Override
    public void close() {
        driver.close();
    }

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

            // tracks
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

            // artistas
            if (!artist.isEmpty()) {
                tx.run(
                    "MERGE (a:Artist {name: $name}) " +
                    "WITH a MATCH (t:Track {id: $trackId}) " +
                    "MERGE (t)-[:PERFORMED_BY]->(a)",
                    parameters("name", artist, "trackId", trackId)
                );
            }

            // Playlists
            if (!playlistName.isEmpty()) {
                tx.run(
                    "MERGE (p:Playlist {name: $name}) " +
                    "WITH p MATCH (t:Track {id: $trackId}) " +
                    "MERGE (t)-[:PART_OF]->(p)",
                    parameters("name", playlistName, "trackId", trackId)
                );
            }

            // géneros
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

    private Map<String, String> mapearDatos(String[] headers, String[] datos) {
        Map<String, String> fila = new HashMap<>();
        int limite = Math.min(headers.length, datos.length);

        for (int i = 0; i < limite; i++) {
            fila.put(headers[i].trim(), datos[i].trim());
        }

        return fila;
    }

    private String limpiar(String valor) {
        return valor == null ? "" : valor.replace("\"", "").trim();
    }

    private double parseDouble(String valor) {
        try {
            return Double.parseDouble(limpiar(valor));
        } catch (Exception e) {
            return 0.0;
        }
    }

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