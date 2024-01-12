# Health Monitoring App

## Table of Contents

- [Introduction](#introduction)
- [Technologies Used](#technologies-used)
- [Supported Sensors](#supported-sensors)
- [Getting Started](#getting-started)
- [Security](#security)
- [Demo](#demo)

## Introduction

During the COVID-19 pandemic, many doctors got worried about being close to their patients. They were afraid of catching or spreading the virus. In response to this concern, our project was created to help doctors and patients connect without being in the same place.

In addition, we've incorporated a prototype of a smartwatch that collects health data from the patient. This information is then visualized in the user interface. To ensure the well-being of the user, we've set thresholds for medical data. If a user's data goes beyond these thresholds, the system sends an alert directly via email to the associated doctor. This feature enables prompt attention to any health issues that may arise.

Furthermore, users can make predictions about their heart health using their collected data, providing a convenient way to check their current health status.

For doctors, we've designed a dedicated interface where they can access and review their patients' information. This interface allows doctors to accept or decline new patients, providing a streamlined process for managing their patient load. This feature enhances the overall efficiency of healthcare delivery in the context of remote interactions.


## Technologies Used  

  1. MongoDB:
    Utility: NoSQL database for flexible and scalable data storage in an unstructured format.

  2. MQTT:
    Utility: Lightweight messaging protocol for efficient, real-time communication between devices and services [HiveMQ Brokers].

  3. Jakarta EE:
  Utility: Enterprise Java platform ensuring scalability and security for robust backend service development.

  4. WildFly:
  Utility: Open-source Java EE application server providing a reliable and high-performance runtime environment.

  5. PWA (Vanilla JS):
  Utility: Progressive Web App developed with Vanilla JavaScript, offering a responsive and native-like frontend experience with offline capabilities and push notifications.


## Supported Sensors

Our application integrates with various sensors to gather essential health data. Here are the supported sensors:

### MAX30100 (Heart Rate + SpO2 Sensor)

- The MAX30100 sensor is used to measure both heart rate and blood oxygen saturation (SpO2).

### DS18B20 (Temperature Sensor)

- The DS18B20 sensor is employed to measure temperature accurately.
### MPS20N0040D (Blood Pressure Sensor)

- The MPS20N0040D sensor is utilized to measure blood pressure.
- It aids in monitoring blood pressure levels, providing crucial information for healthcare assessment.

### Certification and Grading
HTTPS was ensured using Let's Encrypt's Certbot, providing secure communication with the middlewareMiddleware and the MQTT broker. DH (Diffie-Hellman parameters) parameters with 4096 bits are also used for TLS connections. In addition to that, some other security parameters were set on the Node.js server to ensure max security. The grading of the server was tested using SSLlabs, and we had a grade of A+.
