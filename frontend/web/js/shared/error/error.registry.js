// File: shared/error/error.registry.js

import { logger } from "../../core/logger.js";

export {
  registerErrors,
  getErrorMessage,
}

const registry = Object.create(null);

function registerErrors(errorMap) {
  for (const code in errorMap) {
    if (registry[code]) {
      logger.warn(`Duplicate error code registered: ${code}`);
    }
    registry[code] = errorMap[code];
  }
}

function getErrorMessage(code) {
  if (!(code in registry)) {
    return "Code is not registered";
  }
  return registry[code];
}
