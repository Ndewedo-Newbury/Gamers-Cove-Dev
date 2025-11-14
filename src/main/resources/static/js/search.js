// Debounce function to limit API calls
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Search games function
async function searchGames(searchTerm) {
    const resultsContainer = document.getElementById('searchResults');
    
    if (!searchTerm || searchTerm.trim().length === 0) {
        resultsContainer.innerHTML = '';
        resultsContainer.classList.remove('active');
        return;
    }

    console.log('Searching for:', searchTerm);
    
    try {
        const url = `/api/games?search=${encodeURIComponent(searchTerm)}`;
        console.log('Fetching from:', url);
        
        const response = await fetch(url);
        
        console.log('Response status:', response.status);
        
        if (!response.ok) {
            throw new Error(`Search failed with status: ${response.status}`);
        }

        const games = await response.json();
        console.log('Games received:', games);
        console.log('Number of games:', games.length);
        
        displaySearchResults(games);
    } catch (error) {
        console.error('Error searching games:', error);
        resultsContainer.innerHTML = '<div class="search-error">Error searching games. Please try again.</div>';
        resultsContainer.classList.add('active');
    }
}

// Display search results
function displaySearchResults(games) {
    const resultsContainer = document.getElementById('searchResults');
    
    console.log('Displaying results for', games.length, 'games');
    
    if (!games || games.length === 0) {
        resultsContainer.innerHTML = '<div class="no-results">No games found</div>';
        resultsContainer.classList.add('active');
        return;
    }

    // Limit results to 10 for better UX
    const limitedGames = games.slice(0, 10);
    
    const resultsHTML = limitedGames.map(game => {
        console.log('Processing game:', game);
        
        // Handle release date - it might be a string in format "YYYY-MM-DD" or a Date object
        let releaseYear = '';
        if (game.releaseDate) {
            try {
                const date = typeof game.releaseDate === 'string' ? new Date(game.releaseDate) : game.releaseDate;
                if (!isNaN(date.getTime())) {
                    releaseYear = date.getFullYear();
                }
            } catch (e) {
                console.warn('Error parsing release date:', e);
            }
        }
        
        // Handle genres - might be array or string
        let genresText = '';
        if (game.genres) {
            if (Array.isArray(game.genres)) {
                genresText = game.genres.slice(0, 3).join(', ');
            } else if (typeof game.genres === 'string') {
                genresText = game.genres;
            }
        }
        
        const coverHtml = game.coverImageUrl 
            ? `<img src="${escapeHtml(game.coverImageUrl)}" alt="${escapeHtml(game.title || '')}" class="game-cover" onerror="this.parentElement.querySelector('.game-cover-placeholder').style.display='flex'; this.style.display='none';">`
            : '';
        const placeholderHtml = game.coverImageUrl 
            ? '<div class="game-cover-placeholder" style="display:none;">🎮</div>'
            : '<div class="game-cover-placeholder">🎮</div>';
        
        return `
        <a href="/game/${game.id}" class="search-result-item-link">
            <div class="search-result-item">
                ${coverHtml}
                ${placeholderHtml}
                <div class="game-info">
                    <div class="game-title">${escapeHtml(game.title || 'Unknown Game')}</div>
                    ${releaseYear ? `<div class="game-release">${releaseYear}</div>` : ''}
                    ${genresText ? `<div class="game-genres">${escapeHtml(genresText)}</div>` : ''}
                </div>
            </div>
        </a>
    `;
    }).join('');

    resultsContainer.innerHTML = resultsHTML;
    resultsContainer.classList.add('active');
    console.log('Results displayed');
}

// Escape HTML to prevent XSS
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Initialize search functionality
document.addEventListener('DOMContentLoaded', () => {
    console.log('Initializing search functionality...');
    
    const searchInput = document.getElementById('gameSearch');
    const resultsContainer = document.getElementById('searchResults');

    if (!searchInput) {
        console.error('Search input element not found!');
        return;
    }

    if (!resultsContainer) {
        console.error('Search results container not found!');
        return;
    }

    console.log('Search elements found, setting up event listeners');

    // Debounced search function (waits 200ms after user stops typing)
    const debouncedSearch = debounce((searchTerm) => {
        console.log('Debounced search triggered for:', searchTerm);
        searchGames(searchTerm);
    }, 200);

    // Handle input
    searchInput.addEventListener('input', (e) => {
        const searchTerm = e.target.value.trim();
        console.log('Input event, search term:', searchTerm);
        if (searchTerm.length > 0) {
            debouncedSearch(searchTerm);
        } else {
            resultsContainer.innerHTML = '';
            resultsContainer.classList.remove('active');
        }
    });

    // Close results when clicking outside
    document.addEventListener('click', (e) => {
        if (!searchInput.contains(e.target) && !resultsContainer.contains(e.target)) {
            resultsContainer.classList.remove('active');
        }
    });

    // Handle keyboard navigation
    searchInput.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            resultsContainer.classList.remove('active');
            searchInput.blur();
        }
    });
});

