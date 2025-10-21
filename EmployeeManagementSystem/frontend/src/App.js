import React from 'react';
import { Routes, Route, Link, useNavigate } from 'react-router-dom';
import { AppBar, Toolbar, Typography, Drawer, List, ListItem, ListItemText, Box, Container, Button } from '@mui/material';
import EntityList from './components/EntityList';
import EntityForm from './components/EntityForm';
import LogsManager from './components/LogsManager';
import { useAuth } from './context/AuthContext';

// Новые страницы
import LoginPage from './pages/LoginPage';
import LoginPasswordPage from './pages/LoginPasswordPage';
import SignUpPage from './pages/SignUpPage';
import ProfilePage from './pages/ProfilePage';
import ProtectedRoute from './components/ProtectedRoute';

// --- ВОТ ЧТО ПРОПАЛО! ---
// Этот массив является источником данных для ссылок в меню и для роутов
const entities = [
  {
    name: 'Departments',
    path: 'departments',
    api: '/api/departments',
    fields: [
      { key: 'name', label: 'Name' },
      { key: 'description', label: 'Description' },
    ],
    formFields: [
      { key: 'name', label: 'Name', type: 'text', required: true },
      { key: 'description', label: 'Description', type: 'text' },
    ],
  },
  {
    name: 'Employees',
    path: 'employees',
    api: '/api/employees',
    fields: [
      { key: 'firstName', label: 'First Name' },
      { key: 'lastName', label: 'Last Name' },
      { key: 'email', label: 'Email' },
      { key: 'hireDate', label: 'Hire Date' },
      { key: 'salary', label: 'Salary' },
      { key: 'departmentName', label: 'Department' },
      { key: 'positionName', label: 'Position' },
      { key: 'isActive', label: 'Active', type: 'boolean' },
    ],
    formFields: [
      { key: 'firstName', label: 'First Name', type: 'text', required: true },
      { key: 'lastName', label: 'Last Name', type: 'text', required: true },
      { key: 'email', label: 'Email', type: 'email', required: true },
      { key: 'hireDate', label: 'Hire Date', type: 'date', required: true },
      { key: 'salary', label: 'Salary', type: 'number', required: true },
      { key: 'departmentName', label: 'Department Name', type: 'text', required: true },
      { key: 'positionName', label: 'Position Name', type: 'text', required: true },
      { key: 'username', label: 'Username', type: 'text', required: true },
    ],
  },
  {
    name: 'Positions',
    path: 'positions',
    api: '/api/positions',
    fields: [
      { key: 'name', label: 'Name' },
      { key: 'description', label: 'Description' },
      { key: 'minSalary', label: 'Min Salary' },
      { key: 'maxSalary', label: 'Max Salary' },
    ],
    formFields: [
      { key: 'name', label: 'Name', type: 'text', required: true },
      { key: 'description', label: 'Description', type: 'text' },
      { key: 'minSalary', label: 'Min Salary', type: 'number' },
      { key: 'maxSalary', label: 'Max Salary', type: 'number' },
    ],
  },
  {
    name: 'Roles',
    path: 'roles',
    api: '/api/roles',
    fields: [
      { key: 'name', label: 'Name' },
    ],
    formFields: [
      { key: 'name', label: 'Name', type: 'text', required: true },
    ],
  },
  {
    path: 'users',
    name: 'Users',
    api: '/api/users',
    fields: [
      { key: 'username', label: 'Username' },
      { key: 'roles', label: 'Roles' }
    ],
    formFields: [
      { key: 'username', label: 'Username', type: 'text', required: true },
      { key: 'password', label: 'Password', type: 'password', required: false },
      { key: 'roleIds', label: 'Roles', type: 'select-multiple', required: true }
    ]
  }
];
// --- КОНЕЦ ПРОПАВШЕГО БЛОКА ---


// Компонент для основного лейаута после аутентификации
function MainLayout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    // navigate('/login'); // AuthContext теперь сам управляет редиректом
  }

  return (
    <Box sx={{ display: 'flex' }}>
      <AppBar position="fixed" sx={{ zIndex: (theme) => theme.zIndex.drawer + 1, bgcolor: '#1E1E1E' }}>
        <Toolbar>
          <Typography variant="h6" sx={{ flexGrow: 1, fontWeight: 500 }}>
            Employee Management
          </Typography>
          {user && (
            <Box sx={{display: 'flex', alignItems: 'center'}}>
               <Typography sx={{mr: 2}}>Hello, {user.username}</Typography>
               <Button color="inherit" onClick={handleLogout}>Logout</Button>
            </Box>
          )}
        </Toolbar>
      </AppBar>
      <Drawer
        variant="permanent"
        sx={{
          width: 240,
          flexShrink: 0,
          [`& .MuiDrawer-paper`]: {
            width: 240,
            boxSizing: 'border-box',
            bgcolor: '#1E1E1E',
            borderRight: '1px solid #2C2C2C',
          },
        }}
      >
        <Toolbar />
        <List>
          <ListItem component={Link} to="/me" sx={{ '&:hover': { bgcolor: '#2C2C2C' } }}>
            <ListItemText primary="My Profile" primaryTypographyProps={{ color: 'text.primary' }} />
          </ListItem>
          {entities.map((entity) => (
            <ListItem key={entity.path} component={Link} to={`/${entity.path}`} sx={{ '&:hover': { bgcolor: '#2C2C2C' } }}>
              <ListItemText primary={entity.name} primaryTypographyProps={{ color: 'text.primary' }} />
            </ListItem>
          ))}
          <ListItem key="logs" component={Link} to="/logs" sx={{ '&:hover': { bgcolor: '#2C2C2C' } }}>
            <ListItemText primary="Logs" primaryTypographyProps={{ color: 'text.primary' }} />
          </ListItem>
        </List>
      </Drawer>
      <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
        <Toolbar />
        <Container maxWidth="lg">
          <Routes>
             <Route path="/me" element={<ProfilePage />} />
            {entities.map((entity) => (
              <React.Fragment key={entity.path}>
                <Route path={`/${entity.path}`} element={<EntityList entity={entity} />} />
                <Route path={`/${entity.path}/add`} element={<EntityForm entity={entity} />} />
                <Route path={`/${entity.path}/edit/:id`} element={<EntityForm entity={entity} />} />
              </React.Fragment>
            ))}
            <Route path="/logs" element={<LogsManager />} />
            <Route
              path="/"
              element={<Typography variant="h4" sx={{ mt: 4 }}>Welcome, {user?.username}!</Typography>}
            />
          </Routes>
        </Container>
      </Box>
    </Box>
  );
}


function App() {
  const { isAuthenticated } = useAuth();

  return (
      <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/login-password" element={<LoginPasswordPage />} />
          <Route path="/register" element={<SignUpPage />} />

          <Route element={<ProtectedRoute />}>
              <Route path="/*" element={<MainLayout />} />
          </Route>
      </Routes>
  );
}

export default App;