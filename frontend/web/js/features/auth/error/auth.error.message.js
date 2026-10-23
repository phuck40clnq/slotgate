// File: features/auth/error/auth.error.message.js
import { AUTH_ERROR_CODES } from "./auth.error.code.js";

export const AUTH_ERROR_MESSAGES = Object.freeze({

  [AUTH_ERROR_CODES.INVALID_REGISTER_COMMAND]:
    "Invalid registration information.",

  [AUTH_ERROR_CODES.USER_ALREADY_EXISTS]:
    "This email is already registered.",

  [AUTH_ERROR_CODES.INVALID_LOGIN_COMMAND]:
    "Invalid login information.",

  [AUTH_ERROR_CODES.INVALID_CREDENTIALS]:
    "Incorrect email or password.",

});
