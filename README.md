# PROYECTO-2-ALGORITMOS — NEO4J SPOTIFY GRAPH

Proyecto en Java que se conecta a **Neo4j**, **importa datos desde archivos CSV** (dataset de Spotify / dataset de pruebas) y **genera relaciones de similitud** entre canciones para construir un grafo consultable.


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
- **Maven 3.6+** (recomendado)
- **Neo4j Desktop o Neo4j Server** corriendo localmente

---

## Tecnologías Utilizadas

- **Java 17**
- **Maven**
- **Neo4j 5.x**
- **Neo4j Java Driver**

---

## Instalación

### 1. Clonar el repositorio
```bash
git clone https://github.com/Nenerox/Proyecto-2-Algoritmos.git
cd Proyecto-2-Algoritmos
```

### 2. Verificar Java
```bash
java -version
```
Debe mostrar Java 17 o superior.

### 3. Compilar con Maven
```bash
cd demo
mvn clean install
```

---

## Configuración de Neo4j

En `Main.java` están los datos de conexión:

- URI: `neo4j://127.0.0.1:7687`
- User: `neo4j`
- Password: `algoritmos1234`

Asegúrate de que:

1. Neo4j esté corriendo en tu máquina.
2. El puerto **7687** esté disponible.
3. El usuario/contraseña coincidan con tu instancia.

> Si tu Neo4j usa otro password, cambia la variable `password` en `Main.java`.

---

## Archivos de Datos (CSV)

El programa soporta **dos CSV**:

- Dataset completo (Spotify):
  - `demo\src\main\java\com\proyecto\spotify_songs.csv`
- Dataset de pruebas:
  - `demo\src\main\java\com\proyecto\Data_Base_Pruebas.csv`

En `Main.java`, por defecto se importa el de **pruebas**:

```java
manager.importarDatos(rutaPruebas);
```

Si deseas importar el dataset completo, descomenta:

```java
// manager.importarDatos(rutaDatos);
```

---

## Compilación y Ejecución

### Opción 1: Con Maven (Recomendado)

1. Compila:
```bash
cd demo
mvn clean install
```

2. Ejecuta (desde la raíz del repo):
```bash
cd ..
java -cp demo/target/classes com.proyecto.Main
```

> Si tu sistema no encuentra clases, asegúrate de haber compilado y que exista `demo/target/classes`.

---

## ¿Qué hace el programa? (Flujo)

Cuando ejecutas `Main`:

1. Se conecta a Neo4j (`Neo4jManager`)
2. Importa datos desde un CSV:
   - Crea/merge nodos `Track`
   - Crea/merge nodos `Artist`, `Playlist`, `Genre`
   - Crea relaciones:
     - `PERFORMED_BY`
     - `PART_OF`
     - `HAS_GENRE`
3. Crea índices en Neo4j:
   - Para `Track.id`
   - Para `Genre.name`
4. Genera aristas de similitud entre canciones:
   - Solo compara canciones **del mismo género**
   - Calcula un peso `w` (distancia euclidiana aproximada)
   - Si `w < 0.25` crea:
     - `(t1)-[:SIMILAR_TO {weight: w}]->(t2)`

---

## Cálculo de Similitud (SIMILAR_TO)

Para dos canciones del mismo género, se calcula:

- `danceability`
- `energy`
- `popularity` (normalizada dividiendo por 100)

Peso aproximado:
- Mientras menor `w`, más similares.

Condición:
- Solo se crea relación si `w < 0.25`.

---

## Formatos de Fecha Soportados

El parser de fechas intenta:

- `yyyy-MM-dd` (ISO)
- `dd-MM-yyyy`
- y variantes equivalentes

Si la fecha no se puede parsear, se guarda el string “tal cual”.

---

## Consultas Cypher Útiles (para verificar)

### Ver algunos Tracks
```cypher
MATCH (t:Track)
RETURN t
LIMIT 10;
```

### Ver artistas y sus canciones
```cypher
MATCH (t:Track)-[:PERFORMED_BY]->(a:Artist)
RETURN a.name, t.name
LIMIT 20;
```

### Ver similitudes creadas
```cypher
MATCH (t1:Track)-[r:SIMILAR_TO]->(t2:Track)
RETURN t1.name, r.weight, t2.name
ORDER BY r.weight ASC
LIMIT 25;
```

### Ver canciones por género
```cypher
MATCH (t:Track)-[:HAS_GENRE]->(g:Genre)
RETURN g.name, count(t) AS canciones
ORDER BY canciones DESC;
```

---

## Solución de Problemas

### No conecta a Neo4j
- Verifica que Neo4j esté corriendo
- Verifica URI y credenciales en `Main.java`
- Confirma el puerto `7687`

### Error leyendo CSV
- Revisa que el archivo exista en la ruta indicada
- Confirma que el CSV tenga headers y al menos 11 columnas (el código valida `datos.length >= 11`)

### Rutas en Windows
El código usa rutas con `\\`:
- En macOS/Linux puede ser mejor cambiar a `/` o construir rutas con `Paths.get(...)`.

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
