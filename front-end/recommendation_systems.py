#!/usr/bin/env python
# coding: utf-8

# In[1]:


import re
import nltk
from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize
from nltk.stem import PorterStemmer, WordNetLemmatizer
import gensim.downloader as api
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity
from collections.abc import Mapping

# Download NLTK resources
nltk.download('punkt')
nltk.download('stopwords')

# Download the pre-trained Word2Vec model
w2v_model = api.load('word2vec-google-news-300')


# In[2]:


def preprocess_text(text):
    # Remove punctuation
    text = re.sub(r'[^\w\s]', '', text)
    
    # Convert to lowercase
    text = text.lower()
    
    # Remove stopwords
    stop_words = set(stopwords.words('english'))
    words = word_tokenize(text)
    filtered_words = [word for word in words if word not in stop_words]
    text = ' '.join(filtered_words)
    return text


# In[3]:


def apply_stemming(text):
    stemmer = PorterStemmer()
    words = word_tokenize(text)
    stemmed_words = [stemmer.stem(word) for word in words]
    text = ' '.join(stemmed_words)
    return text


# In[4]:


def apply_lemmatization(text):
    lemmatizer = WordNetLemmatizer()
    words = word_tokenize(text)
    lemmatized_words = [lemmatizer.lemmatize(word) for word in words]
    text = ' '.join(lemmatized_words)
    return text


# In[5]:


def get_text_vector(text):
    word_embeddings = []
    for word in text.split():
        try:
            embedding = w2v_model[word]
            word_embeddings.append(embedding)
        except KeyError:
            # If the word is not found in the vocabulary, skip it
            pass
    
    if len(word_embeddings) == 0:
        return None
    
    # Calculate the average of word embeddings
    text_vector = np.mean(word_embeddings, axis=0)
    return text_vector


# In[6]:


def cosine_similarity_between_texts(text1, text2, limm=True):
    # Preprocess the texts
    if limm == True:
        preprocessed_text1 = apply_lemmatization(preprocess_text(text1))
        preprocessed_text2 = apply_lemmatization(preprocess_text(text2))
    else:
        preprocessed_text1 = apply_stemming(preprocess_text(text1))
        preprocessed_text2 = apply_stemming(preprocess_text(text2))
    
    # Get text vectors
    vector1 = get_text_vector(preprocessed_text1)
    vector2 = get_text_vector(preprocessed_text2)
    
    if vector1 is None or vector2 is None:
        return None
    
    # Calculate cosine similarity
    similarity = cosine_similarity([vector1], [vector2])[0][0]
    return similarity


# In[7]:


text1 = "Tokenization is an important step in natural language processing."
text2 = "Sentence tokenization is a technique used to split text into sentences."

# Calculate cosine similarity between texts
similarity = cosine_similarity_between_texts(text1, text2)

print("Cosine Similarity between Text 1 and Text 2 with lemmatization:", similarity)
similarity = cosine_similarity_between_texts(text1, text2, False)
print("Cosine Similarity between Text 1 and Text 2 with stemming:", similarity)


# In[1]:


import mysql.connector
db = mysql.connector.connect(user='root', password='Jana2003?',
                              host='127.0.0.1', database='grad',
                              auth_plugin='mysql_native_password')
cursor = db.cursor()


# In[9]:


def get_all_users_note_except_user_and_followed(username_to_exclude):
    # Fetch the list of users that the specified user follows
    cursor.execute(f"""
        SELECT following_id
        FROM followers
        WHERE follower_id = '{username_to_exclude}'
    """)
    followed_users = cursor.fetchall()
    
    # Extract the list of followed user IDs
    followed_user_ids = [user[0] for user in followed_users]
    
    # Convert the list to a string format suitable for SQL IN clause
    followed_user_ids_str = "', '".join(followed_user_ids)
    
    # Include the specified user in the exclusion list
    followed_user_ids_str = f"'{username_to_exclude}', '{followed_user_ids_str}'"
    
    # Fetch notes excluding those from the specified user and followed users
    query = f"""
        SELECT * FROM note
        WHERE user_id NOT IN ({followed_user_ids_str})
    """
    cursor.execute(query)
    notes = cursor.fetchall()
    return notes


