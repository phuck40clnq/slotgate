// File: core/http/NetworkError.js
export class NetworkError extends Error {
  constructor(cause) {
    super("Network error");
    this.name = "HttpTransportError"
    this.cause = cause;
  }
}
