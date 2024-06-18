import { initializeApp } from "https://www.gstatic.com/firebasejs/9.4.0/firebase-app.js";
import { getAuth, onAuthStateChanged, signInWithEmailAndPassword, createUserWithEmailAndPassword, signOut, sendPasswordResetEmail } from "https://www.gstatic.com/firebasejs/9.4.0/firebase-auth.js";
import { getFirestore, doc, getDoc, getDocs, collection, addDoc, setDoc } from "https://www.gstatic.com/firebasejs/9.4.0/firebase-firestore.js";

// Your Firebase configuration
const firebaseConfig = {
    apiKey: "AIzaSyAu0b1dgkC5hksdkrjYEd_qJZo4eRhoC8o",
    authDomain: "fb-aouth2.firebaseapp.com",
    projectId: "fb-aouth2",
    storageBucket: "fb-aouth2.appspot.com",
    messagingSenderId: "344034577394",
    appId: "1:344034577394:web:ed9d8e93e07cb916fc796b"
};


// Initialize Firebase
const app = initializeApp(firebaseConfig);
const auth = getAuth(app);
const db = getFirestore(app);

export async function addUserToFirestore(userEmail, noteData) {
    try {
        const notesCollectionRef = collection(db, "users", userEmail, "notes");
        const noteDocRef = await addDoc(notesCollectionRef, noteData);
        console.log("Note saved with ID:", noteDocRef.id);
    } catch (error) {
        console.error("Error saving note:", error);
    }
}

// export async function addUserToFirestore(email, userData1) {
//     try {
//         await setDoc(doc(db, "users", email), {
//             userData1
//         });

//         console.log("Document written with ID:");
//     } catch (error) {
//         console.error("Error adding document:", error);
//     }
// }

export const resetPassword = (email) => {
    return sendPasswordResetEmail(auth, email);
}

// Export functions for login and signup
export const login = (email, password) => {
    return signInWithEmailAndPassword(auth, email, password);
};

export const signup = (email, password) => {
    return createUserWithEmailAndPassword(auth, email, password);
};

export const signout = () => {
    return signOut(auth);
}

//const user = auth.currentUser;
const user = auth.currentUser;
export const getUser = () => {
    return new Promise((resolve, reject) => {
        auth.onAuthStateChanged((user) => {
            if (user) {
                console.log('User is logged in:', user);
                resolve(user.email);
            } else {
                console.log('User is not logged in.');
                resolve("hahaha");
            }
        });
    });
};