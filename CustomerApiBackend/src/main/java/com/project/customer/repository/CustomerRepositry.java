package com.project.customer.repository;

import com.project.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepositry extends JpaRepository<Customer, Integer> {

    Customer findByEmail(String email);

    Optional<Customer> findByUuid(String uuid);

}
