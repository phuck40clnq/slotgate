// File: core/http/http.js
import { CONFIG } from "../config.js";
import { HttpError } from "./HttpError.js";
import { NetworkError } from "./NetworkError.js";

let tokenProvider = null;
const GATEWAY_URL = `${CONFIG.API_BASE_URL}`;

// Export
export {
  http,
  setHttpTokenProvider,
}

const http = {
  get: (url, options) =>
    request("GET", url, options),
  post: (url, body, options) =>
    request("POST", url, { ...options, body }),
  put: (url, body, options) =>
    request("PUT", url, { ...options, body }),
  delete: (url, options) =>
    request("DELETE", url, options)
}

function setHttpTokenProvider(provider) {
  tokenProvider = provider;
}

// Helper function
async function request(method, url, options = {}) {
  const needAuth = options.auth !== false;
  const token = needAuth ? tokenProvider?.() : null;

  const body =
    options.body !== undefined
      ? JSON.stringify(options.body)
      : undefined;

  const headers = {
    ...(token && { Authorization: `Bearer ${token}` }),
    ...options.headers,
    ...(body !== undefined && { "Content-Type": "application/json" }),
  };

  let res;
  try {
    res = await fetch(GATEWAY_URL + url, {
      method,
      headers,
      body: body
    });
  } catch (err) {
    console.error(err);
    throw new NetworkError(err);
  }

  let data = null;
  if (res.status !== 204) {
    try {
      data = await res.json();
    } catch {
      data = null;
    }
  }

  if (!res.ok) {
    console.error(res);
    console.error(data);
    throw new HttpError(res, data);
  }

  return data;
}
