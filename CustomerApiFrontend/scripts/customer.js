const base_url = "http://localhost:8080/customer";
let currentPage = 1;
const token = localStorage.getItem("token");
let customerId;

const customerHeading = document.getElementById("customer-heading")

// upon opening the customer page, check if the token is valid
document.addEventListener("DOMContentLoaded", () => {
  if (!token) {
    window.location.href = "./index.html";
  } else {
    fetchCustomer();
  }
});

// Fetch customers based on filters and pagination
async function fetchCustomer(
  searchBy = "id",
  searchTerm = "",
  sortBy = "id",
  sortOrder = "asc",
  page = currentPage
) {
  
  customerHeading.innerHTML = `Customers - Page: ${currentPage}`

  
  const url = new URL(`${base_url}/get/list-of-customers`);
  url.searchParams.append("searchBy", searchBy);
  url.searchParams.append("searchTerm", searchTerm);
  url.searchParams.append("sortBy", sortBy);
  url.searchParams.append("sortOrder", sortOrder);
  url.searchParams.append("currentPage", page);
  url.searchParams.append("itemsPerPage", 5);
  console.log(url);
  try {
    const response = await fetch(url, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
console.log(response)
    if (response.status === 401) {
      alert("Session expired. Please log in again!!!");
      window.location.href = "./index.html"; // Redirect to login page
    } else if (response.status === 403) {
      alert("Forbidden access. Enter correct details!!!");
    } else if (response.ok) {
      const customers = await response.json();
      recordsLength = customers.length;
      const customerTableBody = document.getElementById("customers");
      customerTableBody.innerHTML = "";

      if (customers.length === 0) {
        alert("No records found!!!");
        if (currentPage > 1) {
          currentPage--;
        }
      } else {
        customers.forEach((customer) => {
          console.log(customer);
          const tr = document.createElement("tr");
          tr.innerHTML = `
                        <td>${customer.id}</td>
                        <td>${customer.uuid}</td>
                        <td>${customer.first_name}</td>
                        <td>${customer.last_name}</td>
                        <td>${customer.street}</td>
                        <td>${customer.address}</td>
                        <td>${customer.city}</td>
                        <td>${customer.state}</td>
                        <td>${customer.email}</td>
                        <td>${customer.phone}</td>
                        <td><button style="background-color: green;" class="btn btn-secondary" onclick="customerUpdateHandler(${customer.id})">Edit</button></td>
                        <td><button style="background-color: red;" class="btn btn-secondary" onclick="deleteCustomer(${customer.id})">Delete</button></td>
                    `;
          customerTableBody.appendChild(tr);
        });
      }
    } else {
      alert("Failed to fetch customers!!!");
    }
  } catch (error) {
    console.error("Error fetching customers :", error);
  }
}

// Sync customers with server
async function syncCustomers() {
  try {
    const responseLogIn = await fetch(`${base_url}/get/token/sunbase`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json", 
      },
      body: JSON.stringify({
        login_id: "test@sunbasedata.com",
        password: "Test@123",
      }),
    });
    if (responseLogIn.ok) {
      const tokenFromSunBase = await responseLogIn.text();
      console.log(tokenFromSunBase)
      alert("token successfully fetched from sunbase!!! " + tokenFromSunBase);
      getDataFromSunbase();
    }
  } catch (error) {
    console.log("Error in fetching token from Sunbase: ", error);
  }
}

const getDataFromSunbase = async () => {
  try {
    const response = await fetch(`${base_url}/get/customers/sunbase`, {
      method: "GET",
    });

    if (response.ok) {
      alert("Customers synced successfully!!!");
      fetchCustomer();
    } else if (response.status === 401) {
      alert("Session expired. Please log in again!!!");
      window.location.href = "./index.html"; // Redirect to login page
    } else if (response.status === 403) {
      alert("Forbidden access. Enter correct details!!!");
    } else {
      alert("Failed to sync customers!!!");
    }
  } catch (error) {
    console.error("Error syncing customers: ", error);
  }
};

// Apply filters and sort
function applyFilters() {
  const searchBy = document.getElementById("searchBy").value;
  const searchTerm = document.getElementById("searchTerm").value;
  const sortBy = document.getElementById("sortBy").value;
  const sortOrder = document.getElementById("sortOrder").value;
  fetchCustomer(searchBy, searchTerm, sortBy, sortOrder, currentPage);
}

// Pagination controls
function nextPage() {
  currentPage++;
  fetchCustomer("", "", "", "", currentPage);
}

function prevPage() {
  if (currentPage > 1) {
    currentPage--;
    fetchCustomer("", "", "", "", currentPage);
  }
}

