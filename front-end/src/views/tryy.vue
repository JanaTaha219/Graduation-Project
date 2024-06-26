<template>
  <div class="quiz-container">
    <p class="note-text">The text: {{ noteText }}</p>

    <div v-if="showKeywords" class="keywords-container">
      <h2>Extracted Keywords</h2>
      <div class="keywords-buttons">
        <button
          v-for="(keyword, index) in keywords"
          :key="index"
          class="keyword-button"
          @click="generateQuiz(keyword)"
        >
          {{ keyword }}
        </button>
      </div>
    </div>

    <div v-if="showQuiz" class="quiz-section">
      <div
        v-for="(question, questionIndex) in quiz.questions"
        :key="questionIndex"
        class="question-container"
      >
        <p class="question-text">{{ question.value }}</p>
        <ul class="choices-list">
          <li
            v-for="(choice, choiceIndex) in question.choices"
            :key="choiceIndex"
            class="choice-item"
          >
            <input
              type="radio"
              :id="'question-' + questionIndex + '-choice-' + choiceIndex"
              :value="choice.value"
              v-model="selectedAnswers[questionIndex]"
              class="choice-input"
            />
            <label
              :for="'question-' + questionIndex + '-choice-' + choiceIndex"
              class="choice-label"
            >
              {{ choice.value }}
            </label>
          </li>
        </ul>
      </div>
      <button class="check-answers-button" @click="compareAnswers">
        Check Answers
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";
const currentUserData = ref({});

const noteText = ref(""); // Store note text
const showKeywords = ref(false); // Control visibility of keyword buttons
const keywords = ref([]); // Store extracted keywords
const showQuiz = ref(false); // Control visibility of quiz
const quiz = ref(null); // Store quiz data
const selectedAnswers = ref([]); // Store user selected answers
const router = useRouter();
const subject = ref(""); // Store the selected subject

// Fetch note text from query parameter on mount
onMounted(() => {
  const textFromQuery = router.currentRoute.value.query.text;
  if (textFromQuery) {
    noteText.value = textFromQuery;
    extractKeywords(textFromQuery);
  }
  fetchUserData();
});

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
    console.log("name", currentUserData.value.userName);
  } catch (error) {
    console.error("Error fetching user data:", error.message);
  }
};

const extractKeywords = async (text) => {
  try {
    const response = await axios.post(
      "http://localhost:5000/api/text/keybert",
      { text }
    );
    console.log("Keyword extraction result:", response.data);
    keywords.value = response.data.map((item) => item[0]);
    showKeywords.value = true; // Show the keyword buttons
  } catch (error) {
    console.error("Error fetching keyword data:", error.message);
  }
};

const generateQuiz = async (subjectText) => {
  try {
    const response = await axios.post("http://localhost:5001/generate-quiz", {
      subject: subjectText,
      difficulty: "easy",
    });
    console.log("Generated quiz:", response.data);
    quiz.value = response.data; // Store quiz data
    showQuiz.value = true; // Show the quiz
    selectedAnswers.value = new Array(response.data.questions.length).fill("");
    subject.value = subjectText; // Store the selected subject
  } catch (error) {
    console.error("Error generating quiz:", error.message);
  }
};

const compareAnswers = () => {
  let score = 0;
  const results = quiz.value.questions.map((question, questionIndex) => {
    const selectedValue = selectedAnswers.value[questionIndex];
    const correctAnswer = question.choices.find(
      (choice) => choice.isCorrect
    ).value;
    const isCorrect = selectedValue === correctAnswer;
    if (isCorrect) {
      score++;
    }
    return {
      value: question.value,
      selectedAnswer: selectedValue,
      correctAnswer: correctAnswer,
      isCorrect: isCorrect,
    };
  });

  router.push({
    name: "result",
    params: {
      score: score,
      questions: results,
      title: quiz.value.questions, // Correctly pass the quiz title
      subject: subject.value, // Pass the selected subject
    },
  });
};
</script>

<style scoped>
.quiz-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  font-family: Arial, sans-serif;
}

.note-text {
  font-size: 1.2em;
  margin-bottom: 20px;
}

.keywords-container {
  margin-bottom: 20px;
}

.keywords-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.keyword-button {
  padding: 10px 20px;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.keyword-button:hover {
  background-color: #0056b3;
}

.quiz-section {
  margin-top: 20px;
}

.question-container {
  margin-bottom: 20px;
}

.question-text {
  font-weight: bold;
  margin-bottom: 10px;
}

.choices-list {
  list-style-type: none;
  padding: 0;
}

.choice-item {
  margin-bottom: 10px;
}

.choice-input {
  margin-right: 10px;
}

.choice-label {
  cursor: pointer;
}

.check-answers-button {
  margin-top: 20px;
  padding: 10px 20px;
  background-color: #28a745;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.check-answers-button:hover {
  background-color: #218838;
}
</style>
