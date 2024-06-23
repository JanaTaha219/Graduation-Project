<template>
  <div>
    <h2
      v-if="followingPageData.data && followingPageData.data.saved.length > 0"
      class="title"
    >
      Saved Notes
    </h2>
    <ul
      class="note-list"
      v-if="followingPageData.data && followingPageData.data.saved.length > 0"
    >
      <li
        v-for="(note, index) in followingPageData.data.saved"
        :key="index"
        class="note-card"
      >
        <div class="note-details">
          <p>{{ note.user.uniqueName }}</p>

          <p>
            <a :href="note.url" target="_blank" class="url">{{ note.url }}</a>
          </p>
          <p>{{ note.text }}</p>
          <p>{{ note.userComment }}</p>
          <p>{{ note.creationTime }}</p>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";

const currentUserData = ref({});
const userDetails = ref(null);
const followingPageData = ref({ notes: [] });

const fetchUserData = async () => {
  try {
    // Fetch current user data
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

    const data = await response.json();
    currentUserData.value = data.data;
    userDetails.value = data.data;

    // Fetch user details using the constructed endpoint URL
    const followingPageUrl = `http://localhost:8081/api/v1/users/saved/${currentUserData.value.userName}`;
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
    console.log("Following Page Data:", followingPageData.value);
  } catch (error) {
    console.error("Error fetching data:", error);
  }
};

onMounted(fetchUserData);
</script>
<style>
.user-details {
  background-color: #f0f0f0;
  padding: 10px;
  margin-bottom: 20px;
}

.title-container {
  text-align: center;
  position: relative;
  margin-bottom: 20px;
}

.title {
  background-color: #ffffff;
  padding: 0 20px;
  position: relative;
  display: inline-block;
  z-index: 1;
}

.title-line {
  position: absolute;
  width: 100%;
  top: 50%;
  left: 0;
  height: 1px;
  background-color: #ddd;
  z-index: 0;
}

.note-list {
  list-style-type: none;
  padding: 0;
}

.note-card {
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 15px;
}

.note-card:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.note-details {
  padding: 15px;
}

.note-details p {
  margin: 0;
}

.note-details p:not(:last-child) {
  margin-bottom: 8px;
}

.note-details strong {
  font-weight: bold;
}

.note-details a {
  color: #007bff;
}

.url {
  text-align: right;
  margin-right: -20px;
}
</style>
