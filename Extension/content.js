console.log("hello from content!");
function sendSelectedTextToBackground(selectedText) {
    chrome.runtime.sendMessage({ action: 'selectedText', text: selectedText });
}
var selectedText = "";
document.addEventListener('mouseup', function () {
    selectedText = window.getSelection().toString().trim();
    if (selectedText !== '') {
        sendSelectedTextToBackground(selectedText);
        const currentPageUrl = window.location.href;
        var highlightedLink = currentPageUrl + "#:~:text=" + encodeURIComponent(selectedText);
        console.log(highlightedLink);
        chrome.runtime.sendMessage({ message: "url", url: highlightedLink, text: selectedText });
    }
});



chrome.runtime.sendMessage({ type: "checkLogin" }, (response) => {
    if (response.isLoggedIn) {
        console.log("User is logged in.");
        chrome.runtime.sendMessage({ message: "CLoggedIn" });
    } else {
        console.log("User is not logged in.");
        chrome.runtime.sendMessage({ message: "CNotLoggedIn" });
    }
});

chrome.runtime.onMessage.addListener(function (request, sender, sendResponse) {

    if (request.message == "logout") {
        console.log("logout");

        chrome.runtime.sendMessage({
            message: "Blogout",
        });
    }
    if (request.message == "login" && request.message1 && request.message2) {
        console.log("User name:", request.message1);
        console.log("Password:", request.message2);

        chrome.runtime.sendMessage({
            message: "Blogin",
            email: request.message1,
            pass: request.message2
        });
    }

    if (request.message == "signup" && request.message1 && request.message2) {
        console.log("User name:", request.message1);
        console.log("Password:", request.message2);
        console.log("Confirm password:", request.message3);
        chrome.runtime.sendMessage({
            message: "BSignup",
            email: request.message1,
            pass: request.message2
        });
    }

    if (request.message == "authed") {
        console.log("authed");
        chrome.runtime.sendMessage({ message: "Cauthed" });
        //authed.style.display = 'none';

    }
});