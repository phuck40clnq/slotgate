// File: main.js
import "./app/bootstrap.js";
import { createApp } from "./app/app.js";
import { handleAppError } from "./app/error/appErrorHandler.js";

const app = createApp();

app.start().catch((err) => {
  handleAppError(err, { source: "app.start" });
});

// Global async leaks
window.addEventListener("error", (e) => {
  e.preventDefault();
  handleAppError(e.error || e, { source: "window.error" });
});

window.addEventListener("unhandledrejection", (e) => {
  e.preventDefault();
  handleAppError(e.reason, { source: "unhandledrejection" });
});
