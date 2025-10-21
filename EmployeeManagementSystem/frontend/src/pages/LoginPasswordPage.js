import React, { useState, useEffect } from 'react'; // Убедитесь, что useEffect импортирован
import { useLocation, useNavigate } from 'react-router-dom';
import { Box, Button, Container, TextField, Typography, Paper, Alert } from '@mui/material';
import { useAuth } from '../context/AuthContext';

function LoginPasswordPage() {
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuth();
  const emailOrUsername = location.state?.email;

  // ПРАВИЛЬНЫЙ СПОСОБ ИСПОЛЬЗОВАНИЯ useEffect
  useEffect(() => {
    // Условие теперь внутри хука, а не снаружи
    if (!emailOrUsername) {
      navigate('/login');
    }
  }, [emailOrUsername, navigate]);

  // Если email/username еще не определен, можно показать пустой компонент
  // или заглушку, пока useEffect не сделает редирект.
  if (!emailOrUsername) {
    return null;
  }

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    try {
      await login(emailOrUsername, password);
      navigate('/');
    } catch (err) {
      setError('Invalid username or password.');
      console.error(err);
    }
  };

  return (
    <Container component="main" maxWidth="xs">
      <Paper elevation={6} sx={{ marginTop: 8, padding: 4, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Typography component="h1" variant="h5">
          Enter your password
        </Typography>
        <Typography variant="body2" sx={{ mt: 1 }}>
          For: {emailOrUsername}
        </Typography>
        {error && <Alert severity="error" sx={{ width: '100%', mt: 2 }}>{error}</Alert>}
        <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1, width: '100%' }}>
          <TextField
            margin="normal"
            required
            fullWidth
            name="password"
            label="Password"
            type="password"
            id="password"
            autoComplete="current-password"
            autoFocus
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <Button
            type="submit"
            fullWidth
            variant="contained"
            sx={{ mt: 3, mb: 2 }}
          >
            Continue
          </Button>
        </Box>
      </Paper>
    </Container>
  );
}

export default LoginPasswordPage;