// File: features/user/user.route.js
import { registerRoute } from "../../core/registry.js";
import { route } from "../../shared/routes/route.model.js";

const userRoutes = [
  route({
    path: "/me/profile",
    view: () => import("./profile.view.js"),
  }),

  route({
    path: "/me/profile/edit",
    view: () => import("./edit-profile.view.js"),
  })
];

registerRoute(userRoutes);