// Open the Add Customer popup
function openRegisterCustomerPopup() {
  document.getElementById("addCustomerPopup").style.display = "flex";
}

// Close the register Customer popup
function closeRegisterCustomerPopup() {
  document.getElementById("addCustomerPopup").style.display = "none";
}


// Resiter a new customer
async function registerCustomer() {
  const customer = {
    first_name: document.getElementById("newFirstName").value,
    last_name: document.getElementById("newLastName").value,
    street: document.getElementById("newStreet").value,
    address: document.getElementById("newAddress").value,
    city: document.getElementById("newCity").value,
    state: document.getElementById("newState").value,
    email: document.getElementById("newEmail").value,
    phone: document.getElementById("newPhone").value,
  };

  try {
    const response = await fetch(`${base_url}/create`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(customer),
    });

    if (response.status === 401) {
      alert("Session expired. Please log in again!!!");
      window.location.href = "./index.html"; // Redirect to login page
    } else if (response.status === 403) {
      alert("Forbidden access. Enter correct details!!!");
    } else if (response.ok) {
      alert("Customer added successfully!!!");
      closeRegisterCustomerPopup();
      fetchCustomer();
    } else {
      alert("Failed to add customer!!!");
    }
  } catch (error) {
    console.error("Error: ", error);
  }
}

// customer update pop handler
async function customerUpdateHandler(id) {
  customerId = id; 
  console.log(id);
  try {
    const response = await fetch(`${base_url}/get/id/${id}`, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    if (response.ok) {
      const customer = await response.json();
      console.log(customer);
      if (customer.id == id) {
        openUpdateCustomerPopup(customer);
      } else {
        alert("Customer not found!!!");
      }
    } else if (response.status == 401) {
      alert("Session expired. Please log in again!!!");
      window.location.href = "./index.html"; // Redirect to login page
    } else if (response.status == 403) {
      alert("Forbidden access. Enter correct details!!!");
    } else {
      alert("something went wrong.....");
    }
  } catch (error) {
    console.error("Error deleting customer:", error);
  }
}
// Open the Edit Customer popup with pre-filled data
function openUpdateCustomerPopup(customer) {
  document.getElementById("updateFirstName").value = customer.first_name;
  document.getElementById("updateLastName").value = customer.last_name;
  document.getElementById("updateStreet").value = customer.street;
  document.getElementById("updateAddress").value = customer.address;
  document.getElementById("updateCity").value = customer.city;
  document.getElementById("updateState").value = customer.state;
  document.getElementById("updateEmail").value = customer.email;
  document.getElementById("updatePhone").value = customer.phone;
  document.getElementById("updateCustomerPopup").style.display = "flex";
}

// Close the Edit Customer popup
function closeUpdateCustomerPopup() {
  document.getElementById("updateCustomerPopup").style.display = "none";
}

// Update customer details
async function updateCustomer() {
  const updatedCustomer = {
    first_name: document.getElementById("updateFirstName").value,
    last_name: document.getElementById("updateLastName").value,
    street: document.getElementById("updateStreet").value,
    address: document.getElementById("updateAddress").value,
    city: document.getElementById("updateCity").value,
    state: document.getElementById("updateState").value,
    email: document.getElementById("updateEmail").value,
    phone: document.getElementById("updatePhone").value,
  };

  // use global customerId which was set during edit button press
  try {
    const response = await fetch(`${base_url}/update/id/${customerId}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(updatedCustomer),
    });
    if (response.ok) {
      alert("Customer updated successfully!!!");
      closeUpdateCustomerPopup();
      fetchCustomer();
    } else if (response.status === 401) {
      alert("Session expired. Please log in again!!!");
      window.location.href = "./index.html"; // Redirect to login page
    } else if (response.status === 403) {
      alert("Forbidden access. Enter correct details!!!");
    } else {
      alert("Failed to update customer!!!");
    }
  } catch (error) {
    console.error("Error updating customer:", error);
  }
}

// Delete a customer
async function deleteCustomer(id) {

  try {
    const response = await fetch(`${base_url}/delete/id/${id}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (response.status === 401) {
      alert("Session expired. Please log in again!!!");
      window.location.href = "./index.html"; // Redirect to login page
    } else if (response.status === 403) {
      alert("Forbidden access. Enter correct details!!!");
    } else if (response.ok) {
      alert("Customer deleted successfully!!!");
      fetchCustomer();
    } else {
      alert("Failed to delete customer!!!");
    }
  } catch (error) {
    console.error("Error deleting customer:", error);
  }
}
