// File: core/http/HttpError.js
export class HttpError extends Error {
  constructor(res, data) {
    super("HTTP error");
    this.name = "HttpResponseError";
    this.status = res.status;
    this.data = data;
  }
}
