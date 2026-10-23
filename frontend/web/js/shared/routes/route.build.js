// File: shared/routes/route.build.js
export function buildPath(path, params = {}) {
  let result = path;
  for (const [k, v] of Object.entries(params)) {
    result = result.replace(`:${k}`, encodeURIComponent(v));
  }
  return result;
}
