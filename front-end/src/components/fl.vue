<template>
  <div class="container">
    <ul v-if="followingPageData.data" class="note-list">
      <li
        class="note-item"
        v-for="(note, index) in followingPageData.data.notes"
        :key="index"
      >
        <div class="note-content">
          <div class="note-header">
            <p class="note-username">{{ note.uniqueName }}</p>
          </div>
          <p class="note-text">{{ note.text }}</p>
          <p class="note-comment">note:{{ note.userComment }}</p>
          <a class="note-url" :href="note.url" target="_blank">{{
            note.url
          }}</a>
          {{ new Date(note.creationTime).toLocaleString() }}
        </div>
        <div class="note-actions">
          <icon-wrapper
            @click="like(note)"
            :iconCode="
              note.liked ? 'icon-park-solid:like' : 'solar:heart-linear'
            "
            class="button-icon"
          ></icon-wrapper>
          <icon-wrapper
            @click="save(note.id)"
            iconCode="mingcute:save-line"
            class="button-icon"
          ></icon-wrapper>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import iconWrapper from "../components/iconWrapper.vue";

const currentUserData = ref({});
const followingPageData = ref({ notes: [] });

const like = async (note) => {
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

    const likeResponse = await fetch(
      `http://localhost:8081/api/v1/users/${currentUserData.value.userName}/like/${note.id}`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      }
    );
    if (!likeResponse.ok) {
      throw new Error("Failed to like the note");
    }
    console.log(`User ${currentUserData.value.userName} liked note ${note.id}`);

    // Toggle the liked state
    note.liked = !note.liked;
  } catch (error) {
    console.error("Error liking the note:", error);
  }
};

const save = async (noteId) => {
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

    const saveResponse = await fetch(
      `http://localhost:8081/api/v1/users/${currentUserData.value.userName}/save/${noteId}`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      }
    );
    if (!saveResponse.ok) {
      throw new Error("Failed to save the note");
    }

    console.log(`User ${currentUserData.value.userName} saved note ${noteId}`);
  } catch (error) {
    console.error("Error saving the note:", error);
  }
};

const fetchUserData = async () => {
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

    const followingPageUrl = `http://localhost:8081/api/v1/users/${currentUserData.value.userName}/followingPage`;
    const followingPageResponse = await fetch(followingPageUrl, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });
    if (!followingPageResponse.ok) {
      throw new Error("Failed to fetch following page data");
    }
    followingPageData.value = await followingPageResponse.json();

    // Initialize the liked property for each note
    followingPageData.value.data.notes.forEach((note) => {
      note.liked = false;
    });
  } catch (error) {
    console.error("Error fetching data:", error);
  }
};

onMounted(fetchUserData);
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
  padding-left: 10px; /* Adjusted padding to move content closer to the left border */
  border: 1px solid #ddd;
  border-radius: 5px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease-in-out;
  display: flex;
  justify-content: space-between; /* Added to position content and actions */
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
  color: #007bff; /* Link color */
  text-decoration: underline; /* Underline link */
}

.note-time {
  font-size: 0.5em;
  color: #57ade6;
}

.note-actions {
  display: flex;
  gap: 10px;
  align-self: flex-start;
}

.button-icon {
  font-size: 1.5em;
  color: #555;
  cursor: pointer;
}

.button-icon:hover {
  color: #007bff;
}
</style>
