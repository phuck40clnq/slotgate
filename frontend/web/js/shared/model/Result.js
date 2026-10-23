// File: shared/model/Result.js
export class Result {
  static success(data = {}) {
    return { ok: true, data };
  }

  static fail(error = {}) {
    return { ok: false, error };
  }
}
