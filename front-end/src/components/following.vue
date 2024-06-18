<template>
  <div class="following-list">
    <ul>
      <li v-for="(note, index) in notes" :key="index" class="user-card">
        <div class="user-name">{{ note.uniqueName }}</div>
        <div class="user-email">{{ note.email }}</div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";

const notes = ref(null);
const countfol = ref({});

const getFollowing = async () => {
  try {
    const token = localStorage.getItem("token");
    if (!token) {
      throw new Error("Token not found");
    }

    const response = await fetch(
      "http://localhost:8081/api/v1/users/currentUser",
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      }
    );

    if (!response.ok) {
      throw new Error("Failed to fetch current user data");
    }

    const data = await response.json().catch((err) => {
      console.error("Error parsing JSON:", err);
      throw new Error(`Response is not valid JSON: ${response.statusText}`);
    });

    countfol.value = data.data;

    const userEndpoint = `http://localhost:8081/api/v1/users/${countfol.value.userName}/following`;
    const userResponse = await fetch(userEndpoint, {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });

    if (!userResponse.ok) {
      throw new Error("Failed to fetch user details");
    }

    const userDetailsData = await userResponse.json().catch((err) => {
      console.error("Error parsing JSON:", err);
      throw new Error(`Response is not valid JSON: ${userResponse.statusText}`);
    });

    notes.value = userDetailsData;
    console.log("following", notes.value);
  } catch (error) {
    console.error("Error fetching user data:", error.message);
  }
};

onMounted(() => {
  getFollowing();
});
</script>

<style scoped>
.following-list {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

ul {
  list-style-type: none;
  padding: 0;
}

.user-card {
  padding: 10px;
  margin-bottom: 10px;
  background-color: #fff;
  border-radius: 5px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
}

.user-card:hover {
  transform: translateY(-5px);
}

.user-name {
  font-size: 1.2em;
  font-weight: bold;
  color: #333;
}

.user-email {
  font-size: 0.9em;
  color: #777;
}
</style>
