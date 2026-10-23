// File: core/guard.js
import { GuardResult } from "./guard.result.js";
import { logger } from "./logger.js";
import { guardsRegistry } from "./registry.js";

/**
 * Represents the result of a guard.
 * @typedef {Object} GuardResult
 * @property {boolean} allow - Whether the route is allowed to proceed.
 * @property {string} [redirectTo] - Optional URL to redirect if the guard blocks the route.
 */

/**
 * Runs all guards registered for a given route.
 *
 * Guards are defined in the route object as an array of keys. Each key maps
 * to a guard function in the `guardRegistry`. Guards are run sequentially in
 * the order they appear in the route.guards array. If any guard returns
 * `{ allow: false }`, execution stops immediately and that result is returned.
 *
 * Each guard receives the entire route object as an argument, so it can inspect
 * meta, path, or any other route properties.
 *
 * @param {Object} route - The route object to check guards for.
 * @param {string} route.path - The route path (e.g., "/dashboard").
 * @param {Function} route.view - The async view function returning the render function.
 * @param {Array<string>} [route.guards] - Array of guard keys to execute.
 * @param {Object} [route.meta] - Optional metadata for the route.
 *
 * @returns {Promise<GuardResult>} A promise that resolves to the first failing guard result
 * or `{ allow: true }` if all guards pass.
 *
 * @example
 * const route = {
 *   path: "/dashboard",
 *   guards: ["auth"],
 *   meta: { title: "Dashboard" },
 * };
 *
 * const result = await runGuards(route);
 * if (!result.allow) {
 *   // router decides how to react
 * }
 */
export async function runGuards(route) {
  if (!route.guards?.length) {
    logger.debug("No guards for route", { path: route.path });
    return GuardResult.allow();
  }

  logger.debug("Running guards", { routePath: route.path, guards: route.guards });

  for (const key of route.guards) {
    const guardFn = guardsRegistry.get(key);
    if (!guardFn) {
      logger.warn("Guard key not found in registry", { guardKey: key });
      continue; // Skip if guard key not found
    }

    logger.debug("Running guard", { guardKey: key, route: route.path });
    const result = await guardFn(route); // Pass route to guard

    logger.traceGuard(key, route, result);

    // If not allow, return any guard result
    if (!result.allow) {
      logger.info("Guard blocked route", {
        guardKey: key, route: route.path, redirect: result.redirectTo
      });
      return result; // Stop on first failing guard
    }
  }

  // All guards passed
  logger.debug("All guards passed", { route: route.path });
  return GuardResult.allow();
}
