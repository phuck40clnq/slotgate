// File: error.mapper.js
import { HttpError } from "../../core/http/HttpError.js";
import { NetworkError } from "../../core/http/NetworkError.js";
import { ErrorFactory } from "./error.factory.js";

export {
  getErrorFromApi,
}

const GATEWAY_ERROR_MAP = {
  GATEWAY_401: () => ErrorFactory.unauthorized(),
  GATEWAY_403: () => ErrorFactory.forbidden(),
  GATEWAY_404: () => ErrorFactory.notFound(),
  GATEWAY_502: () => ErrorFactory.gatewayError("Bad gateway", 502),
  GATEWAY_503: () => ErrorFactory.gatewayError("Service unavailable", 503),
  GATEWAY_504: () => ErrorFactory.gatewayError("Upstream timeout", 504),
};

function getErrorFromApi(err) {
  if (err instanceof NetworkError) {
    return ErrorFactory.networkError("Network error");
  }

  if (err instanceof HttpError) {
    const code = err.data?.code;
    if (!code) {
      return ErrorFactory.invalidResponse();
    }

    const factory = GATEWAY_ERROR_MAP[code];
    return factory ? factory() : ErrorFactory.gatewayError();
  }

  return null;
}
