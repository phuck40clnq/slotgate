// File: features/auth/auth.guard.js
import { GuardResult } from "../../core/guard.result.js";
import { registerGuard } from "../../core/registry.js";
import { Session } from "../../core/session.js";
import { Routes } from "../../shared/routes/index.js";

registerGuard("auth", (route) =>
  Session.get().isAuthenticated
    ? GuardResult.allow()
    : GuardResult.redirect(Routes.auth.login()) // TODO: Fix - guard should not know route
);