# In[10]:


all_users_post_except_specific_user = get_all_users_note_except_user_and_followed("rama")
for post in all_users_post_except_specific_user:
    print(post[2])


# In[99]:


def get_all_user_posts(username):
    cursor.execute(f"SELECT text FROM note WHERE user_id = '{username}'")
    posts = cursor.fetchall()
    # Convert each tuple to a string
    posts_as_strings = [post[0] for post in posts]
    return posts_as_strings


# In[100]:


def recommend_user_posts_using_cosine(user_name, number_of_posts):
    user_posts = get_all_user_posts(user_name)

    # Get all other users' posts
    other_users_posts = get_all_users_note_except_user_and_followed(user_name)

    # Initialize recommendations list
    recommendations = []

    # Initialize a set to store the IDs of processed posts
    processed_post_ids = set()

    # Loop through each other user's post
    for post in other_users_posts:
        # Calculate cosine similarity between each user post and the current post
        for user_post in user_posts:
            similarity = cosine_similarity_between_texts(user_post, post[3])
            if similarity and post[0] not in processed_post_ids:  # Check if similarity is not None and post not processed
                recommendations.append({
                    'id': post[0],              # Get the post ID from the tuple
                    'creation_time': post[1],   # Get the creation time from the tuple
                    'user_id': post[2],         # Get the user ID from the tuple
                    'text': post[3],            # Get the post text from the tuple
                    'url': post[4],             # Get the URL from the tuple
                    'comment': post[5],         # Get the comment from the tuple
                    'similarity': similarity    # Include the calculated similarity
                })
                # Add the post ID to the set of processed post IDs
                processed_post_ids.add(post[0])

    # Sort recommendations by similarity (highest first)
    recommendations.sort(key=lambda x: x['similarity'], reverse=True)

    # Return only the requested number of recommendations
    return recommendations[:number_of_posts]


# In[101]:


recommend_user_posts_using_cosine("aa",5)


# In[102]:


import numpy as np
from sklearn.metrics.pairwise import euclidean_distances

def euclidean_similarity_between_texts(text1, text2, limm=True):
    # Preprocess the texts
    if limm == True:
        preprocessed_text1 = apply_lemmatization(preprocess_text(text1))
        preprocessed_text2 = apply_lemmatization(preprocess_text(text2))
    else:
        preprocessed_text1 = apply_stemming(preprocess_text(text1))
        preprocessed_text2 = apply_stemming(preprocess_text(text2))
    
    # Get text vectors
    vector1 = get_text_vector(preprocessed_text1)
    vector2 = get_text_vector(preprocessed_text2)
    
    if vector1 is None or vector2 is None:
        return None
    
    # Calculate Euclidean distance
    distance = euclidean_distances([vector1], [vector2])[0][0]
    
    # Convert distance to similarity
    similarity = 1 / (1 + distance)
    
    return similarity


# In[103]:


def recommend_user_posts_using_euclidean_distances(user_name, number_of_posts):
    
    user_posts = get_all_user_posts(user_name)

    # Get all other users' posts
    other_users_posts = get_all_users_note_except_user_and_followed(user_name)

    # Initialize recommendations list
    recommendations = []

    # Initialize a set to store the IDs of processed posts
    processed_post_ids = set()

    # Loop through each other user's post
    for post in other_users_posts:
        # Calculate cosine similarity between each user post and the current post
        for user_post in user_posts:
            similarity = euclidean_similarity_between_texts(user_post, post[3])
            if similarity and post[0] not in processed_post_ids:  # Check if similarity is not None and post not processed
                recommendations.append({
                    'id': post[0],              # Get the post ID from the tuple
                    'creation_time': post[1],   # Get the creation time from the tuple
                    'user_id': post[2],         # Get the user ID from the tuple
                    'text': post[3],            # Get the post text from the tuple
                    'url': post[4],             # Get the URL from the tuple
                    'comment': post[5],         # Get the comment from the tuple
                    'similarity': similarity    # Include the calculated similarity
                })
                # Add the post ID to the set of processed post IDs
                processed_post_ids.add(post[0])

    # Sort recommendations by similarity (highest first)
    recommendations.sort(key=lambda x: x['similarity'], reverse=True)

    # Return only the requested number of recommendations
    return recommendations[:number_of_posts]


