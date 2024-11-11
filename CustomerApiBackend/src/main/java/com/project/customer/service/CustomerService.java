package com.project.customer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.customer.exception.CustomerAlreadyExistsException;
import com.project.customer.exception.CustomerDoesNotExistsException;
import com.project.customer.exception.IncompleteDetailsException;
import com.project.customer.model.Customer;
import com.project.customer.model.SunBaseUser;
import com.project.customer.repository.CustomerRepositry;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class CustomerService {
    @Autowired
    CustomerRepositry customerRepositry;
    @Autowired
    RestTemplate restTemplate;

    String sunbaseLoginLink = "https://qa.sunbasedata.com/sunbase/portal/api/assignment_auth.jsp";
    String sunbaseDataLink = "https://qa.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=get_customer_list";

    String tokenFromSunbase = "";

    public Customer createCustomer (Customer customer) throws CustomerAlreadyExistsException,
                                                            IncompleteDetailsException {

        if (customerRepositry.findByEmail(customer.getEmail()) != null) {
            throw new CustomerAlreadyExistsException("This email id is already registered with us!!!");
        }
        // verify some important details should be present
        verifyCustomerDetails(customer);

        // set UUID
        customer.setUuid(String.valueOf(UUID.randomUUID()));
        System.out.println(customer.getUuid());

        Customer savedCustomer = customerRepositry.save(customer);
        return savedCustomer;
    }

    public Customer updateCustomer (Integer id, Customer customer) throws CustomerDoesNotExistsException,
                                                                        IncompleteDetailsException {
        Customer existedCustomer = customerRepositry.findById(id).orElse(null);
        if (existedCustomer == null) {
            throw new CustomerDoesNotExistsException("The customer does not exits with id : " + id);
        }
        // verify some important details should be present
        verifyCustomerDetails(customer);

        customer.setId(id);
        customer.setUuid(existedCustomer.getUuid());
        System.out.println(customer.getUuid());

        return customerRepositry.save(customer);
    }

    public Customer getCustomerById(Integer id) throws CustomerDoesNotExistsException {
        Customer existedCustomer = customerRepositry.findById(id).orElse(null);
        if (existedCustomer == null) {
            throw new CustomerDoesNotExistsException ("Customer does not exists with id : " + id);
        }
        return existedCustomer;
    }

    public String deleteCustomerById(Integer id) throws CustomerDoesNotExistsException {
        Customer existedCustomer = customerRepositry.findById(id).orElse(null);
        if (existedCustomer == null) {
            throw new CustomerDoesNotExistsException ("Customer does not exists with id : " + id);
        }

        customerRepositry.deleteById(id);
        return "Customer with id : " + id + " has been deleted successfully!!";
    }





    // function to verify customer object details
    public void verifyCustomerDetails(Customer customer) throws IncompleteDetailsException {
        if (customer.getEmail() == null ||  customer.getEmail() == ""
                || customer.getFirst_name() == null ||  customer.getFirst_name() == ""
                || customer.getLast_name() == null ||  customer.getLast_name() == ""
                || customer.getPhone() == null ||  customer.getPhone() == "") {
            throw new IncompleteDetailsException("The details like email, name or phone is missing!!");
        }
    }


    // token call Sunbase
    public String getTokenFromSunbase(SunBaseUser sunBaseUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> jsonData = new HashMap<>();
        jsonData.put("login_id", sunBaseUser.getLogin_id());
        jsonData.put("password", sunBaseUser.getPassword());

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(jsonData, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(sunbaseLoginLink, HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                tokenFromSunbase = rootNode.path("access_token").asText();
                System.out.println("token created from Sunbase: " + tokenFromSunbase);
                return tokenFromSunbase;
            } else {
                System.out.println("Received non-OK response while generating JWT token: {}");
                return  response.getStatusCode().toString();
            }
        } catch (Exception e) {
            System.out.println("Error generating JWT token from remote API");
            return e.getMessage();
        }
    }




    // Method to get data from Sunbase API
    public String getCustomersFromSunbaseDB() throws Exception {
        return getDataFromSunbase();
    }

    public String getDataFromSunbase() throws Exception {
        try {
            // Set authorization header for Sunbase API request
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + tokenFromSunbase);

            // Make a GET request to Sunbase API
            val response = restTemplate.exchange(sunbaseDataLink, HttpMethod.GET, new HttpEntity<>(headers), List.class);
            List sunbaseCustomers = response.getBody();

            // Store the retrieved data into the local database
            dataStoreFunction(sunbaseCustomers);

            return "Data has been synced Successfully";
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    // Function to store Sunbase data into the local database
    private void dataStoreFunction(List sunbaseCustomers) throws Exception {
        try {
            for(Object customer : sunbaseCustomers) {

                // Convert Sunbase customer data to JSON and then to Customer entity
                String obj = new ObjectMapper().writeValueAsString(customer);
                Customer customerObj = convertJsonToCustomer(obj);
                // Check if the customer already exists in the local database
                Optional<Customer> customerOptional = customerRepositry.findByUuid(customerObj.getUuid());

                if(customerOptional.isEmpty()) {
                    // If not, save the customer to the database
                    customerRepositry.save(customerObj);
                } else {
                    // If exists, update the existing customer details
                    Customer existingCustomer = customerOptional.get();
                    customerObj.setId(existingCustomer.getId());
                    customerObj.setUuid(existingCustomer.getUuid());
                    customerRepositry.save(customerObj);
                }
            }
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    private Customer convertJsonToCustomer(String obj) throws Exception {
        try {
            // Create ObjectMapper instance
            ObjectMapper objectMapper = new ObjectMapper();

            // Convert JSON string to Customer object
            return objectMapper.readValue(obj, Customer.class);

        } catch (Exception e) {
            // Handle any exception that occurs during conversion
            throw new Exception("Error converting JSON to Customer: " + e.getMessage());
        }
    }


}
