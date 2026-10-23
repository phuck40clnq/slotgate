// File: core/logger.js
import { timestamp } from "../shared/utils/time.js";
import { CONFIG } from "./config.js";

const LEVELS = { DEBUG: 0, INFO: 1, WARN: 2, ERROR: 3 };
const currentLevel = LEVELS[CONFIG.LOG_LEVEL] ?? LEVELS.DEBUG;

// level nào thì gửi về server
const SERVER_LOG_LEVEL = LEVELS[CONFIG.SERVER_LOG_LEVEL] ?? LEVELS.ERROR;

export const logger = {
  debug: (msg, meta) => log("DEBUG", msg, meta),
  info:  (msg, meta) => log("INFO", msg, meta),
  warn:  (msg, meta) => log("WARN", msg, meta),
  error: (msg, meta) => log("ERROR", msg, meta),

  // -------- trace helpers --------
  traceRoute: (route, decision) =>
    logger.debug("Route navigation", {
      path: route?.path,
      guards: route?.guards,
      decision
    }),

  traceGuard: (guardKey, route, result) =>
    logger.debug("Guard executed", {
      guardKey,
      routePath: route?.path,
      result
    }),

  traceError: (err, context = {}) =>
    logger.error("Error occurred", {
      ...context,
      message: err.message,
      stack: err.stack
    }),

  traceFunction: (fn, name) => async (...args) => {
    logger.debug("Entering function", { name, args });
    const result = await fn(...args);
    logger.debug("Exiting function", { name, result });
    return result;
  },
};

/* ---------------- internal ---------------- */

function log(level, msg, meta) {
  const lvl = LEVELS[level];
  if (lvl < currentLevel) return;

  const payload = {
    level,
    time: timestamp(),
    msg,
    meta
  };

  // 1️⃣ console
  consoleOutput(level, payload);

  // 2️⃣ server (best-effort)
  if (CONFIG.LOG_ENDPOINT && lvl >= SERVER_LOG_LEVEL) {
    sendToServer(payload);
  }
}

function consoleOutput(level, payload) {
  const line = `[${level}][${payload.time}] ${payload.msg}`;
  const meta = payload.meta ?? "";

  switch (level) {
    case "DEBUG": console.log(line, meta); break;
    case "INFO":  console.info(line, meta); break;
    case "WARN":  console.warn(line, meta); break;
    case "ERROR": console.error(line, meta); break;
  }
}

function sendToServer(payload) {
  // fire & forget
  fetch(CONFIG.LOG_ENDPOINT, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  }).catch(() => {
    // tuyệt đối không throw
  });
}
