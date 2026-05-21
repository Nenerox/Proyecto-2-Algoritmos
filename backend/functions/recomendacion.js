const functions = require("firebase-functions");
const driver = require("./neo4j");

exports.getRecommendations = functions.https.onCall(async (data, context) => {

    const session = driver.session();

    try {

        const genre = data.genre;

        const result = await session.run(
            `
            MATCH (t:Track)-[:HAS_GENRE]->(g:Genre {name: $genre})
            RETURN t.name AS track
            LIMIT 10
            `,
            { genre }
        );

        return result.records.map(record => ({
            name: record.get("track")
        }));

    } finally {

        await session.close();

    }
});