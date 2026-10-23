// File: features/user/user.usecase.js
import { userApi } from "../../shared/api/user.api.js";
import { mapUserProfile } from "./user.mapper.js";
import { handleUserError } from "./error/user.error.handler.js";
import { UserError } from "./error/UserError.js";
import { USER_ERROR_CODES } from "./error/user.error.code.js";

export {
  loadCurrentUserProfileUseCase,
  submitUpdateUserProfileUseCase,
}

async function loadCurrentUserProfileUseCase() {
  try {
    const res = await userApi.getProfile();
    const user = mapUserProfile(res);

    if (user.status !== "ACTIVE") {
      throw new UserError(USER_ERROR_CODES.USER_NOT_ACTIVE, {
        userId: user.id,
      });
    }

    return user;
  } catch (err) {
    handleUserError(err);
  }
}

async function submitUpdateUserProfileUseCase(payload) {
  // 1. Fetch
  let res;

  try {
    res = await userApi.updateProfile(payload);
  } catch (err) {
    handleUserError(err);
  }

  const user = mapUserProfile(res);

  // 2. Business rule
  if (user.status !== "ACTIVE") {
    throw new UserError(
      USER_ERROR_CODES.USER_NOT_ACTIVE,
      {
        userId: user.id,
      }
    );
  }

  // 3. Return
  return user;
}
