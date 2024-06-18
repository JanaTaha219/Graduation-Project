// src/firebase.js
import { initializeApp } from "firebase/app";
import { getStorage } from "firebase/storage";

const firebaseConfig = {
  apiKey: "AIzaSyAu0b1dgkC5hksdkrjYEd_qJZo4eRhoC8o",
  authDomain: "fb-aouth2.firebaseapp.com",
  projectId: "fb-aouth2",
  storageBucket: "fb-aouth2.appspot.com",
  messagingSenderId: "344034577394",
  appId: "1:344034577394:web:ed9d8e93e07cb916fc796b",
};

const app = initializeApp(firebaseConfig);
const storage = getStorage(app);

export { storage };
