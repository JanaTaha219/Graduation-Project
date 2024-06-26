<template>
  <button @click="home()">home</button>
  <div class="profile">
    <div class="user-info">
      <div v-if="userDetails">
        <ul class="note-list">
          <li class="name-item">{{ currentUserData.userName }}</li>
          <li class="email-item">
            <strong>Email:</strong> {{ userDetails.email }}
          </li>
          <li class="bio-item"><strong>Bio:</strong> {{ userDetails.bio }}</li>
          <li class="birthdate-item">
            <strong>Birthdate:</strong> {{ userDetails.birthDate }}
          </li>
        </ul>
        <button @click="getFollowing()">
          Following: {{ userDetails3?.data?.following ?? 0 }}
        </button>
        <button @click="getFollowers()">
          Followers: {{ userDetails2?.data?.followers ?? 0 }}
        </button>
        <button @click="showEditProfile = true" class="edit-profile-button">
          Edit Profile
        </button>

        <modalComp
          :modalActive="showEditProfile"
          @close="showEditProfile = false"
        >
          <div class="modal-content">
            <h2>Edit Profile</h2>
            <form @submit.prevent="editProfile">
              <label for="email">Email:</label>
              <input type="email" id="email" v-model="editForm.email" />

              <label for="bio">Bio:</label>
              <input type="text" id="bio" v-model="editForm.bio" />

              <label for="birthdate">Birthdate:</label>
              <input type="date" id="birthdate" v-model="editForm.birthDate" />

              <button type="submit" class="save-button">Save Changes</button>
            </form>
          </div>
        </modalComp>
      </div>
    </div>
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
    <button @click="logout" class="dashboard-button">
      <div>
        <icon-wrapper
          iconCode="ic:twotone-logout"
          class="button-icon"
        ></icon-wrapper>
        Logout
      </div>
    </button>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import iconWrapper from "../components/iconWrapper.vue";
import modalComp from "../components/modal.vue";
import { useRouter } from "vue-router";

const router = useRouter();
const home = async () => {
  router.push({ path: "/dashBoard" });
};
const currentUserData = ref({});
const userDetails2 = ref(null);
const userDetails3 = ref(null);
const userDetails = ref(null);
const notes = ref([]);
const showEditProfile = ref(false);
const editForm = ref({
  email: "",
  bio: "",
  birthDate: "",
});

const getFollowers = async () => {
  router.push({ path: "/follower" });
};

const getFollowing = async () => {
  router.push({ path: "/following" });
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
    const userEndpoint = `http://localhost:8081/api/v1/users/${data.data.userName}/notes`;
    const userResponse = await fetch(userEndpoint, {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });

    if (!userResponse.ok) throw new Error("Failed to fetch user details");

    const userDetailsData = await userResponse.json();
    notes.value = userDetailsData;
  } catch (error) {
    console.error("Error fetching user data:", error.message);
  }
};

const numberOffollowing = async () => {
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

    const data2 = await response.json();
    const userEndpoint3 = `http://localhost:8081/api/v1/users/followingCount/${data2.data.userName}`;
    const userResponse3 = await fetch(userEndpoint3, {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });

    if (!userResponse3.ok) throw new Error("Failed to fetch user details");

    const userDetailsData3 = await userResponse3.json();
    userDetails3.value = userDetailsData3;
  } catch (error) {
    console.error("Error fetching user data:", error.message);
  }
};

const numberOffollowers = async () => {
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

    const data2 = await response.json();
    const userEndpoint2 = `http://localhost:8081/api/v1/users/followersCount/${data2.data.userName}`;
    const userResponse2 = await fetch(userEndpoint2, {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    });

    if (!userResponse2.ok) throw new Error("Failed to fetch user details");

    const userDetailsData2 = await userResponse2.json();
    userDetails2.value = userDetailsData2;
  } catch (error) {
    console.error("Error fetching user data:", error.message);
  }
};

const logout = () => {
  localStorage.removeItem("token");
  router.push({ path: "/" });
};

const fetchUserData = async () => {
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
    currentUserData.value = data.data;

    const userEndpoint = `http://localhost:8081/api/v1/users/${currentUserData.value.userName}`;
    const userResponse = await fetch(userEndpoint, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!userResponse.ok) throw new Error("Failed to fetch user details");
    console.log("user", currentUserData.value.userName);
    const userDetailsData = await userResponse.json();
    userDetails.value = userDetailsData;

    // Populate the edit form with the current user details
    editForm.value.email = userDetailsData.email;
    editForm.value.bio = userDetailsData.bio;
    editForm.value.birthDate = userDetailsData.birthDate;
  } catch (error) {
    console.error("Error fetching user data:", error.message);
  }
};

const editProfile = async () => {
  try {
    const token = localStorage.getItem("token");
    if (!token) throw new Error("Token not found");

    const response = await fetch(
      `http://localhost:8081/api/v1/users/${currentUserData.value.userName}`,
      {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(editForm.value),
      }
    );

    if (!response.ok) throw new Error("Failed to update user profile");

    const updatedUserData = await response.json();
    userDetails.value = updatedUserData;
    currentUserData.value.email = updatedUserData.email;
    currentUserData.value.bio = updatedUserData.bio;
    currentUserData.value.birthDate = updatedUserData.birthDate;
    showEditProfile.value = false;
  } catch (error) {
    console.error("Error updating user profile:", error.message);
  }
};

onMounted(() => {
  fetchUserData();
  getNotes();
  numberOffollowing();
  numberOffollowers();
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
