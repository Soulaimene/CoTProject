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

    // Function to populate the dropdown with doctor usernames
    function populateDoctors(doctors) {
        const doctorList = document.getElementById('doctor-list');

        doctorList.innerHTML = '';  // Clear existing options

        doctors.forEach(doctor => {
            const listItem = document.createElement('li');
            listItem.classList.add('flex', 'items-center', 'justify-between', 'mb-2');

            const nameSpan = document.createElement('span');
            nameSpan.textContent = doctor;
            nameSpan.classList.add('text-gray-600');

            const inviteButton = document.createElement('button');
            inviteButton.textContent = 'Send Invitation';
            inviteButton.classList.add('bg-blue-500', 'hover:bg-blue-700', 'text-white', 'font-bold', 'py-1', 'px-2', 'rounded', 'text-xs');
            inviteButton.onclick = function() {
                // Get the doctor's name from the nameSpan
                const doctorUsername = doctor;

                // Execute the API to send the invitation to this doctor
                sendInvitation(doctorUsername, inviteButton);
            };

            listItem.appendChild(nameSpan);
            listItem.appendChild(inviteButton);

            doctorList.appendChild(listItem);
        });
    }

// Function to send the invitation
    function sendInvitation(doctorUsername, inviteButton) {
        if (!headers) {
            console.error('Access token not found');
            return;
        }

        // Execute the API to send the invitation
        fetch(`http://localhost:8080/lifeguardian-1.0-SNAPSHOT/api/user/addDoctor/${doctorUsername}`, {
            method: 'POST',
            headers: headers,
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.text();
            })
            .then(responseText => {
                // Handle the success response
                console.log(responseText);

                // Display a popup message
                const popup = document.createElement('div');
                popup.textContent = 'Request sent';
                popup.classList.add('bg-green-500', 'text-white', 'text-sm', 'py-1', 'px-2', 'rounded', 'absolute', 'bottom-0', 'left-0', 'transform', 'translate-y-2', 'opacity-0', 'transition', 'opacity-100', 'duration-300');
                inviteButton.parentNode.appendChild(popup);

                setTimeout(function() {
                    popup.style.opacity = '1';
                }, 10);

                // Hide the popup after a few seconds
                setTimeout(function() {
                    popup.style.opacity = '0';
                    setTimeout(function() {
                        inviteButton.parentNode.removeChild(popup);
                    }, 300);
                }, 3000);
            })
            .catch(error => {
                // Handle the error response
                console.error(error);
            });
    }



    // Fetch the list of doctors from the API
    function getAllDoctors() {




        fetch('http://localhost:8080/lifeguardian-1.0-SNAPSHOT/api/user/getAllDoctors', { headers: headers })
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                return response.json();
            })
            .then(doctors => {
                console.log('Doctors retrieved:', doctors);
                populateDoctors(doctors);  // Populate dropdown with doctors
            })
            .catch(error => {
                console.error('Error fetching doctors:', error);
            });
    }

    // Fetch doctors when the page loads
    document.addEventListener('DOMContentLoaded', getAllDoctors);

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
