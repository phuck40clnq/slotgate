// File: features/user/user.controller.js
import { logger } from "../../core/logger.js";
import { Session } from "../../core/session.js";
import { showFlash } from "../../shared/components/flash.js";
import { reportError } from "../../shared/error/error.port.js";
import { Result } from "../../shared/model/Result.js";
import { UserError } from "./error/UserError.js";
import {
  loadCurrentUserProfileUseCase,
  submitUpdateUserProfileUseCase
} from "./user.usecase.js";

export {
  getUserProfile,
  handleSubmitEditProfileForm,
}

async function getUserProfile() {
  try {
    if (Session.get().user) {
      return Result.success({ user: Session.get().user });
    }

    const user = await loadCurrentUserProfileUseCase();
    Session.setUser(user);
    logger.info("Get user profile successful");

    return Result.success({ user });

  } catch (err) {
    if (err instanceof UserError) {
      return Result.fail({
        feature: "user",
        code: err.code,
      });
    }

    throw err;
  }
}

async function handleSubmitEditProfileForm(payload) {
  try {
    const user = await submitUpdateUserProfileUseCase(payload);
    Session.setUser(user);

    logger.info("Update profile success");

    return Result.success({ user });
  } catch (err) {
    if (err instanceof UserError) {
      return Result.fail({
        feature: "user",
        code: err.code,
      });
    }

    await reportError(err, {
      source: "edit-profile",
      feature: "user"
    });

    return;
  }
}
