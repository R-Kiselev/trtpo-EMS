import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Box, Button, Container, TextField, Typography, Paper } from '@mui/material';

function LoginPage() {
  const [email, setEmail] = useState(''); // Используем как username
  const navigate = useNavigate();

  const handleContinue = () => {
    // В двухэтапной авторизации мы передаем email на следующую страницу
    navigate('/login-password', { state: { email } });
  };

  return (
    <Container component="main" maxWidth="xs">
      <Paper elevation={6} sx={{ marginTop: 8, padding: 4, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Typography component="h1" variant="h5">
          Welcome back
        </Typography>
        <Box component="form" onSubmit={(e) => { e.preventDefault(); handleContinue(); }} noValidate sx={{ mt: 1, width: '100%' }}>
          <TextField
            margin="normal"
            required
            fullWidth
            id="email"
            label="Username"
            name="email"
            autoComplete="email"
            autoFocus
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <Button
            type="submit"
            fullWidth
            variant="contained"
            sx={{ mt: 3, mb: 2 }}
          >
            Continue
          </Button>
          <Typography variant="body2" align="center">
            Don't have an account? <Link to="/register" style={{ color: '#00C4B4' }}>Sign Up</Link>
          </Typography>
        </Box>
      </Paper>
    </Container>
  );
}

export default LoginPage;