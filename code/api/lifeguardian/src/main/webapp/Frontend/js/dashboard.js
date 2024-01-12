
const baseURL = window.location.protocol + "//" + window.location.hostname+ "/" +
    "/"


const setup = () => {


    // Function to parse cookies and retrieve a specific one
    function getCookie(name) {
        let cookieArray = document.cookie.split(';');
        for (let i = 0; i < cookieArray.length; i++) {
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
    let myPendingDoctors = [];
    let globalUserInfo = null; // Keep track of doctors with pending requests

    // Function to see  the available  doctor usernames which are all the doctors except the user"s doctors'
    async function availableDoctors(doctors) {
        const doctorList = document.getElementById('doctor-list');

        doctorList.innerHTML = '';  // Clear existing options
        if (!myPendingDoctors) {
            await loadDoctors();

        }
        doctors.forEach(doctor => {
            const listItem = document.createElement('li');
            listItem.classList.add('flex', 'items-center', 'justify-between', 'mb-2');

            const nameSpan = document.createElement('span');
            nameSpan.textContent = doctor;
            nameSpan.classList.add('text-gray-600');

            const inviteButton = document.createElement('button');
            inviteButton.textContent = myPendingDoctors.includes(doctor) ? 'Pending' : 'Send Invitation';
            inviteButton.classList.add('text-white', 'font-bold', 'py-1', 'px-2', 'rounded', 'text-xs');

            // Check if the doctor is in myPendingDoctors
            if (myPendingDoctors.includes(doctor)) {
                inviteButton.classList.add('bg-gray-500'); // Add gray background
                inviteButton.disabled = true; // Make the button not clickable
            } else {
                inviteButton.classList.add('bg-blue-500', 'hover:bg-blue-700'); // Add blue background
                inviteButton.onclick = function () {
                    // Get the doctor's name from the nameSpan
                    const doctorUsername = doctor;

                    // Execute the API to send the invitation to this doctor
                    sendInvitation(doctorUsername, inviteButton);
                };
            }

            listItem.appendChild(nameSpan);
            listItem.appendChild(inviteButton);

            doctorList.appendChild(listItem);
        });
    }

    // Send the invitation to the doctor if the doctor accept he will no longer be in the available doctor
    // but in the my doctor list instead
    async function sendInvitation(doctorUsername, inviteButton) {
        // Execute the API to send the invitation

        await fetch(`${baseURL}api/user/addDoctor/${doctorUsername}`, {
            method: 'POST',
            headers: headers,
        })
            .then(response => {
                if (response.ok) {
                    // Request was successful
                    createPopup(inviteButton, 'Request sent to doctor', 'bg-green-500');
                    updateInviteButton(inviteButton, "Pending", true);
                } else {
                    // Handle error response
                    return response.text().then(errorMessage => {
                        createPopup(inviteButton, errorMessage, 'bg-red-500');
                    });
                }
            })
            .catch(error => {
                // Handle fetch error
                console.error('Error sending invitation:', error);
                createPopup(inviteButton, error.message, 'bg-red-500');
            });
    }


// to display the error messages
    function createPopup(parentElement, message, bgColorClass) {
        const popup = document.createElement('div');
        popup.textContent = message;
        popup.classList.add(bgColorClass, 'text-white', 'text-sm', 'py-1', 'px-2', 'rounded', 'absolute', 'bottom-0', 'left-0', 'transform', 'translate-y-2', 'opacity-0', 'transition', 'opacity-100', 'duration-300');
        parentElement.parentNode.appendChild(popup);

        setTimeout(function () {
            popup.style.opacity = '1';
        }, 10);

        // Hide the popup after a few seconds
        setTimeout(function () {
            popup.style.opacity = '0';
            setTimeout(function () {
                parentElement.parentNode.removeChild(popup);
            }, 300);
        }, 3000);
    }

    // Function to update the invitation button
    function updateInviteButton(button, text, isPending) {
        button.textContent = text;
        if (isPending) {
            button.classList.add('bg-gray-500');
        } else {
            button.classList.remove('bg-gray-500');
        }
        button.disabled = isPending;
    }

    // funtion to display all the doctors
    function AllMyDoctors(doctors) {
        const mydoctorList = document.getElementById('my-doctor-list');

        mydoctorList.innerHTML = '';  // Clear existing options

        doctors.forEach(doctor => {
            const listItem = document.createElement('li');
            listItem.classList.add('flex', 'items-center', 'justify-between', 'mb-2');

            const nameSpan = document.createElement('span');
            nameSpan.textContent = doctor;
            nameSpan.classList.add('text-gray-600');


            listItem.appendChild(nameSpan);

            mydoctorList.appendChild(listItem);
        });
    }

    // fetch all my doctors

    async function loadDoctors() {
        // Endpoint for your API
        const apiURL = `${baseURL}api/user/getMyDoctors`;

        // Fetch the user's doctors
        await fetch(apiURL, {
            method: 'GET',
            headers: headers, // Ensure you include necessary headers for authorization if required
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                const myDoctors = data.MyDoctors || [];
                myPendingDoctors = data.MyDoctorsPending || [];
                AllMyDoctors(myDoctors);
                adjustHeight('my-doctor-list')
            })
            .catch(error => {
                console.error('Error fetching doctor data:', error);
            });
    }


    // Fetch the available doctors list
    async function getAllDoctors() {
        await fetch(`${baseURL}api/user/getAllDoctors`, {
            headers: headers,
            method: 'GET',
        })
            .then(response => {
                if (response.ok) {
                    // Request was successful, parse JSON response
                    return response.json();
                } else {
                    // Handle error response
                    return response.text().then(errorMessage => {
                        // Handle error as needed
                    });
                }
            })
            .then(doctors => {
                // Doctors have been successfully retrieved
                console.log('Doctors retrieved:', doctors);
                availableDoctors(doctors);
                adjustHeight('doctor-list')// Populate dropdown with doctors
            })
            .catch(error => {
                console.error('Error fetching doctors:', error);
            });
    }


    function adjustHeight(listId) {
        const maxItemsToShow = 5;
        const listElement = document.getElementById(listId);
        const items = listElement.querySelectorAll('li');

        // Calculate the total height of the first 'maxItemsToShow' items
        let totalHeight = 0;
        for (let i = 0; i < Math.min(items.length, maxItemsToShow); i++) {
            totalHeight += items[i].clientHeight + (i < maxItemsToShow - 1 ? 3 : 0); // Add space between items if not the last visible one
        }

        // Set the maximum height and enable scrolling if there are more than 'maxItemsToShow' items
        listElement.style.maxHeight = `${totalHeight}px`;
        listElement.style.overflowY = items.length > maxItemsToShow ? 'scroll' : 'hidden';
    }

// Call this function after the lists are populated


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


    async function getUserHealthData() {
        if (!globalUserInfo) {
            await getUserInfo();
        }
        displayHealthData(globalUserInfo.healthData);
    }

    function displayHealthData({age, cholesterol, gluc, alco, smoke, active}) {
        // Assuming you have HTML elements with the following IDs in your sidebar
        document.getElementById('age-value').textContent = age;
        document.getElementById('cholesterol-value').textContent = mapCholesterol(cholesterol);
        document.getElementById('smoke-value').textContent = mapSmoke(smoke);
        document.getElementById('active-value').textContent = mapActive(active);
        document.getElementById('alco-value').textContent = mapAlco(alco);
        document.getElementById('gluc-value').textContent = mapGluc(gluc);
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

    async function fetchAndUpdateUserInfo() {

        if (globalUserInfo == null) {
            await getUserInfo();

        }
        // Update the username and role
        document.getElementById('user-name').textContent = globalUserInfo.username;
        document.getElementById('user-role').textContent = globalUserInfo.role;
            // Set the text content for both main content and sidebar
            document.getElementById('sidebar-user-name').textContent = globalUserInfo.username;

            document.getElementById('sidebar-user-role').textContent =globalUserInfo.role;




        // Create initials from the username
        const initials = globalUserInfo.username ? globalUserInfo.username.split(' ').map(n => n[0]).join('').toUpperCase() : '--';
        document.getElementById('user-initials').textContent = initials;
        document.getElementById('sidebar-user-initials').textContent = initials;


    }


    // Function to fetch sensor data from the API and update the dashboard
    async function updateSensorData() {
        if (!globalUserInfo) {
            await getUserInfo();
        }
        displaySensorData(globalUserInfo.sensorsData);

        updateHealthStatus(globalUserInfo.sensorsData.apHi,globalUserInfo.sensorsData.saturationData,globalUserInfo.sensorsData.heartRateData)
    }
// Function to display the sensor data on the dashboard
    function displaySensorData({apHi, apLo, heartRateData, saturationData, temp}) {


        // Update heart rate
        const heartRateValueElement = document.querySelector('#heart-rate-value .text-2xl');
        heartRateValueElement.textContent = heartRateData;

        // Update temperature
        const temperatureValueElement = document.querySelector('#temperature-value .text-2xl');
        temperatureValueElement.textContent = temp;

        // Update blood pressure (systolic/diastolic)
        const bloodPressureValueElement = document.querySelector('#blood-pressure-value .text-2xl');
        bloodPressureValueElement.textContent = `${apHi}/${apLo}`;

        // Update oxygen level
        const oxygenLevelValueElement = document.querySelector('#oxygen-level-value .text-2xl');
        oxygenLevelValueElement.textContent = `${saturationData}%`;

        // Add similar updates for any other sensor data you want to display
    }

    function updateHealthStatus(apHi,saturationData,heartRateData) {
        fetch(`${baseURL}api/user/getHealthStatus`,
            {
                headers: headers,
                method: 'GET'
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(healthStatus => {
                // Update and color code BMI status
                const bmiElement = document.getElementById('bmi-value');
                bmiElement.textContent = healthStatus.bmi.toFixed(2);
                bmiElement.className = getBMIColor(healthStatus.bmi_status);

                // Update and color code blood pressure status
                const bloodPressureElement = document.getElementById('blood-pressure-status-value');
                bloodPressureElement.textContent = healthStatus.blood_pressure_status;
                bloodPressureElement.className = getBloodPressureColor(healthStatus.blood_pressure_status);

                // Update and color code saturation status
                const saturationElement = document.getElementById('saturation-value');
                saturationElement.textContent = healthStatus.saturation_status;
                saturationElement.className = getSaturationColor(healthStatus.saturation_status);

                // Update and color code heart rate status
                const heartRateElement = document.getElementById('heart-rate-status-value');
                heartRateElement.textContent = healthStatus.heart_rate_status;
                heartRateElement.className = getHeartRateColor(healthStatus.heart_rate_status);


                if (
                    (healthStatus.blood_pressure_status !== 'Normal' && apHi !== 0) ||
                    (healthStatus.saturation_status !== 'Normal' && saturationData !== 0) ||
                    (healthStatus.heart_rate_status !== 'Maximum' && healthStatus.heart_rate_status !== 'Hard' && heartRateData !== 0)
                ) {
                    getUserLocation();

                }

            })
            .catch(error => {
                console.error('Error fetching health status:', error);
            });
    }

    function getBMIColor(bmiStatus) {
        switch (bmiStatus) {
            case 'Underweight':
                return 'text-blue-600';
            case 'Normal':
                return 'text-green-600';
            case 'Overweight':
                return 'text-yellow-600';
            case 'Obese':
                return 'text-red-600';
            default:
                return 'text-gray-600';
        }
    }

    function getBloodPressureColor(bloodPressureStatus) {
        switch (bloodPressureStatus) {
            case 'High':
                return 'text-red-600';
            case 'Low':
                return 'text-blue-600';
            case 'Normal':
                return 'text-green-600';
            default:
                return 'text-gray-600';
        }
    }

    function getSaturationColor(saturationStatus) {
        switch (saturationStatus) {
            case 'Low':
                return 'text-blue-600';
            case 'Normal':
                return 'text-green-600';
            default:
                return 'text-gray-600';
        }
    }

    function getHeartRateColor(heartRateStatus) {
        switch (heartRateStatus) {
            case 'Very Light':
                return 'text-blue-600';
            case 'Moderate':
                return 'text-green-600';
            case 'Hard':
                return 'text-yellow-600';
            case 'Maximum':
                return 'text-red-600';
            default:
                return 'text-gray-600';
        }
    }

    async function getPrediction() {
        // Disable the button and show the spinner
        var predictButton = document.getElementById('predict-button');
        predictButton.disabled = true;
        var spinner = document.getElementById('spinner');

        spinner.classList.remove('hidden');
        // Assuming baseURL is already defined in your script
        await fetch(`${baseURL}api/user/predict`, {
            method: 'POST',
            headers: headers,
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                displayPredictionPopup(data);
                // Re-enable the button after 10 seconds
                setTimeout(function () {
                    predictButton.disabled = false;
                    spinner.classList.add('hidden');
                    hidePredictionPopup();
                }, 5000);
            })
            .catch(error => {
                console.error('Error:', error);
                // Re-enable the button on error
                predictButton.disabled = false;
                spinner.classList.add('hidden');
            });


    }


    function displayPredictionPopup(prediction) {
        var predictionPopup = document.getElementById("prediction-popup");
        var predictionMessage = document.getElementById("prediction-message");

        // Reset classes and make sure the popup is visible
        predictionPopup.classList.remove("success", "failure");
        predictionPopup.classList.add("visible");

        if (prediction == 0) {
            predictionMessage.textContent = "Your heart is well.";
            predictionPopup.classList.add("success");
        } else if (prediction == 1) {
            predictionMessage.textContent = "You might have a heart failure. Please contact your doctor.";
            predictionPopup.classList.add("failure");
        } else {
            predictionMessage.textContent = "Unable to determine the prediction.";
        }
    }

    function hidePredictionPopup() {
        var predictionPopup = document.getElementById("prediction-popup");
        predictionPopup.classList.remove("visible");
    }


    function getUserLocation() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    const { latitude, longitude } = position.coords;
                    console.log(`Latitude: ${latitude}, Longitude: ${longitude}`);

                    // Now you can use these coordinates as needed, for example, send them to your server
                    sendCoordinatesInEmail(latitude, longitude);
                },
                (error) => {
                    console.error('Error getting user location:', error.message);
                }
            );
        } else {
            console.error('Geolocation is not supported by this browser.');
        }
    }
    async function sendCoordinatesInEmail(latitude, longitude) {
        await fetch(`${baseURL}api/user/send-email/${latitude}/${longitude}`, {
            method: 'POST',
            headers: headers,
        })
            .then(response => {
                if (response.ok) {
                    // Request was successful
                    return response.json();
                } else {
                    // Handle error response
                    return response.text().then(errorMessage => {
                        throw new Error(errorMessage);
                    });
                }
            })
            .then(data => {
                // Handle the response from the backend if needed
                console.log('Email sent successfully:', data);
            })
            .catch(error => {
                // Handle fetch error
                console.error('Error sending email:', error);
            });
    }



    // Fetch doctors when the page loads
    document.addEventListener('DOMContentLoaded', getAllDoctors);
    document.addEventListener('DOMContentLoaded', loadDoctors);
    document.addEventListener('DOMContentLoaded', getUserInfo());
    document.addEventListener('DOMContentLoaded', updateSensorData());
    document.addEventListener('DOMContentLoaded', fetchAndUpdateUserInfo());
    // document.addEventListener('DOMContentLoaded', updateHealthStatus());
    document.addEventListener('DOMContentLoaded', function () {
        // Get the button by its ID
        var predictButton = document.getElementById('predict-button');

        // Ensure the button exists
        if (predictButton) {
            // Attach the event listener to the button
            predictButton.addEventListener('click', getPrediction);
        }
    });
    // Trigger the openSidebar function when the button is clicked
// This function could be in your dashboard.js or another appropriate file


    document.getElementById('user-menu').addEventListener('click', function() {
        console.log('button clicked!')
        document.getElementById('sidebar').classList.add('sidebar-open');
        getUserHealthData()
    });

    document.getElementById('close-sidebar').addEventListener('click', function() {
        document.getElementById('sidebar').classList.remove('sidebar-open');
    });



// Initialize the sidebar setup when the DOM is fully loaded
}
// Initialize the setup when the script loads
const dashboard = setup();