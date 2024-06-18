<template>
  <div v-if="modalActive" class="modal-overlay" @click.self="close">
    <div class="modal-content">
      <button @click="close" class="close-button">&times;</button>
      <div class="modal-buttons">
        <button
          @click="rake"
          :class="{ active: lastClickedButton === 'rake' }"
          class="modal-button"
        >
          rake
        </button>
        <button
          @click="keybert"
          :class="{ active: lastClickedButton === 'keybert' }"
          class="modal-button"
        >
          keybert
        </button>
      </div>
      <div v-if="lastClickedButton">
        <h3>{{ lastClickedButton }}</h3>
      </div>
      <div v-if="apiResult.length">
        <div class="result-buttons">
          <button
            v-for="result in apiResult"
            :key="result"
            class="result-button"
            @click="generateQuiz(result)"
          >
            {{ result }}
          </button>
        </div>
      </div>
      <div
        v-if="
          chatGPTResult && chatGPTResult.quizzes && chatGPTResult.quizzes.length
        "
      >
        <h3>{{ selectedQuiz }}</h3>
        <div
          v-for="(question, index) in chatGPTResult.quizzes[0].questions"
          :key="question.id"
          class="quiz-question"
        >
          <p class="question-text">{{ index + 1 }}. {{ question.value }}</p>
          <div v-if="question.type === 'mc'" class="choices">
            <div
              v-for="choice in question.choices"
              :key="choice.value"
              class="choice-item"
            >
              <input
                type="radio"
                :id="question.id + '-' + choice.value"
                :name="'question-' + question.id"
                :value="choice.value"
              />
              <label
                :for="question.id + '-' + choice.value"
                class="choice-label"
                >{{ choice.value }}</label
              >
            </div>
          </div>
        </div>
        <!-- Button to compare answers -->
        <button
          v-if="chatGPTResult.quizzes"
          @click="compareAnswers"
          class="compare-button"
        >
          Check Answers
        </button>
      </div>
      <ResultModal
        :show="showResult"
        :score="score"
        :quizData="chatGPTResult"
        :selectedQuiz="selectedQuiz"
        @close="showResult = false"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from "vue";
import { defineProps, defineEmits } from "vue";
import axios from "axios";
import ResultModal from "./ResultModal.vue";

const props = defineProps({
  modalActive: {
    type: Boolean,
    default: false,
  },
  note: {
    type: Object,
    default: null,
  },
});

const emit = defineEmits(["close", "editNote", "deleteNote"]);

const apiResult = ref([]);
const lastClickedButton = ref("");
const selectedQuiz = ref(""); // Store the text of the clicked button
const chatGPTResult = ref(null); // Initialize as null
const showResult = ref(false);
const score = ref(0); // Use a ref for score

const close = () => {
  emit("close");
};

const processKeywords = (data) => {
  return data.map((item) => (typeof item === "string" ? item : item[0]));
};

const rake = async () => {
  lastClickedButton.value = "rake";
  selectedQuiz.value = "Rake"; // Store the text of the clicked button
  // Reset the quiz from the past click
  chatGPTResult.value = null;
  if (!props.note) return;
  try {
    const response = await axios.post("http://localhost:5000/api/text/rake", {
      text: props.note.text,
    });
    apiResult.value = processKeywords(response.data);
  } catch (error) {
    console.error("Error calling rake API:", error);
    apiResult.value = ["Error fetching data from rake API"];
  }
};

const keybert = async () => {
  lastClickedButton.value = "keybert";
  selectedQuiz.value = "Keybert"; // Store the text of the clicked button
  // Reset the quiz from the past click
  chatGPTResult.value = null;
  if (!props.note) return;
  try {
    const response = await axios.post(
      "http://localhost:5000/api/text/keybert",
      {
        text: props.note.text,
      }
    );
    apiResult.value = processKeywords(response.data);
  } catch (error) {
    console.error("Error calling keybert API:", error);
    apiResult.value = ["Error fetching data from keybert API"];
  }
};

const generateQuiz = async (text) => {
  selectedQuiz.value = text;
  score.value = 0;
  showResult.value = false;
  try {
    const response = await axios.post("http://localhost:5001/generate-quiz", {
      subject: text,
      difficulty: "easy",
    });

    let quizData = response.data;

    // Ensure each question object in the quiz includes the correct answer
    if (quizData && quizData.quizzes && quizData.quizzes.length > 0) {
      quizData.quizzes.forEach((quiz) => {
        quiz.questions.forEach((question) => {
          // Find the correct answer in the choices array
          const correctChoice = question.choices.find(
            (choice) => choice.isCorrect
          );
          if (correctChoice) {
            question.answer = correctChoice.value;
          } else {
            question.answer = "Answer not found";
          }
        });
      });
    }

    chatGPTResult.value = quizData;
    console.log("Quiz generated:", quizData);
  } catch (error) {
    console.error("Error generating quiz:", error);
    chatGPTResult.value = { quizzes: [], title: "Error generating quiz" };
  }
};
const compareAnswers = () => {
  if (!chatGPTResult.value) return;

  const userAnswers = document.querySelectorAll('input[type="radio"]:checked');

  let newScore = 0; // Initialize a new score variable

  chatGPTResult.value.quizzes[0].questions.forEach((question, index) => {
    const correctAnswer = question.answer; // Retrieve the correct answer from the chatGPTResult
    const userAnswer = userAnswers[index]?.value;
    console.log("user", userAnswer);
    console.log("answer", correctAnswer);
    if (userAnswer === correctAnswer) {
      newScore++; // Increment the new score if the answer is correct
    }
  });

  // Set the quizData and score before showing the ResultModal
  showResult.value = true;
  score.value = newScore; // Update the score immediately
};
watch(
  () => props.note,
  () => {
    apiResult.value = [];
    lastClickedButton.value = "";
    chatGPTResult.value = null; // Reset chatGPTResult
    showResult.value = false; // Reset showResult
    score.value = 0; // Reset the score when the quiz changes
  }
);
</script>

<style scoped>
.modal-button.active {
  background-color: #0056b3; /* Change to desired color */
}
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
  max-height: 80vh; /* Set the max height of the modal */
  overflow-y: auto; /* Enable vertical scrolling */
  width: 90%; /* Optional: adjust the width */
  max-width: 600px; /* Optional: set a max-width */
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

.modal-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}

.modal-button {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  background-color: #007bff;
  color: white;
  font-size: 16px;
}

.modal-button:hover {
  background-color: #0056b3;
}

.result-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
}

.result-button {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  background-color: #4db5e2;
  color: white;
  font-size: 16px;
}

.result-button:hover {
  background-color: #44bece;
}

.quiz-question {
  margin-top: 20px;
}

.question-text {
  margin-bottom: 10px;
  font-size: 18px;
  font-weight: bold;
}

.choices {
  display: flex;
  flex-direction: column;
}

.choice-item {
  margin-bottom: 10px;
  display: flex;
  align-items: center;
}

.choice-item input[type="radio"] {
  margin-right: 10px;
}

.choice-label {
  font-size: 16px;
}

.compare-button {
  margin-top: 20px;
  padding: 12px 24px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  background-color: #4db5e2;
  color: white;
  font-size: 16px;
}

.compare-button:hover {
  background-color: #44bece;
}

.result {
  margin-top: 20px;
}

.result h3 {
  font-size: 20px;
  font-weight: bold;
}

.active {
  background-color: #0056b3;
}
</style>
