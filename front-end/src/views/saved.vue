<template>
  <button @click="home()">home</button>
  <div class="notes-section">
    <div class="container">
      <ul class="note-list">
        <li v-for="note in notes" :key="note.id" class="note-item">
          <div class="note-content">
            <div class="note-header">
              <!-- Removed note.uniqueName since it's not in the provided data -->
            </div>
            <p class="note-text">{{ note.uniqueName }}</p>
            <p class="note-text">{{ note.text }}</p>
            <p class="note-comment">{{ note.userComment }}</p>
            <a :href="note.url" target="_blank" class="note-url">{{
              note.url
            }}</a>
            <p class="note-time">
              {{ new Date(note.creationTime).toLocaleString() }}
            </p>
          </div>
          <div class="note-actions">
            <icon-wrapper
              @click="like(note)"
              iconCode="solar:heart-linear"
              class="button-icon"
            ></icon-wrapper>
            <icon-wrapper
              iconCode="mingcute:save-line"
              class="button-icon"
            ></icon-wrapper>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import iconWrapper from "../components/iconWrapper.vue";
import { useRouter } from "vue-router";

const router = useRouter();
const notes = ref([]);
const home = () => {
  router.push({ path: "/dashboard" });
};
const getNotes = async () => {
  try {
    const token = localStorage.getItem("token");
    if (!token) throw new Error("Token not found");

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

    if (!response.ok) throw new Error("Failed to fetch current user data");

    const data = await response.json();
    const userEndpoint = `http://localhost:8081/api/v1/users/saved/${data.data.userName}`;
    const userResponse = await fetch(userEndpoint, {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });

    if (!userResponse.ok) throw new Error("Failed to fetch user details");

    const userDetailsData = await userResponse.json();
    notes.value = userDetailsData.data.saved;
    console.log(notes.value);
  } catch (error) {
    console.error("Error fetching user data:", error.message);
  }
};

onMounted(() => {
  getNotes();
});
</script>

<style scoped>
.profile {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
}

.user-info {
  background-color: #f8f8f8;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 600px;
  margin-bottom: 20px;
}

.user-info h1 {
  font-size: 32px;
  color: #333;
  margin-bottom: 20px;
  text-align: center;
}

.user-info ul {
  list-style-type: none;
  padding: 0;
  width: 100%;
}

.name-item {
  font-size: 30px;
}

.notes-section {
  width: 100%;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

.note-list {
  list-style-type: none;
  padding: 0;
}

.note-item {
  background-color: #ffffff;
  margin-bottom: 15px;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease-in-out;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.note-item:hover {
  transform: translateY(-5px);
}

.note-content {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.note-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.note-username {
  font-size: 1.2em;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.note-text {
  font-size: 1em;
  color: #555;
  margin-bottom: 10px;
}
.note-comment {
  font-size: 0.9em;
  color: #777;
  margin-bottom: 10px;
}

.note-url {
  font-size: 0.9em;
  color: #1e90ff;
  margin-bottom: 10px;
}

.note-time {
  font-size: 0.8em;
  color: #aaa;
}

.note-actions {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.button-icon {
  cursor: pointer;
  color: #ff6b6b;
  font-size: 1.2em;
  margin-top: 5px;
}

.dashboard-button {
  background-color: #ff6b6b;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.dashboard-button:hover {
  background-color: #e55a5a;
}
</style>