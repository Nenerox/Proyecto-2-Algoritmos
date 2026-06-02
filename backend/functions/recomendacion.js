const functions = require("firebase-functions");
const driver = require("./neo4j");
const neo4j = require("neo4j-driver");

exports.getRecommendations = functions.https.onCall(async (request) => {

    if (!request.auth) {
        throw new functions.https.HttpsError(
            "unauthenticated",
            "Usuario no autenticado"
        );
    }

    const uid = request.auth.uid;

    const limit = Number.parseInt(request.data.limit || 20,10);
    const genre = request.data.genre || null;

    const session = driver.session();

    try {

        let query = `
        MATCH (u:User {uid:$uid})

        MATCH (t:Track)
        MATCH (t)-[:PERFORMED_BY]->(a:Artist)

        WHERE
            t.valence IS NOT NULL
            AND t.energy IS NOT NULL
            AND t.danceability IS NOT NULL
            AND t.instrumentalness IS NOT NULL
            AND t.acousticness IS NOT NULL
            AND t.tempo IS NOT NULL
            AND NOT EXISTS {
                MATCH (u)-[:DISLIKES]->(t)
            }
            AND NOT EXISTS {
                MATCH (u)-[:LIKES]->(t)
            }
        `;

        if (genre) {
            query += `
            AND EXISTS {
                MATCH (t)-[:HAS_GENRE]->(:Genre {name:$genre})
            }
            `;
        }

        query += `
        WITH
            u,
            t,
            a,

            sqrt(
                (t.valence - u.valence)^2 +
                (t.energy - u.energy)^2 +
                (t.danceability - u.danceability)^2 +
                (t.instrumentalness - u.instrumentalness)^2 +
                (t.acousticness - u.acousticness)^2 +
                ((t.tempo - u.tempo) / 200.0)^2
            ) AS distance

        WITH
            u,
            t,
            a,

            CASE
                WHEN u.wantNewMusic = true
                THEN distance + (coalesce(t.popularity, 0) / 50.0)

                ELSE distance - (coalesce(t.popularity, 0) / 100.0)
            END AS score

        RETURN
            t.id AS id,
            t.name AS name,
            a.name AS artist,
            t.album AS album,
            t.popularity AS popularity,

            t.energy AS energy,
            t.danceability AS danceability,
            t.instrumentalness AS instrumentalness,
            t.acousticness AS acousticness,
            t.tempo AS tempo,

            score

        ORDER BY score ASC
        LIMIT toInteger($limit)
        `;

        const result = await session.run(
            query,
            {
                uid,
                genre,
                limit: neo4j.int(limit)
            }
        );

        return result.records.map(record => ({
            id: record.get("id"),
            name: record.get("name"),
            artist: record.get("artist"),
            album: record.get("album"),
            popularity: record.get("popularity"),

            energy: record.get("energy"),
            danceability: record.get("danceability"),
            instrumentalness: record.get("instrumentalness"),
            acousticness: record.get("acousticness"),
            tempo: record.get("tempo"),

            score: record.get("score")
        }));

    } finally {
        await session.close();
    }
});