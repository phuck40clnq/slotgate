// File: shared/error/error.factory.js
import { AppError } from "./AppError.js";
import { ERROR_TYPES } from "./error.types.js";

export {
  unauthorized,
  forbidden,
  notFound,
  networkError,
  gatewayError,
  invalidResponse,
  unknownError,
};

export const ErrorFactory = {
  unauthorized,
  forbidden,
  notFound,
  networkError,
  gatewayError,
  invalidResponse,
  unknownError,
};

// Unauthorized error - 401
function unauthorized(message = "Unauthorized", meta) {
  return createError(ERROR_TYPES.UNAUTHORIZED, {
    message,
    status: 401,
    meta,
  });
}

// Forbidden - 403
function forbidden(message = "Not found", meta) {
  return createError(ERROR_TYPES.FORBIDDEN, {
    message,
    status: 403,
    meta,
  });
}

// Not found error - 404
function notFound(message = "Not found", meta) {
  return createError(ERROR_TYPES.NOT_FOUND, {
    message,
    status: 404,
    meta,
  });
}

// Network error
function networkError(message = "Network error", meta) {
  return createError(ERROR_TYPES.NETWORK_ERROR, {
    message,
    status: null, // not HTTP
    meta,
  });
}

// Http error
function gatewayError(
  message = "Gateway error or service temporarily unavailable",
  status = 502,
  meta
) {
  return createError(ERROR_TYPES.BAD_GATEWAY, {
    message,
    status,
    meta,
  });
}

function invalidResponse(message = "Invalid response", meta) {
  return createError(ERROR_TYPES.INVALID_RESPONSE, {
    message,
    status: 500,
    meta,
  });
}

function unknownError(message = "Something went wrong", meta) {
  return createError(ERROR_TYPES.UNKNOWN, {
    message,
    status: null,
    meta,
  });
}

// Private helper function
function createError(type, { message, status = null, meta = {} }) {
  return new AppError({
    type,
    message,
    status,
    meta,
  });
}
