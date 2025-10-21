import React, { useState, useEffect } from 'react';
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Button, IconButton, Typography, Paper, CircularProgress, Box } from '@mui/material';
import { Edit, Delete } from '@mui/icons-material';
import { Link } from 'react-router-dom';
import api from '../api';
import VisitCounter from './VisitCounter';

function EntityList({ entity }) {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchItems();
  }, [entity.api]);

  const fetchItems = async () => {
    try {
      setLoading(true);
      const response = await api.get(entity.api);
      let fetchedItems = response.data || [];

      if (entity.path === 'employees') {
        fetchedItems = fetchedItems.map(item => ({
          ...item,
          departmentName: item.department?.name || 'N/A',
          positionName: item.position?.name || 'N/A',
        }));
      }

      console.log('Fetched items for', entity.name, fetchedItems);
      setItems(fetchedItems);
      setError(null);
    } catch (error) {
      console.error(`Error fetching ${entity.name}:`, error);
      setError('Failed to load data. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    try {
      await api.delete(`${entity.api}/${id}`);
      fetchItems();
    } catch (error) {
      console.error(`Error deleting ${entity.name}:`, error);
      setError('Failed to delete item.');
    }
  };

  const formatFieldValue = (item, field) => {
    if (field.key === 'roles' && entity.path === 'users') {
      console.log('Processing roles for item:', item);
      return item[field.key] && Array.isArray(item[field.key]) && item[field.key].length > 0
        ? item[field.key].map((role) => role.name || 'Unknown').join(', ')
        : 'N/A';
    }
    if (field.type === 'boolean') {
      return item[field.key] != null ? item[field.key].toString() : 'N/A';
    }
    return item[field.key] != null ? item[field.key] : 'N/A';
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
        <CircularProgress sx={{ color: '#00C4B4' }} />
      </Box>
    );
  }

  if (error) {
    return (
      <Box sx={{ mt: 4 }}>
        <Typography color="error">{error}</Typography>
        <Button
          variant="contained"
          onClick={fetchItems}
          sx={{ mt: 2 }}
        >
          Retry
        </Button>
      </Box>
    );
  }

  return (
    <div>
      <Typography variant="h4" sx={{ mb: 3, fontWeight: 500 }}>
        {entity.name}
      </Typography>
      <Button
        variant="contained"
        component={Link}
        to={`/${entity.path}/add`}
        sx={{ mb: 2, mr: 2 }}
      >
        Add {entity.name.slice(0, -1)}
      </Button>
      <VisitCounter url={entity.api} />
      <TableContainer component={Paper} elevation={3}>
        <Table>
          <TableHead>
            <TableRow>
              {entity.fields.map((field) => (
                <TableCell key={field.key}>{field.label}</TableCell>
              ))}
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {items.map((item) => (
              <TableRow
                key={item.id}
                sx={{
                  '&:hover': {
                    bgcolor: '#2C2C2C',
                  },
                }}
              >
                {entity.fields.map((field) => (
                  <TableCell key={field.key}>
                    {formatFieldValue(item, field)}
                  </TableCell>
                ))}
                <TableCell>
                  <Button
                    component={Link}
                    to={`/${entity.path}/edit/${item.id}`}
                    sx={{ color: '#00C4B4' }}
                    startIcon={<Edit />}
                  >
                    Edit
                  </Button>
                  <Button
                    onClick={() => handleDelete(item.id)}
                    sx={{ color: '#00C4B4' }}
                    startIcon={<Delete />}
                  >
                    Delete
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
}

export default EntityList;