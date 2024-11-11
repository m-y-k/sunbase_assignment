package com.project.customer.service;

import com.project.customer.model.Customer;
import com.project.customer.model.Pagination;
import com.project.customer.repository.CustomerRepositoryPagination;
import com.project.customer.repository.CustomerRepositry;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServicePagination implements CustomerRepositoryPagination {

    @Autowired
    private EntityManager entityManager;

    
    @Override
    public List<Customer> get(String firstName, String lastName, String email, String phone,
            String state, String street, String city , String address, Pagination helper) {

        CriteriaBuilder  criteriaBuilder  =  entityManager.getCriteriaBuilder();
        CriteriaQuery<Customer>  criteriaQuery  =  criteriaBuilder.createQuery(Customer.class);
        Root<Customer>  root  =  criteriaQuery.from(Customer.class);
        List<Predicate>  predicateList  =  new ArrayList<Predicate>();

        if(firstName!=null&&firstName.isEmpty()==false) {
            predicateList.add(criteriaBuilder.like(root.get("firstName"),"%"+firstName+"%"));
        }

        if(lastName!=null&&lastName.isEmpty()==false) {
            predicateList.add(criteriaBuilder.like(root.get("lastName"),"%"+lastName+"%"));
        }

        if(email!=null&&email.isEmpty()==false) {
            predicateList.add(criteriaBuilder.like(root.get("email"),"%"+email+"%"));
        }

        if(city!=null&&city.isEmpty()==false) {
            predicateList.add(criteriaBuilder.like(root.get("city"),"%"+city+"%"));
        }

        if(state!=null&&state.isEmpty()==false) {
            predicateList.add(criteriaBuilder.like(root.get("state"),"%"+state+"%"));
        }

        if(phone!=null&&phone.isEmpty()==false) {
            predicateList.add(criteriaBuilder.like(root.get("phone"),"%"+phone+"%"));
        }

        if(address!=null&&address.isEmpty()==false) {
            predicateList.add(criteriaBuilder.like(root.get("address"),"%"+address+"%"));
        }

        if(street!=null&&street.isEmpty()==false) {
            predicateList.add(criteriaBuilder.like(root.get("street"),"%"+street+"%"));
        }


        // Search by
        if(helper.getSearch()!=null&&helper.getSearch().isEmpty()==false) {
            predicateList.add(criteriaBuilder.like(root.get(helper.getSearchBy()),"%"+helper.getSearch()+"%"));

        }

        // Order by
        if (helper.getSortOrder().equalsIgnoreCase("asc")) {
            criteriaQuery .orderBy( criteriaBuilder .asc( root.get(helper.getSortBy())));
        }else if(helper.getSortOrder().equalsIgnoreCase("desc")) {
            criteriaQuery .orderBy( criteriaBuilder .desc(root.get(helper.getSortBy())));
        }

        // Pagination
        if  (  helper.getCurrentPage() > 0 &&  helper.getItemsPerPage()
                > 0) {
            final  TypedQuery<Customer>  typedQuery  =
                    entityManager.createQuery(criteriaQuery);
            typedQuery.setFirstResult((  helper.getCurrentPage() - 1) *
                    helper.getItemsPerPage());
            typedQuery.setMaxResults(helper.getItemsPerPage());
            return  typedQuery.getResultList();
        }

        criteriaQuery.where(criteriaBuilder.and(predicateList.toArray(new Predicate[predicateList.size()])));
        return  entityManager.createQuery(criteriaQuery).getResultList();
    }


}
