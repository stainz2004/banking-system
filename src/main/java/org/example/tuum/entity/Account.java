package org.example.tuum.entity;

import lombok.Data;

@Data
public class Account {
    private Long id;
    private String customerId;
    private Country country;
}
