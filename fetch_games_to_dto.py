import os, json, time, requests
from datetime import datetime

API_KEY = "b1281628a17e4c04a75d0c022068a09b"
BASE_URL = "https://api.rawg.io/api/games"
OUTPUT_FILE = "games_dto.json"

def fetch_games(page):
    """Fetch a page of games from RAWG API"""
    params = {
        "key": API_KEY,
        "page_size": 40,
        "page": page
    }
    response = requests.get(BASE_URL, params=params)
    if response.status_code == 401:
        raise Exception("❌ Unauthorized — check your RAWG_API_KEY")
    response.raise_for_status()
    return response.json()

def to_dto(game):
    """Transform RAWG JSON into your GameDto structure"""
    return {
        "id": game.get("id"),
        "externalApiId": f"rawg-{game.get('id')}",
        "title": game.get("name"),
        "description": game.get("description_raw") or game.get("slug"),
        "coverImageUrl": game.get("background_image"),
        "releaseDate": game.get("released"),
        "platforms": [p["platform"]["name"] for p in game.get("platforms", [])],
        "genres": [g["name"] for g in game.get("genres", [])],
    }

def main(start_page=51):
    # Load existing data (if present)
    if os.path.exists(OUTPUT_FILE):
        with open(OUTPUT_FILE, "r", encoding="utf-8") as f:
            all_games = json.load(f)
    else:
        all_games = []

    page = start_page
    while True:
        print(f"Fetching page {page}...")
        data = fetch_games(page)
        results = data.get("results", [])
        if not results:
            print("✅ No more games available. Finished fetching.")
            break

        for game in results:
            dto = to_dto(game)
            all_games.append(dto)

        # Save progress every 5 pages
        if page % 5 == 0:
            with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
                json.dump(all_games, f, ensure_ascii=False, indent=2)
            print(f"💾 Progress saved: {len(all_games)} games so far...")

        # Stop if no next page
        if not data.get("next"):
            print("✅ Finished all available pages!")
            break

        page += 1
        time.sleep(1)  # prevent hitting rate limit

if __name__ == "__main__":
    print("🚀 Resuming RAWG fetch from page 51...")
    try:
        main(51)
    except KeyboardInterrupt:
        print("🛑 User stopped manually.")
    except Exception as e:
        print("❌ Error:", e)
