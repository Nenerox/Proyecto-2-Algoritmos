package com.proyecto;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

public class Neo4jManager {
    private DatabaseManagementService managementService;
    private GraphDatabaseService graphDb;
    // Lista de formatos de fecha soportados
    private static final List<DateTimeFormatter> DATE_FORMATS = new ArrayList<>();
    
    // Inicializa los formatos de fecha
    static {
        DATE_FORMATS.add(DateTimeFormatter.ISO_LOCAL_DATE);
        DATE_FORMATS.add(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        DATE_FORMATS.add(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    // Inicia la base de datos Neo4j en la ruta especificada. Si la base de datos no existe, la crea automáticamente
    public void startDatabase(String dbPath) {
        managementService = new DatabaseManagementServiceBuilder(Path.of(dbPath)).build();
        graphDb = managementService.database(DEFAULT_DATABASE_NAME);
        
        // cierra la base de datos si el programa termina forzosamente
        Runtime.getRuntime().addShutdownHook(new Thread(this::stopDatabase));
    }

    // se usa para poder hacer operaciones sobre el grafo
    public GraphDatabaseService getGraphDb() {
        return graphDb;
    }

    //Importa los datos del archivo CSV a la base de datos Neo4j
    // Se asume que el archivo CSV tiene una estructura específica y se crean nodos y relaciones
    public void importarDatos(String rutaArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            String[] headers = null;
            int contador = 0;

            while ((linea = br.readLine()) != null) {
                if (headers == null) {
                    // Lee la primera línea como encabezados
                    headers = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    continue;
                }

                // Divide la línea en campos, respetando comas dentro de comillas
                String[] datos = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                if (datos.length >= 11) {
                    // Crea nodos y relaciones para cada registro
                    crearNodosYRelaciones(headers, datos);
                    contador++; // AGREGADO - Incrementa el contador si se crea una relación

                    // Muestra progreso cada 100 registros
                    if (contador % 100 == 0) {
                        System.out.println("Importados " + contador + " registros...");
                    }
                }
            }

            // Mensaje final con total de registros
            System.out.println("Importación completada. Total de registros importados: " + contador);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método privado para crear nodos y relaciones en Neo4j
    private void crearNodosYRelaciones(String[] headers, String[] datos) {
        // Inicia una transacción
        try (Transaction tx = graphDb.beginTx()) {
            // Mapea los headers con los datos
            Map<String, String> fila = mapearDatos(headers, datos);
            
            // Extrae los valores del CSV
            String trackId = fila.getOrDefault("track_id", "");
            String trackName = fila.getOrDefault("track_name", "").replaceAll("\"", "");
            String artist = fila.getOrDefault("track_artist", "").replaceAll("\"", "");
            String playlistName = fila.getOrDefault("playlist_name", "").replaceAll("\"", "");
            String genre = fila.getOrDefault("playlist_genre", "").replaceAll("\"", "");
            String albumName = fila.getOrDefault("track_album_name", "").replaceAll("\"", "");
            String releaseDate = fila.getOrDefault("track_album_release_date", "");
            String popularity = fila.getOrDefault("track_popularity", "0");
            String danceability = fila.getOrDefault("danceability", "0");
            String energy = fila.getOrDefault("energy", "0");
            
            // Verifica que los datos esenciales no estén vacíos
            if (!trackId.isEmpty() && !artist.isEmpty()) {
                // Crea nodo Track con propiedades musicales
                tx.execute("MERGE (t:Track {id: $id, name: $name, popularity: $pop, danceability: $dance, energy: $energy}) SET t.album = $album, t.releaseDate = $releaseDate",
                    Map.of("id", trackId, "name", trackName, "pop", parseDouble(popularity), 
                           "dance", parseDouble(danceability), "energy", parseDouble(energy), 
                           "album", albumName, "releaseDate", parseDate(releaseDate)));
                
                // Crea nodo Artist y relación PERFORMED_BY
                if (!artist.isEmpty()) {
                    tx.execute("MERGE (a:Artist {name: $name}) WITH a MATCH (t:Track {id: $trackId}) MERGE (t)-[:PERFORMED_BY]->(a)",
                        Map.of("name", artist, "trackId", trackId));
                }
                
                // Crea nodo Playlist y relación PART_OF
                if (!playlistName.isEmpty()) {
                    tx.execute("MERGE (p:Playlist {name: $name}) WITH p MATCH (t:Track {id: $trackId}) MERGE (t)-[:PART_OF]->(p)",
                        Map.of("name", playlistName, "trackId", trackId));
                }
                
                // Crea nodo Genre y relación HAS_GENRE
                if (!genre.isEmpty()) {
                    tx.execute("MERGE (g:Genre {name: $name}) WITH g MATCH (t:Track {id: $trackId}) MERGE (t)-[:HAS_GENRE]->(g)",
                        Map.of("name", genre, "trackId", trackId));
                }
                
                // Confirma la transacción
                tx.commit();
            }
            
        } catch (Exception e) {
            // Manejo de errores
            System.err.println("Error al crear nodos: " + e.getMessage());
        }
    }

    // Método privado para mapear headers con datos
    private Map<String, String> mapearDatos(String[] headers, String[] datos) {
        Map<String, String> fila = new HashMap<>();
        // AGREGADO - Calcula el límite mínimo para evitar índices fuera de rango
        int limite = Math.min(headers.length, datos.length);

        // Itera sobre headers y datos
        for (int i = 0; i < limite; i++) {
            fila.put(headers[i].trim(), datos[i].trim());
        }

        return fila;
    }

    // Método privado para convertir String a double
    private double parseDouble(String valor) {
        try {
            // Elimina comillas y espacios antes de convertir
            return Double.parseDouble(valor.replace("\"", "").trim());
        } catch (Exception e) {
            // Si hay error, retorna 0.0
            return 0.0;
        }
    }

    // Método privado para parsear fechas
    private String parseDate(String valor) {
        // Limpia el valor eliminando comillas y espacios
        String limpio = valor == null ? "" : valor.replace("\"", "").trim();
        
        // Si está vacío, retorna vacío
        if (limpio.isEmpty()) {
            return "";
        }

        // Intenta parsear con diferentes formatos
        for (DateTimeFormatter formatter : DATE_FORMATS) {
            try {
                // Si logra parsear, retorna la fecha en formato estándar
                return LocalDate.parse(limpio, formatter).toString();
            } catch (DateTimeParseException ignored) {
                // Continúa con el siguiente formato
            }
        }

        // Si ningún formato funciona, retorna el valor limpio
        return limpio;
    }

    // Cierra la base de datos de forma segura
    public void stopDatabase() {
        if (managementService != null) {
            managementService.shutdown();
        }
    }
    public void crearAristasSimilitud() {
        try (Transaction tx = graphDb.beginTx()) {

            tx.execute("CREATE INDEX track_id IF NOT EXISTS FOR (t:Track) ON (t.id)");
            tx.execute("CREATE INDEX genre_name IF NOT EXISTS FOR (g:Genre) ON (g.name)");

            tx.execute(
                "MATCH (g:Genre)<-[:HAS_GENRE]-(t1:Track) " +
                "MATCH (g)<-[:HAS_GENRE]-(t2:Track) " +
                "WHERE t1.id < t2.id " +
                "  AND t1.danceability IS NOT NULL AND t2.danceability IS NOT NULL " +
                "  AND t1.energy IS NOT NULL AND t2.energy IS NOT NULL " +
                "  AND t1.popularity IS NOT NULL AND t2.popularity IS NOT NULL " +
                "WITH t1, t2, " +
                "     sqrt( " +
                "       (t1.danceability - t2.danceability)^2 + " +
                "       (t1.energy - t2.energy)^2 + " +
                "       ((t1.popularity - t2.popularity)/100.0)^2 " +
                "     ) AS w " +
                "WHERE w < 0.25 " +
                "MERGE (t1)-[r:SIMILAR_TO]->(t2) " +
                "SET r.weight = w"
            );

            tx.commit();
        }
    }
}