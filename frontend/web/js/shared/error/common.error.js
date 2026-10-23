// File: shared/error/common.error.js
export const COMMON_ERROR_CODES = Object.freeze({

  SERVICE_UNAVAILABLE: "SERVICE_UNAVAILABLE",
  INVALID_REQUEST_BODY: "INVALID_REQUEST_BODY",
  INTERNAL_ERROR: "INTERNAL_ERROR",

});

export const COMMON_ERROR_MESSAGES = Object.freeze({

  [COMMON_ERROR_CODES.SERVICE_UNAVAILABLE]:
    "Something went wrong, service is temporary unavailable.",

  [COMMON_ERROR_CODES.INVALID_REQUEST_BODY]:
    "Invalid request.",

  [COMMON_ERROR_CODES.INTERNAL_ERROR]:
    "Internal server error.",

});
