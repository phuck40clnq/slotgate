// File: core/guard.result.js
export const GuardResult = {
  allow: () => ({ allow: true }),

  redirect: (path) => ({
    allow: false,
    redirectTo: path,
  }),

  block: () => ({ allow: false }),
};