# In[104]:


recommend_user_posts_using_euclidean_distances("aa",5)


# In[105]:


def dot_product_similarity_between_texts(text1, text2, limm=True):
    # Preprocess the texts
    if limm == True:
        preprocessed_text1 = apply_lemmatization(preprocess_text(text1))
        preprocessed_text2 = apply_lemmatization(preprocess_text(text2))
    else:
        preprocessed_text1 = apply_stemming(preprocess_text(text1))
        preprocessed_text2 = apply_stemming(preprocess_text(text2))
    
    # Get text vectors
    vector1 = get_text_vector(preprocessed_text1)
    vector2 = get_text_vector(preprocessed_text2)
    
    if vector1 is None or vector2 is None:
        return None
    
    # Calculate dot product
    dot_product = np.dot(vector1, vector2)
    
    # Normalize vectors
    norm_vector1 = np.linalg.norm(vector1)
    norm_vector2 = np.linalg.norm(vector2)
    
    # Calculate similarity
    similarity = dot_product / (norm_vector1 * norm_vector2)
    
    return similarity


# In[106]:


def recommend_user_posts_using_dot_product(user_name, number_of_posts):
    
    user_posts = get_all_user_posts(user_name)

    # Get all other users' posts
    other_users_posts = get_all_users_note_except_user_and_followed(user_name)

    # Initialize recommendations list
    recommendations = []

    # Initialize a set to store the IDs of processed posts
    processed_post_ids = set()

    # Loop through each other user's post
    for post in other_users_posts:
        # Calculate cosine similarity between each user post and the current post
        for user_post in user_posts:
            similarity = dot_product_similarity_between_texts(user_post, post[3])
            if similarity and post[0] not in processed_post_ids:  # Check if similarity is not None and post not processed
                recommendations.append({
                    'id': post[0],              # Get the post ID from the tuple
                    'creation_time': post[1],   # Get the creation time from the tuple
                    'user_id': post[2],         # Get the user ID from the tuple
                    'text': post[3],            # Get the post text from the tuple
                    'url': post[4],             # Get the URL from the tuple
                    'comment': post[5],         # Get the comment from the tuple
                    'similarity': similarity    # Include the calculated similarity
                })
                # Add the post ID to the set of processed post IDs
                processed_post_ids.add(post[0])

    # Sort recommendations by similarity (highest first)
    recommendations.sort(key=lambda x: x['similarity'], reverse=True)

    # Return only the requested number of recommendations
    return recommendations[:number_of_posts]


# In[107]:


recommend_user_posts_using_dot_product("aa",5)


# In[108]:


from keybert import KeyBERT

def extract_keywords_using_keybert(text):
    # Initialize KeyBERT with the desired model
    model = KeyBERT('all-MiniLM-L6-v2')
    
    # Extract keywords from the text
    keywords = model.extract_keywords(text, keyphrase_ngram_range=(1, 2), stop_words='english')
    
    return keywords

# Example usage
text = "In a bustling city, a solitary street performer captivates the crowd with a mesmerizing melody under the glow of neon lights"
keywords = extract_keywords_using_keybert(text)
print(keywords)


# In[109]:


import nltk
from rake_nltk import Rake
from nltk.corpus import stopwords

# Download the necessary NLTK data (first-time use only)
nltk.download('punkt')
nltk.download('stopwords')

