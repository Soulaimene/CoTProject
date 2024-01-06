const  baseURL = "https://lifeguardian.local/api";
const LoginURL =baseURL+'/api/oauth2/login';
const CurrentUserUrl = baseURL+'/api/current-user';

document.addEventListener('DOMContentLoaded', function () {
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const errorContainer = document.getElementById('error-container');
    const spinner = document.getElementById('spinner');
    document.getElementById('loginForm').addEventListener('submit', function (event) {
        event.preventDefault();
        const username = usernameInput.value;
        const password = passwordInput.value;
        login()

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

        function getHeaders() {
            const accessToken = getCookie('accessToken');  // Get the token from cookies

            if (!accessToken) {
                console.error('Access token not found');
            } else {
                return new Headers({
                    'Authorization': `Bearer ${accessToken}`,
                    'Content-Type': 'application/json'
                })
            }
        }

        function getCurrentUserRole() {
            fetch(CurrentUserUrl, {
                method: 'GET',
                headers: getHeaders(),
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! Status: ${response.status}`);
                    }
                    return response.json();
                })
                .then(data => {
                    // Process the response data
                    console.log(data)
                    const userRole = data.role;
                    if (userRole === 'User') {
                        window.location.href = './dashboard.html';
                    } else if (userRole === 'Doctor') {
                        window.location.href = './doctorPage.html';
                    } else {
                        console.error('Unknown user role');
                    }
                })
                .catch(error => {
                    console.error('Error:', error.message);
                });
        }

        function login() {
            spinner.style.display = 'flex';

            const requestOptions = {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    grand_type: 'PASSWORD',
                    username: username,
                    password: password,
                }),
            };

            fetch(LoginURL, requestOptions)
                .then(response => {
                    spinner.style.display = 'none';

                    if (!response.ok) {
                        throw new Error(`HTTP error! Status: ${response.status}`);
                    }

                    return response.json();
                })
                .then(data => {
                    const {accessToken, refreshToken} = data;
                    document.cookie = `accessToken=${accessToken}; path=/`;
                    document.cookie = `refreshToken=${refreshToken}; path=/`;
                    getCurrentUserRole();
                })
                .catch(error => {
                    spinner.style.display = 'none';
                    console.error('Fetch error:', error);
                    errorContainer.style.display = 'flex';
                })
                .finally(() => {
                    spinner.style.display = 'none';
                });

            usernameInput.addEventListener('input', () => {
                errorContainer.style.display = 'none';
            });

            passwordInput.addEventListener('input', () => {
                errorContainer.style.display = 'none';
            });
        }


    });
})