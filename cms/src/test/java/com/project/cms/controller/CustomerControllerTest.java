package com.project.cms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.cms.dto.CustomerDTO;
import com.project.cms.entity.Customer;
import com.project.cms.service.CustomerService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.mock.web.MockMultipartFile;

import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.*;
        import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer sampleCustomer;
    private CustomerDTO sampleDto;

    @BeforeEach
    void setup() {
        sampleCustomer = new Customer();
        sampleCustomer.setId(1L);
        sampleCustomer.setName("John Doe");
        // Set other Customer fields if needed

        sampleDto = new CustomerDTO();
        sampleDto.setName("John Doe");
        // Set other DTO fields as necessary
    }

    @Test
    void testGetAllCustomers() throws Exception {
        when(customerService.getAllCustomers()).thenReturn(Arrays.asList(sampleCustomer));

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(sampleCustomer.getId()))
                .andExpect(jsonPath("$[0].name").value(sampleCustomer.getName()));

        verify(customerService, times(1)).getAllCustomers();
    }

    @Test
    void testGetOneCustomer() throws Exception {
        when(customerService.getCustomer(1L)).thenReturn(sampleCustomer);

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleCustomer.getId()))
                .andExpect(jsonPath("$.name").value(sampleCustomer.getName()));

        verify(customerService, times(1)).getCustomer(1L);
    }

    @Test
    void testCreateCustomer() throws Exception {
        when(customerService.createCustomer(any(CustomerDTO.class))).thenReturn(sampleCustomer);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleCustomer.getId()))
                .andExpect(jsonPath("$.name").value(sampleCustomer.getName()));

        verify(customerService, times(1)).createCustomer(any(CustomerDTO.class));
    }

    @Test
    void testUpdateCustomer() throws Exception {
        when(customerService.updateCustomer(eq(1L), any(CustomerDTO.class))).thenReturn(sampleCustomer);

        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleCustomer.getId()))
                .andExpect(jsonPath("$.name").value(sampleCustomer.getName()));

        verify(customerService, times(1)).updateCustomer(eq(1L), any(CustomerDTO.class));
    }

    @Test
    void testUploadCustomers() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "customers.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "dummy content".getBytes()
        );

        doNothing().when(customerService).bulkUpload(any());

        mockMvc.perform(multipart("/api/customers/upload")
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(content().string("File uploaded and customers processed successfully."));

        verify(customerService, times(1)).bulkUpload(any());
    }
}
