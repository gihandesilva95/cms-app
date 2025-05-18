package com.project.cms.dto;

import java.time.LocalDate;
import java.util.List;

public class CustomerDTO {
    public Long id;
    public String name;
    public LocalDate dateOfBirth;
    public String nic;
    public List<String> mobileNumbers;
    public List<AddressDTO> addresses;
    public Long parentCustomerId;

    public void setName(String johnDoe) {
    }
}
