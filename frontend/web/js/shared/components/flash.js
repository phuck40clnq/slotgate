// FIle: shared/components/flash.js
import { getSlot } from "../../core/layout.js";
import { logger } from "../../core/logger.js";

export {
  initFlash,
  showFlash,
}

let flashContainer;

function initFlash(slotName = "flash") {
  flashContainer = getSlot(slotName);
}

function showFlash(message, type = "info", duration = 3000) {
  if (!flashContainer) {
    logger.debug("Flash not initialized");
    return;
  }

  logger.debug("Show flash", { message, type, duration });

  const div = document.createElement("div");
  div.className = `flash flash--${type}`;
  div.setAttribute("role", "alert");
  div.textContent = message;

  flashContainer.appendChild(div);

  setTimeout(() => {
    div.classList.add("flash--hide");
    setTimeout(() => div.remove(), 300);
  }, duration);
}
