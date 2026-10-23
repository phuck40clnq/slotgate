// File: features/home/home.route.js
import { registerRoute } from "../../core/registry.js";
import { route } from "../../shared/routes/route.model.js";

const homeRoutes = [
  route({
    path: "/",
    view: () => import("./home.view.js"),
  })
];

registerRoute(homeRoutes);
