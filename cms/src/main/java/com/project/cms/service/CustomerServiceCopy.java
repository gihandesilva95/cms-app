package com.project.cms.service;

import com.project.cms.dto.CustomerDTO;
import com.project.cms.entity.Address;
import com.project.cms.entity.Customer;
import com.project.cms.repository.CityRepository;
import com.project.cms.repository.CountryRepository;
import com.project.cms.repository.CustomerRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceCopy {

    private final CustomerRepository customerRepo;
    private final CountryRepository countryRepo;
    private final CityRepository cityRepo;

    public CustomerServiceCopy(CustomerRepository customerRepo, CountryRepository countryRepo, CityRepository cityRepo) {
        this.customerRepo = customerRepo;
        this.countryRepo = countryRepo;
        this.cityRepo = cityRepo;
    }

    public List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }

    public Customer getCustomer(Long id) {
        return customerRepo.findById(id).orElse(null);
    }

    @Transactional
    public Customer createCustomer(CustomerDTO dto) {
        if (customerRepo.existsByNic(dto.nic)) throw new RuntimeException("NIC already exists");

        Customer customer = new Customer();
        populateCustomer(customer, dto);
        return customerRepo.save(customer);
    }

    @Transactional
    public Customer updateCustomer(Long id, CustomerDTO dto) {
        Customer customer = customerRepo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        populateCustomer(customer, dto);
        return customerRepo.save(customer);
    }

    private void populateCustomer(Customer customer, CustomerDTO dto) {
        customer.setName(dto.name);
        customer.setDateOfBirth(dto.dateOfBirth);
        customer.setNic(dto.nic);
        customer.setMobileNumbers(dto.mobileNumbers);

        if (dto.parentCustomerId != null) {
            Customer parent = customerRepo.findById(dto.parentCustomerId).orElse(null);
            customer.setParentCustomer(parent);
        }

        if (dto.addresses != null) {
            List<Address> addresses = dto.addresses.stream().map(a -> {
                Address addr = new Address();
                addr.setAddressLine1(a.addressLine1);
                addr.setAddressLine2(a.addressLine2);
                addr.setCity(cityRepo.findById(a.cityId).orElse(null));
                addr.setCountry(countryRepo.findById(a.countryId).orElse(null));
                addr.setCustomer(customer);
                return addr;
            }).collect(Collectors.toList());
            customer.setAddresses(addresses);
        }
    }

    //  Bulk upload method
    @Transactional
    public void bulkUpload(MultipartFile file) {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Customer> customers = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Start from 1 to skip headers
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String name = getCellValue(row.getCell(0));
                String nic = getCellValue(row.getCell(1));
                String dobStr = getCellValue(row.getCell(2));
                String mobile1 = getCellValue(row.getCell(3));
                String mobile2 = getCellValue(row.getCell(4));
                String addr1 = getCellValue(row.getCell(5));
                String addr2 = getCellValue(row.getCell(6));
                String cityIdStr = getCellValue(row.getCell(7));
                String countryIdStr = getCellValue(row.getCell(8));

                if (name == null || nic == null || dobStr == null) continue;

                if (customerRepo.existsByNic(nic)) continue;

                Customer customer = new Customer();
                customer.setName(name);
                customer.setNic(nic);
                customer.setDateOfBirth(LocalDate.parse(dobStr));
                List<String> mobileList = new ArrayList<>();
                if (mobile1 != null) mobileList.add(mobile1);
                if (mobile2 != null) mobileList.add(mobile2);
                customer.setMobileNumbers(mobileList);

                Address address = new Address();
                address.setAddressLine1(addr1);
                address.setAddressLine2(addr2);
                address.setCity(cityRepo.findById(Long.parseLong(cityIdStr)).orElse(null));
                address.setCountry(countryRepo.findById(Long.parseLong(countryIdStr)).orElse(null));
                address.setCustomer(customer);

                customer.setAddresses(Collections.singletonList(address));
                customers.add(customer);

                // Optional: Save in batches to avoid memory issues
                if (customers.size() % 500 == 0) {
                    customerRepo.saveAll(customers);
                    customers.clear();
                }
            }

            if (!customers.isEmpty()) {
                customerRepo.saveAll(customers);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing Excel file: " + e.getMessage(), e);
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalDate().toString();
            } else {
                return String.valueOf((long) cell.getNumericCellValue());
            }
        } else {
            return cell.getStringCellValue().trim();
        }
    }
}
