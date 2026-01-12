const API_BASE = "/api";

const state = {
  categories: [],
  products: [],
  cart: { items: [], subtotal: 0 },
  token: localStorage.getItem("commercia_token") || null,
};

const categorySelect = document.getElementById("categorySelect");
const searchInput = document.getElementById("searchInput");
const productGrid = document.getElementById("productGrid");
const cartItems = document.getElementById("cartItems");
const cartSubtotal = document.getElementById("cartSubtotal");
const checkoutForm = document.getElementById("checkoutForm");
const checkoutNotice = document.getElementById("checkoutNotice");
const loginBtn = document.getElementById("loginBtn");
const registerBtn = document.getElementById("registerBtn");
const logoutBtn = document.getElementById("logoutBtn");

function setToken(token) {
  state.token = token;
  if (token) {
    localStorage.setItem("commercia_token", token);
  } else {
    localStorage.removeItem("commercia_token");
  }
  refreshCart();
  updateCheckoutNotice();
}

async function apiFetch(path, options = {}) {
  const headers = { "Content-Type": "application/json", ...(options.headers || {}) };
  if (state.token) {
    headers.Authorization = `Bearer ${state.token}`;
  }
  const response = await fetch(`${API_BASE}${path}`, { ...options, headers });
  if (!response.ok) {
    const data = await response.json().catch(() => ({}));
    throw new Error(data.message || "Request failed");
  }
  return response.status === 204 ? null : response.json();
}

async function loadCategories() {
  state.categories = await apiFetch("/catalog/categories");
  categorySelect.innerHTML = '<option value="">All categories</option>' +
    state.categories
      .map(cat => `<option value="${cat.id}">${cat.name}</option>`)
      .join("");
}

async function loadProducts() {
  const categoryId = categorySelect.value;
  const query = categoryId ? `?categoryId=${categoryId}` : "";
  state.products = await apiFetch(`/catalog/products${query}`);
  renderProducts();
}

function renderProducts() {
  const filter = searchInput.value.toLowerCase();
  const products = state.products.filter(p =>
    p.name.toLowerCase().includes(filter) || p.description.toLowerCase().includes(filter)
  );

  productGrid.innerHTML = products
    .map(product => `
      <div class="product-card">
        <img src="${product.imageUrl}" alt="${product.name}" />
        <h3>${product.name}</h3>
        <p>${product.description}</p>
        <div class="price">$${product.price}</div>
        <button data-id="${product.id}">Add to cart</button>
      </div>
    `)
    .join("");

  productGrid.querySelectorAll("button").forEach(btn => {
    btn.addEventListener("click", async () => {
      try {
        await apiFetch("/cart/items", {
          method: "POST",
          body: JSON.stringify({ productId: Number(btn.dataset.id), quantity: 1 })
        });
        await refreshCart();
      } catch (err) {
        alert(err.message);
      }
    });
  });
}

async function refreshCart() {
  if (!state.token) {
    state.cart = { items: [], subtotal: 0 };
    renderCart();
    return;
  }
  try {
    state.cart = await apiFetch("/cart");
  } catch (err) {
    state.cart = { items: [], subtotal: 0 };
  }
  renderCart();
}

function renderCart() {
  if (!state.cart.items.length) {
    cartItems.innerHTML = "<p class=\"notice\">Your cart is empty.</p>";
  } else {
    cartItems.innerHTML = state.cart.items
      .map(item => `
        <div class="cart-item">
          <div>
            <strong>${item.productName}</strong><br />
            $${item.unitPrice} x ${item.quantity}
          </div>
          <div>
            <button class="secondary" data-id="${item.id}">Remove</button>
          </div>
        </div>
      `)
      .join("");
  }

  cartSubtotal.textContent = `$${Number(state.cart.subtotal).toFixed(2)}`;

  cartItems.querySelectorAll("button").forEach(btn => {
    btn.addEventListener("click", async () => {
      try {
        await apiFetch(`/cart/items/${btn.dataset.id}`, { method: "DELETE" });
        await refreshCart();
      } catch (err) {
        alert(err.message);
      }
    });
  });
}

function updateCheckoutNotice() {
  checkoutNotice.textContent = state.token
    ? "Ready to place your order."
    : "Log in to checkout.";
}

loginBtn.addEventListener("click", async () => {
  const email = document.getElementById("loginEmail").value;
  const password = document.getElementById("loginPassword").value;
  try {
    const data = await apiFetch("/auth/login", {
      method: "POST",
      body: JSON.stringify({ email, password })
    });
    setToken(data.token);
    alert(`Welcome back, ${data.user.fullName}`);
  } catch (err) {
    alert(err.message);
  }
});

registerBtn.addEventListener("click", async () => {
  const fullName = document.getElementById("registerName").value;
  const email = document.getElementById("registerEmail").value;
  const password = document.getElementById("registerPassword").value;
  const phone = document.getElementById("registerPhone").value;
  try {
    const data = await apiFetch("/auth/register", {
      method: "POST",
      body: JSON.stringify({ fullName, email, password, phone })
    });
    setToken(data.token);
    alert(`Welcome, ${data.user.fullName}`);
  } catch (err) {
    alert(err.message);
  }
});

logoutBtn.addEventListener("click", () => {
  setToken(null);
  alert("Logged out");
});

checkoutForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  if (!state.token) {
    alert("Please log in first.");
    return;
  }
  const formData = new FormData(checkoutForm);
  const payload = Object.fromEntries(formData.entries());
  try {
    const order = await apiFetch("/orders", {
      method: "POST",
      body: JSON.stringify(payload)
    });
    await refreshCart();
    alert(`Order #${order.id} placed!`);
  } catch (err) {
    alert(err.message);
  }
});

categorySelect.addEventListener("change", loadProducts);
searchInput.addEventListener("input", renderProducts);

(async function init() {
  await loadCategories();
  await loadProducts();
  await refreshCart();
  updateCheckoutNotice();
})();
