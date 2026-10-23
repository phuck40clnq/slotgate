// File: features/dashboard/dashboard.route.js
import { registerRoute } from "../../core/registry.js";
import { route } from "../../shared/routes/route.model.js";

const dashboardRoutes = [
  route({
    path: "/dashboard",
    view: () => import("./dashboard.view.js"),
    guards: [ "auth", ]
    }),
];

registerRoute(dashboardRoutes);
