<template>
  <div id="dashboard">
    <nav>
      <div class="nav-links">
        <router-link to="/dashboard/fl" class="nav-link">Following</router-link>
        <router-link to="/dashboard/fy" class="nav-link">For You</router-link>
      </div>
      <div class="search-container">
        <input
          class="search-input"
          type="search"
          placeholder="Search"
          v-model="searchTerm"
        />
        <button class="search-button" @click="search">Search</button>
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

      <button @click="liked" class="dashboard-button">
        <div>
          <icon-wrapper
            iconCode="solar:like-broken"
            class="button-icon"
          ></icon-wrapper>
          Liked
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

    <!-- Modal for Search Results -->
    <searchModal :isOpen="isModalOpen" @close="isModalOpen = false">
      <div class="results">
        <ul>
          <li v-for="note in notes" :key="note.id">
            <router-link
              :to="{
                name: 'UserProfile',
                params: { username: note.uniqueName },
              }"
            >
              <p><strong>Unique Name:</strong> {{ note.uniqueName }}</p>
            </router-link>
            <p class="note-email"><strong>Email:</strong> {{ note.email }}</p>
          </li>
        </ul>
      </div>
    </searchModal>

    <router-view />
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import iconWrapper from "../components/iconWrapper.vue";
import searchModal from "../components/searchModal.vue"; // Import Modal component

const router = useRouter();
const searchTerm = ref("");
const notes = ref([]);
const isModalOpen = ref(false); // State to control modal visibility

const dm = () => {
  router.push({ path: "/dm" });
};
const tryy = () => {
  router.push({ path: "/tryy" });
};
const profile = () => {
  router.push({ path: "/profile" });
};

const saved = () => {
  router.push({ path: "/saved" });
};

const liked = () => {
  router.push({ path: "/liked" });
};

const search = async () => {
  try {
    const token = localStorage.getItem("token");
    if (!token) throw new Error("Token not found");

    const response = await fetch(
      `http://localhost:8081/api/v1/users/like/${searchTerm.value}`,
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      }
    );

    if (!response.ok) throw new Error("Failed to fetch search results");

    const data = await response.json();
    notes.value = data;
    isModalOpen.value = true; // Open the modal to show results
  } catch (error) {
    console.error("Error fetching search results:", error.message);
  }
};
</script>

<style scoped>
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

.results {
  margin-top: 20px;
}

.results ul {
  list-style-type: none;
  padding: 0;
}

.results li {
  background-color: #ffffff;
  margin-bottom: 15px;
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  text-align: left; /* Align text to the left */
}

.note-email {
  font-size: 0.7em; /* Make email text smaller */
  color: #555; /* Change email text color to a lighter shade */
  margin-top: 5px; /* Add a small margin to the top */
}
</style>
