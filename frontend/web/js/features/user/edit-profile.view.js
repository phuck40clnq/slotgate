// File: features/user/edit-profile.view.js
import { Router } from "../../core/router.js";
import { showFlash } from "../../shared/components/flash.js";
import { getErrorMessage } from "../../shared/error/error.registry.js";
import { Routes } from "../../shared/routes/index.js";
import { renderEditProfileComponent } from "./edit-profile.component.js";
import { getUserProfile, handleSubmitEditProfileForm } from "./user.controller.js";

export default async function render() {
  const container = document.createElement("section");

  const profileResult = await getUserProfile();

  if (!profileResult) return;

  if (!profileResult.ok) {
    container.textContent = getErrorMessage(profileResult.code);
    return container;
  }

  // 1. Load current profile
  const user = profileResult.data.user;

  // 2. Render form component
  const formEl = renderEditProfileComponent(
    user,
    {
      onSubmit: async (payload) => {
        const result = await handleSubmitEditProfileForm(payload);

        if (!result.ok) {
          formEl.showError(
            getErrorMessage(result.code)
          );
          return;
        }

        showFlash("Profile updated", "success");
        Router.navigate(Routes.user.profile());
    },

    onCancel: () => {
      showFlash("Cancel edit profile", "success");
      Router.navigate(Routes.user.profile());
    }
    }
  );

  container.append(formEl);
  return container;
}

