from flask import Flask, request, jsonify
import requests
import json
import random

app = Flask(__name__)

@app.route('/generate-quiz', methods=['POST'])
def generate_quiz():
    api_url = 'https://api.arlinear.com/functions/v1/generate-quiz'
    api_key = '1991f4da-6085-41e1-b43f-f3f8c58d0e79'

    # Extract data from the request body
    request_data = request.get_json()
    subject = request_data.get('subject')
    difficulty = request_data.get('difficulty')

    # Data to be sent in the POST request
    data = {
        'subject': subject,
        'instructions': "A Multiple Choice quiz on " + subject,
        'gradeLevel': 'grade 6',
        'difficulty': difficulty,
        'numQuestions': 50  # Requesting 50 questions
    }

    # Headers for the request
    headers = {
        'Authorization': api_key,
        'Content-Type': 'application/json'
    }

    # Making the POST request to the API
    response = requests.post(api_url, headers=headers, data=json.dumps(data))

    # Check if the request was successful
    if response.status_code == 200:
        quiz_data = response.json()

        # Extract the 50 questions
        questions = quiz_data['quizzes'][0]['questions']

        # Select 5 random questions from the 50
        random_questions = random.sample(questions, 5)

        # Create a new quiz object with the selected questions
        new_quiz = {
            "quizId": quiz_data['quizzes'][0]['quizId'],
            "title": quiz_data['quizzes'][0]['title'],
            "questions": random_questions
        }

        return jsonify(new_quiz), 200
    else:
        return jsonify({"error": "Failed to generate quiz"}), response.status_code

if __name__ == '__main__':
    app.run(debug=True)
