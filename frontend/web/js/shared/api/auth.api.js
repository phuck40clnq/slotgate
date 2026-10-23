// File: features/auth/auth.api.js
import { http } from "../../core/http/http.js";

const AUTH_API_PATH = "/auth";

/**
 * Authentication API
 */
export const authApi = {
  /**
   * Login user.
   *
   * @param {Object} data - Login payload
   * @param {string} payload.email - email
   * @param {string} payload.password - Password
   * @returns {Promise<Object>} Server response
   */
  login(data) {
    return http.post(`${AUTH_API_PATH}/login`, data, {
      auth: false
    });
  },

  /**
   * Register new user.
   *
   * @param {Object} data - Register payload
   * @param {string} payload.email - email
   * @param {string} payload.password - Password
   * @returns {Promise<Object>} Server response
   */
  register(data) {
    return http.post(`${AUTH_API_PATH}/register`, data, {
      auth: false
    })
  }
}
