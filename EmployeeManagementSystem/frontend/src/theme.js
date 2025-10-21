import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    mode: 'dark',
    primary: {
      main: '#00C4B4', // Tiffany Blue
      contrastText: '#FFFFFF',
    },
    secondary: {
      main: '#B0B0B0',
      contrastText: '#FFFFFF',
    },
    background: {
      default: '#121212',
      paper: '#1E1E1E',
    },
    text: {
      primary: '#FFFFFF',
      secondary: '#B0B0B0',
    },
  },
  typography: {
    fontFamily: 'Roboto, sans-serif',
    h4: {
      fontWeight: 500,
    },
    button: {
      textTransform: 'none',
    },
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: 8,
          padding: '8px 16px',
          '&:hover': {
            backgroundColor: '#00A89A', // Slightly darker Tiffany for hover
          },
        },
      },
    },
    MuiTable: {
      styleOverrides: {
        root: {
          backgroundColor: '#1E1E1E',
        },
      },
    },
    MuiTableCell: {
      styleOverrides: {
        root: {
          borderBottom: '1px solid #2C2C2C',
        },
        head: {
          color: '#B0B0B0',
          fontWeight: 500,
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            '& fieldset': {
              borderColor: '#B0B0B0', // Light grey
            },
            '&:hover fieldset': {
              borderColor: '#FFFFFF', // White on hover
            },
            '&.Mui-focused fieldset': {
              borderColor: '#00C4B4', // Tiffany blue on focus
            },
          },
           // Ensure TextField label and input color are correct
          '& .MuiInputLabel-root': {
            color: '#B0B0B0',
            '&.Mui-focused': {
              color: '#00C4B4',
            },
          },
          '& .MuiInputBase-input': {
             color: '#FFFFFF',
          },
        },
      },
    },
    MuiOutlinedInput: {
      styleOverrides: {
        root: {
          '& fieldset': {
            borderColor: '#B0B0B0', // Light grey
          },
          '&:hover fieldset': {
            borderColor: '#FFFFFF', // White on hover
          },
          '&.Mui-focused fieldset': {
            borderColor: '#00C4B4', // Tiffany blue on focus
          },
        },
      },
    },
     MuiInputLabel: {
      styleOverrides: {
        root: {
          color: '#B0B0B0',
          '&.Mui-focused': {
            color: '#00C4B4',
          },
        },
      },
    },
    MuiInputBase: {
      styleOverrides: {
        input: {
          color: '#FFFFFF', // Default input text color
          '&::placeholder': {
            color: '#B0B0B0',
            opacity: 1,
          },
           // === Вариант 3 ===
          '& input[type="date"]::-webkit-calendar-picker-indicator': {
             color: '#FFFFFF', // Пытаемся установить цвет напрямую
             cursor: 'pointer',
           },
           // Также можно попробовать fill, если это SVG
          // '& input[type="date"]::-webkit-calendar-picker-indicator': {
          //    fill: '#FFFFFF',
          //    cursor: 'pointer',
          // },
        },
      },
    },
  },
});

export default theme;