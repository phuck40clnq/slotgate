// File: app/bootstrap.js

// register all routes - side effects
import "../features/auth/index.js";
import "../features/dashboard/index.js";
import "../features/home/index.js";
import "../features/user/index.js";

// Wiring infras
// --- http ---
import { Session } from "../core/session.js";
import { setHttpTokenProvider } from "../core/http/http.js";
setHttpTokenProvider(() => Session.get().token);

// --- error ---
import { bindErrorHandler } from "../shared/error/error.port.js";
import { handleAppError } from "./error/appErrorHandler.js";
bindErrorHandler(handleAppError);
