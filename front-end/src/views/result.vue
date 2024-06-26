<template>
  <div class="quiz-result">
    <button class="home-button" @click="home()">Home</button>
    <h1>Quiz Result</h1>
    <p class="score">Score: {{ score }}/5</p>

    <div v-if="score < 3" class="message study-harder">
      <p>Study harder!</p>
    </div>
    <div v-else class="message good-job">
      <p>Good job!</p>
    </div>
    <p class="subject">subject:{{ subject }}</p>

    <div v-if="unfollowedUsers.length" class="unfollowed-users-section">
      <h4>Users you may want to follow:</h4>
      <div
        v-for="user in unfollowedUsers"
        :key="user.username"
        class="user-card"
      >
        <div class="card-content">
          <p>
            <strong>{{ user.username }}</strong>
          </p>
          <p>Mark: {{ user.mark }}</p>
          <button @click="toggleFollow(user)" class="follow-button">
            {{ user.isFollowing ? "Unfollow" : "Follow" }}
          </button>
        </div>
      </div>
    </div>

    <div v-if="score < 3 && videos.length" class="videos-section">
      <h4>To study more about this topic, watch these videos:</h4>
      <div v-for="video in videos" :key="video.id.videoId" class="video">
        <iframe
          width="100%"
          height="200"
          :src="'https://www.youtube.com/embed/' + video.id.videoId"
          frameborder="0"
          allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture"
          allowfullscreen
        ></iframe>
        <p class="video-title">{{ video.snippet.title }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watchEffect, onMounted } from "vue";
import axios from "axios";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();

const home = async () => {
  router.push({ path: "/dashBoard" });
};

const score = ref(route.params.score);
const subject = ref(route.params.subject);
const currentUserData = ref({});
const questions = ref(route.params.questions);
const videos = ref([]);
const unfollowedUsers = ref([]);

// Function to fetch YouTube videos based on a query
const fetchYouTubeVideos = async () => {
  const apiKey = "AIzaSyAelMhgbXmlE2qLhLSWAb1syefc_ZG1WLE";
  const maxResults = 5;
  const query = subject.value;

  const url = `https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&q=${query}&maxResults=${maxResults}&key=${apiKey}`;

  try {
    const response = await axios.get(url);
    videos.value = response.data.items;
  } catch (error) {
    console.error("Error fetching YouTube videos:", error);
    videos.value = [];
  }
};

// Function to add mark to the backend
const addMark = async (user_id, mark, quiz_type) => {
  try {
    const token = localStorage.getItem("token");
    if (!token) {
      throw new Error("Token not found");
    }

    await axios.post(
      "http://localhost:5000/add_mark",
      {
        user_id: user_id,
        quiz_type: quiz_type,
        mark: mark,
      },
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      }
    );
  } catch (error) {
    console.error("Error adding mark:", error.message);
  }
};

// Fetch current user data on component mount
const fetchUserData = async () => {
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

    const data = await response.json();
    currentUserData.value = data.data;
    addMark(currentUserData.value.userName, score.value, subject.value);
    fetchUnfollowedUsers(currentUserData.value.userName, subject.value);
  } catch (error) {
    console.error("Error fetching user data:", error.message);
  }
};

// Fetch unfollowed users with average score above 2
const fetchUnfollowedUsers = async (user_unique_name, quiz_type) => {
  try {
    const token = localStorage.getItem("token");
    if (!token) {
      throw new Error("Token not found");
    }

    const response = await axios.post(
      "http://localhost:5000/get_unfollowed_users",
      {
        quiz_type: quiz_type,
        user_unique_name: user_unique_name,
      },
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      }
    );

    unfollowedUsers.value = response.data.map((user) => ({
      username: user.username,
      mark: user.mark,
      isFollowing: false, // default state
    }));
  } catch (error) {
    console.error("Error fetching unfollowed users:", error.message);
  }
};

// Function to toggle follow/unfollow
const toggleFollow = async (user) => {
  try {
    const token = localStorage.getItem("token");
    if (!token) {
      throw new Error("Token not found");
    }

    const url = user.isFollowing
      ? "http://localhost:5000/unfollow_user"
      : "http://localhost:5000/follow_user";

    await axios.post(
      url,
      {
        user_id: currentUserData.value.userName,
        follow_user_id: user.username,
      },
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      }
    );

    user.isFollowing = !user.isFollowing;
  } catch (error) {
    console.error("Error toggling follow:", error.message);
  }
};

// Watch for changes in subject and fetch YouTube videos accordingly
watchEffect(() => {
  if (subject.value) {
    fetchYouTubeVideos();
  }
});

// On component mount, fetch user data then add mark
onMounted(async () => {
  await fetchUserData();
});
</script>

<style scoped>
.quiz-result {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  text-align: center;
  font-family: Arial, sans-serif;
}

.home-button {
  margin-bottom: 20px;
  padding: 10px 20px;
  font-size: 16px;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.home-button:hover {
  background-color: #45a049;
}

h1 {
  font-size: 2em;
  margin-bottom: 20px;
}

.message {
  margin: 20px 0;
  padding: 10px;
  border-radius: 5px;
}

.good-job {
  background-color: #ccffcc;
  color: #006600;
}

.score {
  font-size: 1.2em;
  margin: 10px 0;
  background-color: #ffcccc;
  color: #cc0000;
}

.videos-section {
  margin-top: 30px;
}

.video {
  margin-bottom: 20px;
}

.video-title {
  margin-top: 10px;
  font-size: 1em;
  color: #333;
}

.unfollowed-users-section {
  margin-top: 30px;
}

.user-card {
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 10px;
  padding: 15px;
  margin-bottom: 15px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-content {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.follow-button {
  padding: 5px 10px;
  font-size: 14px;
  cursor: pointer;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  transition: background-color 0.3s ease;
}

.follow-button:hover {
  background-color: #0056b3;
}
</style>