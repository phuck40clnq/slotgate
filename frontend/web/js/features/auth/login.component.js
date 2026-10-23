// File: features/auth/login.component.js

/**
 * Render login form component
 *
 * Creates a login form with email and password fields
 *
 * @param {(payload: { email: string, password: string }) => void} [onSubmit] - Callback function when the form is submitted
 * @returns {HTMLElement} A `section` element containing the login form
 */
export function renderLoginComponent(onSubmit) {
  /** @type {HTMLElement} */
  const container = document.createElement("section");

  container.innerHTML = `
    <h2>Login</h2>
    <form id="loginForm" novalidate>
      <input type="email" name="email" placeholder="Email" autofocus required />
      <input type="password" name="password" placeholder="Password" required />
      <button type="submit">Login</button>
      <p id="error" role="alert" style="color:red"></p>
    </form>
  `;

  const form = container.querySelector("#loginForm");
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
