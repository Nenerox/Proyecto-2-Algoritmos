const recommendations = require("./recomendacion");
exports.getRecommendations = recommendations.getRecommendations;

const savePreferences = require("./savePreferences");
exports.savePreferences = savePreferences.savePreferences;

const getUserGenres = require("./getUserGenres");
exports.getUserGenres = getUserGenres.getUserGenres;

const saveDailyMood = require("./saveDailyMood");
exports.saveDailyMood = saveDailyMood.saveDailyMood;

const favorites = require("./favorites");
exports.addFavorite = favorites.addFavorite;
exports.getFavorites = favorites.getFavorites;
exports.removeFavorite = favorites.removeFavorite;