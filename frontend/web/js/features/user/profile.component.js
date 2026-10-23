// File: features/user/profile.component.js

/**
 * User profile data
 *
 * @typedef {Object} User
 * @property {string} email - User email address
 * @property {string} name - User full name
 * @property {string} avatarUrl - URL of the user avatar image
 */

/**
 * Render profile view
 *
 * @function renderProfileComponent
 * @param {User} user - User data used to populate the profile
 * @param {(action: string) => void} [onAction] - Callback invoked when a profile action is triggered
 * @returns {HTMLElement} A `section` element containing the profile view
 */
export function renderProfileComponent(user, onAction) {
  /** @type {HTMLElement} */
  const container = document.createElement("section");
  container.className = "profile";

  container.innerHTML = `
    <h1>Profile</h1>

    <div class="profile-card">
      <img
        src="${user.avatarUrl}"
        alt="Avatar"
        class="profile-avatar"
      />

      <div class="profile-info">
        <p><strong>Email:</strong> ${user.email}</p>
        <p><strong>Name:</strong> ${user.name}</p>
        <p><strong>AvatarUrl:</strong> ${user.avatarUrl}</p>
      </div>
    </div>

    <div class="profile-actions">
      <button data-action="edit">Edit profile</button>
    </div>
  `;

  container.addEventListener("click", (e) => {
    const action = e.target.dataset.action;
    if (!action) return;
    onAction?.(action);
  });

  return container;
}
