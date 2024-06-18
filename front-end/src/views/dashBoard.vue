<template>
  <div id="dashboard">
    <nav>
      <div class="nav-links">
        <router-link to="/dashBoard/fl" class="nav-link">Following</router-link>
        <router-link to="/dashBoard/fy" class="nav-link">For You</router-link>
      </div>
      <div class="search-container">
        <input class="search-input" type="search" placeholder="Search" />
        <button class="search-button" type="submit">Search</button>
      </div>
    </nav>
    <div class="button-container">
      <button @click="dm" class="dashboard-button">
        <div>
          <icon-wrapper
            iconCode="uil:message"
            class="button-icon"
          ></icon-wrapper>
          Direct Message
        </div>
      </button>
      <button @click="saved" class="dashboard-button">
        <div>
          <icon-wrapper
            iconCode="dashicons:saved"
            class="button-icon"
          ></icon-wrapper>
          Saved
        </div>
      </button>
      <button @click="profile" class="dashboard-button">
        <div>
          <icon-wrapper
            iconCode="iconoir:profile-circle"
            class="button-icon"
          ></icon-wrapper>
          Profile
        </div>
      </button>
    </div>

    <router-view />
  </div>
</template>

<script setup lang="ts">
import iconWrapper from "../components/iconWrapper.vue";
import { useRouter } from "vue-router";

const router = useRouter();
const dm = () => {
  const token = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJqYW5hIiwicm9sZXMiOiJST0xFX1VTRVIiLCJpYXQiOjE3MTg3MTQ1MTAsImV4cCI6MTcxODgwMDkxMH0.t8FO8OBC8FtUlnTBfmp7FrniAEstMp46h6KISSup-fv5mcvxzlIsbXIjrltkkcg2";

  // Redirect to React app with token as query parameter
  window.location.href = `http://localhost:3000/?token=${encodeURIComponent(token)}`;
};
const profile = () => {
  router.push({ path: "/profile" });
};
const saved = () => {
  router.push({ path: "/saved" });
};
</script>

<style>
#dashboard {
  font-family: Arial, Helvetica, sans-serif;
  text-align: center;
  color: blueviolet;
}

nav {
  padding: 10px;
  background-color: #f8f9fa;
  border-bottom: 2px solid blueviolet;
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.nav-links {
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 1;
}

.nav-link {
  text-decoration: none;
  color: blueviolet;
  font-weight: bold;
  margin: 0 10px;
  transition: color 0.3s ease;
}

.nav-link:hover {
  color: rgb(216, 185, 246);
}

.router-link-exact-active {
  color: rgb(216, 185, 246);
}

.search-container {
  display: flex;
  align-items: center;
}
.search-input {
  padding: 8px;
  margin-right: 8px;
  border: 1px solid #ced4da;
  border-radius: 4px;
}

.search-button {
  padding: 8px 16px;
  background-color: #6c757d;
  border: none;
  border-radius: 4px;
  color: #fff;
  cursor: pointer;
}

.search-button:hover {
  background-color: #495057;
}

.button-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  margin-top: 20px;
}

.button-container button {
  margin-bottom: 10px;
}

.dashboard-button {
  border: none;
  background-color: #6c757d;
  color: #fff;
  border-radius: 5px;
  padding: 10px 20px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.dashboard-button:hover {
  background-color: #495057;
}

.button-icon {
  margin-right: 10px;
}
</style>
