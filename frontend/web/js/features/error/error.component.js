// File: features/error/error.component.js
export default function render(error, onAction) {
  const el = document.createElement("section");

  // TODO: Remove code, status, not for client details
  el.innerHTML = `
    <div class="error">
      <h1>Oops, something went wrong!</h1>
      <p>Message: ${error.message}</p>
      <p>Code: ${error.code}</p>
      <small>${error.status}</small>
      <div class="actions">
        <button data-action="home">Home</button>
        <button data-action="back">Back</button>
      </div>
    </div>
  `;

  el.addEventListener("click", (e) => {
    const action = e.target.dataset.action;
    if (action) onAction(action);
  });

  return el;
}
