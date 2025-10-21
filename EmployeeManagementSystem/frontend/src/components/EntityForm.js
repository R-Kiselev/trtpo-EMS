import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  TextField, Button, Box, Typography, Checkbox, FormControlLabel, Paper,
  Select, MenuItem, InputLabel, FormControl, Chip, OutlinedInput
} from '@mui/material';
import api from '../api';

function EntityForm({ entity }) {
  const { id } = useParams();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({});
  const [error, setError] = useState(null);
  const [allRoles, setAllRoles] = useState([]); // For user roles selector

  useEffect(() => {
    const initializeForm = () => {
      const initialFormState = entity.formFields.reduce(
        (acc, field) => {
          let initialValue = '';
          if (field.type === 'checkbox') {
            initialValue = false;
          } else if (field.key === 'hireDate' && entity.path === 'employees') {
            initialValue = new Date().toISOString().split('T')[0];
          } else if (field.key === 'roleIds' && entity.path === 'users') {
            initialValue = []; // Initialize as empty array for multi-select
          }
          return { ...acc, [field.key]: initialValue };
        },
        {}
      );
      setFormData(initialFormState);
    };

    if (id) {
      fetchItem();
    } else {
      initializeForm();
    }

    if (entity.path === 'users') {
      fetchAllRoles();
    }
  }, [id, entity.api, entity.formFields, entity.path]); // entity.api dependency might be redundant if path is stable

  const fetchAllRoles = async () => {
    try {
      const response = await api.get('/api/roles');
      setAllRoles(response.data || []);
    } catch (err) {
      console.error("Error fetching all roles:", err);
      setError("Failed to load roles for selection.");
    }
  };

  const fetchItem = async () => {
    try {
      const response = await api.get(`${entity.api}/${id}`);
      const data = response.data;

      if (entity.path === 'employees') {
        setFormData({
          firstName: data.firstName || '',
          lastName: data.lastName || '',
          email: data.email || '',
          hireDate: data.hireDate ? data.hireDate.split('T')[0] : '', // Ensure date format
          salary: data.salary || '',
          departmentName: data.department?.name || '',
          positionName: data.position?.name || '',
          username: data.user?.username || '',
        });
      } else if (entity.path === 'users') {
        setFormData({
          username: data.username || '',
          password: '', // Keep password blank for edit, user can type a new one if they want to change
          // Убедимся, что roleIds - это массив ID. Если бэкенд возвращает массив объектов ролей,
          // вам нужно будет преобразовать его здесь: data.roles ? data.roles.map(role => role.id) : []
          roleIds: Array.isArray(data.roleIds) ? data.roleIds : (Array.isArray(data.roles) ? data.roles.map(role => role.id) : []),
        });
      } else {
        // Generic handling, ensure all formFields are present in formData
        const initialData = entity.formFields.reduce((acc, field) => {
            acc[field.key] = data[field.key] !== undefined ? data[field.key] : (field.type === 'checkbox' ? false : '');
            return acc;
        }, {});
        setFormData(initialData);
      }
    } catch (error) {
      console.error(`Error fetching ${entity.name.slice(0,-1)}:`, error);
      setError(error.message || 'Failed to load data.');
    }
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
  };

  const handleHireDateFocus = () => {
    if (entity.path === 'employees' && !formData.hireDate) {
      setFormData((prev) => ({
        ...prev,
        hireDate: new Date().toISOString().split('T')[0],
      }));
    }
  };

  const resolveNamesToIds = async (data) => {
    if (entity.path === 'employees') {
      try {
        // Department resolution
        const departmentRes = await api.get('/api/departments');
        const department = departmentRes.data.find((d) => d.name === data.departmentName);
        if (!department && data.departmentName) throw new Error(`Department "${data.departmentName}" not found`);

        // Position resolution
        const positionRes = await api.get('/api/positions');
        const position = positionRes.data.find((p) => p.name === data.positionName);
        if (!position && data.positionName) throw new Error(`Position "${data.positionName}" not found`);

        // User resolution
        let userRes;
        if (data.username) { // Only try to resolve if username is provided
            try {
            userRes = await api.get(`/api/users/username/${data.username}`);
            if (!userRes.data) throw new Error(`User with username "${data.username}" not found`);
            } catch (err) {
            if (err.response && err.response.status === 404) {
                throw new Error(`User with username "${data.username}" not found`);
            } else {
                throw err;
            }
            }
        }


        return {
          firstName: data.firstName,
          lastName: data.lastName,
          email: data.email,
          hireDate: data.hireDate,
          salary: data.salary,
          isActive: data.isActive !== undefined ? data.isActive : true, // Default to true if not specified
          departmentId: department ? department.id : null,
          positionId: position ? position.id : null,
          userId: userRes ? userRes.data.id : null,
        };
      } catch (error) {
        throw error;
      }
    } else if (entity.path === 'users') {
      const payload = {
        username: data.username,
        roleIds: data.roleIds || [],
      };
      // Only include password if it's provided (not empty string)
      if (data.password && data.password.trim() !== '') {
        payload.password = data.password;
      }
      return payload;
    }
    return data; // For other entities
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setError(null);
      const resolvedData = await resolveNamesToIds(formData);
      if (id) {
        await api.put(`${entity.api}/${id}`, resolvedData);
      } else {
        await api.post(entity.api, resolvedData);
      }
      navigate(`/${entity.path}`);
    } catch (error) {
      console.error(`Error saving ${entity.name.slice(0,-1)}:`, error);
      if (error.message && !error.message.toLowerCase().includes('request failed')) {
        setError(error.message);
      } else if (error.response && error.response.data && error.response.data.message) {
        setError(error.response.data.message);
      } else {
        setError(`Error saving ${entity.name.slice(0, -1)}. Check console for details.`);
      }
    }
  };

  return (
    <Box sx={{ maxWidth: 600, mx: 'auto', mt: 4 }}>
      <Paper elevation={3} sx={{ p: 3, bgcolor: '#1E1E1E' }}>
        <Typography variant="h4" sx={{ mb: 3, fontWeight: 500 }}>
          {id ? `Edit ${entity.name.slice(0, -1)}` : `Add ${entity.name.slice(0, -1)}`}
        </Typography>
        {error && (
          <Typography color="error" sx={{ mb: 2 }}>
            {error}
          </Typography>
        )}
        <form onSubmit={handleSubmit}>
          {entity.formFields.map((field) => (
            <Box key={field.key} sx={{ mb: 2 }}>
              {field.type === 'checkbox' ? (
                <FormControlLabel
                  control={
                    <Checkbox
                      name={field.key}
                      checked={Boolean(formData[field.key])}
                      onChange={handleChange}
                      sx={{
                        color: '#00C4B4',
                        '&.Mui-checked': { color: '#00C4B4' },
                      }}
                    />
                  }
                  label={field.label}
                  sx={{ color: 'text.primary' }}
                />
              ) : field.type === 'select-multiple' && entity.path === 'users' && field.key === 'roleIds' ? (
                <FormControl fullWidth variant="outlined">
                  <InputLabel id={`${field.key}-label`} style={{ color: '#B0B0B0' }}>{field.label}</InputLabel>
                  <Select
                    labelId={`${field.key}-label`}
                    multiple
                    name={field.key}
                    value={formData[field.key] || []}
                    onChange={handleChange}
                     // InputProps are handled by theme, but we keep the color for selected items
                    input={<OutlinedInput label={field.label} />} // Removed redundant sx={{ color: '#FFFFFF' }} as theme handles it
                    renderValue={(selected) => (
                      <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                        {selected.map((value) => {
                          const role = allRoles.find(r => r.id === value);
                          return <Chip key={value} label={role ? role.name : 'Unknown Role'} sx={{ bgcolor: '#00C4B4', color: 'white' }} />;
                        })}
                      </Box>
                    )}
                    MenuProps={{
                      PaperProps: {
                        style: {
                          backgroundColor: '#1E1E1E', // Dropdown background
                          color: '#FFFFFF', // Dropdown text color
                        },
                      },
                    }}
                    sx={{
                      '& .MuiSelect-icon': { color: '#B0B0B0' }, // Dropdown arrow color
                      // Styles for the Select's root OutlinedInput are handled by theme
                    }}
                  >
                    {allRoles.map((role) => (
                      <MenuItem
                        key={role.id}
                        value={role.id}
                        sx={{
                          color: 'text.primary',
                          '&:hover': { bgcolor: '#2C2C2C' },
                          '&.Mui-selected': { bgcolor: '#00504A', '&:hover': { bgcolor: '#006960' } }, // Custom selected style
                        }}
                      >
                        {role.name}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              ) : (
                <TextField
                  fullWidth
                  label={field.label}
                  name={field.key}
                  type={field.type || 'text'}
                  value={formData[field.key] || ''}
                  onChange={handleChange}
                  required={field.required}
                  variant="outlined"
                  InputLabelProps={{ style: { color: '#B0B0B0' } }} // Keeping label color style here for clarity
                  InputProps={{
                     style: { color: '#FFFFFF' }, // Keeping input text color style here for clarity
                     ...(field.type === 'date' && { // Apply styles only for date type
                       // Styles for the date picker icon - Testing different filters here
                       sx: {
                         '& input[type="date"]::-webkit-calendar-picker-indicator': {
                            // *** Попробуйте раскомментировать один из этих вариантов ***
                            // Вариант 1: Original attempt
                            // filter: 'invert(1) brightness(0.9)',

                            // Вариант 2: Simple invert
                            // filter: 'invert(1)',

                            // Вариант 3: Direct color (less likely to work universally)
                            // color: '#FFFFFF',

                             // Вариант 4: Even stronger invert + brightness
                             filter: 'invert(100%) brightness(200%)', // Усиленный фильтр


                            cursor: 'pointer',
                          },
                       },
                       inputProps: {
                         style: { color: formData[field.key] ? '#FFFFFF' : '#B0B0B0' }, // Color for date value/placeholder
                         ...(field.key === 'hireDate' && entity.path === 'employees' && {
                           onFocus: handleHireDateFocus,
                         }),
                       },
                     }),
                   }}
                  sx={{
                    // Border styles are handled by theme
                  }}
                />
              )}
            </Box>
          ))}
          <Box sx={{ display: 'flex', gap: 2 }}>
            <Button type="submit" variant="contained" color="primary">
              Save
            </Button>
            <Button
              variant="outlined"
              onClick={() => navigate(`/${entity.path}`)}
              sx={{
                borderColor: '#00C4B4', color: '#00C4B4',
                '&:hover': { borderColor: '#00A89A', bgcolor: '#2C2C2C' },
              }}
            >
              Cancel
            </Button>
          </Box>
        </form>
      </Paper>
    </Box>
  );
}

export default EntityForm;