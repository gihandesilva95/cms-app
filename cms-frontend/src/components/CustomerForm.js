import React, { useState, useEffect } from 'react';
import {
  TextField, Button, Typography, Box, Stack, Paper, Snackbar, Alert,
  MenuItem, Select, InputLabel, FormControl
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import {
  createCustomer, updateCustomer, getCustomer,
  getCities, getCountries
} from '../services/customerService';

const CustomerForm = ({ onSuccess, customerId }) => {
  const [form, setForm] = useState({
    name: '',
    dateOfBirth: '',
    nic: '',
    addressLine1: '',
    addressLine2: '',
    cityId: '',
    countryId: '',
    mobileNumbers: [''],
  });

  const [cities, setCities] = useState([]);
  const [countries, setCountries] = useState([]);
  const [isEdit, setIsEdit] = useState(false);

  const [snackbar, setSnackbar] = useState({
    open: false,
    message: '',
    severity: 'success',
  });

  useEffect(() => {
    getCountries()
      .then(res => setCountries(res.data))
      .catch(() => setCountries([]));
    getCities()
      .then(res => setCities(res.data))
      .catch(() => setCities([]));
  }, []);

  useEffect(() => {
    const loadCustomer = async () => {
      if (customerId && customerId > 0) {
        try {
          const res = await getCustomer(customerId);
          const customer = res.data;
          const addr = customer.addresses?.[0] || {};
          setForm({
            name: customer.name || '',
            dateOfBirth: customer.dateOfBirth || '',
            nic: customer.nic || '',
            addressLine1: addr.addressLine1 || '',
            addressLine2: addr.addressLine2 || '',
            cityId: addr.city?.id?.toString() || '',
            countryId: addr.country?.id?.toString() || '',
            mobileNumbers: customer.mobileNumbers?.length ? customer.mobileNumbers : [''],
          });
          setIsEdit(true);
        } catch (err) {
          console.error('Failed to fetch customer:', err);
          setIsEdit(false);
        }
      } else {
        setForm({
          name: '',
          dateOfBirth: '',
          nic: '',
          addressLine1: '',
          addressLine2: '',
          cityId: '',
          countryId: '',
          mobileNumbers: [''],
        });
        setIsEdit(false);
      }
    };

    loadCustomer();
  }, [customerId]);


  const handleSnackbarClose = () => {
    setSnackbar({ ...snackbar, open: false });
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleMobileChange = (index, value) => {
    const newMobiles = [...form.mobileNumbers];
    newMobiles[index] = value;
    setForm({ ...form, mobileNumbers: newMobiles });
  };

  const addMobileField = () => {
    setForm({ ...form, mobileNumbers: [...form.mobileNumbers, ''] });
  };

  const validateForm = () => {
    return (
      form.name.trim() !== '' &&
      form.dateOfBirth.trim() !== '' &&
      form.nic.trim() !== '' &&
      form.addressLine1.trim() !== '' &&
      form.addressLine2.trim() !== '' &&
      form.cityId.trim() !== '' &&
      form.countryId.trim() !== '' &&
      form.mobileNumbers.every(num => num.trim() !== '')
    );
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      setSnackbar({ open: true, message: 'Please fill all fields.', severity: 'error' });
      return;
    }

    const payload = {
      name: form.name,
      dateOfBirth: form.dateOfBirth,
      nic: form.nic,
      mobileNumbers: form.mobileNumbers,
      addresses: [
        {
          addressLine1: form.addressLine1,
          addressLine2: form.addressLine2,
          cityId: parseInt(form.cityId),
          countryId: parseInt(form.countryId),
        }
      ],
    };

    try {
      if (isEdit) {
        await updateCustomer(customerId, payload);
        setSnackbar({ open: true, message: 'Customer updated!', severity: 'success' });
      } else {
        await createCustomer(payload);
        setSnackbar({ open: true, message: 'Customer added!', severity: 'success' });
      }
      onSuccess();
      // Reset form after success
      setForm({
        name: '',
        dateOfBirth: '',
        nic: '',
        addressLine1: '',
        addressLine2: '',
        cityId: '',
        countryId: '',
        mobileNumbers: [''],
      });
    } catch (error) {
      console.error(error);
      setSnackbar({ open: true, message: 'Error submitting form.', severity: 'error' });
    }
  };

  return (
    <>
      <Paper elevation={3} sx={{ p: 4, my: 4 }}>
        <Typography variant="h6" gutterBottom>
          {isEdit ? 'Edit Customer' : 'Add Customer'}
        </Typography>
        <Box component="form" onSubmit={handleSubmit} noValidate>
          <Stack spacing={2}>
            <TextField
              label="Name"
              name="name"
              value={form.name}
              onChange={handleChange}
              fullWidth
              required
            />
            <TextField
              label="Date of Birth"
              type="date"
              name="dateOfBirth"
              value={form.dateOfBirth}
              onChange={handleChange}
              InputLabelProps={{ shrink: true }}
              fullWidth
              required
            />
            <TextField
              label="NIC"
              name="nic"
              value={form.nic}
              onChange={handleChange}
              fullWidth
              required
            />
            <TextField
              label="Address Line 1"
              name="addressLine1"
              value={form.addressLine1}
              onChange={handleChange}
              fullWidth
              required
            />
            <TextField
              label="Address Line 2"
              name="addressLine2"
              value={form.addressLine2}
              onChange={handleChange}
              fullWidth
              required
            />

            <FormControl fullWidth required>
              <InputLabel id="country-label">Country</InputLabel>
              <Select
                labelId="country-label"
                name="countryId"
                value={form.countryId}
                label="Country"
                onChange={handleChange}
              >
                <MenuItem value="">
                  <em>Select Country</em>
                </MenuItem>
                {countries.map(c => (
                  <MenuItem key={c.id} value={c.id.toString()}>
                    {c.name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <FormControl fullWidth required>
              <InputLabel id="city-label">City</InputLabel>
              <Select
                labelId="city-label"
                name="cityId"
                value={form.cityId}
                label="City"
                onChange={handleChange}
              >
                <MenuItem value="">
                  <em>Select City</em>
                </MenuItem>
                {cities.map(city => (
                  <MenuItem key={city.id} value={city.id.toString()}>
                    {city.name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>

            <Typography variant="subtitle1">Mobile Numbers</Typography>
            {form.mobileNumbers.map((num, i) => (
              <TextField
                key={i}
                label={`Mobile ${i + 1}`}
                value={num}
                onChange={(e) => handleMobileChange(i, e.target.value)}
                fullWidth
                required
              />
            ))}
            <Button onClick={addMobileField} startIcon={<AddIcon />} variant="outlined">
              Add Mobile
            </Button>

            <Button type="submit" variant="contained" color="primary">
              Submit
            </Button>
          </Stack>
        </Box>
      </Paper>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={3000}
        onClose={handleSnackbarClose}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert severity={snackbar.severity} onClose={handleSnackbarClose} sx={{ width: '100%' }}>
          {snackbar.message}
        </Alert>
      </Snackbar>
    </>
  );
};

export default CustomerForm;
