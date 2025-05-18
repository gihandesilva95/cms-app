import React, { useState } from 'react';
import {
  Container,
  Typography,
  Button,
  Box,
  Paper,
  Snackbar,
  Alert,
  LinearProgress,
  Divider,
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import DescriptionIcon from '@mui/icons-material/Description';
import { useNavigate } from 'react-router-dom';
import { uploadCustomersExcel } from '../services/customerService';

const BulkUpload = () => {
  const [file, setFile] = useState(null);
  const [snackbar, setSnackbar] = useState({
    open: false,
    message: '',
    severity: 'success',
  });
  const [uploading, setUploading] = useState(false);

  const navigate = useNavigate();

  const handleSnackbarClose = () => {
    setSnackbar({ ...snackbar, open: false });
  };

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const handleUpload = async () => {
    if (!file) {
      setSnackbar({
        open: true,
        message: 'Please select an Excel (.xlsx) file.',
        severity: 'warning',
      });
      return;
    }

    const formData = new FormData();
    formData.append('file', file);

    setUploading(true);
    try {
      await uploadCustomersExcel(formData);
      setSnackbar({
        open: true,
        message: 'Bulk upload successful!',
        severity: 'success',
      });
      setFile(null);
    } catch (error) {
      console.error(error);
      setSnackbar({
        open: true,
        message: 'Upload failed. Please check the file format and data.',
        severity: 'error',
      });
    } finally {
      setUploading(false);
    }
  };

  return (
    <Container maxWidth="sm" sx={{ mt: 6 }}>
      <Paper
        elevation={4}
        sx={{
          p: 4,
          borderRadius: 3,
          boxShadow: '0 4px 20px rgba(0,0,0,0.05)',
        }}
      >
        <Box display="flex" alignItems="center" mb={3}>
          <Button
            startIcon={<ArrowBackIcon />}
            variant="outlined"
            onClick={() => navigate('/')}
          >
            Back
          </Button>
          <Typography variant="h5" sx={{ ml: 3, fontWeight: 600 }}>
            Bulk Customer Upload
          </Typography>
        </Box>

        <Divider sx={{ mb: 3 }} />

        <Box
          sx={{
            border: '2px dashed #ccc',
            borderRadius: 2,
            p: 4,
            textAlign: 'center',
            bgcolor: '#fafafa',
            transition: '0.3s ease',
            '&:hover': { backgroundColor: '#f0f0f0' },
          }}
        >
          <CloudUploadIcon sx={{ fontSize: 50, color: 'primary.main', mb: 2 }} />
          <Typography variant="body1" gutterBottom>
            Drag & drop or choose an Excel (.xlsx) file
          </Typography>

          <input
            type="file"
            accept=".xlsx"
            id="excel-file"
            style={{ display: 'none' }}
            onChange={handleFileChange}
          />
          <label htmlFor="excel-file">
            <Button variant="contained" component="span">
              Choose File
            </Button>
          </label>

          {file && (
            <Box mt={2} display="flex" justifyContent="center" alignItems="center" gap={1}>
              <DescriptionIcon color="action" />
              <Typography variant="body2">{file.name}</Typography>
            </Box>
          )}
        </Box>

        <Button
          fullWidth
          sx={{ mt: 3, py: 1.3 }}
          variant="contained"
          color="primary"
          onClick={handleUpload}
          disabled={uploading || !file}
        >
          Upload Now
        </Button>

        {uploading && <LinearProgress sx={{ mt: 2 }} />}

        <Snackbar
          open={snackbar.open}
          autoHideDuration={3000}
          onClose={handleSnackbarClose}
          anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
        >
          <Alert
            severity={snackbar.severity}
            onClose={handleSnackbarClose}
            sx={{ width: '100%' }}
          >
            {snackbar.message}
          </Alert>
        </Snackbar>
      </Paper>
    </Container>
  );
};

export default BulkUpload;
