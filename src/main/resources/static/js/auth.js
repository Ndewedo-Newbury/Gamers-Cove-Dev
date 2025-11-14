// Firebase configuration
const firebaseConfig = {
    apiKey: "AIzaSyDmvxeKXRJNJH-MY--9PpodY7_vpYsnZaI",
    authDomain: "gamers-cove-profile.firebaseapp.com",
    projectId: "gamers-cove-profile",
    storageBucket: "gamers-cove-profile.appspot.com",
    messagingSenderId: "106337419128",
    appId: "1:106337419128:web:8a1b3c4d5e6f7g8h9i0j1"
};

// Initialize Firebase
firebase.initializeApp(firebaseConfig);
const auth = firebase.auth();
const db = firebase.firestore();

// Store the current user's ID token
let currentUserToken = null;

// Function to get the current user's ID token
async function getIdToken(forceRefresh = false) {
    const user = auth.currentUser;
    if (!user) return null;
    
    try {
        currentUserToken = await user.getIdToken(forceRefresh);
        return currentUserToken;
    } catch (error) {
        console.error('Error getting ID token:', error);
        return null;
    }
}

// Set up request interceptor to add auth token to all requests
const originalFetch = window.fetch;
window.fetch = async function(resource, options = {}) {
    // Only add token to same-origin API requests
    const isApiRequest = resource.startsWith('/api/');
    const isSameOrigin = !resource.startsWith('http') || resource.startsWith(window.location.origin);
    
    if (isApiRequest && isSameOrigin) {
        const token = await getIdToken();
        if (token) {
            options.headers = {
                ...options.headers,
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            };
        }
    }
    
    return originalFetch(resource, options);
};

// Initialize FirebaseUI
const ui = new firebaseui.auth.AuthUI(auth);

// FirebaseUI config
const uiConfig = {
    signInFlow: 'popup',
    // Remove the default success URL to prevent auto-redirect
    signInSuccessUrl: false, 
    signInOptions: [
        {
            provider: firebase.auth.GoogleAuthProvider.PROVIDER_ID,
            customParameters: {
                prompt: 'select_account', // Forces account selection even when one account is available
                scope: 'profile email'   // Request profile and email scopes
            }
        },
        firebase.auth.EmailAuthProvider.PROVIDER_ID,
    ],
    callbacks: {
        signInSuccessWithAuthResult: function(authResult, redirectUrl) {
            const user = authResult.user;
            // Create or update user document in Firestore
            return db.collection('users').doc(user.uid).set({
                displayName: user.displayName,
                email: user.email,
                photoURL: user.photoURL,
                lastLogin: firebase.firestore.FieldValue.serverTimestamp(),
                stats: {
                    gamesPlayed: 0,
                    favoriteGenre: 'None',
                    totalPlaytime: 0
                },
                recentActivity: [{
                    type: 'login',
                    message: 'Joined Gamers Cove!',
                    timestamp: firebase.firestore.FieldValue.serverTimestamp()
                }]
            }, { merge: true }).then(() => {
                // Force a page reload to ensure auth state is properly updated
                window.location.assign('/profile.html');
                return false; // Prevent default redirect
            }).catch(error => {
                console.error('Error updating user data:', error);
                window.location.assign('/profile.html');
                return false;
            });
        }
    }
};

// Update UI elements based on user state
function updateUserUI(user) {
    const profileDropdown = document.getElementById('profile-dropdown');
    const authContainer = document.getElementById('firebaseui-auth-container');
    const profileWidget = document.querySelector('.profile-widget');
    const profilePic = document.getElementById('profile-pic');
    const profileName = document.getElementById('profile-name');
    
    if (user) {
        // User is signed in
        console.log('User is signed in:', user);
        
        // Hide auth UI and show profile elements
        if (authContainer) authContainer.style.display = 'none';
        if (profileDropdown) profileDropdown.style.display = 'none';
        
        // Update profile link and info
        if (profileWidget) {
            profileWidget.href = '/profile.html';
            profileWidget.style.pointerEvents = 'auto'; // Ensure the link is clickable
            
            // Update profile picture in the header if it exists
            if (profilePic) {
                profilePic.src = user.photoURL || 'https://ui-avatars.com/api/?name=' + 
                    encodeURIComponent(user.displayName || 'U') + '&background=random';
                profilePic.style.display = 'block';
            }
            
            // Update profile name in the header if it exists
            if (profileName) {
                profileName.textContent = user.displayName || 'Profile';
                profileName.style.display = 'block';
            }
        }
    } else {
        // User is signed out
        console.log('User is signed out');
        
        // Reset UI elements
        if (profileDropdown) profileDropdown.style.display = 'none';
        if (profilePic) profilePic.style.display = 'none';
        if (profileName) profileName.style.display = 'none';
        
        // Show auth UI
        if (authContainer) {
            authContainer.style.display = 'block';
            // Only initialize FirebaseUI if it hasn't been initialized yet
            if (!authContainer.hasAttribute('data-initialized')) {
                ui.start('#firebaseui-auth-container', uiConfig);
                authContainer.setAttribute('data-initialized', 'true');
            }
        }
    }
}

