import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import { BrowserRouter } from 'react-router-dom';
import { ThemeProvider } from '@mui/material/styles';
import { CssBaseline } from '@mui/material';
import theme from './theme';
import { AuthProvider } from './context/AuthContext'; // Импортируем провайдер

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <ThemeProvider theme={theme}>
    <BrowserRouter>
      <AuthProvider> {/* Оборачиваем приложение в провайдер */}
        <CssBaseline />
        <App />
      </AuthProvider>
    </BrowserRouter>
  </ThemeProvider>
);