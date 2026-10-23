// File: shared/error/error.types.js
export const ERROR_TYPES = Object.freeze({
  UNAUTHORIZED    : "UNAUTHORIZED",       // 401
  FORBIDDEN       : "FORBIDDEN",          // 403
  NOT_FOUND       : "NOT_FOUND",          // 404

  NETWORK_ERROR: "NETWORK_ERROR", // Cannot connect to gateway
  INVALID_RESPONSE: 'INVALID_RESPONSE', // Server contract broken

  BAD_GATEWAY          : "BAD_GATEWAY",

  BUSINESS: "BUSINESS",

  UNKNOWN: "UNKNOWN",
});