// Handle redirects based on auth state
function handleAuthRedirect(user) {
    const currentPath = window.location.pathname;
    
    // Only redirect if the user is on the profile page while not logged in
    if (!user && currentPath === '/profile.html') {
        window.location.href = '/';
        return true; // Indicate redirect is happening
    }
    
    // No automatic redirect to profile - user must click the profile button
    return false; // No redirect happened
}

// Update auth state handler
auth.onAuthStateChanged(async (user) => {
    const isRedirecting = handleAuthRedirect(user);
    
    // Update the ID token when auth state changes
    if (user) {
        // Force refresh the token to ensure it's fresh
        await getIdToken(true);
        
        // Set up token refresh before it expires
        user.getIdTokenResult().then((idTokenResult) => {
            // Get the time until the token expires (minus 5 minutes as buffer)
            const expiresIn = (idTokenResult.expirationTime.getTime() - Date.now()) - (5 * 60 * 1000);
            
            // Schedule token refresh
            if (expiresIn > 0) {
                setTimeout(() => {
                    getIdToken(true).catch(console.error);
                }, expiresIn);
            }
        });
    } else {
        // Clear token when signed out
        currentUserToken = null;
    }
    
    if (!isRedirecting) {
        updateUserUI(user);
    }
});

// Profile widget click handler
document.querySelector('.profile-widget')?.addEventListener('click', function(e) {
    if (!auth.currentUser) {
        e.preventDefault();
        const authContainer = document.getElementById('firebaseui-auth-container');
        if (authContainer) {
            authContainer.style.display = 'block';
            ui.start('#firebaseui-auth-container', uiConfig);
        }
    }
});

// Close dropdown when clicking outside
document.addEventListener('click', function(event) {
    const dropdown = document.getElementById('profile-dropdown');
    const profileBtn = document.querySelector('.profile-widget');
    
    if (!profileBtn.contains(event.target) && !dropdown.contains(event.target)) {
        dropdown.style.display = 'none';
    }
});

// Sign out button
document.getElementById('sign-out')?.addEventListener('click', function() {
    auth.signOut().then(() => {
        document.getElementById('profile-dropdown').style.display = 'none';
    });
});

// Listen for auth state changes
auth.onAuthStateChanged((user) => {
    const profileDropdown = document.getElementById('profile-dropdown');
    const authContainer = document.getElementById('firebaseui-auth-container');
    
    if (user) {
        // User is signed in
        authContainer.style.display = 'none';
        
        // Update UI with user info
        const profilePic = document.getElementById('profile-pic');
        const profileName = document.getElementById('profile-name');
        const profileEmail = document.getElementById('profile-email');
        
        profilePic.src = user.photoURL || 'https://via.placeholder.com/50';
        profileName.textContent = user.displayName || 'User';
        profileEmail.textContent = user.email || '';
        
        // Get user data from Firestore
        db.collection('users').doc(user.uid).get().then((doc) => {
            if (doc.exists) {
                const userData = doc.data();
                if (userData.favoriteGame) {
                    document.getElementById('favorite-game').textContent = userData.favoriteGame;
                }
            }
        });
        
        // Show profile dropdown if it was just opened
        if (profileDropdown.style.display !== 'none') {
            profileDropdown.style.display = 'block';
        }
    } else {
        // User is signed out
        profileDropdown.style.display = 'none';
    }
});
