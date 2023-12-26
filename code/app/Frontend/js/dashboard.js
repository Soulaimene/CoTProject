const setup = () => {
    function getSidebarStateFromLocalStorage() {
        if (window.localStorage.getItem('isSidebarOpen')) {
            return JSON.parse(window.localStorage.getItem('isSidebarOpen'))
        }
        return false;  // Default state
    }

    function setSidebarStateToLocalStorage(value) {
        window.localStorage.setItem('isSidebarOpen', value)
    }

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
    let myPendingDoctors = [];  // Keep track of doctors with pending requests

    // Function to populate the dropdown with doctor usernames
    function availableDoctors(doctors) {
        const doctorList = document.getElementById('doctor-list');

        doctorList.innerHTML = '';  // Clear existing options

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
    function sendInvitation(doctorUsername, inviteButton) {
        // Execute the API to send the invitation
        fetch(`http://localhost:8080/lifeguardian-1.0-SNAPSHOT/api/user/addDoctor/${doctorUsername}`, {
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

    function loadDoctors() {
        // Endpoint for your API
        const apiURL = 'http://localhost:8080/lifeguardian-1.0-SNAPSHOT/api/user/getMyDoctors';

        // Fetch the user's doctors
        fetch(apiURL, {
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
            })
            .catch(error => {
                console.error('Error fetching doctor data:', error);
            });
    }







    // Fetch the list of doctors from the API

    // Fetch the list of doctors from the API
    function getAllDoctors() {
        fetch('http://localhost:8080/lifeguardian-1.0-SNAPSHOT/api/user/getAllDoctors', {
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
                availableDoctors(doctors);  // Populate dropdown with doctors
            })
            .catch(error => {
                console.error('Error fetching doctors:', error);
            });
    }

    // Fetch doctors when the page loads
    document.addEventListener('DOMContentLoaded', getAllDoctors);
    document.addEventListener('DOMContentLoaded', loadDoctors);


    return {
        loading: true,
        isSidebarOpen: getSidebarStateFromLocalStorage(),
        toggleSidbarMenu() {
            this.isSidebarOpen = !this.isSidebarOpen
            setSidebarStateToLocalStorage(this.isSidebarOpen)
        },
        isSettingsPanelOpen: false,
        isSearchBoxOpen: false,
    }

}
// Initialize the setup when the script loads
const dashboard = setup();
