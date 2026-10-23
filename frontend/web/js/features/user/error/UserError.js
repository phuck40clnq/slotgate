// File: features/user/error/UserError.js
import { BusinessError } from "../../../shared/error/BusinessError.js";
import { COMMON_ERROR_CODES } from "../../../shared/error/common.error.js";
import { USER_ERROR_CODES } from "./user.error.code.js";

export class UserError extends BusinessError {
  constructor(code, options = {}) {
    super(code, {
      status: 400,
      ...options,
    });
  }

  static fromCode(code) {
    const userCodes = Object.values(USER_ERROR_CODES);
    const commonCodes = Object.values(COMMON_ERROR_CODES);
    if (![...userCodes, ...commonCodes].includes(code)) {
      return null;
    }

    return new UserError(code);
  }
}
