// File: features/auth/register.component.js

/**
 * Render login form component
 *
 * Creates a login form with email and password fields
 *
 * @param {(payload: { email: string, password: string }) => void} [onSubmit] - Callback function when the form is submitted
 * @returns {HTMLElement} A `section` element containing the login form
 */
export function renderRegisterComponent(onSubmit) {
  const container = document.createElement("section");

  container.innerHTML = `
    <h2>Register</h2>
    <form id="registerForm" novalidate>
      <input type="email" name="email" placeholder="Email" autofocus required />
      <input type="password" name="password" placeholder="Password" required />
      <button type="submit">Register</button>
      <p id="error" role="alert" style="color:red"></p>
    </form>
  `;

  const form = container.querySelector("#registerForm");
  const errorEl = container.querySelector("#error");
  const submitBtn = form.querySelector("button");

  form.addEventListener("submit", (e) => {
    e.preventDefault();
    errorEl.textContent = "";

    const payload = Object.fromEntries(
      new FormData(form).entries()
    );

    // UI validation ONLY
    if (!payload.email) {
      errorEl.textContent = "Email is required";
      return;
    }

    if (!payload.password || payload.password.length < 1) {
      errorEl.textContent = "Password must be at least 1 character";
      return;
    }

    submitBtn.disabled = true;

    Promise
      .resolve(onSubmit(payload))
      .finally(() => {
        submitBtn.disabled = false;
      });
  });


  // Expose method for view
  container.showError = (message) => {
    errorEl.textContent = message;
  };

  return container;
}
