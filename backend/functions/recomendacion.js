const functions = require("firebase-functions");
const driver = require("./neo4j");

exports.getRecommendations = functions.https.onCall(async (request) => {

    const session = driver.session();

    try {

        const genre = request.data.genre;
        const energy = request.data.energy;
        const danceability = request.data.danceability;
        const valence = request.data.valence;
        const limit = request.data.limit || 1;

        const result = await session.run(
            `
            MATCH (t:Track)-[:HAS_GENRE]->(g:Genre)

            WHERE g.name = $genre

            RETURN
                t.id AS id,
                t.name AS name,

                (
                    ABS(t.energy - $energy)
                    +
                    ABS(t.danceability - $danceability)
                    +
                    ABS(t.valence - $valence)
                )

                AS similarityScore

            ORDER BY similarityScore ASC
            LIMIT $limit
            `,
            {
                genre,
                energy,
                danceability,
                valence,
                limit
            }
        );

        return result.records.map(record => {

            const id = record.get("id");

            return {
                id: id,
                name: record.get("name"),
                spotifyLink:
                    `https://open.spotify.com/track/${id}`
            };
        });

    } catch (error) {

        console.error(error);

        throw new functions.https.HttpsError(
            "internal",
            error.message
        );

    } finally {

        await session.close();

    }
});