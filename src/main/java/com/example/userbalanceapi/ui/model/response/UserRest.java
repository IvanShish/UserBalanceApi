package com.example.userbalanceapi.ui.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRest {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private Double balance;
}
