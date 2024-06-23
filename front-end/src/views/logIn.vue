<template>
  <div class="login-container">
    <form @submit.prevent="login">
      <h1 class="login-heading">Login</h1>
      <h2 class="sub-heading">Enter your details below to continue</h2>

      <div class="form-group">
        <input
          type="text"
          v-model="email"
          class="input-field"
          placeholder="Email"
          required
        />
      </div>
      <div class="form-group">
        <input
          type="password"
          v-model="password"
          class="input-field"
          placeholder="Password"
          required
        />
      </div>

      <div class="form-group">
        <button type="submit" class="btn-login">Log In</button>
      </div>
      <div class="signup-option">
        <p>
          Don't have an account?
          <router-link to="/signup" class="btn-signup">SIGN UP</router-link>
        </p>
      </div>
    </form>
  </div>
  <div id="notification" class="notification" v-if="notificationVisible">
    Invalid credentials. Please try again.
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";

const email = ref("");
const password = ref("");
const notificationVisible = ref(false);

const router = useRouter();

const login = () => {
  const loginData = {
    uniqueName: email.value,
    password: password.value,
  };

  fetch("http://localhost:8081/api/v1/users/authenticate", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(loginData),
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error("Invalid credentials");
      }
      return response.json();
    })
    .then((data) => {
      const token = data.data.token;
      console.log("Token:", token);
      localStorage.setItem("token", token);

      // Redirect to dashboard after successful login
      router.push("/dashboard");
    })
    .catch((error) => {
      console.error("Login failed:", error.message);
      notificationVisible.value = true; // Show the notification only when login fails
    });
};
</script>

<style>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.login-form {
  width: 300px;
  padding: 20px;
  border-radius: 8px;
  background-color: #ffffff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.login-heading {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #333333;
  /* Heading color */
}

.sub-heading {
  font-size: 16px;
  margin-bottom: 20px;
  color: #666666;
  /* Subheading color */
}

.form-group {
  margin-bottom: 15px;
}

.input-field {
  width: 100%;
  padding: 10px;
  border: 1px solid #cccccc;
  /* Input border color */
  border-radius: 5px;
  outline: none;
}

.btn-login {
  width: 100%;
  padding: 10px;
  border: none;
  border-radius: 5px;
  background-color: blueviolet;
  /* Button background color */
  color: #ffffff;
  /* Button text color */
  font-weight: bold;
  cursor: pointer;
}

.btn-signup {
  padding: 10px;
  border: none;
  background-color: transparent;
  color: blueviolet;
  /* Sign up button color */
  font-weight: bold;
  cursor: pointer;
}

.signup-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.notification {
  position: fixed;
  bottom: 200px; /* Adjust the bottom value to lift the notification up */
  left: 50%;
  transform: translateX(-50%);
  color: red;
}
</style>
