package com.proyecto;

public class Main {
    public static void main(String[] args) {

        String uri = "neo4j://127.0.0.1:7687";
        String user = "neo4j";
        String password = "algoritmos1234";

        String rutaDatos = "demo\\src\\main\\java\\com\\proyecto\\spotify_songs.csv";
        String rutaPruebas = "demo\\src\\main\\java\\com\\proyecto\\Data_Base_Pruebas.csv";

        try (Neo4jManager manager = new Neo4jManager(uri, user, password)) {

            System.out.println("Conectando a Neo4j...");

            manager.importarDatos(rutaPruebas);

            //para base de datos completa, usar esta línea en lugar de la anterior
            // manager.importarDatos(rutaDatos);

            System.out.println("Datos importados correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}