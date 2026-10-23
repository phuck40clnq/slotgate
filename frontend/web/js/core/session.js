// File: core/session.js

const STORAGE_KEY = "access_token";

let state = {
  token: null,
  user: null,
};

const listeners = new Set();

export const Session = {

  // ----- READ -----
  get() {
    return Object.freeze({
      ...state,
      isAuthenticated: Boolean(state.token),
    });
  },

  // ----- SETTER -----
  setToken(token) {
    state = { ...state, token };
    persistToken(token);
    notify();
  },

  setUser(user) {
    state = { ...state, user };
    notify();
  },

  // ----- REACTIVE -----
  subscribe(listener) {
    listeners.add(listener);
    // Emit immediately
    listener(this.get());

    return () => listeners.delete(listener);
  },

  // ----- CLEAR -----
  clear() {
    state = { token: null, user: null };
    persistToken(null);
    notify();
  },

  // ----- BOOTSTRAP -----
  hydrate() {
    const token = localStorage.getItem(STORAGE_KEY);
    if (token) {
      state = { ...state, token };
    }
  }

}

function notify() {
  const snapshot = Session.get();
  listeners.forEach((listener => listener(snapshot)));
}

function persistToken(token) {
  if (token) {
    localStorage.setItem(STORAGE_KEY, token);
  } else {
    localStorage.removeItem(STORAGE_KEY);
  }
}
