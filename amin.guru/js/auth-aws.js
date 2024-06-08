/*
Authenticate with cognito using apikey
const AWS = require('aws-sdk');
const cognito = new AWS.CognitoIdentityServiceProvider();
const cognitoIdentity = new AWS.CognitoIdentity();
*/

const authurl = "https://anycompanyflight.auth.eu-west-1.amazoncognito.com/login?client_id=5m7v7n3imr4i4hn1s0rrhu4v8b&response_type=token&scope=aws.cognito.signin.user.admin+email+openid+phone+profile&redirect_uri=https%3A%2F%2Famin.guru%2Fwsaws.html"

document.addEventListener('DOMContentLoaded', () => {
    // Function to parse the URL hash to get the access token
    function parseHash(hash) {
        let params = {};
        hash.substring(1).split('&').forEach(param => {
            let [key, value] = param.split('=');
            params[key] = decodeURIComponent(value);
        });
        return params;
    }

    // Function to display the authentication status
    function displayAuthStatus() {
        const authStatusDiv = document.getElementById('auth-status');
        const hash = window.location.hash;
        const params = parseHash(hash);

        if (params.access_token) {
            console.log('Authentication successful! Access Token:',params.access_token);
            authStatusDiv.innerHTML = `<p>Authentication successful! Access Token: ${params.access_token}</p>`;
            // Optionally, you can add more actions here, like storing the tokens in localStorage
            // localStorage.setItem('access_token', params.access_token);
        } else {
            // Redirect to index.html if token is not present
            window.location.href = 'index.html';
        }
    }

    // Run the displayAuthStatus function on page load
    if (window.location.hash) {
        displayAuthStatus();
    } else {
        // Redirect to index.html if no hash is present
        console.log('no hash');
        window.location.href = 'index.html';
    }
});

// ---------------------------------

/*

// to hndle code flow
document.addEventListener('DOMContentLoaded', () => {
    async function getToken(authorizationCode) {
        const clientId = '5m7v7n3imr4i4hn1s0rrhu4v8b';
        const redirectUri = 'https://amin.guru/wsaws.html';
        const tokenUrl = 'https://anycompanyflight.auth.eu-west-1.amazoncognito.com/oauth2/token';

        const body = new URLSearchParams({
            grant_type: 'authorization_code',
            client_id: clientId,
            redirect_uri: redirectUri,
            code: authorizationCode
        });

        try {
            const response = await fetch(tokenUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: body.toString()
            });

            if (response.ok) {
                const data = await response.json();
                return data;
            } else {
                throw new Error('Token request failed');
            }
        } catch (error) {
            console.error('Error fetching token:', error);
        }
    }

    function displayAuthStatus() {
        const authStatusDiv = document.getElementById('auth-status');
        const urlParams = new URLSearchParams(window.location.search);
        const code = urlParams.get('code');

        if (code) {
            getToken(code).then(data => {
                if (data) {
                    authStatusDiv.innerHTML = `<p>Authentication successful! Access Token: ${data.access_token}</p>`;
                    // You can also store the tokens in local storage or a cookie for later use
                } else {
                    authStatusDiv.innerHTML = '<p>Authentication failed or cancelled.</p>';
                }
            });
        } else {
            authStatusDiv.innerHTML = '<p>No authorization code found in URL.</p>';
        }
    }

    displayAuthStatus();
});

*/

