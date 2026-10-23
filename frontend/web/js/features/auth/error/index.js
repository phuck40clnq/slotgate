// File: features/auth/error.index.js

export { AuthError } from "./AuthError.js";
export { handleAuthError } from "./auth.error.handler.js";

import { registerErrors } from "../../../shared/error/error.registry.js";
import { AUTH_ERROR_MESSAGES } from "./auth.error.message.js";

registerErrors(AUTH_ERROR_MESSAGES);

