package com.project.cms.service;

import com.project.cms.dto.AddressDTO;
import com.project.cms.dto.CustomerDTO;
import com.project.cms.entity.Address;
import com.project.cms.entity.City;
import com.project.cms.entity.Country;
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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepo;
    private final CountryRepository countryRepo;
    private final CityRepository cityRepo;

    public CustomerService(CustomerRepository customerRepo, CountryRepository countryRepo, CityRepository cityRepo) {
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
        if (customerRepo.existsByNic(dto.nic)) {
            throw new RuntimeException("NIC already exists");
        }

        Customer customer = new Customer();
        populateCustomer(customer, dto);
        return customerRepo.save(customer);
    }

    @Transactional
    public Customer updateCustomer(Long id, CustomerDTO dto) {
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));
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
            // Ensure list is initialized
            if (customer.getAddresses() == null) {
                customer.setAddresses(new ArrayList<>());
            } else {
                customer.getAddresses().clear();
            }

            for (AddressDTO a : dto.addresses) {
                Address addr = new Address();
                addr.setAddressLine1(a.addressLine1);
                addr.setAddressLine2(a.addressLine2);
                City city = cityRepo.findById(a.cityId)
                        .orElseThrow(() -> new RuntimeException("City not found with ID: " + a.cityId));
                Country country = countryRepo.findById(a.countryId)
                        .orElseThrow(() -> new RuntimeException("Country not found with ID: " + a.countryId));
                addr.setCity(city);
                addr.setCountry(country);
                addr.setCustomer(customer);
                customer.getAddresses().add(addr);
            }
        }
    }

    @Transactional
    public void bulkUpload(MultipartFile file) {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Customer> customers = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String name = getCellValue(row.getCell(0));
                String dobStr = getCellValue(row.getCell(1));
                String nic = getCellValue(row.getCell(2));
                String addr1 = getCellValue(row.getCell(3));
                String addr2 = getCellValue(row.getCell(4));
                String cityIdStr = getCellValue(row.getCell(5));
                String countryIdStr = getCellValue(row.getCell(6));
                String mobileNumbers = getCellValue(row.getCell(7));

                if (name == null || nic == null || dobStr == null) continue;
                if (customerRepo.existsByNic(nic)) continue;

                Customer customer = new Customer();
                customer.setName(name);
                customer.setNic(nic);
                customer.setDateOfBirth(parseDate(dobStr));

                List<String> mobileList = Arrays.stream(mobileNumbers.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                customer.setMobileNumbers(mobileList);

                Address address = new Address();
                address.setAddressLine1(addr1);
                address.setAddressLine2(addr2);
                address.setCity(cityRepo.findById(Long.parseLong(cityIdStr)).orElse(null));
                address.setCountry(countryRepo.findById(Long.parseLong(countryIdStr)).orElse(null));
                address.setCustomer(customer);

                customer.setAddresses(Collections.singletonList(address));
                customers.add(customer);

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

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                } else {
                    double numericValue = cell.getNumericCellValue();
                    long longVal = (long) numericValue;
                    return String.valueOf(longVal);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            case _NONE:
            case ERROR:
            default:
                return null;
        }
    }

    private LocalDate parseDate(String dateStr) {
        List<DateTimeFormatter> formatters = Arrays.asList(
                DateTimeFormatter.ofPattern("M/d/yyyy"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ISO_LOCAL_DATE
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException ignored) {}
        }
        throw new RuntimeException("Invalid date format: " + dateStr);
    }
}
