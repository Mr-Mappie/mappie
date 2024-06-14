const { parse, stringify } = require('himalaya'); // eslint-disable-line
const Image = require('@11ty/eleventy-img'); // eslint-disable-line

module.exports = async (src, cls) => {
  const metadata = await Image(src, {
    formats: ['svg'],
    dryRun: true,
  });
  const svg = parse(metadata.svg[0].buffer.toString());

  svg[0].attributes.push({
    key: 'class',
    value: cls || 'icon',
  });

  return stringify(svg);
};
