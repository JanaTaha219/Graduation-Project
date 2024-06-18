chrome.storage.local.get(['message', 'url', 'date', 'hour', 'min', 'text'], (result) => {
    if (result.message === "usersData") {
        document.getElementById("save").style.display = 'block';
        document.getElementById("authed").style.display = 'none';
        document.getElementById("logout").style.display = 'block';
        var message = "Received message:";
        var url = result.url;
        var selectedText = result.text;
        var currentDate = new Date().toLocaleDateString();
        var currentHour = new Date().getHours();
        var currentMinute = new Date().getMinutes();
        document.getElementById("hello").textContent =
            "Selected Text: " + selectedText;
        const logoutButton = document.getElementById('logout-button');
        const saveButton = document.getElementById('save-button');

        saveButton.addEventListener('click', function () {
            chrome.runtime.sendMessage({
                message: "userComment",
                comment: document.getElementById('user-comment').value,
                url,
                selectedText
            });
            document.getElementById("hello").textContent = "Saved!";

        });
        logoutButton.addEventListener('click', function () {
            chrome.tabs.query({ active: true, currentWindow: true }, function (tabs) {
                chrome.tabs.sendMessage(tabs[0].id, { message: "logout" });
                document.getElementById("authed").style.display = 'block';
                document.getElementById("logout").style.display = 'none';
                document.getElementById("save").style.display = 'none';
                document.getElementById("hello").style.display = 'none';
            });
        });
    }
    if (result.message === "LoggedIn") {
        //document.getElementById("hello").textContent = result.message;
        document.getElementById("authed").style.display = 'none';
        document.getElementById("logout").style.display = 'block';
        const logoutButton = document.getElementById('logout-button');

        logoutButton.addEventListener('click', function () {
            chrome.tabs.query({ active: true, currentWindow: true }, function (tabs) {
                chrome.tabs.sendMessage(tabs[0].id, { message: "logout" });
                document.getElementById("authed").style.display = 'block';
                document.getElementById("logout").style.display = 'none';
                document.getElementById("hello").style.display = 'none';
                document.getElementById("save").style.display = 'none';

            });
        });
    }
    if (result.message === "NotLoggedIn") {
        document.getElementById("authed").style.display = 'block';
        document.getElementById("save").style.display = 'none';
        document.getElementById("logout").style.display = 'none';
        document.getElementById("hello").style.display = 'none';

    }
});
document.addEventListener('DOMContentLoaded', () => {
    const loginButton = document.getElementById('login-button');
    const signupButton = document.getElementById('signup-button');
    const loginForm = document.getElementById('login-form');
    const signupForm = document.getElementById('signup-form');
    const authed = document.getElementById('authed')
    const resetPass = document.getElementById('forget-pass');


    chrome.runtime.sendMessage({ message: "hello" });

    resetPass.addEventListener('click', () => {
        chrome.runtime.sendMessage({
            message: "resetPassword",
            email: document.getElementById('login-email').value
        });
    })

    loginButton.addEventListener('click', () => {
        loginForm.style.display = 'block';
        signupForm.style.display = 'none';
        document.getElementById("save").style.display = 'none';
        document.getElementById("hello").style.display = 'none';

        const email = document.getElementById("login-email");
        const pass = document.getElementById("login-password");

        const sendButton = document.getElementById("submit-login");
        sendButton.addEventListener("click", function () {
            chrome.tabs.query({ active: true, currentWindow: true }, function (tabs) {
                chrome.tabs.sendMessage(tabs[0].id, { message: "login", message1: email.value, message2: pass.value });

            });
        });
    });
    signupButton.addEventListener('click', () => {
        signupForm.style.display = 'block';
        loginForm.style.display = 'none';
        document.getElementById("save").style.display = 'none';
        document.getElementById("hello").style.display = 'none';
        const email = document.getElementById("signup-email");
        const pass = document.getElementById("signup-password");
        const Cpass = document.getElementById("signup-Cpassword");

        const sendButton = document.getElementById("submit-signup");
        sendButton.addEventListener("click", function () {
            chrome.tabs.query({ active: true, currentWindow: true }, function (tabs) {
                chrome.tabs.sendMessage(tabs[0].id, { message: "signup", message1: email.value, message2: pass.value, message3: Cpass.value });
            });

        });
    });

    chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {
        if (request.message === "Cauthed") {
            authed.style.display = 'none';
            document.getElementById("save").style.display = 'none';
            document.getElementById("hello").style.display = 'none';
            document.getElementById("logout").style.display = 'block';
            const logoutButton = document.getElementById('logout-button');
            logoutButton.addEventListener('click', function () {
                chrome.tabs.query({ active: true, currentWindow: true }, function (tabs) {
                    chrome.tabs.sendMessage(tabs[0].id, { message: "logout" });
                    document.getElementById("authed").style.display = 'block';
                    document.getElementById("logout").style.display = 'none';
                });
            });
        }
    })
});