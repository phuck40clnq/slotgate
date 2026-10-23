// File: features/auth/auth.usecase.js
import { authApi } from "../../shared/api/auth.api.js"
import { handleAuthError, AuthError } from "./error/index.js";

export {
  loginUseCase,
  registerUseCase,
  logoutUseCase
}

async function loginUseCase(payload) {
  try {
    return await authApi.login(payload);
  } catch (err) {
    handleAuthError(err);
  }
}

async function registerUseCase(payload) {
  try {
    return await authApi.register(payload);
  } catch (err) {
    handleAuthError(err);
  }
}

async function logoutUseCase() {
  // Maybe use authApi to logout
  // return await authApi.logout();
}
