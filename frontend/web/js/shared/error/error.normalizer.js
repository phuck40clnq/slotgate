// File: shared/error/error.handler.js
import { logger } from "../../core/logger.js";
import { BusinessError } from "./BusinessError.js";
import { unknownError } from "./error.factory.js";
import { getErrorFromApi } from "./error.mapper.js";
import { ERROR_TYPES } from "./error.types.js";
import { AppError } from "./AppError.js";
import { HttpError } from "../../core/http/HttpError.js";
import { NetworkError } from "../../core/http/NetworkError.js";

export {
  normalizeError,
}

/**
 * Handle and return the AppError
 *
 * @param {Error} error
 * @returns {AppError}
 */
function normalizeError(error) {
  // This is for dev
  console.log("Normalizing error...");
  console.error(error);

  // AppError -> pass through
  if (error instanceof AppError) {
    return error;
  }

  // BusinessError -> Map into AppError
  if (error instanceof BusinessError) {
    return new AppError({
      type: ERROR_TYPES.BUSINESS,
      message: error.message,
      status: error.status,
      code: error.code,
      meta: error.meta,
    });
  }

  // API / network / http error
  if (isApiError(error)) {
    const mapped = getErrorFromApi(error);
    if (mapped) return mapped;
  }

  // Unknown / technical error
  return unknownError();
}

function isApiError(err) {
  return (
    err && (
      err instanceof HttpError ||
      err instanceof NetworkError
    )
  );
}
