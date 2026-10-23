// File: features/auth/auth.route.js
import { registerRoute } from "../../core/registry.js"
import { route } from "../../shared/routes/route.model.js"

const authRoutes = [
  // Login view
  route({
    path: "/login",
    view: () => import("./login.view.js"),
    meta: { access: "guest" }
  }),

  // Register view
  route({
    path: "/register",
    view: () => import("./register.view.js"),
    meta: { access: "guest" }
  }),
]

registerRoute(authRoutes);
