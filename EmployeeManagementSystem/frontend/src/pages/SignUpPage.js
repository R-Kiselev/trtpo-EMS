import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Box, Button, Container, TextField, Typography, Paper, Alert } from '@mui/material';
import { useAuth } from '../context/AuthContext';

function SignUpPage() {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { register } = useAuth();

  const handleSubmit = async (event) => {
      event.preventDefault();
      setError('');
      if (password.length < 8) {
          setError('Password must be at least 8 characters long.');
          return;
      }
      try {
          await register(username, password);
          navigate('/');
      } catch (err) {
          // --- НАЧАЛО ИЗМЕНЕНИЙ ---
          if (err.response && err.response.data) {
              // Если бэкенд прислал конкретное сообщение
              setError(err.response.data);
          } else {
              // Общая ошибка
              setError('Registration failed. Please try again.');
          }
          console.error(err);
          // --- КОНЕЦ ИЗМЕНЕНИЙ ---
      }
  };

  return (
    <Container component="main" maxWidth="xs">
      <Paper elevation={6} sx={{ marginTop: 8, padding: 4, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Typography component="h1" variant="h5">
          Sign Up
        </Typography>
        {error && <Alert severity="error" sx={{ width: '100%', mt: 2 }}>{error}</Alert>}
        <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1, width: '100%' }}>
          <TextField margin="normal" required fullWidth id="username" label="Username" name="username" value={username} onChange={(e) => setUsername(e.target.value)} autoFocus />
          <TextField margin="normal" required fullWidth id="email" label="Email Address" name="email" value={email} onChange={(e) => setEmail(e.target.value)} />
          <TextField margin="normal" required fullWidth name="password" label="Password" type="password" id="password" value={password} onChange={(e) => setPassword(e.target.value)} />
          <Button type="submit" fullWidth variant="contained" sx={{ mt: 3, mb: 2 }}>
            Continue
          </Button>
           <Typography variant="body2" align="center">
            Already have an account? <Link to="/login" style={{ color: '#00C4B4' }}>Sign In</Link>
          </Typography>
        </Box>
      </Paper>
    </Container>
  );
}

export default SignUpPage;