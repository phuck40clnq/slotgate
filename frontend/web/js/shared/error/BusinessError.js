// File: shared/error/BusinessError.js
export class BusinessError extends Error {
  constructor(code, { status, meta } = {}) {
    super(code);

    this.name = this.constructor.name || "BusinessError";
    this.code = code;
    this.status = status;
    this.meta = meta;
  }
}
