package com.project.customer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// SunBase API Object required Type
public class SunBaseUser {

    private String login_id;
    private String password;

}