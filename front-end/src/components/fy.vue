<template>
  <div class="container">
    <p class="selected-method">{{ selectedMethod }}</p>
    <div class="filter-container">
      <div>
        <button @click="filterNotes(0, 0.2)" class="filter-button">low</button>
        <button @click="filterNotes(0.2, 0.7)" class="filter-button">
          medium
        </button>
        <button @click="filterNotes(0.7, 1)" class="filter-button">high</button>
        <button @click="reset()" class="filter-button">reset</button>
      </div>
    </div>
    <ul class="note-list">
      <li class="note-item" v-for="note in filteredNotes" :key="note.id">
        <div class="note-content">
          <div class="note-header">
            <p class="note-username" @click="goToUserProfile(note.user_id)">
              {{ note.user_id }}
            </p>
          </div>
          <p class="note-text">{{ note.text }}</p>
          <p class="note-comment">{{ note.comment }}</p>
          <button @click="navigateToUrl(note.url)" class="note-url-button">
            Go to URL
          </button>
          <p class="note-time">{{ note.creation_time }}</p>
          <p>{{ note.similarity }}</p>
        </div>
        <div class="note-actions">
          <div class="icon-group">
            <IconWrapper
              @click="toggleLike(note.id)"
              :iconCode="
                likedNotes.has(note.id)
                  ? 'solar:heart-bold'
                  : 'solar:heart-linear'
              "
              class="button-icon"
            ></IconWrapper>
            <IconWrapper
              @click="toggleSave(note.id)"
              :iconCode="
                savedNotes.has(note.id)
                  ? 'mingcute:save-fill'
                  : 'mingcute:save-line'
              "
              class="button-icon"
            ></IconWrapper>
          </div>
          <button
            v-if="!followedUsers.has(note.user_id)"
            @click="follow(note.user_id)"
            class="dashboard-button"
          >
            <div>
              <IconWrapper iconCode="mdi:add" class="button-icon"></IconWrapper>
            </div>
          </button>
          <button class="note-button" @click="tryy(note.text)">
            Show Modal
          </button>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import IconWrapper from "../components/iconWrapper.vue";
import { useRouter } from "vue-router";

const originalNotes = ref([]);
const filteredNotes = ref([]);
const likedNotes = ref(new Set());
const savedNotes = ref(new Set());
const followedUsers = ref(new Set());
const selectedMethod = ref("");
const router = useRouter();
const navigateToUrl = (url) => {
  if (!url.startsWith("http://") && !url.startsWith("https://")) {
    url = "http://" + url;
  }
  window.open(url, "_blank");
};
const goToUserProfile = (username) => {
  router.push({ name: "UserProfile", params: { username } });
};

const reset = async () => {
  getRecommendationsCosine();
};

const getRecommendationsCosine = async () => {
  try {
    const token = localStorage.getItem("token");
    if (!token) {
      throw new Error("Token not found");
    }
    const currentUserResponse = await fetch(
      "http://localhost:8081/api/v1/users/currentUser",
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      }
    );
    if (!currentUserResponse.ok) {
      throw new Error("Failed to fetch current user data");
    }
    const currentUserDataJson = await currentUserResponse.json();
    const currentUserData = currentUserDataJson.data;
    const response = await axios.post("http://localhost:5000/api/predict", {
      username: currentUserData.userName,
      numberOfPosts: 100,
    });
    originalNotes.value = response.data;
    filteredNotes.value = response.data;
  } catch (error) {
    console.error("Error fetching recommendations:", error);
  }
};

const filterNotes = (minSimilarity, maxSimilarity) => {
  minSimilarity = parseFloat(minSimilarity);
  maxSimilarity = parseFloat(maxSimilarity);
  filteredNotes.value = originalNotes.value.filter((note) => {
    const noteSimilarity = parseFloat(note.similarity);
    return noteSimilarity >= minSimilarity && noteSimilarity <= maxSimilarity;
  });
};

