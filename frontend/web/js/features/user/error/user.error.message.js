// File: features/user/error/user.error.message.js
import { USER_ERROR_CODES } from "./user.error.code.js";

export const USER_ERROR_MESSAGES = Object.freeze({

  [USER_ERROR_CODES.USER_NOT_FOUND]:
    "Cannot find user.",

  [USER_ERROR_CODES.USER_NOT_ACTIVE]:
    "Sorry your nick is not active, please register another account"

});
