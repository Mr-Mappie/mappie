module.exports = (collection, parent) => {
  if (!parent) return collection;
  const filtered = collection.filter((item) => item.data.eleventyNavigation?.parent === parent);
  return filtered.sort((a, b) => a.data.eleventyNavigation.order - b.data.eleventyNavigation.order);
};
