import React, { useEffect, useState } from 'react';
import {
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Paper, Typography, IconButton
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import { getCustomers } from '../services/customerService';

const CustomerTable = ({ onEdit, reload }) => {
  const [customers, setCustomers] = useState([]);

  useEffect(() => {
    getCustomers()
      .then(res => setCustomers(res.data))
      .catch(err => {
        console.error('Failed to fetch customers:', err);
        setCustomers([]);
      });
  }, [reload]);

  return (
    <Paper elevation={3} sx={{ p: 3, mb: 4 }}>
      <Typography variant="h6" gutterBottom>
        Customer List
      </Typography>
      <TableContainer>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Name</TableCell>
              <TableCell>NIC</TableCell>
              <TableCell>Date of Birth</TableCell>
              <TableCell>Address</TableCell>
              <TableCell>Mobile Numbers</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {customers.map((c) => (
              <TableRow key={c.id}>
                <TableCell>{c.name}</TableCell>
                <TableCell>{c.nic}</TableCell>
                <TableCell>{c.dateOfBirth}</TableCell>
                <TableCell>
                  {c.addresses?.length > 0 ? (
                    c.addresses.map((addr, i) => (
                      <div key={i}>
                        {addr.addressLine1}, {addr.addressLine2}, {addr.city?.name}, {addr.country?.name}
                      </div>
                    ))
                  ) : (
                    'N/A'
                  )}
                </TableCell>
                <TableCell>{c.mobileNumbers?.join(', ')}</TableCell>
                <TableCell>
                  <IconButton color="primary" onClick={() => onEdit(c.id)}>
                    <EditIcon />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Paper>
  );
};

export default CustomerTable;
