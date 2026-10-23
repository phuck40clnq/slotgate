// File: features/auth/error/auth.error.handler.js
import { HttpError } from "../../../core/http/HttpError.js";
import { logger } from "../../../core/logger.js";
import { AuthError } from "./AuthError.js";

export function handleAuthError(err) {
  logger.error("Error in auth: " + err);

  if (!(err instanceof HttpError)) throw err;

  const code = err.data?.code;
  const authError = AuthError.fromCode(code);
  if (authError) throw authError;

  logger.info("Passed auth error handler");
  throw err;
}
