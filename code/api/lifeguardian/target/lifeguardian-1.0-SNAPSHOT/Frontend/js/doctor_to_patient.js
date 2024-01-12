
const baseURL = window.location.protocol + "//" + window.location.hostname+ ":8080/"
let patientName = ""

const setup = () => {




    // Function to parse cookies and retrieve a specific one
    function getCookie(name) {
        let cookieArray = document.cookie.split(';');
        for(let i = 0; i < cookieArray.length; i++) {
            let cookiePair = cookieArray[i].split('=');
            if (name === cookiePair[0].trim()) {
                return decodeURIComponent(cookiePair[1]);
            }
        }
        return null;
    }
    const accessToken = getCookie('accessToken');  // Get the token from cookies

    if (!accessToken) {
        console.error('Access token not found');
    } else {
        var headers = new Headers({
            'Authorization': `Bearer ${accessToken}`,
            'Content-Type': 'application/json'
        });
    }
    let globalUserInfo = null; // Keep track of doctors with pending requests
    async function getUserInfo() {
        try {
            const response = await fetch(`${baseURL}api/me`, {
                headers: headers,
                method: 'GET'
            });

            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }

            const data = await response.json();
            globalUserInfo = data;  // Set the global variable
            console.log(data);
        } catch (error) {
            console.error('Error fetching user info:', error);
        }
    }

    async function getPatientInfo() {
        try {
            const response = await fetch(`${baseURL}api/doctor/getPatientInfo/${patientName}`, {
                headers: headers,
                method: 'GET'
            });

            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }

            const data = await response.json();
            console.log(data);
            const healthData = data.health_data;
            displayHealthData(healthData)
            const sensorData = data.sensor_data;
            displaySensorData(sensorData);
            const  healthStatus =data.health_status ;
            displayHealthStatus(healthStatus);
// Set the global variable
            console.log(data);
        } catch (error) {
            console.error('Error fetching user info:', error);
        }
    }
    function displaySensorData({ap_hi, ap_lo, heartRateData, saturationData, temp}) {


        // Update heart rate
        const heartRateValueElement = document.querySelector('#heart-rate-value .text-2xl');
        heartRateValueElement.textContent = heartRateData ;

        // Update temperature
        const temperatureValueElement = document.querySelector('#temperature-value .text-2xl');
        temperatureValueElement.textContent = temp ;

        // Update blood pressure (systolic/diastolic)
        const bloodPressureValueElement = document.querySelector('#blood-pressure-value .text-2xl');
        bloodPressureValueElement.textContent = `${ap_hi }/${ap_lo }`;

        // Update oxygen level
        const oxygenLevelValueElement = document.querySelector('#oxygen-level-value .text-2xl');
        oxygenLevelValueElement.textContent = `${saturationData  }%`;

        // Add similar updates for any other sensor data you want to display
    }
    function displayHealthData({age,height,weight, cholesterol, gluc, alco,smoke,active}) {
        document.getElementById('age-value').textContent = age;
        document.getElementById('cholesterol-value').textContent = mapCholesterol(cholesterol);
        document.getElementById('height-value').textContent = height;
        document.getElementById('weight-value').textContent = weight;
        document.getElementById('smoke-value').textContent = mapSmoke(smoke);
        document.getElementById('active-value').textContent = mapActive(active);
        document.getElementById('alco-value').textContent = mapAlco(alco);
        document.getElementById('gluc-value').textContent = mapGluc(gluc);

    }
    function displayHealthStatus({bmi, blood_pressure_status, bmi_status, heart_rate_status,saturation_status}) {

                const bmiElement = document.getElementById('bmi-value');
                bmiElement.textContent =bmi.toFixed(2);
                bmiElement.className = getBMIColor(bmi_status);

                // Update and color code blood pressure status
                const bloodPressureElement = document.getElementById('blood-pressure-status-value');
                bloodPressureElement.textContent = blood_pressure_status;
                bloodPressureElement.className = getBloodPressureColor(blood_pressure_status);

                // Update and color code saturation status
                const saturationElement = document.getElementById('saturation-value');
                saturationElement.textContent = saturation_status;
                saturationElement.className = getSaturationColor(saturation_status);

                // Update and color code heart rate status
                const heartRateElement = document.getElementById('heart-rate-status-value');
                heartRateElement.textContent = heart_rate_status;
                heartRateElement.className = getHeartRateColor(heart_rate_status);


    }
    function mapCholesterol(value) {
        const cholesterolMap = {
            '0': 'Normal',
            '1': 'high',
            '2': 'very high'
        };
        return cholesterolMap[value] || 'Unknown';
    }

    function mapActive(value) {
        const activeMap = {
            '0': 'Active',
            '1': 'Not Active'
        };
        return activeMap[value] || 'Unknown';
    }

    function mapSmoke(value) {
        const smokeMap = {
            '0': 'Non-Smoker',
            '1': 'Smoker'
        };
        return smokeMap[value] || 'Unknown';
    }

    function mapGluc(value) {
        const glucMap = {
            '1': 'Normal',
            '2': 'high',
            '3': 'very high'
        };
        return glucMap[value] || 'Unknown';
    }

    function mapAlco(value) {
        const alcoMap = {
            '0': 'Non-Alcoholic',
            '1': 'Alcoholic'
        };
        return alcoMap[value] || 'Unknown';
    }

    function getBMIColor(bmiStatus) {
        switch (bmiStatus) {
            case 'Underweight': return 'text-blue-600';
            case 'Normal': return 'text-green-600';
            case 'Overweight': return 'text-yellow-600';
            case 'Obese': return 'text-red-600';
            default: return 'text-gray-600';
        }
    }

    function getBloodPressureColor(bloodPressureStatus) {
        switch (bloodPressureStatus) {
            case 'High': return 'text-red-600';
            case 'Low': return 'text-blue-600';
            case 'Normal': return 'text-green-600';
            default: return 'text-gray-600';
        }
    }

    function getSaturationColor(saturationStatus) {
        switch (saturationStatus) {
            case 'Low': return 'text-blue-600';
            case 'Normal': return 'text-green-600';
            default: return 'text-gray-600';
        }
    }

    function getHeartRateColor(heartRateStatus) {
        switch (heartRateStatus) {
            case 'Very Light': return 'text-blue-600';
            case 'Moderate': return 'text-green-600';
            case 'Hard': return 'text-yellow-600';
            case 'Maximum': return 'text-red-600';
            default: return 'text-gray-600';
        }
    }
    async function fetchAndUpdateUserInfo() {

        if (globalUserInfo == null) {
            await getUserInfo();

        }
        // Update the username and role
        document.getElementById('user-name').textContent = globalUserInfo.username ;
        document.getElementById('user-role').textContent = globalUserInfo.role;

        // Create initials from the username
        const initials = globalUserInfo.username ? globalUserInfo.username.split(' ').map(n => n[0]).join('').toUpperCase() : '--';
        document.getElementById('user-initials').textContent = initials;

    }
// Function to get query parameters from the URL
    function getQueryParam(param) {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get(param);
    }

// Function to navigate back to the doctor dashboard
    function returnToDashboard() {
        window.location.href = 'doctorPage.html'; // Make sure this is the correct path to your doctor dashboard page
    }

// Function to set the patient's name in the header
    function setPatientName() {
        patientName = getQueryParam('patientUsername');
        if (patientName) {
            document.getElementById('patient-name').textContent = patientName;
        }
    }

// Function to attach event listeners
    function attachEventListeners() {
        const returnButton = document.getElementById('return-button');
        if (returnButton) {
            returnButton.addEventListener('click', returnToDashboard);
        }
    }

// Call the necessary functions when the DOM is fully loaded
    document.addEventListener('DOMContentLoaded', function() {
        setPatientName();
        attachEventListeners();
        getUserInfo();
        fetchAndUpdateUserInfo();
        getPatientInfo();

    });


}
// Initialize the setup when the script loads
const dashboard = setup();