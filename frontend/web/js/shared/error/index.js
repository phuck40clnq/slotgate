// File: shared/error/index.js
import { COMMON_ERROR_MESSAGES } from "./common.error.js";
import { registerErrors } from "./error.registry.js";

registerErrors(COMMON_ERROR_MESSAGES);

export * from "./error.factory.js";
export { normalizeError } from "./error.normalizer.js";
export { AppError } from "./AppError.js";
export { BusinessError } from "./BusinessError.js";
