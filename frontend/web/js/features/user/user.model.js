// File: features/user/user.model.js
export function user({
  id, email, name, avatarUrl, status
}) {
  return Object.freeze({
    id,
    email,
    name,
    avatarUrl,
    status,
    isActive: status === 'ACTIVE',
  });
}
