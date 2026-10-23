// File: core/registry.js

export {
  routesRegistry,
  guardsRegistry,
  registerRoute,
  registerGuard
}

const routesRegistry = [];
const guardsRegistry = new Map();

/**
 * @param {Route | Route[]} routes
 */
function registerRoute(routes) {
  if (!Array.isArray(routes)) {
    routesRegistry.push(routes);
    return;
  }

  routesRegistry.push(...routes);
}

/**
 * @param {string} key
 * @param {Function} guardFn
 */
function registerGuard(key, guardFn) {
  guardsRegistry.set(key, guardFn);
}

