import tensorflow as tf
import zipfile
import io
import cv2
import requests
from PIL import Image
import numpy as np
import json

def main ():
    #CODICE PER DETERMINARE PREVISIONI SU TUTTE LE IMMAGINI E AGGIUNGERE LE ETICHETTE DELLE PREVISIONI AD UN ARRAY TEMPORANEO (CHE SI RESETTA QUANDO SI CAMBIA IMMAGINE)
    for i in range(1, 151, 1):
        pred_this = np.empty(shape=0)
    # print("/content/out"+str(i)+".png")
    # img = cv2.imread("/content/out"+str(i)+".png")
    # image = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
    # pilImage = Image.fromarray(image)
    # buffered = io.BytesIO()
    # pilImage.save(buffered, quality=100, format="JPEG")
    # m = MultipartEncoder(fields={'file': ("imageToUpload", buffered.getvalue(), "image/jpeg")})
    # response = requests.post("https://detect.roboflow.com/1-pclhz/2?api_key=gx4DtuEuM2Mvvdn6RuLv", data=m, headers={'Content-Type': m.content_type})
    # risposta1 = response.json()
    # q = len(risposta1["predictions"])
    # for j in range(q):
    #     pred_this = np.append(pred_this, risposta1["predictions"][j]["class"])
    print(pred_this)

    print("Yessss I made it")
    return "Hola"