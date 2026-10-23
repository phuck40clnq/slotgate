// File: features/auth/auth.controller.js
import { logger } from "../../core/logger.js";
import { Router } from "../../core/router.js";
import { Session } from "../../core/session.js";
import { showFlash } from "../../shared/components/flash.js";
import { reportError } from "../../shared/error/error.port.js";
import { Result } from "../../shared/model/Result.js";
import { Routes } from "../../shared/routes/index.js";
import { loginUseCase, logoutUseCase, registerUseCase } from "./auth.usecase.js";
import { AuthError } from "./error/index.js";

export {
  handleSubmitLoginForm,
  handleSubmitRegisterForm,
  logoutAppication,
}

async function handleSubmitLoginForm(payload) {
  try {
    const res = await loginUseCase(payload);
    if (res.token) {
      Session.setToken(res.token);
    }
    logger.info("Login successful", { token: res.token });

    return Result.success();
  } catch (err) {
    if (err instanceof AuthError) {
      return Result.fail({
        feature: "auth",
        code: err.code,
      });
    }

    await reportError(err, {
      source: "login",
      feature: "auth"
    });

    return;
  }
}

async function handleSubmitRegisterForm(payload) {
  try {
    const res = await registerUseCase(payload);
    if (res.token) {
      Session.setToken(res.token);
    }
    logger.info("Register successful", { token: res.token });

    return Result.success();
  } catch (err) {
    if (err instanceof AuthError) {
      return Result.fail({
        feature: "auth",
        code: err.code,
      });
    }

    await reportError(err, {
      source: "register",
      feature: "auth"
    });

    return;
  }
}

async function logoutAppication() {
  // TODO: Controller is handle view responsibility
  try {
    await logoutUseCase();
    Session.clear();
    showFlash("Logged out", "info");
    Router.navigate("/");
  } catch (err) {
    await reportError(err, {
      source: "logout",
      feature: "auth"
    });

    return;
  }
}
