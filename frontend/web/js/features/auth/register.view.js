// File: features/auth/register.view.js
import { Router } from "../../core/router.js";
import { showFlash } from "../../shared/components/flash.js";
import { getErrorMessage } from "../../shared/error/error.registry.js";
import { Routes } from "../../shared/routes/index.js";
import { handleSubmitRegisterForm } from "./auth.controller.js";
import { renderRegisterComponent } from "./register.component.js";

/**
 * Render the register view and handles the form submission.
 *
 * - Creates a register form with email and password fields
 * - Handles submit event and calls the `register` controller
 * - Displays error messages when register fails
 *
 * @async
 * @returns {Promise<HTMLElement>} A `section` element containing the login form
 */
export default async function render() {
  const container = document.createElement("section");

  const registerForm = renderRegisterComponent(async (payload) => {
    const result = await handleSubmitRegisterForm(payload);

    // If report error, pass
    if (!result) return;

    // If auth error, show error
    if (!result.ok) {
      const message = getErrorMessage(result.error.code);
      registerForm.showError(message);
      return;
    }

    // Register success
    showFlash("Register success", "success");
    Router.navigate(Routes.dashboard.dashboard());
  });

  container.append(registerForm);

  return container;
}
