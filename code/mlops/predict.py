import sys
import joblib
from preprocess import preprocess_data
import json
import os

CotProjectPath = os.environ['CotProjectPath']
model_path = CotProjectPath +r'\CoTProject\code\mlops\rf_model_73.joblib'

def predict_heart_attack(data):
    # Preprocess the data
    preprocessed_data = preprocess_data.preprocess(data)

    # Load the RandomForest model
    loaded_rf_model = joblib.load(model_path)

    # Make a prediction
    prediction = loaded_rf_model.predict(preprocessed_data)
    return prediction[0]

if __name__ == '__main__':
    input_data = json.loads(sys.argv[1])
    prediction = predict_heart_attack(input_data)
    print(prediction)
