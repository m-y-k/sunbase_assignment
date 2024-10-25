# SunBase Assignment Backend  

This project includes the code for managing customer information through an API. The main goal is to create functionality for adding, viewing, updating, and deleting customer data (CRUD operations). Later, the focus will shift to synchronizing this customer data with an external API to keep it consistent and accurate across systems.

## All APIs are mentioned below and API json file is also attached with Backend repository.

### First Part

0. **Create User**
   - Path: `http://localhost:8080/auth/addNewUser`
   - Method: POST
   - Body: {
                "password" : "123",
                "email" : "Yusuf@gmail.com"
            }
   - Description: Creates a new user in the system.

1. **Login User**
   - Path: `http://localhost:8080/auth/generateToken`
   - Method: POST
   - Body: {
                "password" : "123",
                "username" : "Yusuf@gmail.com"
            }
   - Description: Logs in a User, if credentials are matched. And returns a Jwt Token for further Api calls.
   - Response: eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJZdXN1ZkBnbWFpbC5jb20iLCJpYXQiOjE3Mjk4NDA1MjAsImV4cCI6MTcyOTg0MjMyMH0.Fog5YpE8eKHaB-fZuHJ4-xcQPXM5EZ9XEjbEtPcCdJE

2. **Create Customer**
   - Path: `http://localhost:8080/customer/create`
   - Method: POST
   - Authorization: [Jwt Token]
   - Body: {
            "first_name": "Yusuf", 
            "last_name": "Khan", 
            "street": "Elvnu Street", 
            "address": "H no 2 ",
            "city": "",
            "state": "Delhi",
            "email": "yusuf@gmail.com", 
            "phone": "12345678"
            }
   - Description: Creates a new customer in the database and returns the record with Id.

3. **Update Customer**
   - Path: `http://localhost:8080/customer/update/id/{id}`
   - Method: PUT
   - Authorization: [Jwt Token]
   - Body: {
            "first_name": "Mohammad Yusuf", 
            "last_name": "Khan", 
            "street": "Elvnu Street", 
            "address": "H no 2 ",
            "city": "",
            "state": "Delhi",
            "email": "yusufKhan@gmail.com", 
            "phone": "12345678"
            }
   - Description: Updates an existing customer in the database and returns the record with Id.

4. **Get a Customer Based on ID**
   - Path: `http://localhost:8080/customer/get/id/{id}`
   - Method: GET
   - Authorization: [Jwt Token]
   - Description: Returns a customer with its details.

5. **Delete a Customer**
   - Path: `http://localhost:8080/customer/delete/id/{id}`
   - Method: DELETE
   - Authorization: [Jwt Token]
   - Description: Delete a customer from database with the given id.
  
6. **Get List of Customers** 
   - Path: `http://localhost:8080/customer/get/list-of-customers`
   - Method: GET
   - Authorization: [Jwt Token]
   - Description: Gets a paginated, sorted, and searchable list of customers.
  
1. ( **Login Page**)
![image](LogIn.PNG)

2. (**Customers Page**)
![image](Customers.PNG)

3. (**Add Customer Page**)
![image](AddCustomer.PNG)

### Second Part

In the second part, Synchronize feature is added:

- **Synchronize List**
  - Description: A "Sync" button is added on the customer list page. 
    By hitting this button it will trigger a call to first get a 'token' from SunBase API,
    then using that token, it will get the customers list. (Getting Token and making customers data (from SunBase) calls are being made at backend level)
    The retrieved customers are then saved in the database. 
    If a customer already exists in the database, its information is updated instead of inserting a new record. 
    
  

### Setup 

- Clone both Frontend and Backend repositories.
- Set up Application properties in **application.properties** file.
- Run both the backend and frontend projects.