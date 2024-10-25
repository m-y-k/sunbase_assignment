package com.project.customer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// Login Object Type
public class AuthRequest {

    private String username;
    private String password;

}

