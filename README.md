# SYMPHONIX — NEO4J SPOTIFY GRAPH & MOBILE APP

**Symphonix** es una plataforma integral de descubrimiento musical que combina una aplicación móvil en **Kotlin/Compose**, un backend en **Node.js** y una base de datos de grafos en **Neo4j**. El sistema permite gestionar preferencias musicales, analizar estados de ánimo y generar relaciones de similitud para ofrecer recomendaciones personalizadas mediante el uso de grafos.

---

## Descripción del Proyecto

Este proyecto implementa una solución de extremo a extremo:

- **Frontend Móvil**: App nativa desarrollada con **Jetpack Compose** (Material 3) para descubrir música, gestionar favoritos y analizar métricas emocionales.
- **Backend Serverless**: Funciones en **Firebase Functions** que ejecutan la lógica de negocio y algoritmos de recomendación.
- **Modelo de Grafo (Neo4j)**:
  - **Nodos**: `Track`, `Artist`, `Playlist`, `Genre`, `User`.
  - **Relaciones**: 
    - `(Track)-[:PERFORMED_BY]->(Artist)`
    - `(Track)-[:HAS_GENRE]->(Genre)`
    - `(User)-[:LIKES]->(Track)` (Sistema de favoritos sincronizado con el grafo)
    - `(Track)-[:SIMILAR_TO {weight}]->(Track)` (Similitud por atributos de audio)
- **Algoritmo de Recomendación**: Cálculo de proximidad basado en características numéricas (danceability, energy, valence, tempo).

---

## Instalación de Neo4j

Para que Symphonix funcione, necesitas una instancia de Neo4j activa y configurada.

### Link oficial de descarga
- **Neo4j Desktop (Recomendado):** [https://neo4j.com/download/](https://neo4j.com/download/)

### Configuración del entorno
1. Descarga e instala **Neo4j Desktop**.
2. Crea una base de datos local (DBMS) y presiona **Start**.
3. Asegúrate de que la instancia esté escuchando en: `neo4j://127.0.0.1:7687`.
4. El backend se conecta automáticamente usando las credenciales configuradas en `backend/functions/neo4j.js`.

---

## Vista del Grafo (Neo4j Browser)

Al interactuar con la aplicación, puedes visualizar las conexiones de tus canciones favoritas en tiempo real con esta consulta en **Neo4j Browser**:


---

##  Estructura del Proyecto

El repositorio se organiza en los siguientes componentes principales:
---

---

## Requisitos y Tecnologías

- **Android**: Java 17+, Android Studio Ladybug+, Firebase Auth & Functions.
- **Backend**: Node.js 22+, Firebase CLI, NPM.
- **Base de Datos**: Neo4j 5.x+, Cypher Query Language.
- **Librerías Android**: Jetpack Compose, Coil (Imágenes), Navigation Compose.

---


### 2. Configuración de la App Android
- Agrega tu archivo `google-services.json` en `android/frontend/app/`.
- Sincroniza Gradle y ejecuta el proyecto en un emulador o dispositivo real.

### 3. Importación de Datos (Java)

## Pasos para la Ejecución

### 1. Desplegar el Backend
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