def extract_keywords_using_rake(text):
    # Initialize RAKE with NLTK's stopwords and set maximum phrase length to 2
    custom_stopwords = set(stopwords.words('english'))  # You can add more stopwords if needed
    r = Rake(stopwords=custom_stopwords, min_length=1, max_length=2)  # Adjust min_length and max_length here
    # Extract keywords from the text
    r.extract_keywords_from_text(text)
    
    # Get the ranked phrases as a list of tuples (phrase, score)
    ranked_phrases_with_scores = r.get_ranked_phrases_with_scores()
    
    # Extract the ranked phrases without scores
    ranked_phrases = [phrase for score, phrase in ranked_phrases_with_scores]
    
    return ranked_phrases

# Example usage
text = "In a bustling city, a solitary street performer captivates the crowd with a mesmerizing melody under the glow of neon lights "
keywords = extract_keywords_using_rake(text)
print(keywords)


# ## connecting to gemini AI

# In[2]:


import google.generativeai as genai

def get_category(prompt):
    API_KEY = "AIzaSyDX1lheYeca-QP7ZiaGUjvGsHYINcJi7WM"

    genai.configure(api_key=API_KEY)

    model_name = "gemini-1.5-pro-latest"

    # Create a generative model object
    model = genai.GenerativeModel(model_name)


    # Generate text with some customization options (optional)
    generation_config = {
        "temperature": 0.8,  # Controls randomness (0 = deterministic, 1 = more random)
        "max_output_tokens": 2048  # Maximum number of tokens to generate
    }
    response = model.generate_content(prompt, generation_config=generation_config)

    # Print the generated text
    print("response: ",response.text)
    return response.text


# ### Test method

# In[3]:



#get_category("Is machine learning a part of Artificial Intelligence, sports, cooking, or something else? If it's something else, what is it? Just wirte the topic only")


# In[4]:


#get_category("Is machine learning a part of sports, cooking, or something else? If it's something else, what is it? Just wirte the topic only")


# ## add mark to the db

# In[5]:


def get_db_connection():
    return mysql.connector.connect(user='root', password='Jana2003?',
                              host='127.0.0.1', database='grad',
                              auth_plugin='mysql_native_password')


# ### get all quizes types

# In[6]:


def get_all_quiz_types():
    connection = get_db_connection()
    cursor = connection.cursor()
    
    quiz_types_query = "SELECT DISTINCT quiz_type FROM marks"
    
    cursor.execute(quiz_types_query)
    quiz_types = cursor.fetchall()
    
    cursor.close()
    connection.close()
    
    return [quiz_type[0] for quiz_type in quiz_types]


# ### get quiz category

# In[7]:


def categorize_topic(topic):
    quiz_types = get_all_quiz_types()
    
    if not quiz_types:
        query = f"What is the topic of {topic}? Just write the topic only"
    else:
        categories_str = ", ".join(quiz_types)
        query = f"Is {topic} a part of {categories_str}, or something else? If it's something else, what is it? Just write the topic only"
    
    print("category:", get_category(query))
    return get_category(query)


# In[8]:


topic = "machine learning"
#category = categorize_topic(topic)
#print(f"The category for '{topic}' is: {category}")


# In[9]:


def add_mark(user_id, quiz_type, mark):
    connection = get_db_connection()
    cursor = connection.cursor()
    
    add_mark_query = (
        "INSERT INTO marks (unique_name, quiz_type, mark) "
        "VALUES (%s, %s, %s)"
    )
    
    cursor.execute(add_mark_query, (user_id, categorize_topic(quiz_type), mark))
    connection.commit()
    
    cursor.close()
    connection.close()


# In[10]:


def get_avg_marks(user_id, quiz_type):
    connection = get_db_connection()
    cursor = connection.cursor()
    
    avg_marks_query = (
        "SELECT AVG(mark) FROM marks "
        "WHERE unique_name = %s AND quiz_type = %s"
    )
    
    cursor.execute(avg_marks_query, (user_id, quiz_type))
    avg_mark = cursor.fetchone()[0]
    
    cursor.close()
    connection.close()
    
    return avg_mark


