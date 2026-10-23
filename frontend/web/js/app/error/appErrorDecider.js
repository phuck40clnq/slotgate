// File: app/error/appErrorDecider.js
import { ERROR_TYPES } from "../../shared/error/error.types.js";
import { Routes } from "../../shared/routes/index.js";

const REDIRECT_BY_ERROR_TYPE = {
  [ERROR_TYPES.UNAUTHORIZED]: {
    to: Routes.auth.login(),
    message: "Please login again",
  },
};

/**
 * Decide how the app should react to an error
 *
 * @param {WebError} error
 * @param {object} context
 * @returns {object} decision
 */
export function decide(error, context = {}) {
  // Redirect rules
  const redirect = REDIRECT_BY_ERROR_TYPE[error.type];
  if (redirect) {
    return {
      type: "redirect",
      ...redirect,
    };
  }

  return { type: "render" };
}
