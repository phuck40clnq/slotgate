// File: app/bootstrapSession.js
import { Session } from "../core/session.js";
import { mapUserProfile } from "../features/user/user.mapper.js";
import { userApi } from "../shared/api/user.api.js";

export async function bootstrapSession() {
  Session.hydrate();

  const { token } = Session.get();
  if (!token) return;

  try {
    const profileResponse = await userApi.getProfile();
    const user = mapUserProfile(profileResponse)
    Session.setUser(user);
  } catch {
    Session.clear();
  }
}
