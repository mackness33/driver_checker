import tensorflow as tf
import zipfile
!pip install requests pillow
!pip install requests_toolbelt
import io
import cv2
import requests
from PIL import Image
from requests_toolbelt.multipart.encoder import MultipartEncoder
from google.colab.patches import cv2_imshow
import numpy as np
import json

#CODICE PER DETERMINARE PREVISIONI SU TUTTE LE IMMAGINI E AGGIUNGERE LE ETICHETTE DELLE PREVISIONI AD UN ARRAY TEMPORANEO (CHE SI RESETTA QUANDO SI CAMBIA IMMAGINE)
'''
for i in range(1, 151, 1):
  pred_this = np.empty(shape=0)
  print("/content/out"+str(i)+".png")
  img = cv2.imread("/content/out"+str(i)+".png")
  image = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
  pilImage = Image.fromarray(image)  
  buffered = io.BytesIO()
  pilImage.save(buffered, quality=100, format="JPEG")
  m = MultipartEncoder(fields={'file': ("imageToUpload", buffered.getvalue(), "image/jpeg")})
  response = requests.post("https://detect.roboflow.com/1-pclhz/2?api_key=gx4DtuEuM2Mvvdn6RuLv", data=m, headers={'Content-Type': m.content_type})
  risposta1 = response.json()
  q = len(risposta1["predictions"])
  for j in range(q):
    pred_this = np.append(pred_this, risposta1["predictions"][j]["class"])
  print(pred_this)
'''

#CODICE PER ANALIZZARE IL VIDEO SU 3 FRAME.
temp_predictions = np.empty(shape=0)
pred_on_frame = np.empty(shape=0)
cond_tot = 0
pass_tot = 0

for i in range(1, 151, 1):
    print(i)
    cond_parz = 0
    pass_parz = 0
    cond_elems = 0
    pass_elems = 0
    pred_this = np.empty(shape=0)
    print("/content/out"+str(i)+".png")
    img = cv2.imread("/content/out"+str(i)+".png")
    image = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
    pilImage = Image.fromarray(image)
    buffered = io.BytesIO()
    pilImage.save(buffered, quality=100, format="JPEG")
    m = MultipartEncoder(fields={'file': ("imageToUpload", buffered.getvalue(), "image/jpeg")})
    response = requests.post("https://detect.roboflow.com/1-pclhz/2?api_key=gx4DtuEuM2Mvvdn6RuLv", data=m, headers={'Content-Type': m.content_type})
    risposta1 = response.json()
    q = len(risposta1["predictions"])
    for j in range(q):
        pred_this = np.append(pred_this, risposta1["predictions"][j]["class"])
        if risposta1["predictions"][j]["class"] == "finestrino conducente" or risposta1["predictions"][j]["class"] == "cintura conducente":
            cond_elems = cond_elems + 1
        elif risposta1["predictions"][j]["class"] == "finestrino passeggero" or risposta1["predictions"][j]["class"] == "cintura passeggero":
            pass_elems = pass_elems + 1
    print(pred_this)
    if cond_elems > pass_elems:
        temp_predictions = np.append(temp_predictions, "conducente")
    elif pass_elems > cond_elems:
        temp_predictions = np.append(temp_predictions, "passeggero")
    elif cond_elems == pass_elems:
        temp_predictions = np.append(temp_predictions, "nessuna")
    if i > 2:
        if temp_predictions[i-1] == "conducente":
            cond_parz = cond_parz + 1
        elif temp_predictions[i-1] == "passeggero":
            pass_parz = pass_parz + 1
        if temp_predictions[i-2] == "conducente":
            cond_parz = cond_parz + 1
        elif temp_predictions[i-2] == "passeggero":
            pass_parz = pass_parz + 1
        if temp_predictions[i-3] == "conducente":
            cond_parz = cond_parz + 1
        elif temp_predictions[i-3] == "passeggero":
            pass_parz = pass_parz + 1
    if cond_parz > pass_parz:
        pred_on_frame = np.append(pred_on_frame, "conducente")
    elif pass_parz > cond_parz:
        pred_on_frame = np.append(pred_on_frame, "passeggero")

