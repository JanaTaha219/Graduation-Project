<template>
  <div class="login-container">
    <form class="login-form" @submit.prevent="signup">
      <h1 class="login-heading">SIGNUP</h1>
      <h2 class="sub-heading">Enter your details below to continue</h2>

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
        <input
          type="text"
          v-model="username"
          class="input-field"
          placeholder="Username"
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
        <input
          type="date"
          v-model="birthday"
          class="input-field"
          placeholder="Birthday"
          required
        />
      </div>

      <div class="form-group">
        <input
          type="text"
          v-model="bio"
          class="input-field"
          placeholder="Bio"
        />
      </div>

      <div class="form-group">
        <button type="submit" class="btn-login">SIGN UP</button>
      </div>

      <div class="signup-option">
        <p>
          Already have an account?
          <router-link to="/login" class="btn-signup">LOG IN</router-link>
        </p>
      </div>
    </form>
    <div class="notification" v-if="notificationVisible">
      Invalid credentials. Please try again.
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";

const email = ref("");
const username = ref("");
const password = ref("");
const birthday = ref("");
const bio = ref("");
const notificationVisible = ref(false);
const router = useRouter();

const signup = async () => {
  try {
    const response = await fetch("http://localhost:8081/api/v1/users", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email: email.value,
        uniqueName: username.value,
        password: password.value,
        birthDate: birthday.value,
        bio: bio.value,
      }),
    });
    if (!response.ok) {
      throw new Error("Invalid credentials");
    }
    const data = await response.json();
    const token = data.data.token;
    console.log("Token:", token);
    localStorage.setItem("token", token);
    router.push("/dashboard"); // Redirect to dashboard after successful signup
  } catch (error) {
    console.error("Signup failed:", error.message);
    notificationVisible.value = true; // Show the notification
  }
};
</script>

<style scoped>
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
  color: #333333; /* Heading color */
}

.sub-heading {
  font-size: 16px;
  margin-bottom: 20px;
  color: #666666; /* Subheading color */
}

.form-group {
  margin-bottom: 15px;
}

.input-field {
  width: 100%;
  padding: 10px;
  border: 1px solid #cccccc; /* Input border color */
  border-radius: 5px;
  outline: none;
}

.btn-login {
  width: 100%;
  padding: 10px;
  border: none;
  border-radius: 5px;
  background-color: blueviolet; /* Button background color */
  color: #ffffff; /* Button text color */
  font-weight: bold;
  cursor: pointer;
}

.btn-signup {
  padding: 10px;
  border: none;
  background-color: transparent;
  color: blueviolet; /* Sign up button color */
  font-weight: bold;
  cursor: pointer;
}

.signup-option {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
