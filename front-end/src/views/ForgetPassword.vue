<template>
  <div class="forgot-password-container">
    <form @submit.prevent="resetPassword">
      <h1 class="heading">Forgot Password</h1>
      <h2 class="sub-heading">Enter your email to reset your password</h2>

      <div class="form-group">
        <input
          type="email"
          v-model="email"
          class="input-field"
          placeholder="Email"
          required
        />
      </div>

      <div class="form-group">
        <button type="submit" class="btn-reset">Reset Password</button>
      </div>

      <div class="login-option">
        <p>
          Remembered your password?
          <router-link to="/" class="btn-login">Log In</router-link>
        </p>
      </div>
    </form>
  </div>
  <div id="notification" class="notification" v-if="notificationVisible">
    {{ notificationMessage }}
  </div>
</template>

<script setup>
import { ref } from "vue";
import axios from "axios";

const email = ref("");
const notificationVisible = ref(false);
const notificationMessage = ref("");

const resetPassword = () => {
  axios
    .post("http://localhost:8081/api/v1/users/forgetPassword", {
      email: email.value,
    })
    .then((response) => {
      notificationMessage.value = "Password reset link sent to your email.";
      notificationVisible.value = true;
    })
    .catch((error) => {
      console.error("Error resetting password:", error);
      notificationMessage.value =
        "Failed to send reset link. Please try again.";
      notificationVisible.value = true;
    });
};
</script>

<style>
.forgot-password-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

form {
  width: 300px;
  padding: 20px;
  border-radius: 8px;
  background-color: #ffffff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.heading {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #333333;
}

.sub-heading {
  font-size: 16px;
  margin-bottom: 20px;
  color: #666666;
}

.form-group {
  margin-bottom: 15px;
}

.input-field {
  width: 100%;
  padding: 10px;
  border: 1px solid #cccccc;
  border-radius: 5px;
  outline: none;
}

.btn-reset {
  width: 100%;
  padding: 10px;
  border: none;
  border-radius: 5px;
  background-color: blueviolet;
  color: #ffffff;
  font-weight: bold;
  cursor: pointer;
}

.btn-login {
  padding: 10px;
  border: none;
  background-color: transparent;
  color: blueviolet;
  font-weight: bold;
  cursor: pointer;
}

.login-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notification {
  position: fixed;
  bottom: 100px;
  left: 50%;
  transform: translateX(-50%);
  color: red;
}
</style>
