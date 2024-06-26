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
            <p class="note-number">{{ index + 1 }}-</p>
            <!-- Note number -->
            <p class="note-username" @click="goToUserProfile(note.uniqueName)">
              {{ note.uniqueName }}
            </p>
          </div>
          <p class="note-text">{{ note.text }}</p>
          <p class="note-comment">note: {{ note.userComment }}</p>
          <button @click="navigateToUrl(note.url)" class="note-url-button">
            Go to URL
          </button>
          {{ new Date(note.creationTime).toLocaleString() }}
        </div>
        <div class="note-actions">
          <icon-wrapper
            @click="toggleLike(note)"
            :iconCode="
              note.liked ? 'icon-park-solid:like' : 'solar:heart-linear'
            "
            class="button-icon"
          ></icon-wrapper>
          <icon-wrapper
            @click="toggleSave(note)"
            :iconCode="note.saved ? 'mingcute:save-fill' : 'mingcute:save-line'"
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
import { useRouter } from "vue-router";
const router = useRouter();

const currentUserData = ref({});
const followingPageData = ref({ notes: [] });

const toggleLike = async (note) => {
  try {
    const token = localStorage.getItem("token");
    if (!token) {
      throw new Error("Token not found");
    }

    const url = `http://localhost:8081/api/v1/users/${currentUserData.value.userName}/${
      note.liked ? "unlike" : "like"
    }/${note.id}`;
    const method = note.liked ? "DELETE" : "POST";

    const likeResponse = await fetch(url, {
      method: method,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });

    if (!likeResponse.ok) {
      throw new Error("Failed to like/unlike the note");
    }
    note.liked = !note.liked;
  } catch (error) {
    console.error("Error liking/unliking the note:", error);
  }
};

const toggleSave = async (note) => {
  try {
    const token = localStorage.getItem("token");
    if (!token) {
      throw new Error("Token not found");
    }

    const url = `http://localhost:8081/api/v1/users/${currentUserData.value.userName}/${
      note.saved ? "unsave" : "save"
    }/${note.id}`;
    const method = note.saved ? "DELETE" : "POST";

    const saveResponse = await fetch(url, {
      method: method,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });

    if (!saveResponse.ok) {
      throw new Error("Failed to save/unsave the note");
    }
    note.saved = !note.saved;
  } catch (error) {
    console.error("Error saving/unsaving the note:", error);
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

    followingPageData.value.data.notes.forEach((note) => {
      note.liked = note.likedByCurrentUser;
      note.saved = note.savedByCurrentUser;
    });
  } catch (error) {
    console.error("Error fetching data:", error);
  }
};

const goToUserProfile = (username) => {
  router.push({ name: "UserProfile", params: { username } });
};

const navigateToUrl = (url) => {
  if (!url.startsWith("http://") && !url.startsWith("https://")) {
    url = "http://" + url;
  }
  window.open(url, "_blank");
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
  margin-bottom: 10px;
}

.note-text {
  color: #666;
  margin-bottom: 10px;
}

.note-comment {
  color: #999;
  margin-bottom: 10px;
}

.note-url-button {
  color: #007bff;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0;
  margin-bottom: 10px;
}

.note-url-button:hover {
  text-decoration: none;
}

.note-actions {
  display: flex;
  align-items: center;
}

.button-icon {
  cursor: pointer;
  margin-right: 10px;
  color: #007bff;
}

.button-icon:hover {
  color: #0056b3;
}
</style>
