from flask import Flask, request, jsonify
from flask_cors import CORS

import requests
import json

app = Flask(__name__)
CORS(app, origins=['http://localhost:5173'])


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
        'instructions': "A Multiple  choice quiz on "+subject,
        'gradeLevel': 'grade 6',
        'difficulty': difficulty,
        'numQuestions': 5  # Number of questions is always 5
    }

    # Headers for the request
    headers = {
        'Authorization': api_key,
        'Content-Type': 'application/json'
    }

    response = requests.post(api_url, headers=headers, data=json.dumps(data))

    # Return the response from the API
    return jsonify(response.json()), response.status_code

if __name__ == '__main__':
    app.run(debug=True, port=5001)