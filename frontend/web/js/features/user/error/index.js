// File: features/user/error.index.js

export { UserError } from "./UserError.js";
export { handleUserError } from "./user.error.handler.js";

import { registerErrors } from "../../../shared/error/error.registry.js";
import { USER_ERROR_MESSAGES } from "./user.error.message.js";

registerErrors(USER_ERROR_MESSAGES);
