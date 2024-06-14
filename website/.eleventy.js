require('dotenv').config();

const { execSync } = require('child_process');
const dateFilter = require('./src/filters/date-filter.js');
const eleventyNavigationPlugin = require('@11ty/eleventy-navigation');
const htmlMinTransform = require('./src/transforms/html-min-transform.js');
const isProduction = process.env.NODE_ENV === 'production';
const markdownIt = require('markdown-it');
const markdownItAnchor = require('markdown-it-anchor');
const pluginTOC = require('eleventy-plugin-toc');
const syntaxHighlight = require('@11ty/eleventy-plugin-syntaxhighlight');
const w3DateFilter = require('./src/filters/w3-date-filter.js');
const parentFilter = require('./src/filters/parent-filter.js');
const markdownRenderShortcode = require('./src/shortcodes/markdown-render.js');
const svgIconShortcode = require('./src/shortcodes/svg-icon.js');

module.exports = config => {
  config.addFilter('dateFilter', dateFilter);
  config.addFilter('w3DateFilter', w3DateFilter);
  config.addFilter('parentFilter', parentFilter);
  config.addFilter('debugger', (...args) => {
    console.log(...args);
    debugger;
  });

  config.addPlugin(eleventyNavigationPlugin);
  config.addPlugin(syntaxHighlight);
  config.addPlugin(pluginTOC);

  config.addPassthroughCopy({ './src/robots.txt': '/robots.txt' });
  config.addPassthroughCopy('./src/img/**');
  config.addPassthroughCopy('./src/css/**');
  config.addPassthroughCopy('./src/js/**');
  config.addPassthroughCopy('./src/font/**');

  config.addCollection('posts', collection => {
    const items = collection.getFilteredByGlob('./src/posts/**/posts/*.md');
    return items.sort((a, b) => a.data.eleventyNavigation.order - b.data.eleventyNavigation.order);
  });

  config.addAsyncShortcode('svgIcon', svgIconShortcode);
  config.addAsyncShortcode('markdownRender', markdownRenderShortcode);

  if (isProduction) {
    config.addTransform('htmlmin', htmlMinTransform);
  }

  const markdownLib = markdownIt({ html: true }).use(
    markdownItAnchor,
    {
      permalink: true,
      permalinkClass: 'anchor',
      permalinkSymbol: '#'
    }
  );

  config.setLibrary('md', markdownLib);

  config.on('eleventy.after', () => {
    execSync(`npx pagefind --source dist --glob \"**/*.html\"`, { encoding: 'utf-8' })
  });

  return {
    markdownTemplateEngine: 'njk',
    dataTemplateEngine: 'njk',
    htmlTemplateEngine: 'njk',
    dir: {
      input: 'src',
      output: 'dist'
    },
    passthroughFileCopy: true,
    pathPrefix: './',
  };
};
