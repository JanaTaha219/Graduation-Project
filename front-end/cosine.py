#!/usr/bin/env python
# coding: utf-8

# ## import libraries

# In[100]:


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


# ## Text pre-processing

# In[101]:


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


# ## stemming 
# <br>
# stemming:-Stemming: remove prefix and suffix <br>
# -e.g.: Original Word: "running" <br>
# Stem: "run" <br>
# Stemming is not accurate, may create non-words, but it's fast

# In[102]:


def apply_stemming(text):
    stemmer = PorterStemmer()
    words = word_tokenize(text)
    stemmed_words = [stemmer.stem(word) for word in words]
    text = ' '.join(stemmed_words)
    return text


# ## lemmatization
# <br>
# Lemmatization: reducing words to their base or dictionary form. <br>
# -e.g.: Original Word: "better" <br>
# Lemma: "good" <br>
# lemmatization is more accurate, returns real words but it's slow.

# In[103]:


def apply_lemmatization(text):
    lemmatizer = WordNetLemmatizer()
    words = word_tokenize(text)
    lemmatized_words = [lemmatizer.lemmatize(word) for word in words]
    text = ' '.join(lemmatized_words)
    return text


# ## w2v_model
# <br>
# numerical representations of words in a high-dimensional vector space where words with similar meanings are closer to each other.
# <br>
# - Word: King (vector: [0.8, 0.5, 0.3]) <br>
# - Word: Queen (vector: [0.75, 0.45, 0.25]) (Notice the closeness) <br>
# - Word: Dog (vector: [0.2, 0.7, 0.1]) (Further away due to different meaning) <br>
# 

# In[104]:


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


# ## Cosine similarity
# <br>
# ما بعرف شو هي :)

# In[105]:


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


# ### Trying consine similarity

# In[106]:


# Example texts
text1 = "Tokenization is an important step in natural language processing."
text2 = "Sentence tokenization is a technique used to split text into sentences."

# Calculate cosine similarity between texts
similarity = cosine_similarity_between_texts(text1, text2)

print("Cosine Similarity between Text 1 and Text 2 with lemmatization:", similarity)
similarity = cosine_similarity_between_texts(text1, text2, False)
print("Cosine Similarity between Text 1 and Text 2 with stemming:", similarity)


# ## Connecting to MySQL database

# In[107]:


import mysql.connector
db = mysql.connector.connect(user='root', password='School11?',
                              host='127.0.0.1', database='my_database',
                              auth_plugin='mysql_native_password')
cursor = db.cursor()


# ## عشان نجيب كل النوتس الي بالداتابيس ما عدا النوتس تبعت اليوزر يلي بدنا نعمله ريكومند + يلي عاملهم فلو 
# 
# ## To retrieve all notes except those belonging to the user for whom we wish to recommend posts and those who does follow them

# In[108]:


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


# ### testing the method

# In[109]:


all_users_post_except_specific_user = get_all_users_note_except_user_and_followed("rama")
for post in all_users_post_except_specific_user:
    print(post[2])


# ## عشان نجيب النوتس تبعت اليوزر يلي بدنا نعمله ريكومند
# ## To fetch all the user's notes from the database in order to recommend posts for them based on those notes.

# In[111]:


def get_all_user_posts(username):
    cursor.execute(f"SELECT text FROM note WHERE user_id = '{username}'")
    posts = cursor.fetchall()
    # Convert each tuple to a string
    posts_as_strings = [post[0] for post in posts]
    return posts_as_strings

posts = get_all_user_posts("rama")
print(posts[0], " ", posts[1])
cosine_similarity_between_texts(posts[0], posts[1])


# ## Compine all previous method to recommend user notes

# In[112]:


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


# ### Testing the method

# In[113]:


recommend_user_posts_using_cosine("aa",5)


# ## Euclidean similarity
# <br>
# ما بعرف شو هي :)

# In[114]:


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


# ## Compine all previous method to recommend user notes

# In[115]:


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


# ### Testing the method

# In[116]:


recommend_user_posts_using_euclidean_distances("aa",5)


# # Dot Product

# In[117]:


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


# In[118]:


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


# In[119]:


recommend_user_posts_using_dot_product("aa",5)


# In[120]:


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


# In[121]:


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


# In[ ]:





# In[ ]:





# In[ ]:



