<template>
  <div v-if="show" class="modal-overlay" @click.self="close">
    <div class="modal-content">
      <button @click="close" class="close-button">&times;</button>
      <h3>Result: {{ score }}/5</h3>
      <p v-if="score >= 3">Good job!</p>
      <p v-else>Study harder!</p>

      <div v-if="quizData">
        <h4>Questions:</h4>
        <div
          v-for="(question, index) in quizData.quizzes[0].questions"
          :key="question.id"
        >
          <p>
            <strong>{{ index + 1 }}. {{ question.value }}</strong>
          </p>
          <p>Correct Answer: {{ question.answer }}</p>
        </div>
      </div>

      <div v-if="score < 3 && videos.length">
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
          <p>{{ video.snippet.title }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, ref, watch, onMounted } from "vue";
import axios from "axios";

const props = defineProps({
  show: {
    type: Boolean,
    default: false,
  },
  score: {
    type: Number,
    default: 0,
  },
  quizData: {
    type: Object,
    default: null,
  },
  selectedQuiz: {
    type: String,
    default: "",
  },
 
  subject: {
    type: String,
    default: "",
  },
});

const emit = defineEmits(["close"]);
const currentUserName=ref("")
const videos = ref([]);
const fetchUserName = async () => {
      const token = localStorage.getItem('token'); // Adjust this according to where you store the token
      if (!token) {
        console.error('No token found');
        return;
      }

      try {
        const response = await axios.get('http://localhost:8081/api/v1/users/currentUser', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        currentUserName.value = response.data.userName;
        
      } catch (error) {
        console.error('Error fetching user name:', error);
      }
    };
const fetchYouTubeVideos = async (query) => {
  const apiKey = "AIzaSyAelMhgbXmlE2qLhLSWAb1syefc_ZG1WLE"; // Replace with your actual API key
  const maxResults = 5; // Number of videos to fetch
  const url = `https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&q=${query}&maxResults=${maxResults}&key=${apiKey}`;

  try {
    console.log("Fetching YouTube videos for query:", query);

    const response = await axios.get(url);
    videos.value = response.data.items;
    console.log("Fetched videos:", videos.value);
  } catch (error) {
    console.error("Error fetching YouTube videos:", error);
    videos.value = [];
  }
};

const sendResultToApi = async () => {
  fetchUserName();
  const url = "http://localhost:5000/add_mark";
  const data = {
    user_id: currentUserName.value ,
    quiz_type: props.subject,
    mark: props.score,
  };
  console.log(data)

  try {
    const response = await axios.post(url, data);
    console.log("Result sent to API:", response.data);
  } catch (error) {
    console.error("Error sending result to API:", error);
  }
};

watch(
  () => props.show,
  async (newVal) => {
    if (newVal) {
      if (props.selectedQuiz) {
        await fetchYouTubeVideos(props.selectedQuiz);
      }
      await sendResultToApi();
    }
  }
);

const close = () => {
  emit("close");
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 10px;
  position: relative;
  max-height: 80vh;
  overflow-y: auto;
  width: 90%;
  max-width: 600px;
}

.close-button {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
}
</style>
