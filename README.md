# Conexión entre Android Studio, Firebase y Neo4j Aura

## Arquitectura del proyecto

La aplicación se divide en tres partes principales:

```text id="z1"
Android Studio
      ↓
Firebase Functions
      ↓
Neo4j Aura
```

Cada una tiene una función distinta dentro del proyecto.


# 1. Android Studio

Android Studio se encarga únicamente de la interfaz de usuario.

La app:

* muestra preguntas al usuario
* recibe respuestas
* envía solicitudes a Firebase
* muestra las canciones recomendadas

La aplicación Android no se conecta directamente a Neo4j Aura.

# 2. Firebase Functions

Firebase Functions funciona como intermediario entre Android y Neo4j Aura.

Aquí es donde se coloca la lógica de recomendaciones.

Firebase:

* recibe datos desde Android
* genera queries Cypher
* consulta Neo4j Aura
* devuelve resultados en formato JSON

Firebase es el componente que se conecta a Neo4j Aura.


# 3. Neo4j Aura

Neo4j Aura contiene toda la base de datos de grafos.

La instancia ya tiene:

* canciones
* artistas
* géneros
* playlists
* relaciones de similitud

Neo4j Aura únicamente almacena y consulta el grafo.


# Funcionamiento general

## Paso 1

El usuario responde preguntas en la app.

## Paso 2

Android envía esa información a Firebase Functions.

Ejemplo:

```json id="z2"
{
  "genre": "rock",
  "energy": 0.7,
  "danceability": 0.6
}
```


## 3

Firebase convierte esas respuestas en una query Cypher.

Ejemplo:

```cypher id="z3"
MATCH (t:Track)-[:HAS_GENRE]->(g:Genre)
WHERE g.name = $genre
AND t.energy > $energy
AND t.danceability > $danceability
RETURN t
LIMIT 10
```

## 4

Neo4j Aura ejecuta la query y devuelve resultados.

## 5

Firebase recibe los resultados y los devuelve a Android.


## 6

Android muestra las recomendaciones al usuario.

# Relaciones importantes del grafo

## Nodos

* `Track`
* `Artist`
* `Genre`
* `Playlist`


## Relaciones

* `PERFORMED_BY`
* `PART_OF`
* `HAS_GENRE`
* `SIMILAR_TO`


# Relación SIMILAR_TO

La relación `SIMILAR_TO` contiene una propiedad llamada `weight`.

Mientras menor sea el valor del peso, más similares son las canciones.

# Queries principales utilizadas

## Recomendaciones desde una canción

```cypher id="z5"
MATCH (t:Track {name: $song})-[r:SIMILAR_TO]->(rec)
RETURN rec.name, r.weight
ORDER BY r.weight ASC
LIMIT 5
```

## Recomendaciones por género

```cypher id="z6"
MATCH (t:Track)-[:HAS_GENRE]->(g:Genre)
WHERE g.name = $genre
RETURN t
LIMIT 10
```


## Recomendaciones por características musicales

```cypher id="z7"
MATCH (t:Track)
WHERE t.energy > $energy
AND t.danceability > $danceability
RETURN t
LIMIT 10
```


# Conexión desde Firebase Functions

Firebase utiliza el paquete:

```bash id="z8"
npm install neo4j-driver
```

# Ejemplo de conexión

```javascript id="z9"
const neo4j = require('neo4j-driver');

const driver = neo4j.driver(
  process.env.NEO4J_URI,
  neo4j.auth.basic(
    process.env.NEO4J_USERNAME,
    process.env.NEO4J_PASSWORD
  )
);


# Variables necesarias

Firebase necesita las siguientes variables:

NEO4J_URI
NEO4J_USERNAME
NEO4J_PASSWORD

Estas variables corresponden a la instancia creada en Neo4j Aura.
