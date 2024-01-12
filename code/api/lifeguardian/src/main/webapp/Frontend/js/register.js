
const baseURL = window.location.protocol + "//" + window.location.hostname + "//"

document.addEventListener('DOMContentLoaded', function () {
    const username = document.getElementById("username");
    const email = document.getElementById("email");
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const passwordError = document.getElementById('passwordError');
    const userTypeSelect = document.getElementById('userType');
    const submitButton = document.getElementById('submitButton');
    const togglesField = document.getElementById("togglesField");

    const emptyFields = [];
    let endpoint = baseURL + "api/register";
    let smoke = 0;
    const toggleValues = {
        smokingSwitch: 0,
        activitySwitch: 0,
        alcoholSwitch: 0,
        CardioDisease: 0
    };

    //
    // Methods calls
    //
    handleUserTypeChange();
    addToggleListener('smokingSwitch');
    addToggleListener('activitySwitch');
    addToggleListener('alcoholSwitch');
    addToggleListener('CardioDisease');

    //
    // Add Event Listeners for our methods
    //
    userTypeSelect.addEventListener('change', handleUserTypeChange);

    //
    // Methods Implementation
    //
    function handleUserTypeChange() {
        // Check the user type and show/hide fields accordingly
        if (userTypeSelect.value === 'Patient') {
            togglesField.style.display = 'block';
        } else {
            togglesField.style.display = 'none';
        }
    }

    function addToggleListener(switchId) {
        const toggleSwitch = document.getElementById(switchId);

        if (toggleSwitch) {
            toggleSwitch.addEventListener('click', function () {
                const switchChecked = toggleSwitch.checked;

                // Create a dynamic variable name based on the switch ID
                const variableName = `${switchId.charAt(0).toUpperCase() + switchId.slice(1)}`;

                // Update the toggleValues object
                toggleValues[variableName] = switchChecked ? 1 : 0;
            });
        }
    }
    function mapGenderToInteger(genderString) {
        // Assuming 'Male' maps to 0 and 'Female' maps to 1
        return genderString.toLowerCase() === 'male' ? 0 : 1;
    }

    // Add event listener for form submission
    document.getElementById('SignupForm').addEventListener('submit', function (event) {
        event.preventDefault();

        if (isEmpty(username)) emptyFields.push(username);
        if (isEmpty(email)) emptyFields.push(email);
        if (isEmpty(confirmPasswordInput)) emptyFields.push(confirmPasswordInput);
        if (isEmpty(passwordInput)) emptyFields.push(passwordInput);

        // Check if passwords match
        if (passwordInput.value !== confirmPasswordInput.value) {
            passwordError.classList.remove('hidden');
        } else {
            passwordError.classList.add('hidden');
            let role;
            if (userTypeSelect.value === 'Patient') {
                role = 'User';
                const weight = document.getElementById("weight");
                const height = document.getElementById("height");
                const age = document.getElementById("age");
                const gender = document.getElementById("gender");
                const genderValue = mapGenderToInteger(gender.value);
                const EmergencyContact = document.getElementById("EmergencyEmail");
                const CholesterolLevel = document.getElementById("cholesterol");
                const gluc = document.getElementById("glucose");


                // Check if any of the fields is empty
                if (isEmpty(weight)) emptyFields.push(weight);
                if (isEmpty(EmergencyContact)) emptyFields.push(EmergencyContact);
                if (isEmpty(height)) emptyFields.push(height);
                if (isEmpty(age)) emptyFields.push(age);
                if (isEmpty(gender)) emptyFields.push(gender);
                const data = {
                    email: email.value,
                    username: username.value,
                    password: passwordInput.value,
                    role: role,
                    emergencyContactEmail: EmergencyContact.value,
                    healthData: {
                        age: parseInt(age.value),
                        height: parseInt(height.value),
                        weight: parseInt(weight.value),
                        gender: mapGenderToInteger(gender.value),
                        cholesterol: parseInt(CholesterolLevel.value),
                        gluc: parseInt(gluc.value),
                        smoke: toggleValues.smokingSwitch,
                        alco: toggleValues.alcoholSwitch,
                        active: toggleValues.activitySwitch
                    },
                    prediction: 0
                };
                RequestAPI(endpoint, data);
            } else if (userTypeSelect.value === 'Doctor') {
                role = 'Doctor';
                const data = {
                    email: email.value,
                    username: username.value,
                    password: passwordInput.value,
                    role: role}
                RequestAPI(endpoint, data);
            }
        }

        if (emptyFields.length > 0) {
            emptyFields.forEach(field => {
                field.classList.add('empty-field', 'vibrate');
                field.addEventListener('animationend', () => {
                    field.classList.remove('empty-field', 'vibrate');
                }, { once: true });
            });
        }
    });
});

    function isEmpty(input) {
    const isEmpty = input.value.trim() === '';
    return isEmpty;
    }

    function RequestAPI(endpoint, data) {
        const username = document.getElementById("username");
        username.addEventListener('input', () => {
            const errorContainer = document.getElementById('error-container');
            const errorMessage = document.querySelector('#error-container .text-white');
            errorContainer.classList.add('hidden');
            errorMessage.textContent = '';
        });

        const spinner = document.getElementById('spinner');
        spinner.style.display = 'block';

        // Build the request parameters
        const requestOptions = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        };

        fetch(endpoint, requestOptions)
            .then(response => {
                if (!response.ok) {
                    return response.text()
                }
            })
            .then(responseMessage => {
                const errorContainer = document.getElementById('error-container');
                const errorMessage = document.querySelector('#error-container .error-msg');

                if (responseMessage) {
                    errorMessage.textContent = responseMessage;
                    errorContainer.classList.remove('hidden');

                } else {
                    window.location.href = './login.html';
                }
            })
            .catch(error => {
                console.error('Error', error.message);
            })
            .finally(() => {
                spinner.style.display = 'none';
            });
    }