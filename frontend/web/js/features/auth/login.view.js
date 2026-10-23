// File: features/auth/login.view.js
import { Router } from "../../core/router.js";
import { showFlash } from "../../shared/components/flash.js";
import { getErrorMessage } from "../../shared/error/error.registry.js";
import { Routes } from "../../shared/routes/index.js";
import { handleSubmitLoginForm } from "./auth.controller.js";
import { renderLoginComponent } from "./login.component.js";

/**
 * Render the login view and handles the form submission.
 *
 * - Creates a login form with email and password fields
 * - Handles submit event and calls the `login` controller
 * - Displays error messages when login fails
 *
 * @async
 * @returns {Promise<HTMLElement>} A `section` element containing the login form
 */
export default async function render() {
  /** @type {HTMLElement} */
  const container = document.createElement("section");

  const loginForm = renderLoginComponent(async (payload) => {
    const result = await handleSubmitLoginForm(payload);

    // If report error, pass
    if (!result) return;

    // If auth error, show error
    if (!result.ok) {
      const message = getErrorMessage(result.error.code);
      loginForm.showError(message);
      return;
    }

    // Login success
    showFlash("Login success", "success");
    Router.navigate(Routes.dashboard.dashboard());
  });

  container.append(loginForm);

  return container;
}
