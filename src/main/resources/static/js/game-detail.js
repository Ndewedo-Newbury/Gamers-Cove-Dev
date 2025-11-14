// Get game ID from URL
function getGameIdFromUrl() {
    const path = window.location.pathname;
    const match = path.match(/\/game\/(\d+)/);
    return match ? match[1] : null;
}

// Fetch game data from API
async function loadGameDetails() {
    const gameId = getGameIdFromUrl();
    const loadingEl = document.getElementById('loading');
    const errorEl = document.getElementById('error');
    const contentEl = document.getElementById('gameContent');

    if (!gameId) {
        loadingEl.style.display = 'none';
        errorEl.style.display = 'block';
        return;
    }

    try {
        const response = await fetch(`/api/games/${gameId}`);
        
        if (!response.ok) {
            throw new Error('Game not found');
        }

        const game = await response.json();
        displayGameDetails(game);
        
        loadingEl.style.display = 'none';
        contentEl.style.display = 'block';
    } catch (error) {
        console.error('Error loading game:', error);
        loadingEl.style.display = 'none';
        errorEl.style.display = 'block';
    }
}

// Display game details
function displayGameDetails(game) {
    console.log('Displaying game:', game);

    // Title and Name
    const titleEl = document.getElementById('gameTitle');
    const nameEl = document.getElementById('gameName');
    if (titleEl) titleEl.textContent = game.title || 'Unknown Game';
    if (nameEl && game.name) {
        nameEl.textContent = game.name;
        nameEl.style.display = game.name !== game.title ? 'block' : 'none';
    }

    // Cover Image
    const coverEl = document.getElementById('gameCover');
    const placeholderEl = coverEl.nextElementSibling;
    if (game.coverImageUrl) {
        coverEl.src = game.coverImageUrl;
        coverEl.style.display = 'block';
        placeholderEl.style.display = 'none';
    } else {
        coverEl.style.display = 'none';
        placeholderEl.style.display = 'flex';
    }

    // Release Date
    const releaseEl = document.getElementById('gameReleaseDate');
    if (releaseEl && game.releaseDate) {
        try {
            const date = new Date(game.releaseDate);
            releaseEl.textContent = `Released: ${date.toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' })}`;
            releaseEl.style.display = 'block';
        } catch (e) {
            releaseEl.style.display = 'none';
        }
    }

    // Description
    const descEl = document.getElementById('gameDescription');
    if (descEl) {
        descEl.textContent = game.description || 'No description available.';
    }

    // Platforms
    const platformsEl = document.getElementById('gamePlatforms');
    if (platformsEl) {
        if (game.platforms && game.platforms.length > 0) {
            platformsEl.innerHTML = game.platforms.map(platform => 
                `<span class="tag">${escapeHtml(platform)}</span>`
            ).join('');
        } else {
            platformsEl.innerHTML = '<span class="no-data">No platforms listed</span>';
        }
    }

    // Genres
    const genresEl = document.getElementById('gameGenres');
    if (genresEl) {
        if (game.genres && game.genres.length > 0) {
            genresEl.innerHTML = game.genres.map(genre => 
                `<span class="tag">${escapeHtml(genre)}</span>`
            ).join('');
        } else {
            genresEl.innerHTML = '<span class="no-data">No genres listed</span>';
        }
    }

    // Additional Info
    const externalApiIdEl = document.getElementById('gameExternalApiId');
    const gameIdEl = document.getElementById('gameId');
    if (externalApiIdEl) externalApiIdEl.textContent = game.externalApiId || 'N/A';
    if (gameIdEl) gameIdEl.textContent = game.id || 'N/A';

    // Update page title
    document.title = `${game.title || 'Game'} - Gamers Cove`;
}

// Escape HTML to prevent XSS
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    loadGameDetails();
});

