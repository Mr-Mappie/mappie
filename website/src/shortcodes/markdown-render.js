const markdownIt = require('markdown-it'); // eslint-disable-line

module.exports = async (children) => {
  const md = new markdownIt(); // eslint-disable-line
  return md.render(children);
};
