<template>
  <div>
    <div v-for="(question, questionIndex) in userName" :key="questionIndex">
      <p>{{ question.value }}</p>
      <ul>
        <li
          v-for="(choice, choiceIndex) in question.choices"
          :key="choiceIndex"
        >
          <input
            type="radio"
            :name="'question-' + questionIndex"
            :id="'question-' + questionIndex + '-choice-' + choiceIndex"
            :value="choice.value"
          />
          <label :for="'question-' + questionIndex + '-choice-' + choiceIndex">
            {{ choice.value }}
          </label>
        </li>
      </ul>
    </div>
    <button @click="compareAnswers">Check Answers</button>
    <div v-if="showResults">
      <p>Your score: {{ score }}/5</p>
      <p v-for="(result, index) in results" :key="index">
        Question {{ index + 1 }}: {{ result }}
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";

const userName = ref([]); // Create a ref to store the questions array
const results = ref([]); // Create a ref to store the results
const showResults = ref(false); // Create a ref to control the visibility of results
const score = ref(0); // Create a ref to store the score

const getQ = async () => {
  try {
    const response = await axios.post("http://localhost:5001/generate-quiz", {
      subject: "programming",
      difficulty: "easy",
    });
    // Access the questions array from the response
    console.log("data", response.data.questions);
    userName.value = response.data.questions;
  } catch (error) {
    console.error("Error fetching user data:", error.message);
  }
};

const compareAnswers = () => {
  results.value = [];
  score.value = 0; // Reset score
  userName.value.forEach((question, questionIndex) => {
    const selected = document.querySelector(
      `input[name="question-${questionIndex}"]:checked`
    );
    if (selected) {
      const selectedValue = selected.value;
      const correctChoice = question.choices.find((choice) => choice.isCorrect);
      if (correctChoice && selectedValue === correctChoice.value) {
        results.value.push("Correct");
        score.value++; // Increment score for correct answers
      } else {
        results.value.push("Incorrect");
      }
    } else {
      results.value.push("No answer selected");
    }
  });
  showResults.value = true;
};

onMounted(() => {
  getQ();
});
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
