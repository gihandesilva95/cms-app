import React, { useState } from 'react';
import CustomerTable from './components/CustomerTable';
import CustomerForm from './components/CustomerForm';
import BulkUpload from './components/BulkUpload';
import { Container, Typography, Button, Box, Paper } from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { BrowserRouter as Router, Routes, Route, useNavigate } from 'react-router-dom';

const CustomerPage = () => {
  const [formCustomerId, setFormCustomerId] = useState(null); // null = show table, number = edit
  const [reload, setReload] = useState(false);
  const navigate = useNavigate();

  const handleFormSuccess = () => {
    setFormCustomerId(null);
    setReload(!reload); // toggle to trigger re-fetch in CustomerTable
  };

  return (
    <Container sx={{ mt: 5 }}>
      {formCustomerId === null ? (
        <Paper sx={{ p: 4 }} elevation={3}>
          <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
            <Typography variant="h5">Customer Management System</Typography>
            <Box>
              <Button variant="outlined" sx={{ mr: 2 }} onClick={() => navigate('/bulk-upload')}>
                Bulk Upload
              </Button>
              <Button variant="contained" onClick={() => setFormCustomerId(0)}>
                + Add Customer
              </Button>
            </Box>
          </Box>
          <CustomerTable
            key={reload}
            onEdit={(id) => setFormCustomerId(id)}
          />
        </Paper>
      ) : (
        <Paper sx={{ p: 4 }} elevation={3}>
          <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
            <Button startIcon={<ArrowBackIcon />} onClick={() => setFormCustomerId(null)}>
              Back to List
            </Button>
            <Typography variant="h5">
              {formCustomerId > 0 ? 'Edit Customer' : 'Add Customer'}
            </Typography>
          </Box>
          <CustomerForm
            customerId={formCustomerId}
            onSuccess={handleFormSuccess}
          />
        </Paper>
      )}
    </Container>
  );
};

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<CustomerPage />} />
        <Route path="/bulk-upload" element={<BulkUpload />} />
      </Routes>
    </Router>
  );
}

export default App;
