// File: features/user/edit-profile.component.js

/**
 * User profile data
 *
 * @typedef {Object} User
 * @property {string} email - User email address
 * @property {string} name - User full name
 * @property {string} avatarUrl - URL of the user avatar image
 */

/**
 * @function renderEditProfileComponent
 * @param {User} user - User data used to populate the profile
 * @param {(action: string, payload?: Object) => void} [onAction] - Callback invoked when a profile action is triggered
 * @returns {HTMLElement} A `section` element containing the profile view
 */
export function renderEditProfileComponent(
  user,
  { onSubmit, onCancel } = {}
) {
  /** @type {HTMLElement} */
  const container = document.createElement("section");
  container.className = "edit-profile";

  container.innerHTML = `
    <h1>Edit Profile</h1>
    <form id="editProfileForm" novalidate>

      <label>
        Name
        <input type="text" name="name" value="${user.name}" autofocus required />
      </label>

      <label>
        Avatar URL
        <input name="avatarUrl" value="${user.avatarUrl}" />
      </label>

      <button type="submit">Save</button>
      <button type="button" data-action="cancel">Cancel</button>

      <p id="error" style="color:red"></p>
    </form>
  `;

  const form = container.querySelector("#editProfileForm");
  const errorEl = container.querySelector("#error");

  form.addEventListener("submit", (e) => {
    e.preventDefault();
    errorEl.textContent = "";

    const formData = new FormData(form);
    const rawData = Object.fromEntries(formData.entries());

    const payload = toRequest(rawData)

    Promise
      .resolve(onSubmit(payload))
      .catch((err) => {
        console.error(err)
      // ❗ KHÔNG xử lý error hệ thống
      // chỉ báo UX cơ bản
      errorEl.textContent = "Update failed";
    });
  });

  container.addEventListener("click", (e) => {
    const action = e.target.dataset.action;
    if (action === "cancel") {
      onCancel?.();
    }
  });

  // Expose method for view
  container.showError = (message) => {
    errorEl.textContent = message;
  };

  return container;
}

function toRequest(rawData) {
  return {
    fullName: rawData.name?.trim() || "Default full name",
    avatarUrl: rawData.avatarUrl || "/default.png"
  };
}
