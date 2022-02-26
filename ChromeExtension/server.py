from unicodedata import name
from flask import Flask, render_template, request
app = Flask(__name__)

import requests
import json
import os
from PIL import Image

def get_data(api_key):
    raw_response = requests.get(f'https://api.nasa.gov/planetary/apod?api_key={api_key}').text
    response = json.loads(raw_response)
    return response

def get_date(response):
    date = response['date']
    return date

def get_url(response):
    url = response['url']
    return url

def download_image(url, date):
    if os.path.isfile(f'{date}.png') == False:
        raw_image = requests.get(url).content
        with open(f'{date}.jpg', 'wb') as file:
            file.write(raw_image)
    else:
        return FileExistsError
        
def getNewPicture():
    print("yo")
    response = get_data("L1rCrYxdmYFpywxIkOoN0Q0fNJ8pTG3NZEyGAuRk")
    download_image(get_url(response), get_date(response))
    url = get_url(response)
    return url

@app.route('/')
def index():
  return render_template('index.html')

#C:/Users/mattb/Documents/VTHacks2022/VTHacks2022/nasaPictures.py
@app.route('/my-link/', methods = ['POST','GET'])
def my_link():
    print("it do be changing!!!")
    imageLink = getNewPicture()
    return render_template("index.html",image = imageLink)

if __name__ == '__main__':
  app.run(debug=True)