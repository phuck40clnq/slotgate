// File: shared/routes/route.model.js
export function route({ path, view, guards, meta }) {
  if (!path) {
    throw new Error("Route requires path");
  }

  if (!view) {
    throw new Error("Route must have view");
  }

  // Normalize view: Take .render or fallback .default of view
  let normalizedView = view;
  if (view && typeof view === "function") {
    normalizedView = async () => {
      const m = await view();

      // Fallback default function
      const renderFn = m.render ?? m.default;

      if (typeof renderFn !== "function") {
        throw new Error("Route view must export a render() function");
      }

      return renderFn;
    };
  }

  return Object.freeze({
    path: path,
    view: normalizedView,
    guards: guards || [],
    meta: meta || {},
  });
}
