// File: app/error/appErrorPresenter.js
import { logger } from "../../core/logger.js";
import { Router } from "../../core/router.js";

export async function renderError(processedError) {
  if (!processedError) {
    throw new Error("renderError called without processedError");
  }

  const mod = await import("../../features/error/error.component.js");
  const render = mod.default;

  if (typeof render !== "function") {
    throw new Error("Error component does not export render()");
  }

  return render(processedError, handleErrorAction);
}

function handleErrorAction(action) {
  logger.debug("Error action triggered", { action });

  switch (action) {
    case "home":
      Router.navigate("/");
      break;

    case "back":
      Router.navigate(Router.getLastStablePath());
      break;

    default:
      logger.warn("Unknown error action", { action });
  }
}
