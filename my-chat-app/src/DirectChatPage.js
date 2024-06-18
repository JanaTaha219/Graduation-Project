import React, { useState, useEffect } from 'react';
import { ChatEngine, getOrCreateChat } from 'react-chat-engine';
import axios from 'axios';

const DirectChatPage = () => {
    const [username, setUsername] = useState('');
    const [credentials, setCredentials] = useState({ userName: '', userSecret: '' });

    useEffect(() => {
        const fetchCredentials = async () => {
            try {
                // Replace the hardcoded token with a dynamic way to get it
                const token = 'your_token_here'; // Replace with your token logic

                const [userResponse, passwordResponse] = await Promise.all([
                    axios.get('http://localhost:8081/api/v1/users/currentUser', {
                        headers: {
                            Authorization: `Bearer ${token}`
                        }
                    }),
                    axios.get('http://localhost:8081/api/v1/users/password', {
                        headers: {
                            Authorization: `Bearer ${token}`
                        }
                    })
                ]);

                const userName = userResponse.data.data.userName;
                const userSecret = passwordResponse.data.data.password;

                setCredentials({ userName, userSecret });
            } catch (error) {
                console.error('Error fetching user credentials:', error);
            }
        };

        fetchCredentials();
    }, []);

    function createDirectChat(creds) {
        getOrCreateChat(
            creds,
            { is_direct_chat: true, usernames: [username] },
            () => setUsername('')
        );
    }

    function renderChatForm(creds) {
        return (
            <div>
                <input
                    placeholder='Username'
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                />
                <button onClick={() => createDirectChat(creds)}>
                    Create
                </button>
            </div>
        );
    }

    if (!credentials.userName || !credentials.userSecret) {
        return <div>Loading...</div>;
    }

    return (
        <ChatEngine
            height='100vh'
            userName={credentials.userName}
            userSecret={credentials.userSecret}
            projectID='210b3df0-505f-411e-9b4e-575f7bd94b71'
            renderNewChatForm={(creds) => renderChatForm(creds)}
        />
    );
};

export default DirectChatPage;
