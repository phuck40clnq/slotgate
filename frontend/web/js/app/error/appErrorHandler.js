// File: app/error/appErrorHandler.js
import { logger } from "../../core/logger.js";
import { normalizeError } from "../../shared/error/index.js";
import { decide } from "./appErrorDecider.js";
import { execute } from "./appErrorExecutor.js";

export async function handleAppError(rawError, ctx = {}) {
  try {
    // 1. Normalize – always AppError
    const error = normalizeError(rawError);

    // 2. Log – single, structured, atomic
    logger.traceError(error, {
      boundary: "app",
      source: ctx.source,
      path: ctx.path,
      route: ctx.route?.path,
      type: error.type,
      status: error.status,
      code: error.code,
    });

    // 3. Decide – pure decision
    const decision = decide(error, ctx);

    // 4. Execute – side effects
    await execute(decision, error, ctx);

  } catch (fatal) {
    // ABSOLUTE LAST FALLBACK
    logger.error("AppErrorHandler failed", {
      originalError: rawError,
      fatal,
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