# ## test methods

# In[21]:


#add_mark('jana', 'machine learning', 88)

# Getting average marks for a specific quiz type
avg_math_marks = get_avg_marks('jana', 'math')
print(f"Average marks for Jana in Math: {avg_math_marks}")

avg_science_marks = get_avg_marks('jana', 'science')
print(f"Average marks for Jana in Science: {avg_science_marks}")
get_all_quiz_types()


# ## Retrieve users and their marks avg, who have taken the same tests, received marks higher than 2, and are not followed by the user.

# In[27]:


def get_unfollowed_users_with_avg_above_2(user_name):
    try:
        # Connect to the database
        connection = get_db_connection()
        cursor = connection.cursor()

        # Query to find users who have implemented the same tests as the given user,
        # are not followed by the given user, and their average mark is > 2
        query = """
            SELECT m.unique_name, AVG(m.mark) AS avg_mark
            FROM marks m
            WHERE m.quiz_type IN (
                SELECT DISTINCT quiz_type
                FROM marks
                WHERE unique_name = %s
            ) AND m.unique_name != %s
            AND m.unique_name NOT IN (
                SELECT f.following_id
                FROM followers f
                WHERE f.follower_id = %s
            )
            GROUP BY m.unique_name
            HAVING avg_mark > 2
        """

        # Execute the query with user_name as parameter
        cursor.execute(query, (user_name, user_name, user_name))

        # Fetch all the results
        results = cursor.fetchall()

        # Close cursor and connection
        cursor.close()
        connection.close()

        return results

    except mysql.connector.Error as error:
        print(f"Error connecting to MySQL: {error}")
        return []


# In[28]:


user_name = 'jana'  # Replace with the user name you want to query
users_with_avg_marks_above = get_unfollowed_users_with_avg_above_2(user_name)
print(f"Users who have implemented the same tests as {user_name}, are not followed by {user_name}, and have an average mark > 2:")
for user, avg_mark in users_with_avg_marks_above:
    print(f"{user}: {avg_mark}")


# In[29]:


def get_unfollowed_users_with_avg_above_2(quiz_type, user_unique_name):
    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)
    quiz_type = categorize_topic(quiz_type)
    # Fetch the users who had a test on the given subject and their average mark is > 2
    query = """
    SELECT m.unique_name, AVG(m.mark) as average_mark
    FROM marks m
    WHERE m.quiz_type = %s
    GROUP BY m.unique_name
    HAVING AVG(m.mark) > 2
    """
    
    cursor.execute(query, (quiz_type,))
    users_above_2 = cursor.fetchall()
    
    users_above_2 = {user['unique_name']: user['average_mark'] for user in users_above_2}
    
    # Fetch the users followed by the given user
    query = """
    SELECT f.following_id
    FROM followers f
    WHERE f.follower_id = %s
    """
    
    cursor.execute(query, (user_unique_name,))
    followed_users = cursor.fetchall()
    
    followed_users = {user['following_id'] for user in followed_users}
    
    # Get the users who are not followed by the given user
    unfollowed_users_with_avg = {user: avg for user, avg in users_above_2.items() if user not in followed_users}
    
    cursor.close()
    connection.close()
    
    # Sort the result by average mark in descending order
    sorted_unfollowed_users_with_avg = dict(sorted(unfollowed_users_with_avg.items(), key=lambda item: item[1], reverse=True))
    
    return sorted_unfollowed_users_with_avg


# In[30]:


quiz_type = "math"
user_unique_name = "jana"
result = get_unfollowed_users_with_avg_above_2(quiz_type, user_unique_name)
print("user who has a test on subject + got high mark + not followed by user:")
for user, avg in result.items():
    print(f"User: {user}, Average Mark: {avg}")


# In[ ]:




