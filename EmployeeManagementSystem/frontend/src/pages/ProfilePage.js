import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Box, Typography, Paper, Button } from '@mui/material';

function ProfilePage() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    if (!user) {
        return <Typography>Loading...</Typography>;
    }

    const handleLogout = () => {
        logout();
        navigate('/login');
    }

    return (
        <Paper sx={{ p: 4 }}>
            <Typography variant="h4" gutterBottom>My Profile</Typography>
            <Box>
                <Typography variant="h6">Username:</Typography>
                <Typography gutterBottom>{user.username}</Typography>

                <Typography variant="h6">Roles:</Typography>
                <Typography>
                    {user.roles && user.roles.length > 0
                        ? user.roles.map(role => role.name).join(', ')
                        : 'No roles assigned.'}
                </Typography>
            </Box>
            <Button variant="contained" onClick={handleLogout} sx={{ mt: 3 }}>
                Logout
            </Button>
        </Paper>
    );
}

export default ProfilePage;