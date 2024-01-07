const isLocal = window.location.hostname === 'localhost';

window.env = {
    API_KEY: isLocal ? 'local-api-key' : 'gcp-api-key',
    BASE_URL: isLocal ? 'http://localhost:8080' : 'https://lifeguardian1.me',
};