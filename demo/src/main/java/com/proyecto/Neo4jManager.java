package com.proyecto;
import java.nio.file.Path;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.GraphDatabaseService;

public class Neo4jManager {
    private DatabaseManagementService managementService;
    private GraphDatabaseService graphDb;

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

    //cierra la base de datos de forma segura
    public void stopDatabase() {
        if (managementService != null) {
            managementService.shutdown();
        }
    }

    //Se tiene que hacer el método para importar los datos del csv a la base de datos.
    public void importarDatos(String rutaArchivo) {
    }
}