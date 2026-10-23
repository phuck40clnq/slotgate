// File: shared/routes/index.js
import { ROUTES } from "./route.paths.js";
import { buildPath } from "./route.build.js";

export const Routes = Object.freeze({
  home: Object.freeze({
    home: () => ROUTES.HOME,
  }),

  dashboard: Object.freeze({
    dashboard: () => ROUTES.DASHBOARD,
  }),

  auth: Object.freeze({
    login: () => ROUTES.AUTH.LOGIN,
    register: () => ROUTES.AUTH.REGISTER,
  }),

  user: Object.freeze({
    profile: () => ROUTES.USER.PROFILE,
    edit_profile: () => ROUTES.USER.EDIT_PROFILE,
  }),
});
