const functions = require("firebase-functions");
const driver = require("./neo4j");

// Esta función crea la conexión :LIKES entre el usuario y la canción
exports.addFavorite = functions.https.onCall(async (request) => {
    if (!request.auth) {
        throw new functions.https.HttpsError(
            "unauthenticated",
            "Usuario no autenticado"
        );
    }

    const uid = request.auth.uid;
    const trackId = request.data.trackId;

    if (!trackId) {
        throw new functions.https.HttpsError(
            "invalid-argument",
            "Falta el trackId"
        );
    }

    const session = driver.session();

    try {
        await session.run(
            `
            MERGE (u:User {uid: $uid})
            WITH u
            MATCH (t:Track {id: $trackId})
            MERGE (u)-[:LIKES]->(t)
            `,
            { uid, trackId }
        );
        return { success: true };
    } catch (error) {
        console.error("Error al agregar favorito:", error);
        throw new functions.https.HttpsError("internal", error.message);
    } finally {
        await session.close();
    }
});

// Esta función devuelve todas las canciones que el usuario tiene con conexión :LIKES
exports.getFavorites = functions.https.onCall(async (request) => {
    if (!request.auth) {
        throw new functions.https.HttpsError(
            "unauthenticated",
            "Usuario no autenticado"
        );
    }

    const uid = request.auth.uid;
    const session = driver.session();

    try {
        const result = await session.run(
            `
            MATCH (u:User {uid: $uid})-[:LIKES]->(t:Track)
            MATCH (t)-[:PERFORMED_BY]->(a:Artist)
            RETURN
                t.id AS id,
                t.name AS name,
                a.name AS artist,
                t.album AS album,
                t.popularity AS popularity
            `,
            { uid }
        );

        return result.records.map(record => ({
            id: record.get("id"),
            name: record.get("name"),
            artist: record.get("artist"),
            album: record.get("album"),
            popularity: record.get("popularity")
        }));
    } catch (error) {
        console.error("Error al obtener favoritos:", error);
        throw new functions.https.HttpsError("internal", error.message);
    } finally {
        await session.close();
    }
});