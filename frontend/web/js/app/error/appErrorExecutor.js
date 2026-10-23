// File: app/error/appErrorExecutor.js
import { getSlot } from "../../core/layout.js";
import { logger } from "../../core/logger.js";
import { Router } from "../../core/router.js";
import { showFlash } from "../../shared/components/flash.js";
import { renderError } from "./appErrorPresenter.js";

export async function execute(decision, error, context) {
  try {
    safeFlash(decision?.message);
    switch (decision.type) {
      case "redirect":
        await safeRedirect(decision);
        break;

      case "render":
      default:
        await safeRender(error, context);
        break;
    }
  } catch (executorError) {
    logger.error("AppErrorExecutor failed (sync)", {
      executorError,
      decision,
      error,
    });

    hardFallback();
  }
}

// Safe functions
function safeFlash(message) {
  try {
    if (message) showFlash(message);
  } catch (e) {
    logger.warn("Flash failed", { e, message });
  }
}

function safeRedirect(decision) {
  Router
    .navigate(decision.to || "/")
    .catch(e => {
      logger.error("Redirect failed (async)", { e, decision });
      hardFallback();
    });
}

async function safeRender(error, ctx) {
  logger.info("Render error view", {
    path: ctx?.path,
    type: error.type,
  });

  try {
    const el = await renderError(error);

    const main = getSlot("main");
    if (!main) {
      throw new Error("Main slot not found");
    }

    main.replaceChildren(el);
  } catch (uiError) {
    logger.error("Error presenter failed (async)", {
      uiError,
      originalError: error,
    });

    hardFallback();
  }
}

function hardFallback() {
  document.body.innerHTML = `
    <h1>Something went wrong</h1>
    <p>Please reload the page.</p>
    <button onclick="location.reload()">Reload</button>
    <a href="/">Home</a>
  `;
}
