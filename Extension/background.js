console.log("hello from background!");
import { login, signup, getUser, signout, resetPassword, addUserToFirestore } from "./firebase.js";

var selectedText = "";
var currentDate = new Date().toLocaleDateString();
var currentHour = new Date().getHours();
var currentMinute = new Date().getMinutes();
var url = "";
var e = "";
var text = "";
var comment = "";
var token = "";
var xxx = "";

chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
    async function addNoteToUser(comment, url, selectedText, userName) {
        try {
            const noteData = {
                userComment: comment,
                url: url,
                text: selectedText
            };
            try {
                xxx = await fetchCurrentUser();
                console.log('xxxx:', xxx);
            } catch (error) {
                console.error('Error getting user info:', error);
            }

            console.log("boody: ", noteData, " user name: ", userName);
            console.log("url: ", `http://localhost:8081/api/v1/users/${xxx}/note`);
            console.log("tokeeen: ", token);
            const response = await fetch(`http://localhost:8081/api/v1/users/${xxx}/note`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(noteData)
            });

            if (response.ok) {
                const data = await response.json();
                console.log('Note added successfully:', data);
                return data;
            } else {
                throw new Error('Failed to add note to user');
            }
        } catch (error) {
            console.error('Error:', error);
            return null;
        }
    }
    if (request.message === "userComment") {
        const currentDate = new Date();
        const currentHour = currentDate.getHours();
        const currentMinute = currentDate.getMinutes();
        comment = request.comment;
        addNoteToUser(comment, request.url, request.selectedText, fetchCurrentUser());

    }
    if (request.message === "resetPassword") {
        resetPassword(request.email)
            .then(() => {
                console.log("hello from background. Email sent");
            })
            .catch((error) => {
                console.log("hello from background. Not Logged in. Email is not sent!");
                console.log(error);
            });
    }
    if (request.message == "url" && request.url) {
        const currentDate = new Date();
        const currentHour = currentDate.getHours();
        const currentMinute = currentDate.getMinutes();
        console.log("Current url:", request.url, " Selected text: ", request.text, " Date: ", currentDate
            , " Hour: ", currentHour, ":", currentMinute);
        url = request.url;
        text = request.text;
        if (fetchCurrentUser() != null) {
            chrome.storage.local.set({
                message: "usersData",
                url: request.url,
                date: currentDate.toISOString(),
                hour: currentHour,
                min: currentMinute,
                text: text,
                email: e
            });
        }
    }

    async function fetchCurrentUser() {
        try {
            const response = await fetch('http://localhost:8081/api/v1/users/currentUser', {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const data = await response.json();
            if (data === null) {
                console.log('No user logged in');
                return null;
            } else {
                console.log('User logged in:', data.data.userName, 'with role:', data.data.role);
                return data.data.userName;
            }
        } catch (error) {
            console.error('There was a problem with the fetch operation:', error);
            throw error; // Rethrow the error to be caught later if necessary
        }
    }



    if (request.type === "checkLogin") {
        fetchCurrentUser()
            .then(isLoggedIn => {
                if (isLoggedIn) {
                    console.log("There's a user");
                    chrome.storage.local.set({ message: "LoggedIn" });
                } else {
                    console.log("No user logged in");
                    chrome.storage.local.set({ message: "NotLoggedIn" });
                }
                sendResponse({ isLoggedIn });
            })
            .catch(error => {
                // Handle any errors that occurred during fetchCurrentUser()
                console.error('Error checking login status:', error);
                // Optionally, handle the error by setting a default message
                chrome.storage.local.set({ message: "NotLoggedIn" });
                sendResponse({ isLoggedIn: false });
            });
    }
    // });
    // chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
    if (request.message == "Blogout") {
        console.log("logout");
        console.log("tokennn:", token)
        fetch("http://localhost:8081/api/v1/users/out", {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                console.log(data); // Handle response data here
            })
            .catch(error => {
                console.error('There was a problem with the fetch operation:', error);
            });
    }


    if (request.message == "Blogin" && request.email && request.pass) {
        console.log("User name:", request.email);
        console.log("Password:", request.pass);
        const loginData = {
            uniqueName: request.email,
            password: request.pass
        };
        fetch('http://localhost:8081/api/v1/users/authenticate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginData)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Invalid credentials');
                }
                return response.json();
            })
            .then(data => {
                token = data.data.token;
                console.log('Token:', token);
                chrome.tabs.sendMessage(sender.tab.id, { message: "authed", email: request.email, pass: request.pass });
            })
            .catch(error => {
                console.error('Login failed:', error.message);
            });
    }

    if (request.message == "BSignup" && request.email && request.pass) {
        console.log("User name:", request.email);
        console.log("Password:", request.pass);

        const signupData = {
            uniqueName: "b",
            email: "b@gmail.com",
            password: "password",
            bio: "Hello I want to graduate :'(",
            birthDate: "2003-01-18",
            role: "ROLE_USER"
        };
        fetch('http://localhost:8081/api/v1/users', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(signupData)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Invalid credentials');
                }
                return response.json();
            })
            .then(data => {
                token = data.data.token;
                console.log('Token:', token);
                chrome.tabs.sendMessage(sender.tab.id, { message: "authed", email: request.email, pass: request.pass });
            })
            .catch(error => {
                console.error('Sign up failed:', error.message);
            });
    }
});