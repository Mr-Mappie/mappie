const crypto = require('crypto');

module.exports = {
  random() {
    return crypto.randomUUID();
  },
  year() {
    const date = new Date();
    return date.getFullYear();
  },
};