for p in range(len(pred_on_frame)):
    if pred_on_frame[p] == "conducente":
        cond_tot = cond_tot + 1
    elif pred_on_frame[p] == "passeggero":
        pass_tot = pass_tot + 1

#print(len(pred_on_frame))
#print(pred_on_frame)
#print(cond_tot)
#print(pass_tot)

if cond_tot > pass_tot:
    print("Conducente")
elif pass_tot > cond_tot:
    print("Passeggero")

'''
#CODICE PER ANALIZZARE IL VIDEO SU 5 FRAME.
temp_predictions = np.empty(shape=0)
pred_on_frame = np.empty(shape=0)
cond_tot = 0
pass_tot = 0

for i in range(1, 151, 1):
  cond_parz = 0
  pass_parz = 0
  cond_elems = 0
  pass_elems = 0
  pred_this = np.empty(shape=0)
  print("/content/out"+str(i)+".png")
  img = cv2.imread("/content/out"+str(i)+".png")
  image = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
  pilImage = Image.fromarray(image)  
  buffered = io.BytesIO()
  pilImage.save(buffered, quality=100, format="JPEG")
  m = MultipartEncoder(fields={'file': ("imageToUpload", buffered.getvalue(), "image/jpeg")})
  response = requests.post("https://detect.roboflow.com/1-pclhz/2?api_key=gx4DtuEuM2Mvvdn6RuLv", data=m, headers={'Content-Type': m.content_type})
  risposta1 = response.json()
  q = len(risposta1["predictions"])
  for j in range(q):
    pred_this = np.append(pred_this, risposta1["predictions"][j]["class"])
    if risposta1["predictions"][j]["class"] == "finestrino conducente" or risposta1["predictions"][j]["class"] == "cintura conducente":
      cond_elems = cond_elems + 1
    elif risposta1["predictions"][j]["class"] == "finestrino passeggero" or risposta1["predictions"][j]["class"] == "cintura passeggero":
      pass_elems = pass_elems + 1
  print(pred_this)
  if cond_elems > pass_elems:
    temp_predictions = np.append(temp_predictions, "conducente")
  elif pass_elems > cond_elems::
    temp_predictions = np.append(temp_predictions, "passeggero")
  else:
    temp_predictions = np.append(temp_predictions, "nessuna")
  if i > 4:
    if temp_predictions[i-1] == "conducente":
      cond_parz = cond_parz + 1
    elif temp_predictions[i-1] == "passeggero":
      pass_parz = pass_parz + 1
    if temp_predictions[i-2] == "conducente":
      cond_parz = cond_parz + 1
    elif temp_predictions[i-2] == "passeggero":
      pass_parz = pass_parz + 1
    if temp_predictions[i-3] == "conducente":
      cond_parz = cond_parz + 1
    elif temp_predictions[i-3] == "passeggero":
      pass_parz = pass_parz + 1
    if temp_predictions[i-4] == "conducente":
      cond_parz = cond_parz + 1
    elif temp_predictions[i-4] == "passeggero":
      pass_parz = pass_parz + 1
    if temp_predictions[i-5] == "conducente":
      cond_parz = cond_parz + 1
    elif temp_predictions[i-5] == "passeggero":
      pass_parz = pass_parz + 1
  if cond_parz > pass_parz:
    pred_on_frame = np.append(pred_on_frame, "conducente")
  elif pass_parz > cond_parz:
    pred_on_frame = np.append(pred_on_frame, "passeggero")

for p in range(len(pred_on_frame)):
  if pred_on_frame[p] == "conducente":
    cond_tot = cond_tot + 1
  elif pred_on_frame[p] == "passeggero":
    pass_tot = pass_tot + 1

if cond_tot > pass_tot:
  print("Conducente")
elif pass_tot > cond_tot:
  print("Passeggero")


#CODICE PER ANALIZZARE IL VIDEO SU 10 FRAME.
temp_predictions = np.empty(shape=0)
pred_on_frame = np.empty(shape=0)
cond_tot = 0
pass_tot = 0

for i in range(1, 151, 1):
  cond_parz = 0
  pass_parz = 0
  cond_elems = 0
  pass_elems = 0
  pred_this = np.empty(shape=0)
  print("/content/out"+str(i)+".png")
  img = cv2.imread("/content/out"+str(i)+".png")
  image = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
  pilImage = Image.fromarray(image)  
  buffered = io.BytesIO()
  pilImage.save(buffered, quality=100, format="JPEG")
  m = MultipartEncoder(fields={'file': ("imageToUpload", buffered.getvalue(), "image/jpeg")})
  response = requests.post("https://detect.roboflow.com/1-pclhz/2?api_key=gx4DtuEuM2Mvvdn6RuLv", data=m, headers={'Content-Type': m.content_type})
  risposta1 = response.json()
  q = len(risposta1["predictions"])
  for j in range(q):
    pred_this = np.append(pred_this, risposta1["predictions"][j]["class"])
    if risposta1["predictions"][j]["class"] == "finestrino conducente" or risposta1["predictions"][j]["class"] == "cintura conducente":
      cond_elems = cond_elems + 1
    elif risposta1["predictions"][j]["class"] == "finestrino passeggero" or risposta1["predictions"][j]["class"] == "cintura passeggero":
      pass_elems = pass_elems + 1
  print(pred_this)
  if cond_elems > pass_elems:
    temp_predictions = np.append(temp_predictions, "conducente")
  elif pass_elems > cond_elems::
    temp_predictions = np.append(temp_predictions, "passeggero")
  else:
    temp_predictions = np.append(temp_predictions, "nessuna")
  if i > 9:
    if temp_predictions[i-1] == "conducente":
      cond_parz = cond_parz + 1
    elif temp_predictions[i-1] == "passeggero":
      pass_parz = pass_parz + 1
    if temp_predictions[i-2] == "conducente":
      cond_parz = cond_parz + 1
    elif temp_predictions[i-2] == "passeggero":
      pass_parz = pass_parz + 1
    if temp_predictions[i-3] == "conducente":
      cond_parz = cond_parz + 1
    elif temp_predictions[i-3] == "passeggero":
      pass_parz = pass_parz + 1
    if temp_predictions[i-4] == "conducente":
      cond_parz = cond_parz + 1
    elif temp_predictions[i-4] == "passeggero":
      pass_parz = pass_parz + 1
    if temp_predictions[i-5] == "conducente":
      cond_parz = cond_parz + 1
    elif temp_predictions[i-5] == "passeggero":
      pass_parz = pass_parz + 1
    if temp_predictions[i-6] == "conducente":
      cond_parz = cond_parz + 1
    elif temp_predictions[i-6] == "passeggero":
      pass_parz = pass_parz + 1
    if temp_predictions[i-7] == "conducente":
      cond_parz = cond_parz + 1
    elif temp_predictions[i-7] == "passeggero":
      pass_parz = pass_parz + 1
    if temp_predictions[i-8] == "conducente":
      cond_parz = cond_parz + 1
    elif temp_predictions[i-8] == "passeggero":
      pass_parz = pass_parz + 1
    if temp_predictions[i-9] == "conducente":
      cond_parz = cond_parz + 1
    elif temp_predictions[i-9] == "passeggero":
      pass_parz = pass_parz + 1
    if temp_predictions[i-10] == "conducente":
      cond_parz = cond_parz + 1
    elif temp_predictions[i-10] == "passeggero":
      pass_parz = pass_parz + 1
  if cond_parz > pass_parz:
    pred_on_frame = np.append(pred_on_frame, "conducente")
  elif pass_parz > cond_parz:
    pred_on_frame = np.append(pred_on_frame, "passeggero")

for p in range(len(pred_on_frame)):
  if pred_on_frame[p] == "conducente":
    cond_tot = cond_tot + 1
  elif pred_on_frame[p] == "passeggero":
    pass_tot = pass_tot + 1

if cond_tot > pass_tot:
  print("Conducente")
elif pass_tot > cond_tot:
  print("Passeggero")
  '''