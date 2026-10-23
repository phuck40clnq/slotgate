// File: features/user/user.mapper.js
import { invalidResponse } from "../../shared/error/error.factory.js";
import { user } from "./user.model.js";

export function mapUserProfile(res) {
  if (!isValidUserProfileResponse(res)) {
    throw invalidResponse(
      "Invalid UserProfile API response",
      { response: res }
    )
  }

  return user({
    id: res.userId,
    email: res.email,
    name: res.fullName,
    avatarUrl: normalizeAvatar(res.avatarUrl),
    status: res.status,
  })
}

function isValidUserProfileResponse(res) {
  return (
    res != null &&
    typeof res === "object" &&
    typeof res.userId === "string" &&
    typeof res.email === "string" &&
    typeof res.fullName === "string" &&
    typeof res.status === "string"
  )
}

function normalizeAvatar(avatarUrl) {
  return typeof avatarUrl === 'string' && avatarUrl.length > 0
    ? avatarUrl
    : '/default.png';
}
