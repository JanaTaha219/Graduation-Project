<template>
  <div>
    <div class="action-buttons">
      <button class="action-button" @click="getRecommendationsCosine()">
        cosine
      </button>
      <button class="action-button" @click="getRecommendationsEu()">
        euclidean
      </button>
      <button class="action-button" @click="getRecommendationsDot()">
        dot product
      </button>
      <!-- Display selected method here -->
    </div>
    <p class="selected-method">{{ selectedMethod }}</p>
    <div class="filter-container">
      <input
        type="number"
        v-model="similarityThreshold"
        placeholder="Enter similarity threshold"
        class="threshold-input"
      />
      <button @click="filterNotesBySimilarity" class="filter-button">
        Filter
      </button>
    </div>

    <ul class="note-list">
      <li class="note-item" v-for="note in notes" :key="note.id">
        <div class="note-content">
          <div class="note-header">
            <p class="note-username">{{ note.user_id }}</p>
          </div>
          <p class="note-text">{{ note.text }}</p>
          <p class="note-comment">{{ note.comment }}</p>
          <a class="note-url" :href="note.url" target="_blank">{{
            note.url
          }}</a>
          <p class="note-time">{{ note.creation_time }}</p>
          <p>{{ note.similarity }}</p>
        </div>
        <div class="note-actions">
          <div class="icon-group">
            <iconWrapper
              @click="toggleLike(note.id)"
              :iconCode="
                likedNotes.has(note.id)
                  ? 'solar:heart-bold'
                  : 'solar:heart-linear'
              "
              class="button-icon"
            ></iconWrapper>
            <iconWrapper
              @click="toggleSave(note.id)"
              :iconCode="
                savedNotes.has(note.id)
                  ? 'mingcute:save-fill'
                  : 'mingcute:save-line'
              "
              class="button-icon"
            ></iconWrapper>
          </div>
          <button
            v-if="!followedUsers.has(note.user_id)"
            @click="follow(note.user_id)"
            class="dashboard-button"
          >
            <div>
              <icon-wrapper
                iconCode="mdi:add"
                class="button-icon"
              ></icon-wrapper>
            </div>
          </button>
          <button class="note-button" @click="showModal(note)">
            Show Modal
          </button>
        </div>
      </li>
    </ul>
    <NoteModal
      :modalActive="isModalActive"
      :note="currentNote"
      @close="isModalActive = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import iconWrapper from "../components/iconWrapper.vue";
import NoteModal from "../components/modalProfile.vue"; // Import the modal component
const similarityThreshold = ref(null); // Store the similarity threshold entered by the user

const clearThreshold = () => {
  similarityThreshold.value = null; // Clear the similarity threshold
};
const currentNote = ref(null);

const numberOfPosts = ref(100);
const notes = ref([]);
const currentUserId = ref(" ");
const currentUsername = ref(null);
const currentUserData = ref(" ");
const likedNotes = ref(new Set());
const savedNotes = ref(new Set());
const followedUsers = ref(new Set());
const selectedMethod = ref("");
const selectedMethod2 = ref("");
const isModalActive = ref(false);
const filterNotesBySimilarity = () => {
  if (similarityThreshold.value === null || similarityThreshold.value === "") {
    // Check if the input is empty or null
    return;
  }

  // Convert the input to a number
  const threshold = parseFloat(similarityThreshold.value);

  // Filter the notes based on the similarity threshold
  notes.value = notes.value.filter(
    (note) => parseFloat(note.similarity) >= threshold
  );
};
const showModal = (note) => {
  currentNote.value = note;
  isModalActive.value = true;
};
const rake = async () => {
  selectedMethod2.value = "rake";

  try {
    const token = localStorage.getItem("token");
    if (!token) {
      throw new Error("Token not found");
    }
    console.log("Token:", token);

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
    currentUserData.value = currentUserDataJson.data;
    console.log("text", currentNote.value.text);

    const response = await axios.post("http://localhost:5000/api/text/rake", {
      text: currentNote.value.text,
    });

    notes.value = response.data;
    console.log("rake", notes.value);
  } catch (error) {
    console.error("Error fetching recommendations:", error);
  }
};

const keybert = () => {
  selectedMethod2.value = "keybert";
};

const getRecommendationsCosine = async () => {
  selectedMethod.value = "cosine";
  clearThreshold();

  try {
    const token = localStorage.getItem("token");
    if (!token) {
      throw new Error("Token not found");
    }
    console.log("Token:", token);

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
    currentUserData.value = currentUserDataJson.data;
    const response = await axios.post("http://localhost:5000/api/predict", {
      username: currentUserData.value.userName,
      numberOfPosts: 100,
    });

    notes.value = response.data;
    console.log("heelo", notes.value);
  } catch (error) {
    console.error("Error fetching recommendations:", error);
  }
};
const getRecommendationsEu = async () => {
  selectedMethod.value = "euclidean Distances";
  clearThreshold();

  try {
    const token = localStorage.getItem("token");
    if (!token) {
      throw new Error("Token not found");
    }
    console.log("Token:", token);

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
    currentUserData.value = currentUserDataJson.data;
    const response = await axios.post(
      "http://localhost:5000/api/predict/euclideanDistances",
      {
        username: currentUserData.value.userName,
        numberOfPosts: 100,
      }
    );

    notes.value = response.data;
    console.log("eu", notes.value);
  } catch (error) {
    console.error("Error fetching recommendations:", error);
  }
};
const getRecommendationsDot = async () => {
  selectedMethod.value = "dot product";
  clearThreshold();

  try {
    const token = localStorage.getItem("token");
    if (!token) {
      throw new Error("Token not found");
    }
    console.log("Token:", token);

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
    currentUserData.value = currentUserDataJson.data;
    const response = await axios.post(
      "http://localhost:5000/api/predict/dotProduct",
      {
        username: currentUserData.value.userName,
        numberOfPosts: 100,
      }
    );

    notes.value = response.data;
    console.log("dot", notes.value);
  } catch (error) {
    console.error("Error fetching recommendations:", error);
  }
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
    currentUserData.value = currentUserDataJson.data;

    if (likedNotes.value.has(noteId)) {
      // Unlike the note
      await axios.delete(
        `http://localhost:8081/api/v1/users/${currentUserData.value.userName}/unlike/${noteId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      likedNotes.value.delete(noteId);
    } else {
      // Like the note
      await axios.post(
        `http://localhost:8081/api/v1/users/${currentUserData.value.userName}/like/${noteId}`,
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
    console.log(error);
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
    currentUserData.value = currentUserDataJson.data;

    if (savedNotes.value.has(noteId)) {
      // Unlike the note
      await axios.delete(
        `http://localhost:8081/api/v1/users/${currentUserData.value.userName}/unsave/${noteId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      savedNotes.value.delete(noteId);
    } else {
      // Like the note
      await axios.post(
        `http://localhost:8081/api/v1/users/${currentUserData.value.userName}/save/${noteId}`,
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
    console.log(error);
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
    currentUserData.value = currentUserDataJson.data;

    const response = await axios.post(
      `http://localhost:8081/api/v1/users/${currentUserData.value.userName}/follow/${userIdB}`,
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

onMounted(() => {
  getRecommendationsCosine();
});
</script>

<style scoped>
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
