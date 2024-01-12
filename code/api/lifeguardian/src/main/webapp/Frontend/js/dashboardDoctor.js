
const baseURL = window.location.protocol + "//" + window.location.hostname+ "//"


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

    // Function to see  the available  doctor usernames which are all the doctors except the user"s doctors'
    async function displayPendingPatients() {
        const pendingPatientsList = document.getElementById('pending-patients-list');

        pendingPatientsList.innerHTML = ''; // Clear existing options

        const response = await fetch(`${baseURL}api/doctor/getAllPendingPatients`, {
            headers: headers,
            method: 'GET',
        });
        if (!response.ok) {
            console.error('Error fetching pending patients:', response.statusText);
            return;
        }
        const patients = await response.json();

        patients.forEach(patient => {
            const listItem = document.createElement('li');
            listItem.classList.add('flex', 'items-center', 'justify-between', 'mb-2');

            const nameSpan = document.createElement('span');
            nameSpan.textContent = patient;
            nameSpan.classList.add('text-gray-600');

            const acceptButton = document.createElement('button');
            acceptButton.textContent = 'Accept';
            acceptButton.classList.add('text-white', 'font-bold', 'py-1', 'px-2', 'rounded', 'text-xs', 'bg-green-500', 'hover:bg-green-600');
            acceptButton.onclick = function () {
                selectPendingPatient(patient, acceptButton,rejectButton,'accept');
            };

            const rejectButton = document.createElement('button');
            rejectButton.textContent = 'Reject';
            rejectButton.classList.add('text-white', 'font-bold', 'py-1', 'px-2', 'rounded', 'text-xs', 'bg-red-500', 'hover:bg-red-600');
            rejectButton.onclick = function () {
                selectPendingPatient(patient, rejectButton,acceptButton, 'rejected');
            };

            listItem.appendChild(nameSpan);
            listItem.appendChild(acceptButton);
            listItem.appendChild(rejectButton);

            pendingPatientsList.appendChild(listItem);


        });
    }


    // Function to accept or reject a pending patient
    async function selectPendingPatient(patientUsername, selectedButton, otherButton, status) {
        // Assuming headers are already set up correctly with 'Content-Type': 'application/json'
        await fetch(`${baseURL}api/doctor/selectPendingUser/${patientUsername}/${status}`, {
            method: 'POST',
            headers: headers,
        })
            .then(response => {
                if (response.ok) {
                    // Apply visual changes based on the response
                    if (status === 'accept') {
                        selectedButton.textContent = 'Accepted';
                        selectedButton.classList.replace('bg-green-500', 'bg-gray-500');
                        selectedButton.parentNode.style.textDecoration = 'none'; // Remove any text decoration
                        selectedButton.parentNode.style.color = 'green'; // Change text color to green
                        otherButton.style.display = 'none'; // Hide the reject button
                    } else if (status === 'rejected') {
                        selectedButton.textContent = 'Rejected';
                        selectedButton.classList.replace('bg-red-500', 'bg-gray-500');
                        selectedButton.parentNode.style.color = 'red'; // Change text color to red
                        otherButton.style.display = 'none'; // Hide the accept button
                    }
                    selectedButton.disabled = true; // Disable the selected button after action
                } else {
                    // Handle error response
                    console.error('Error updating patient status:', response.statusText);
                }
            })
            .catch(error => {
                // Handle fetch error
                console.error('Error:', error);
            });
    }


    async function loadPatients() {
        const myPatientsList = document.getElementById("my-patients-list");

        if (globalUserInfo == null) {
            await getUserInfo();
        }

        const myPatients = globalUserInfo.patients;
        myPatients.forEach(patient => {
            const listItem = document.createElement('li');
            listItem.classList.add('flex', 'items-center', 'justify-between', 'mb-2');

            const nameSpan = document.createElement('span');
            nameSpan.textContent = patient;
            nameSpan.classList.add('text-gray-600');

            const viewButton = document.createElement('button');
            viewButton.textContent = 'View';
            viewButton.classList.add('text-white', 'font-bold', 'py-1', 'px-2', 'rounded', 'text-xs', 'bg-blue-500', 'hover:bg-blue-700');
            viewButton.onclick = function () {
                window.location.href = `doctor_to_patient.html?patientUsername=${encodeURIComponent(patient)}`;
            };

            listItem.appendChild(nameSpan);
            listItem.appendChild(viewButton); // Append the button to the list item
            myPatientsList.appendChild(listItem); // Append the list item to the list
        });
    }



    document.addEventListener('DOMContentLoaded',getUserInfo());
    document.addEventListener('DOMContentLoaded',fetchAndUpdateUserInfo());
    document.addEventListener('DOMContentLoaded', displayPendingPatients);

    document.addEventListener('DOMContentLoaded', loadPatients);
    document.addEventListener('selectPendingPatient', loadPatients);









}
// Initialize the setup when the script loads
const dashboard = setup();