const functions = require("firebase-functions");
const driver = require("./neo4j");

exports.getUserGenres = functions.https.onCall(async (request) => {

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
            MATCH (u:User {uid:$uid})
            -[:LIKES_GENRE]->
            (g:Genre)

            RETURN g.name AS genre
            `,
            { uid }
        );

        return result.records.map(
            record => record.get("genre")
        );

    } finally {
        await session.close();
    }
});