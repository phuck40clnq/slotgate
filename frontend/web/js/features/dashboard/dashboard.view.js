// File: features/dashboard/dashboard.view.js

export default async function render() {
  const div = document.createElement("div");
  div.innerHTML = `
      <h2>Dashboard</h2>
      <small>Flag</small>
      <button id="logoutBtn">Heheeee</button>
  `;

  return div;
}
