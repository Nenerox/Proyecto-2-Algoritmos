package com.proyecto;

/**
 * Se conecta a Neo4j, importa los datos
 * y genera las relaciones del grafo.
 */
public class Main {

    /**
     * Ejecuta todo el flujo del programa
     * 
     */
    public static void main(String[] args) {

        // Datos de conexión a Neo4j
        String uri = "neo4j://127.0.0.1:7687";
        String user = "neo4j";
        String password = "algoritmos1234";

        // Rutas de archivos CSV
        String rutaDatos = "demo\\src\\main\\java\\com\\proyecto\\spotify_songs.csv";
        String rutaPruebas = "demo\\src\\main\\java\\com\\proyecto\\Data_Base_Pruebas.csv";

        try (Neo4jManager manager = new Neo4jManager(uri, user, password)) {

            System.out.println("Conectando a Neo4j...");

            // Importa datos de prueba
            manager.importarDatos(rutaPruebas);

            // Para usar el dataset completo, se debe descomentar la siguiente línea
            // manager.importarDatos(rutaDatos);

            // Crea relaciones de similitud entre canciones
            manager.crearAristasSimilitud();

            System.out.println("Datos importados correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}