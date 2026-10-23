// File: app/app.js
import { mountLayout } from "../core/layout.js";
import { logger } from "../core/logger.js";
import { Router } from "../core/router.js";
import { logoutAppication } from "../features/auth/auth.controller.js";
import { initFlash, initNavbar } from "../shared/components/index.js";
import { bootstrapSession } from "./bootstrapSession.js";

export function createApp() {
  return { start };
}

async function start() {
  logger.info("App starting...");
  await bootstrapSession();
  initGlobalUI();
  initEvents();
}

function initGlobalUI() {
  mountLayout();
  logger.debug("Layout mounted");
  initComponent();
  logger.debug("Component initialized");
}

function initEvents() {
  Router.init();
  logger.debug("Router initialized");
}

// ----- Private helper -----
function initComponent() {
  initFlash();
  initNavbar({
    onLogout: logoutAppication
  })
}
