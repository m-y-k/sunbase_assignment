package com.project.customer.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Pagination {

    String  sortBy = "";
    String  sortOrder = "";
    String  search  =  "" ;
    String  searchBy  =  "";
    int  currentPage = 1;   // default 1 from frontEnd
    int  itemsPerPage = 5;  // default 5 from frontEnd

}
