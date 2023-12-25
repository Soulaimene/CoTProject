document.addEventListener('DOMContentLoaded', function () {
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const errorContainer = document.getElementById('error-container');
    const spinner = document.getElementById('spinner');
    document.getElementById('loginForm').addEventListener('submit', function (event) {
        event.preventDefault();

        const username = usernameInput.value;
        const password = passwordInput.value;
        spinner.style.display = 'flex';
        // Make an HTTP request using Axios
        axios.post("https://lifeguardian.local/lifeguardian/api/oauth2/login", {
            grand_type: 'PASSWORD',
            username: username,
            password: password,
        })
            .then(response => {
                // Handle successful response
                spinner.style.display = 'none';
                const { accessToken, refreshToken } = response.data;

                // Save tokens in cookies
                document.cookie = `accessToken=${accessToken}; path=/`;
                document.cookie = `refreshToken=${refreshToken}; path=/`;

                window.location.href = './dashboard.html';
            })
            .catch(error => {
                spinner.style.display = 'none';
                // Handle error
                if (error.response) {
                    console.error(error.response.data);
                    console.error(error.response.status);
                    console.error(error.response.headers);

                    // Display error container with red background and opacity
                    errorContainer.style.display = 'flex';
                } else if (error.request) {
                    console.error(error.request);
                } else {
                    console.error('Error', error.message);
                }
            })
            .finally(() => {
                // Hide the spinner covering the entire page when the request is complete
                spinner.style.display = 'none';
            });
    });

    // Add event listeners to hide the error container when typing starts
    usernameInput.addEventListener('input', function () {
        errorContainer.style.display = 'none';
    });

    passwordInput.addEventListener('input', function () {
        errorContainer.style.display = 'none';
    });
});
