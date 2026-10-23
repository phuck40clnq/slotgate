// File: featurers/user/user.api.js
import { http } from "../../core/http/http.js";

const USER_API_PATH = "/user";

export const userApi = {
  /**
   * Get user profile by access token.
   *
   * @returns {Promise<Object>} Server response
   */
  getProfile() {
    return http.get(`${USER_API_PATH}/me/profile`);
  },

  /**
   * Update current user profile.
   *
   * @param {Object} data - Update profile payload
   * @param {string} data.email - email of user
   * @param {string} data.fullName - full name of user
   * @param {string} data.avatarUrl - avatar url (path to resource)
   * @returns {Promise<Object>} Server response
   */
  updateProfile(data) {
    return http.put(`${USER_API_PATH}/me/profile/edit`, data);
  }
}
