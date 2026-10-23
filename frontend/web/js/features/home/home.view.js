// File: features/home/home.view.js
export default async function render() {
  const div = document.createElement("div");
  div.innerHTML = `
    <h2>Welcome to My App</h2>
    <p>
      <a href="/login" data-link>Login</a>
      <a href="/register" data-link>Register</a>
      <a href="/dashboard" data-link>Dashboard</a>
    </p>
  `;

  return div;
}
