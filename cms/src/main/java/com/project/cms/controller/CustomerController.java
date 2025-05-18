package com.project.cms.controller;

import com.project.cms.dto.CustomerDTO;
import com.project.cms.entity.Customer;
import com.project.cms.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @GetMapping
    public List<Customer> getAll() {
        return service.getAllCustomers();
    }

    @GetMapping("/{id}")
    public Customer getOne(@PathVariable Long id) {
        return service.getCustomer(id);
    }


    @PostMapping
    public ResponseEntity<?> create(@RequestBody CustomerDTO dto) {
        try {
            Customer created = service.createCustomer(dto);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            e.printStackTrace(); // log the full stack trace in console
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Create failed: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CustomerDTO dto) {
        try {
            Customer updated = service.updateCustomer(id, dto);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            e.printStackTrace(); // check console for error details
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Update failed: " + e.getMessage());
        }
    }

    // Bulk upload endpoint for Excel file
    @PostMapping("/upload")
    public ResponseEntity<String> uploadCustomers(@RequestParam("file") MultipartFile file) {
        try {
            service.bulkUpload(file);
            return ResponseEntity.ok("File uploaded and customers processed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload file: " + e.getMessage());
        }
    }
}
