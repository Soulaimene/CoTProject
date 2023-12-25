document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('loginForm').addEventListener('submit', function (event) {
        event.preventDefault();

        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        // Determine the role based on user selection


        // Make an HTTP request using Axios
        axios.post("https://lifeguardian.local/lifeguardian/api/oauth2/login", {
            grand_type: 'PASSWORD',
            username: username,
            password: password,
        })
            .then(response => {
                // Handle successful response
                const { accessToken, refreshToken } = response.data;

                // Save tokens in cookies
                document.cookie = `accessToken=${accessToken}; path=/`;
                document.cookie = `refreshToken=${refreshToken}; path=/`;
                console.log("Connected")
                // window.location.href = './index.html';
            })
            .catch(error => {
                // Handle error
                if (error.response) {
                    console.error(error.response.data);
                    console.error(error.response.status);
                    console.error(error.response.headers);
                } else if (error.request) {
                    console.error(error.request);
                } else {
                    console.error('Error', error.message);
                }
            });

    });
});
