// File: shared/error/error.port.js
import { logger } from "../../core/logger.js";

export {
  bindErrorHandler,
  reportError,
}

let handler = null;

function bindErrorHandler(fn) {
  handler = fn;
}

async function reportError(err, context) {
  if (!handler) {
    logger.error("ErrorPort not bound", err);
    return Promise.resolve();;
  }
  return handler(err, context);
}
