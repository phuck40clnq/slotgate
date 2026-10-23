// File: features/auth/error/AuthError.js
import { BusinessError } from "../../../shared/error/BusinessError.js";
import { COMMON_ERROR_CODES } from "../../../shared/error/common.error.js";
import { AUTH_ERROR_CODES } from "./auth.error.code.js";

export class AuthError extends BusinessError {
  constructor(code, options = {}) {
    super(code, {
      status: 400,
      ...options,
    });
  }

  static fromCode(code) {
    const authCodes = Object.values(AUTH_ERROR_CODES);
    const commonCodes = Object.values(COMMON_ERROR_CODES);
    if (![...authCodes, ...commonCodes].includes(code)) {
      return null;
    }

    return new AuthError(code);
  }
}
