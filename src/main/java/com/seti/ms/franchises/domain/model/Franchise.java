package com.seti.ms.franchises.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Franchise {
    private String id;
    private String name;
    private List<Branch> branches;
}