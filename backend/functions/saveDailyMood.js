const functions = require("firebase-functions");
const driver = require("./neo4j");

exports.saveDailyMood = functions.https.onCall(async (request) => {

    if (!request.auth) {
        throw new functions.https.HttpsError(
            "unauthenticated",
            "Usuario no autenticado"
        );
    }

    const uid = request.auth.uid;

    const {
        valence,
        energy,
        danceability,
        instrumentalness,
        acousticness,
        tempo,
        wantNewMusic
    } = request.data;

    const session = driver.session();
    
    try {
        await session.run(
            `
            MERGE (u:User {uid:$uid})

            SET
                u.valence = $valence,
                u.energy = $energy,
                u.danceability = $danceability,
                u.instrumentalness = $instrumentalness,
                u.acousticness = $acousticness,
                u.tempo = $tempo,
                u.wantNewMusic = $wantNewMusic,
                u.lastUpdated = datetime()

            RETURN u
            `,
            {
                uid,
                valence,
                energy,
                danceability,
                instrumentalness,
                acousticness,
                tempo,
                wantNewMusic
            }
        );
        return {
            success: true
        };

    } catch (error) {
        console.error(error);
        throw new functions.https.HttpsError("internal", error.message);
    } finally {
        await session.close();
    }
});