import React, { useEffect, useState } from 'react';
import { ChatEngine } from 'react-chat-engine';
import axios from 'axios';

const App = () => {
    const [credentials, setCredentials] = useState({ userName: '', userSecret: '' });

    useEffect(() => {
        // Function to parse query parameters from URL
        const getQueryVariable = (variable) => {
            const query = window.location.search.substring(1);
            const vars = query.split('&');
            for (let i = 0; i < vars.length; i++) {
                const pair = vars[i].split('=');
                if (decodeURIComponent(pair[0]) === variable) {
                    return decodeURIComponent(pair[1]);
                }
            }
            console.error('Query variable %s not found', variable);
        };

        // Fetch credentials from API using the token
        const fetchCredentials = async (token) => {
            try {
                const userResponse = await axios.get('http://localhost:8081/api/v1/users/currentUser', {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                });
                const passwordResponse = await axios.get('http://localhost:8081/api/v1/users/password', {
                    headers: {
                        Authorization: `Bearer ${token}`
                    }
                });

                const userName = userResponse.data.data.userName;
                const userSecret = passwordResponse.data.data.password;

                console.log("React: ", token);
                setCredentials({ userName, userSecret });
            } catch (error) {
                console.error('Error fetching user credentials:', error);
            }
        };

        // Get token from URL query parameter
        const token = getQueryVariable('token');
        if (token) {
            fetchCredentials(token);
        } else {
            console.error('Token not found in URL');
        }
    }, []);

    if (!credentials.userName || !credentials.userSecret) {
        return <div>Loading...</div>;
    }

    return (
        <ChatEngine
            projectID='210b3df0-505f-411e-9b4e-575f7bd94b71'
            userName={credentials.userName}
            userSecret={credentials.userSecret}
        />
    );
};

export default App;