const toggleLike = async (noteId) => {
  try {
    const token = localStorage.getItem("token");
    if (!token) {
      throw new Error("Token not found");
    }
    const currentUserResponse = await fetch(
      "http://localhost:8081/api/v1/users/currentUser",
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      }
    );
    if (!currentUserResponse.ok) {
      throw new Error("Failed to fetch current user data");
    }
    const currentUserDataJson = await currentUserResponse.json();
    const currentUserData = currentUserDataJson.data;
    if (likedNotes.value.has(noteId)) {
      await axios.delete(
        `http://localhost:8081/api/v1/users/${currentUserData.userName}/unlike/${noteId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      likedNotes.value.delete(noteId);
    } else {
      await axios.post(
        `http://localhost:8081/api/v1/users/${currentUserData.userName}/like/${noteId}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      likedNotes.value.add(noteId);
    }
  } catch (error) {
    console.error("Error toggling like:", error);
  }
};

const toggleSave = async (noteId) => {
  try {
    const token = localStorage.getItem("token");
    if (!token) {
      throw new Error("Token not found");
    }
    const currentUserResponse = await fetch(
      "http://localhost:8081/api/v1/users/currentUser",
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      }
    );
    if (!currentUserResponse.ok) {
      throw new Error("Failed to fetch current user data");
    }
    const currentUserDataJson = await currentUserResponse.json();
    const currentUserData = currentUserDataJson.data;
    if (savedNotes.value.has(noteId)) {
      await axios.delete(
        `http://localhost:8081/api/v1/users/${currentUserData.userName}/unsave/${noteId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      savedNotes.value.delete(noteId);
    } else {
      await axios.post(
        `http://localhost:8081/api/v1/users/${currentUserData.userName}/save/${noteId}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      savedNotes.value.add(noteId);
    }
  } catch (error) {
    console.error("Error toggling save:", error);
  }
};

const follow = async (userIdB) => {
  try {
    const token = localStorage.getItem("token");
    if (!token) {
      throw new Error("Token not found");
    }
    const currentUserResponse = await fetch(
      "http://localhost:8081/api/v1/users/currentUser",
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      }
    );
    if (!currentUserResponse.ok) {
      throw new Error("Failed to fetch current user data");
    }
    const currentUserDataJson = await currentUserResponse.json();
    const currentUserData = currentUserDataJson.data;
    const response = await axios.post(
      `http://localhost:8081/api/v1/users/${currentUserData.userName}/follow/${userIdB}`,
      {},
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
    if (response.status === 200) {
      followedUsers.value.add(userIdB);
    } else {
      throw new Error("Failed to follow user");
    }
  } catch (error) {
    console.error("Error following user:", error);
  }
};

const tryy = (noteText) => {
  router.push({ path: "/tryy", query: { text: noteText } });
};

onMounted(() => {
  getRecommendationsCosine();
});
</script>

<style>
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
  padding-left: 10px;
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
  width: 100%;
}

.note-username {
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.note-text {
  font-size: 1.3em;
  margin-bottom: 10px;
  color: #555;
}

.note-comment {
  font-style: italic;
  margin-bottom: 10px;
  color: #555;
}

.note-url {
  font-size: 0.9em;
  color: #007bff;
  text-decoration: underline;
}

.note-time {
  font-size: 0.5em;
  color: #57ade6;
}

.note-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-self: flex-start;
}

.icon-group {
  display: flex;
  gap: 10px;
}

.button-icon {
  font-size: 1.5em;
  color: #555;
  cursor: pointer;
}

.button-icon:hover {
  color: #007bff;
}

.action-buttons {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.action-button {
  border: none;
  background-color: #6c757d;
  color: #fff;
  border-radius: 5px;
  padding: 10px 20px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin: 0 10px; /* Add some horizontal spacing between buttons */
}

.selected-method {
  margin-top: 10px;
  font-weight: bold;
  color: #555;
  margin-bottom: 70px;
}

.action-button:hover {
  background-color: #495057;
}

.note-button {
  border: none;
  background-color: #007bff;
  color: #fff;
  border-radius: 5px;
  padding: 10px 20px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.note-button:hover {
  background-color: #0056b3;
}

.filter-container {
  margin-top: 20px;
}

.threshold-input {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.filter-button {
  padding: 10px 20px;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin-left: 10px;
}
</style>
