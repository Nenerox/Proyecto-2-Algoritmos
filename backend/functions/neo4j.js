const neo4j = require("neo4j-driver");

const URI = "neo4j+s://6b0e96ad.databases.neo4j.io";
const USER = "6b0e96ad";
const PASSWORD = "hu5iom_sl69EXx05ribA3ysQLpNycIXs_VasdwAOvYE";

const driver = neo4j.driver(
    URI,
    neo4j.auth.basic(USER, PASSWORD)
);

module.exports = driver;