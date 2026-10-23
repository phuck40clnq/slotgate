// File: shared/components/navbar.js
import { getSlot } from "../../core/layout.js";
import { Session } from "../../core/session.js";
import { Routes } from "../routes/index.js";

export {
  initNavbar,
}

let unsubscribe;

function initNavbar({ slotName = "nav", onLogout }) {
  const container = getSlot(slotName);

  unsubscribe = Session.subscribe((state) => {
    renderNavbar(container, state);
  });

  renderNavbar();

  container.addEventListener("click", (e) => {
    const action = e.target.dataset.action;
    if (action === "logout") {
      onLogout?.();
    }
  });
}

function renderNavbar(container, session) {
  if (!container) return;
  container.replaceChildren(
    createNavbar(session.isAuthenticated)
  );
}

function createNavbar(isAuthenticated) {
  const nav = document.createElement("nav");

  const isAuth = isAuthenticated;

  nav.innerHTML = isAuth
    ? authTemplate()
    : guestTemplate();

  return nav;
}

function guestTemplate() {
  return `
    <a href="${Routes.home.home()}" data-link>Home</a>
    <a href="${Routes.auth.login()}" data-link>Login</a>
    <a href="${Routes.auth.register()}" data-link>Register</a>
    <a href="${Routes.user.profile()}" data-link>Profile hehe</a>
  `;
}

function authTemplate() {
  return `
    <a href="${Routes.home.home()}" data-link>Home</a>
    <a href="${Routes.dashboard.dashboard()}" data-link>Dashboard</a>
    <a href="${Routes.user.profile()}" data-link>Profile</a>
    <button data-action="logout">Logout</button>
  `;
}
