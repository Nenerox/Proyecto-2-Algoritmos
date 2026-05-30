const functions = require("firebase-functions");
const driver = require("./neo4j");

exports.savePreferences = functions.https.onCall(async (request) => {

    if (!request.auth) {
        throw new functions.https.HttpsError(
            "unauthenticated",
            "Usuario no autenticado"
        );
    }

    const uid = request.auth.uid;
    const genres = request.data.genres;
    const session = driver.session();

    try {
        await session.run(
            `
            MERGE (u:User {uid: $uid})
            `,
            { uid }
        );

        await session.run(
            `
            MATCH (u:User {uid:$uid})
            OPTIONAL MATCH (u)-[r:LIKES_GENRE]->()
            DELETE r
            `,
            { uid }
        );

        for (const genre of genres) {
            await session.run(
                `
                MATCH (u:User {uid:$uid})

                MERGE (g:Genre {name:$genre})

                MERGE (u)-[:LIKES_GENRE]->(g)
                `,
                {
                    uid,
                    genre
                }
            );
        }

        return {
            success: true
        };

    } finally {
        await session.close();
    }
});