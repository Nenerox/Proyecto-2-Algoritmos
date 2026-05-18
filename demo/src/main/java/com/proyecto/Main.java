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
        String uri = "neo4j+s://6b0e96ad.databases.neo4j.io";
        String user = "6b0e96ad";
        String password = "hu5iom_sl69EXx05ribA3ysQLpNycIXs_VasdwAOvYE";

        // Rutas de archivos CSV
        String rutaDatos = "demo\\src\\main\\java\\com\\proyecto\\dataset.csv";

        try (Neo4jManager manager = new Neo4jManager(uri, user, password)) {

            System.out.println("Conectando a Neo4j...");

            // Importa datos
            manager.importarDatos(rutaDatos);

            // Crea relaciones de similitud entre canciones
            manager.crearAristasSimilitud();

            System.out.println("Datos importados correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}