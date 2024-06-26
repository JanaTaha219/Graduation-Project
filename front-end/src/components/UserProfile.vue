<template>
  <button @click="home()">home</button>

  <div class="profile">
    <div class="user-info">
      <div v-if="userDetails">
        <ul class="note-list">
          <li class="name-item">{{ userDetails.uniqueName }}</li>
          <li class="email-item">
            <strong>Email:</strong> {{ userDetails.email }}
          </li>
          <li class="bio-item"><strong>Bio:</strong> {{ userDetails.bio }}</li>
          <li class="birthdate-item">
            <strong>Birthdate:</strong> {{ userDetails.birthDate }}
          </li>
        </ul>

        <button @click="getFollowing()">
          Following: {{ userDetails3?.data?.following }}
        </button>
        <button @click="getFollowers()">
          Followers: {{ userDetails2?.data?.followers }}
        </button>
        <button v-if="!userDetails.isFollowing" @click="followUser">
          Follow
        </button>
        <button v-else @click="unfollowUser">Unfollow</button>

        <div class="notes-section">
          <div class="container">
            <ul class="note-list">
              <li v-for="note in notes" :key="note.id" class="note-item">
                <div class="note-content">
                  <div class="note-header">
                    <p class="note-username">{{ note.uniqueName }}</p>
                  </div>
                  <p class="note-text">{{ note.text }}</p>
                  <p class="note-comment">{{ note.userComment }}</p>
                  <button
                    @click="navigateToUrl(note.url)"
                    class="note-url-button"
                  >
                    Go to URL
                  </button>
                  <p class="note-time">
                    {{ new Date(note.creationTime).toLocaleString() }}
                  </p>
                </div>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import iconWrapper from "../components/iconWrapper.vue";
const currentUserData = ref(" ");
import axios from "axios";

const route = useRoute();
const router = useRouter();
const followedUsers = ref(new Set());
const currentUsername = ref(null);
const home = () => {
  router.push({ path: "/dashboard" });
};
const navigateToUrl = (url) => {
  if (!url.startsWith("http://") && !url.startsWith("https://")) {
    url = "http://" + url;
  }
  window.open(url, "_blank");
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
const fetchCurrentUser = async () => {
  try {
    const token = localStorage.getItem("token");
    if (!token) throw new Error("Token not found");

    const response = await fetch(
      "http://localhost:8081/api/v1/users/currentUser",
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      }
    );

    if (!response.ok) throw new Error("Failed to fetch current user data");

    const data = await response.json();
    currentUsername.value = data.username; // Assuming API response has a 'username' field
  } catch (error) {
    console.error("Error fetching current user data:", error.message);
  }
};

const followUser = async () => {
  try {
    const profileUsername = route.params.username;
    const token = localStorage.getItem("token");
    if (!token) throw new Error("Token not found");

    const response = await fetch(
      `http://localhost:8081/api/v1/users/${currentUsername.value}/follow/${profileUsername}`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      }
    );

    if (!response.ok) throw new Error("Failed to follow user");

    // Update userDetails to reflect following status
    userDetails.value.isFollowing = true;
  } catch (error) {
    console.error("Error following user:", error.message);
  }
};

const unfollowUser = async () => {
  try {
    const profileUsername = route.params.username;
    const token = localStorage.getItem("token");
    if (!token) throw new Error("Token not found");

    const response = await fetch(
      `http://localhost:8081/api/v1/users/${currentUsername.value}/unfollow/${profileUsername}`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      }
    );

    if (!response.ok) throw new Error("Failed to unfollow user");

    // Update userDetails to reflect unfollowing status
    userDetails.value.isFollowing = false;
  } catch (error) {
    console.error("Error unfollowing user:", error.message);
  }
};

const userDetails = ref(null);
const userDetails2 = ref(null);
const userDetails3 = ref(null);
const notes = ref([]);

const fetchUserData = async () => {
  try {
    const username = route.params.username;
    const token = localStorage.getItem("token");
    if (!token) throw new Error("Token not found");

    const response = await fetch(
      `http://localhost:8081/api/v1/users/${username}`,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      }
    );

    if (!response.ok) throw new Error("Failed to fetch user data");

    const data = await response.json();
    userDetails.value = data;
  } catch (error) {
    console.error("Error fetching user data:", error.message);
  }
};

const fetchFollowingCount = async () => {
  try {
    const username = route.params.username;
    const token = localStorage.getItem("token");
    if (!token) throw new Error("Token not found");

    const response = await fetch(
      `http://localhost:8081/api/v1/users/followingCount/${username}`,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      }
    );

    if (!response.ok) throw new Error("Failed to fetch following count");

    const data = await response.json();
    userDetails3.value = data;
  } catch (error) {
    console.error("Error fetching following count:", error.message);
  }
};

const fetchFollowersCount = async () => {
  try {
    const username = route.params.username;
    const token = localStorage.getItem("token");
    if (!token) throw new Error("Token not found");

    const response = await fetch(
      `http://localhost:8081/api/v1/users/followersCount/${username}`,
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      }
    );

    if (!response.ok) throw new Error("Failed to fetch followers count");

    const data = await response.json();
    userDetails2.value = data;
  } catch (error) {
    console.error("Error fetching followers count:", error.message);
  }
};

const getNotes = async () => {
  try {
    const username = route.params.username;
    const token = localStorage.getItem("token");
    if (!token) throw new Error("Token not found");

    const userEndpoint = `http://localhost:8081/api/v1/users/${username}/notes`;
    const userResponse = await fetch(userEndpoint, {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });

    if (!userResponse.ok) throw new Error("Failed to fetch user notes");

    const userDetailsData = await userResponse.json();
    notes.value = userDetailsData;
  } catch (error) {
    console.error("Error fetching user notes:", error.message);
  }
};

const getFollowers = () => {
  router.push({ path: "/follower" });
};

const getFollowing = () => {
  router.push({ path: "/following" });
};

onMounted(() => {
  fetchCurrentUser();

  fetchUserData();
  fetchFollowingCount();
  fetchFollowersCount();
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
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  left: -5px;
}

.dashboard-button:hover {
  background-color: #e55a5a;
}
</style>
