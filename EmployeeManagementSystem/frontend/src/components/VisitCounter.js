import React, { useState, useEffect } from 'react';
import { Button, Typography, Box, CircularProgress } from '@mui/material';
import api from '../api';

function VisitCounter({ url }) {
  const [count, setCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchVisitCount = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await api.get('/api/visits', { params: { url } });
      setCount(response.data || 0);
    } catch (error) {
      console.error('Error fetching visit count:', error);
      setError('Failed to load visit count.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchVisitCount();
  }, [url]);

  return (
    <Box sx={{ mb: 2, display: 'flex', alignItems: 'center', gap: 2 }}>
      {loading ? (
        <CircularProgress size={20} sx={{ color: '#00C4B4' }} />
      ) : error ? (
        <Typography variant="body1" sx={{ color: 'error.main' }}>
          {error}
        </Typography>
      ) : (
        <Typography variant="body1" sx={{ color: 'text.secondary' }}>
          Visits to {url}: {count}
        </Typography>
      )}
      <Button
        variant="outlined"
        onClick={fetchVisitCount}
        sx={{
          borderColor: '#00C4B4',
          color: '#00C4B4',
          '&:hover': {
            borderColor: '#00A89A',
            bgcolor: '#2C2C2C',
          },
        }}
      >
        Refresh
      </Button>
    </Box>
  );
}

export default VisitCounter;