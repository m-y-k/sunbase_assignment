package com.project.customer.repository;

import com.project.customer.model.Customer;
import com.project.customer.model.Pagination;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepositoryPagination {
    List<Customer> get (String firstName, String lastName, String email, String phone
            , String state, String street, String city , String address, Pagination pagination);

}
