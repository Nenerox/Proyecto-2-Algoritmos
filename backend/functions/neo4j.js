const neo4j = require("neo4j-driver");

const driver = neo4j.driver(
    "neo4j+s://6b0e96ad.databases.neo4j.io",
        neo4j.auth.basic(
            "neo4j",
            "hu5iom_sl69EXx05ribA3ysQLpNycIXs_VasdwAOvYE"
        )
);

module.exports = driver;