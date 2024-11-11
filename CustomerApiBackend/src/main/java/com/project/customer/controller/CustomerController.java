package com.project.customer.controller;


import com.project.customer.exception.CustomerAlreadyExistsException;
import com.project.customer.exception.CustomerDoesNotExistsException;
import com.project.customer.exception.IncompleteDetailsException;
import com.project.customer.model.Customer;
import com.project.customer.model.SunBaseUser;
import com.project.customer.model.Pagination;
import com.project.customer.service.CustomerService;
import com.project.customer.service.CustomerServicePagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @Autowired
    CustomerServicePagination customerServicePagination;

    // create a customer
    @PostMapping("/create")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) throws CustomerAlreadyExistsException,
                                                                                        IncompleteDetailsException {

        Customer returnedCustomer = customerService.createCustomer(customer);

        return new ResponseEntity(returnedCustomer, HttpStatus.CREATED);
    }

    // update a customer
    @PutMapping("/update/id/{id}")
    public ResponseEntity updateCustomer(@PathVariable Integer id,
                                         @RequestBody Customer customer)
                                            throws IncompleteDetailsException,
                                                CustomerDoesNotExistsException {
        return new ResponseEntity(customerService.updateCustomer(id, customer), HttpStatus.OK);
    }

    // Get a list of customer (API with pagination sorting and searching )
    @GetMapping("/get/list-of-customers")
    public ResponseEntity<List<Customer>> getListOfCustomers (@RequestParam(value = "first_name", required = false, defaultValue = "") String first_name,
                                              @RequestParam(value = "last_name", required = false, defaultValue = "") String last_name,
                                              @RequestParam(value = "street", required = false, defaultValue = "") String street,
                                              @RequestParam(value = "address", required = false, defaultValue = "") String address,
                                              @RequestParam(value = "city", required = false, defaultValue = "") String city,
                                              @RequestParam(value = "state", required = false, defaultValue = "") String state,
                                              @RequestParam(value= "email", required=false, defaultValue="") String email,
                                              @RequestParam(value = "phone", required = false, defaultValue = "") String phone,
                                              @RequestParam(value = "sortOrder", required = false, defaultValue = "") String sortOrder,
                                              @RequestParam(value = "sortBy", required = false, defaultValue = "") String sortBy,
                                              @RequestParam(value = "search", required = false, defaultValue = "") String search,
                                              @RequestParam(value = "searchBy", required = false, defaultValue = "") String searchBy,
                                              @RequestParam(value = "currentPage", required = false, defaultValue = "0") int currentPage,
                                              @RequestParam(value = "itemsPerPage", required = false, defaultValue = "0") int itemsPerPage){
        Pagination pagination = new Pagination();
        pagination.setSortOrder(sortOrder);
        pagination.setSortBy(sortBy);
        pagination.setSearch(search);
        pagination.setSearchBy(searchBy);
        pagination.setCurrentPage(currentPage);
        pagination.setItemsPerPage(itemsPerPage);

        List<Customer> customerListAll = customerServicePagination.get(first_name, last_name, email, phone,
                state,  street,  city , address, pagination);

        return new ResponseEntity<>(customerListAll, HttpStatus.OK);
    }




    // Get a single customer based on ID
    @GetMapping("/get/id/{id}")
    public ResponseEntity<Customer> getCustomerById (@PathVariable Integer id) throws CustomerDoesNotExistsException {

        return new ResponseEntity<Customer>(customerService.getCustomerById(id),HttpStatus.OK);
    }

    // Delete a customer
    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity deleteCustomerById (@PathVariable Integer id) throws CustomerDoesNotExistsException {

        return new ResponseEntity(customerService.deleteCustomerById(id), HttpStatus.ACCEPTED);
    }


    // getToken from Sunbase
    @PostMapping("/get/token/sunbase")
    public String getTokenFromSunbase(@RequestBody SunBaseUser sunBaseUser) {
        return customerService.getTokenFromSunbase(sunBaseUser);
    }

    // get data from Sunbase Api
    @GetMapping("/get/customers/sunbase")
    public String getCustomersFromSunbaseDB () throws Exception {
        return customerService.getCustomersFromSunbaseDB();
    }

}
