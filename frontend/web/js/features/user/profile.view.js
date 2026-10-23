// File: features/user/profile.view.js
import { Router } from "../../core/router.js";
import { getErrorMessage } from "../../shared/error/error.registry.js";
import { Routes } from "../../shared/routes/index.js";
import { renderProfileComponent } from "./profile.component.js";
import { getUserProfile } from "./user.controller.js";

export default async function render() {
  const container = document.createElement("section");

  const result = await getUserProfile();

  // If report error, passed
  if (!result) {
    return;
  }

  // Show error if it is features error
  if (!result.ok) {
    console.error(result);
    const message = getErrorMessage(result.error.code);
    container.textContent = message;
    return container;
  }

  // 1. Load data
  const user = result.data.user;

  // 2. Render component + wire action
  const profileEl = renderProfileComponent(user, (action) => {
    switch (action) {
      case "edit":
        Router.navigate(Routes.user.edit_profile());
        break;
    }
  });

  container.append(profileEl);
  return container;
}
