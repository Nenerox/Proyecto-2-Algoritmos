Este proyecto utiliza una base de datos de grafos en Neo4j Aura para generar recomendaciones de canciones utilizando relaciones de similitud entre tracks.

La lógica de recomendación se basa en relaciones `SIMILAR_TO` con pesos (`weight`) calculados según características musicales.

---

# Instancia Neo4j Aura

URI
NEO4J_URI=neo4j+s://TU_URI_AQUI

Usuario
NEO4J_USERNAME=TU_USUARIO

Contraseña
NEO4J_PASSWORD=TU_PASSWORD

# Uso desde Firebase Functions

Instalar driver:

npm install neo4j-driver


## Ejemplo de conexión

```javascript
const neo4j = require('neo4j-driver');

const driver = neo4j.driver(
  process.env.NEO4J_URI,
  neo4j.auth.basic(
    process.env.NEO4J_USERNAME,
    process.env.NEO4J_PASSWORD
  )
);

#Lógica esperada de la app

1. Usuario responde preguntas
2. Firebase genera query Cypher
3. Neo4j devuelve recomendaciones
4. Android muestra canciones recomendadas

# Notas

* La instancia Aura ya contiene el grafo cargado.
* No es necesario volver a importar el CSV.
* El grafo ya contiene relaciones con pesos.
* La lógica de recomendación debe consultar `SIMILAR_TO`.
