const functions = require("firebase-functions");
const driver = require("./neo4j");

exports.addDislike = functions.https.onCall(async (request) => {

    if (!request.auth) {
        throw new functions.https.HttpsError(
            "unauthenticated",
            "Usuario no autenticado"
        );
    }

    const uid = request.auth.uid;
    const trackId = request.data.trackId;

    const session = driver.session();

    try {

        await session.run(
            `
            MERGE (u:User {uid:$uid})
            MATCH (t:Track {id:$trackId})
            MERGE (u)-[r:DISLIKES]->(t)

            ON CREATE SET r.createdAt = datetime()
            `,
            { uid, trackId }
        );

        return {
            success: true
        };

    } finally {
        await session.close();
    }
});

exports.getDislikes = functions.https.onCall(async (request) => {

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
            MATCH (u:User {uid:$uid})-[r:DISLIKES]->(t:Track)
            MATCH (t)-[:PERFORMED_BY]->(a:Artist)

            RETURN
                t.id AS id,
                t.name AS name,
                a.name AS artist,
                t.album AS album,

            ORDER BY r.createdAt DESC
            `,
            { uid }
        );

        return result.records.map(record => ({
            id: record.get("id"),
            name: record.get("name"),
            artist: record.get("artist"),
            album: record.get("album"),
        }));

    } finally {
        await session.close();
    }
});

exports.removeDislike = functions.https.onCall(async (request) => {

    if (!request.auth) {
        throw new functions.https.HttpsError(
            "unauthenticated",
            "Usuario no autenticado"
        );
    }

    const uid = request.auth.uid;
    const trackId = request.data.trackId;

    const session = driver.session();

    try {

        await session.run(
            `
            MATCH (u:User {uid:$uid})-[r:DISLIKES]->(t:Track {id:$trackId})
            DELETE r
            `,
            {
                uid,
                trackId
            }
        );

        return {
            success: true
        };

    } finally {
        await session.close();
    }
});