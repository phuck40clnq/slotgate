// File: core/layout.js

import { logger } from "./logger.js";

export {
  mountLayout,
  getSlot,
}

let slots = {}

function mountLayout() {
  const app = document.getElementById("app")
  if (!app) {
    logger.error("#app not found in DOM");
    throw new Error("#app not found")
  }

  logger.debug("Mounting main layout");
  app.innerHTML = `
    <header id="header"></header>
    <div id="nav-root"></div>
    <div id="flash-root"></div>
    <main id="main"></main>
  `;

  slots = {
    header: document.getElementById("header"),
    main: document.getElementById("main"),
    flash: document.getElementById("flash-root"),
    nav: document.getElementById("nav-root")
  };

  logger.debug("Layout slots initialized", { slots: Object.keys(slots) });
}

function getSlot(name) {
  const slot = slots[name]
  if (!slot) {
    throw new Error(`Slot "${name}" not found`)
  }
  return slot
}
