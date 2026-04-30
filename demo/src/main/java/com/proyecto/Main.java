package com.proyecto;

public class Main {
    public static void main(String[] args) {
        Neo4jManager manager = new Neo4jManager();

        try {
            //Inicia la base de datos (no cambiar nada de esta línea, la ruta es relativa al proyecto)
            manager.startDatabase("datos_grafos");
            String rutaDatos = "demo\\src\\main\\java\\com\\proyecto\\spotify_songs.csv";

            //completar el metodo en Neo4jManager para importar los datos del csv a la base de datos
            //manager.importarDatos(rutaDatos);

            System.out.println("Base de datos Neo4j iniciada correctamente.");
        } catch (Exception e) {
            System.err.println("Error al iniciar la base de datos Neo4j:");
            e.printStackTrace();
        } finally {
            manager.stopDatabase();
        }

    }
}