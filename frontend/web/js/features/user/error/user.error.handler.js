// File: features/user/error/user.error.handler.js
import { HttpError } from "../../../core/http/HttpError.js";
import { logger } from "../../../core/logger.js";
import { UserError } from "./UserError.js";

export function handleUserError(err) {
  logger.error("Error in user: " + err);

  if (!(err instanceof HttpError)) throw err;

  const code = err.data?.code;
  const userError = UserError.fromCode(code);
  if (userError) throw userError;

  logger.info("Passed user error handler");
  throw err;
}
