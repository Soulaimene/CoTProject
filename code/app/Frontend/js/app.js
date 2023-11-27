document.getElementById('loginForm').addEventListener('submit', function(event) {
    event.preventDefault(); // Prevent the default form submission

    // Fetch data from form
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    // Create an object with the user data
    const userData = {
        username: email,  // Assuming your backend uses 'username' instead of 'email'
        password: password
    };

    // Make a POST request to your register API
    fetch('https:/lifeguardian.local/lifeguardian/api/auth/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(userData),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            // Handle successful response
            console.log('Registration successful', data);
        })
        .catch(error => {
            // Handle errors
            console.error('Error during registration:', error);
        });
});




















// to register the sw.js :
if("serviceWorker" in navigator){
    navigator.serviceWorker.register('./sw.js')
        .then((reg) => console.log("service worker registered",reg))
        .catch((err) => console.log("service worker not registered",err));
}

