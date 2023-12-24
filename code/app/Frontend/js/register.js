document.addEventListener('DOMContentLoaded', function () {
    const username = document.getElementById("username")
    const email = document.getElementById("email")

    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const passwordError = document.getElementById('passwordError');
    const userTypeSelect = document.getElementById('userType');
    const submitButton = document.getElementById('submitButton')
    const togglesField = document.getElementById("togglesField")


    //
    // Methods calls
    //
    handleUserTypeChange();


    //
    // Add Event Listeners for out methods
    //
    userTypeSelect.addEventListener('change', handleUserTypeChange);


    //
    // Methods Implementation
    //
    function handleUserTypeChange() {
        // Check the user type and show/hide fields accordingly
        if (userTypeSelect.value === 'Patient') {
            togglesField.style.display= 'block'
        } else {
            togglesField.style.display= 'none';

        }
    }







    // Add event listener for form submission
    document.getElementById('loginForm').addEventListener('submit', function (event) {
        event.preventDefault();
        const weight = document.getElementById("weight")
        const height = document.getElementById("height")
        const age = document.getElementById("age")
        const gender = document.getElementById("gender")
        const cholesterolLevel = document.getElementById("cholesterol")
        const gluc = document.getElementById("glucose")
        const cardio = document.getElementById('CardioDisease')
        const smoking = document.getElementById('smokingSwitch')
        const alc = document.getElementById('alcoholSwitch')
        const active = document.getElementById('activitySwitch')


        // Check if passwords match
        if (passwordInput.value !== confirmPasswordInput.value) {
            passwordError.classList.remove('hidden');
            return; // Stop form submission if passwords do not match
        } else {
            passwordError.classList.add('hidden');
            let role;
            if (userTypeSelect.value() === 'Patient') {
                role = 'User';
            } else if (userTypeSelect.value() === 'Doctor') {
                role = 'Doctor';
            }

        }
    });
});
