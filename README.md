# SYMPHONIX — NEO4J SPOTIFY GRAPH

**Symphonix** es una aplicación en Java que se conecta a **Neo4j**, **importa datos desde archivos CSV** (dataset de Spotify / dataset de pruebas) y **genera relaciones de similitud** entre canciones para construir un grafo consultable desde **Neo4j Browser**.

---

## Descripción

Este proyecto implementa:

- **Conexión a Neo4j** usando el **Neo4j Java Driver**
- **Importación de datos desde CSV** para crear nodos y relaciones
- **Modelo de grafo** con nodos:
  - `Track`
  - `Artist`
  - `Playlist`
  - `Genre`
- **Relaciones del grafo**:
  - `(Track)-[:PERFORMED_BY]->(Artist)`
  - `(Track)-[:PART_OF]->(Playlist)`
  - `(Track)-[:HAS_GENRE]->(Genre)`
  - `(Track)-[:SIMILAR_TO {weight}]->(Track)`
- **Cálculo de similitud** por género y características numéricas (danceability, energy, popularity)
- **Creación de índices** en Neo4j para optimizar operaciones
- Manejo básico de errores con `try/catch` y cierre automático con `AutoCloseable`

---

## Instalación de Neo4j (Link oficial + uso)

Para poder ejecutar Symphonix necesitas tener **Neo4j** instalado y corriendo localmente.

### Link oficial de descarga
- **Neo4j Download (oficial):** https://neo4j.com/download/

### ¿Cómo se usa para este proyecto?
1. Entra al link y descarga **Neo4j Desktop** (recomendado porque es lo más sencillo para el curso).
2. Instálalo y ábrelo.
3. Crea una base de datos local (DBMS) y presiona **Start**.
4. Abre **Neo4j Browser** (desde Neo4j Desktop).
5. Verifica que la instancia esté escuchando en:
   - `neo4j://127.0.0.1:7687`
6. Ajusta las credenciales en `Main.java` si tu contraseña es distinta.

> En este proyecto, Neo4j se usa como el motor de base de datos grafo donde se guardan los nodos (`Track`, `Artist`, `Genre`, `Playlist`) y las relaciones (`SIMILAR_TO`, etc.), para luego poder consultarlos con **Cypher** desde Neo4j Browser.

---

## Vista del Grafo (Neo4j Browser)

Al correr Symphonix e importar los datos, puedes visualizar el grafo en **Neo4j Browser**.

Consulta típica para ver conexiones:

```cypher
MATCH (n)-[r]->(m)
RETURN n, r, m
LIMIT 50
```

**Ejemplo de resultados (dataset de pruebas):**
- **Nodos:** 42
  - `Track`: 38
  - `Artist`: 2
  - `Genre`: 1
  - `Playlist`: 1
- **Relaciones:** 50
  - `SIMILAR_TO`: 44
  - `HAS_GENRE`: 2
  - `PART_OF`: 2
  - `PERFORMED_BY`: 2

> Los números pueden variar si importas el dataset completo.

---

## Estructura del Proyecto

> **TRABAJAR SIEMPRE DESDE** la carpeta `demo/` (ahí vive el proyecto Maven).

```
Proyecto-2-Algoritmos/
├── README.md
├── demo/                              ← TRABAJAR SIEMPRE DESDE AQUÍ
│   ├── pom.xml
│   └── src/
│       └── main/
│           └── java/
│               └── com/
│                   └── proyecto/
│                       ├── Main.java
│                       └── Neo4jManager.java
└── datos_grafos/
    └── data/                          ← Archivos locales de Neo4j (generados por Neo4j)
```

---

## Requisitos

- **Java 17 o superior**
- **Maven 3.6+**
- **Neo4j Desktop o Neo4j Server** corriendo localmente

---

## Tecnologías Utilizadas

- **Java 17**
- **Maven**
- **Neo4j 5.x**
- **Neo4j Java Driver**

---

## Instalación del Proyecto

### 1. Clonar el repositorio
```bash
git clone https://github.com/Nenerox/Proyecto-2-Algoritmos.git
cd Proyecto-2-Algoritmos
```

### 2. Compilar con Maven
```bash
cd demo
mvn clean install
```

---

## Configuración de Neo4j (Credenciales)

En `Main.java` están los datos de conexión:

- URI: `neo4j://127.0.0.1:7687`
- User: `neo4j`
- Password: `algoritmos1234`

---

## Archivos de Datos (CSV)

- Dataset completo:
  - `demo\src\main\java\com\proyecto\spotify_songs.csv`
- Dataset de pruebas:
  - `demo\src\main\java\com\proyecto\Data_Base_Pruebas.csv`

Por defecto se importa el de **pruebas**:

```java
manager.importarDatos(rutaPruebas);
```

---

## Ejecución

Compilar:
```bash
cd demo
mvn clean install
```

Ejecutar (desde la raíz del repo):
```bash
cd ..
java -cp demo/target/classes com.proyecto.Main
```

---

## Contribuyentes

- **María Jimena Vásquez Meléndez** — 25092  
- **José Alejandro Sagastume Valey** — 25257  
- **Andrés Pineda Schwarz** — 25212  
- **Miguel Angel Sajquín González** — 252149  

---

## Curso

**CC2003 - Algoritmos y Estructura de Datos**  
Universidad del Valle de Guatemala  
Proyecto 2
