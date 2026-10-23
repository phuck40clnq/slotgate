// File: core/router.js
import { getSlot } from "./layout.js";
import { runGuards } from "./guard.js";
import { routesRegistry } from "./registry.js";
import { AppError } from "../shared/error/index.js";
import { logger } from "./logger.js";
import { handleAppError } from "../app/error/appErrorHandler.js";
import { showFlash } from "../shared/components/flash.js";

const Router = Object.freeze({
  init: initRouter,
  navigate,
  getLastStablePath,
});

export { Router };

let lastStablePath = "/";

/**
 * Initialize the SPA router.
 *
 * Responsibilities:
 * - Handle link clicks with [data-link]
 * - Handle popstate events
 * - Render initial route
 *
 * This function must be called exactly once at app bootstrap.
 *
 * @returns {void}
 */
function initRouter() {
  document.addEventListener("click", onLinkClick);
  window.addEventListener("popstate", render);

  render();
}

/**
 * Performs a user-intent navigation.
 *
 * This pushes a new entry into the browser history and triggers
 * a route render. Intended for user-driven navigation such as:
 * - menu clicks
 * - button actions
 *
 * Architectural rule:
//  * - Must NOT be called from guards, controllers, or views.
//  * - Prefer <a data-link> for static navigation.
 *
 * @param {string} path - Absolute path to navigate to (e.g. "/profile")
 * @returns {Promise<void>}
 */
async function navigate(path) {
  history.pushState({}, "", path);
  await render();
}

function getLastStablePath() {
  return lastStablePath;
}

/**
 * Resolves and renders the current route.
 *
 * Execution flow:
 * 1. Resolve route by path
 * 2. Run route guards
 * 3. Handle guard decisions (redirect / block)
 * 4. Lazy-load and mount view
 * 5. Handle and render errors
 *
 * *Internal router lifecycle function.*
 *
 * @returns {Promise<void>}
 */
async function render() {
  const main = getSlot("main");
  const path = normalizedPath(location.pathname);

  try {
    const route = await resolveRoute(path);
    logger.traceRoute(route);

    // Run route guards
    const decision = await runGuards(route);
    logger.traceRoute(route, decision);

    if (!decision.allow) {
      if (!decision.redirectTo) {
        logger.warn("Guard blocked route", { path });
        return; // blocked
      }

      if (decision.redirectTo === path) {
        logger.error("Redirect loop detected", { path });
        return;
      }

      const returnUrl = path;

      const targetRedirectTo = appendQuery(
        decision.redirectTo,
        { returnUrl }
      );

      logger.info("Guard redirecting", { from: path, to: targetRedirectTo });
      return redirect(targetRedirectTo);
    }

    // Lazy load view module
    logger.debug("Loading view module", { routePath: route.path });
    const view = await route.view();
    const el = await view();

    mount(main, el);
    logger.info("Rendered route", { routePath: route.path });

    // Set last stable path if everything is good
    lastStablePath = path;
  } catch (err) {
    logger.traceError(err, { path });
    handleAppError(
      err,
      {
        source: "router",
        path: path
      }
    );
  }
}

/* ----------------------- HELPERS ----------------------- */
/**
 * Performs a system-enforced redirect.
 *
 * This replaces the current history entry and re-renders the route.
 * Used for:
 * - guard failures
 * - authorization errors
 * - forced application redirects
 *
 * *Internal use only. Must NOT be exported.*
 *
 * @param {string} path - Absolute path to redirect to
 * @returns {Promise<void>}
 */
async function redirect(path) {
  history.replaceState({}, "", path);
  await render();
}

/**
 * Append query parameters to a given path and return the resulting URL string.
 *
 * @param {*} path
 *   The URL path to append query parameters to (e.g. "/products").
 *
 * @param {*} query
 *   An object representing query parameters.
 *   Keys and values will be converted to strings.
 *
 * @returns {string}
 *   The resulting URL pathname with query string.
 *
 * @example
 * appendQuery("/products", { page: 2, sort: "price" });
 * // -> "/products?page=2&sort=price"
 */
function appendQuery(path, query) {
  const url = new URL(path, location.origin);
  Object.entries(query).forEach(([k, v]) =>
    url.searchParams.set(k, v)
  );
  return url.pathname + url.search;
}

function mount(container, el) {
  if (!el) return;
  container.replaceChildren(el);
}

/**
 * Normalize path: remove trailing slash, root = "/"
 * @param {string} path
 * @returns {string} normalized path
 */
function normalizedPath(path) {
  let p = path.replace(/\/$/, "");
  if (p === "") p = "/";
  return p;
}

/**
 * Resolve a path to a route object.
 * Simplified: no children, exact match only.
 *
 * @param {string} path
 * @returns {Promise<object>} route
 * @throws {@link AppError} if route not found.
 * @see {@link AppError} for more details.
 */
async function resolveRoute(path) {
  const normalized = normalizedPath(path);

  const route = routesRegistry.find(r => r.path === normalized);

  if (!route) {
    logger.warn("Route not found", { path });
    throw notFound(path);
  }

  logger.debug("Route resolved", { path: route.path });
  return route;
}

function onLinkClick(e) {
  const link = e.target.closest("[data-link]");
  if (!link) return;
  e.preventDefault();
  const path = link.getAttribute("href");
  logger.debug("Link clicked", { href: path });
  showFlash(`link click ${path}`);
  navigate(path);
}
