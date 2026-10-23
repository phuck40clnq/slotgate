// File: shared/error/AppError.js
export class AppError extends Error {
  constructor({ type, code, message, status, meta }) {
    if (!type) {
      throw new Error("AppError requires `type`");
    }
    if (!message) {
      throw new Error("AppError requires `message`");
    }

    super(message);

    this.name = "AppError";
    this.type = type;
    this.code = code;
    this.status = status;
    this.meta = meta ?? {};

    Error.captureStackTrace?.(this, AppError);
  }
}
