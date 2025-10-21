import React, { useState } from 'react';
import { TextField, Button, Typography, Box, Paper } from '@mui/material';
import api from '../api';

function LogsManager() {
  const [date, setDate] = useState('');
  const [taskId, setTaskId] = useState('');
  const [logContent, setLogContent] = useState('');
  const [taskStatus, setTaskStatus] = useState('');

  const handleGenerateLog = async () => {
    try {
      const response = await api.post('/api/logs/generate', null, { params: { date } });
      setTaskId(response.data);
      alert(`Task created with ID: ${response.data}`);
    } catch (error) {
      console.error('Error generating log:', error);
      alert('Failed to generate log');
    }
  };

  const handleCheckStatus = async () => {
    try {
      const response = await api.get(`/api/logs/status/${taskId}`);
      setTaskStatus(`Status: ${response.data.status}${response.data.errorMessage ? `, Error: ${response.data.errorMessage}` : ''}`);
    } catch (error) {
      console.error('Error checking status:', error);
      setTaskStatus('Task not found');
    }
  };

  const handleViewLog = async () => {
    try {
      const response = await api.get('/api/logs/view', { params: { date } });
      setLogContent(response.data);
    } catch (error) {
      console.error('Error viewing log:', error);
      setLogContent('Log not found');
    }
  };

  const handleDownloadLog = async () => {
    try {
      const response = await api.get(`/api/logs/download/${taskId}`, { responseType: 'blob' });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `task-${taskId}.log`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      console.error('Error downloading log:', error);
      alert('Failed to download log');
    }
  };

  return (
    <Box sx={{ maxWidth: 600, mx: 'auto', mt: 4 }}>
      <Paper elevation={3} sx={{ p: 3, bgcolor: '#1E1E1E' }}>
        <Typography variant="h4" sx={{ mb: 3, fontWeight: 500 }}>
          Log Management
        </Typography>
        <TextField
          fullWidth
          label="Date (yyyy-MM-dd)"
          value={date}
          onChange={(e) => setDate(e.target.value)}
          variant="outlined"
          sx={{ mb: 2 }}
          InputLabelProps={{ style: { color: '#B0B0B0' } }}
          InputProps={{ style: { color: '#FFFFFF' } }}
        />
        <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
          <Button variant="contained" onClick={handleGenerateLog}>
            Generate Log
          </Button>
          <Button variant="contained" onClick={handleViewLog}>
            View Log
          </Button>
        </Box>
        <TextField
          fullWidth
          label="Task ID"
          value={taskId}
          onChange={(e) => setTaskId(e.target.value)}
          variant="outlined"
          sx={{ mb: 2 }}
          InputLabelProps={{ style: { color: '#B0B0B0' } }}
          InputProps={{ style: { color: '#FFFFFF' } }}
        />
        <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
          <Button variant="contained" onClick={handleCheckStatus}>
            Check Status
          </Button>
          <Button variant="contained" onClick={handleDownloadLog}>
            Download Log
          </Button>
        </Box>
        {taskStatus && (
          <Typography sx={{ mt: 2, color: 'text.secondary' }}>{taskStatus}</Typography>
        )}
        {logContent && (
          <Box sx={{ mt: 2, p: 2, bgcolor: '#2C2C2C', maxHeight: 400, overflow: 'auto', borderRadius: 1 }}>
            <Typography variant="body2" sx={{ color: 'text.primary' }}>
              {logContent}
            </Typography>
          </Box>
        )}
      </Paper>
    </Box>
  );
}

export default LogsManager;