from flask_cors import CORS
from flask import Flask, request, jsonify
from cosine import recommend_user_posts_using_cosine, recommend_user_posts_using_euclidean_distances, recommend_user_posts_using_dot_product, preprocess_text, apply_lemmatization, extract_keywords_using_rake, extract_keywords_using_keybert

app = Flask(__name__)
CORS(app, origins=['http://localhost:5173'])

@app.route('/api/predict', methods=['POST'])
def api_predict():
    data = request.json
    username = data.get('username')
    number_of_posts =  data.get('numberOfPosts')
    result = recommend_user_posts_using_cosine(username, number_of_posts)
    for item in result:
        item['similarity'] = float(item['similarity'])
    print(result)
    return jsonify(result)

@app.route('/api/predict/euclideanDistances', methods=['POST'])
def api_predict_using_euclidean_distances():
    data = request.json
    username = data.get('username')
    number_of_posts =  data.get('numberOfPosts')
    result = recommend_user_posts_using_euclidean_distances(username, number_of_posts)
    for item in result:
        item['similarity'] = float(item['similarity'])
    print(result)
    return jsonify(result)

@app.route('/api/predict/dotProduct', methods=['POST'])
def api_predict_using_dot_product():
    data = request.json
    username = data.get('username')
    number_of_posts =  data.get('numberOfPosts')
    result = recommend_user_posts_using_dot_product(username, number_of_posts)
    for item in result:
        item['similarity'] = float(item['similarity'])
    print(result)
    return jsonify(result)

@app.route('/api/text/rake', methods=['POST'])
def tokenize_using_rake():
    data = request.json
    text = data.get('text')
    result = extract_keywords_using_rake(text)
    print(result)
    return jsonify(result)

@app.route('/api/text/keybert', methods=['POST'])
def tokenize_using_keybert():
    data = request.json
    text = data.get('text')
    result = extract_keywords_using_keybert(text)
    print(result)
    return jsonify(result)

@app.route('/')
def hello():
    return 'Hello, World!'

if __name__ == '__main__':
    app.run(debug=True)