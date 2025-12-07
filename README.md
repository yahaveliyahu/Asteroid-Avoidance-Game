# 🚀 Asteroid Avoidance Game

An Android game where the player controls a rocket moving between five lanes, trying to dodge falling obstacles. The rocket has limited lives and reacts to collisions with vibrations and sound effects.

---

## 🎮 Gameplay Features

- Smooth rocket movement between 5 lanes
- Two control modes: **Touch buttons** or **Sensor-based tilt control**
- Randomly falling obstacles (stones), coins (1/5/10 points), and hearts (+1 life)
- Collision detection with:
  - Obstacles: lose life and vibrate
  - Coins: add to score and play sound
  - Hearts: gain a life and show ❤️ toast
- Difficulty scaling: speed increases every 20 distance units
- Boost mechanic using a **Boost button** or tilting forward
- Brake mechanic using **tilt back**
- Game over screen with retry/menu
- High Score system with name + GPS location
- Location shown on embedded Google Map
- Responsive UI with cloud-themed game screen and space-themed menu

---

## 🎮 Watch the Game in Action

▶️ [Click here to watch the video](https://youtu.be/Zi9DRx0wKAg))

---

## 🧠 Highlights & Mechanics

- **Sensor control**: use accelerometer for movement & speed control
- **Dynamic difficulty**: gets harder the further you go
- **High scores**:
  - Saved locally using SharedPreferences with Gson
  - Top 10 scores displayed with name, score, and clickable location
- **Map integration**:
  - Google Maps shows high-score locations with markers
  - Zoom adjusts based on number of scores
- **UI details**:
  - Scrollable names in high score table
  - Feedback toasts and sound effects for actions

---

## 🧱 Tech Stack

- Kotlin
- Android SDK
- XML Layouts
- Google Maps SDK
- View System
- SharedPreferences + Gson
- Coroutines

---

## 📂 Project Structure

├── logic/              # Rocket movement, sound logic
│   └── RocketPlayer.kt
├── manager/            # Game logic and obstacle spawning
│   ├── GameManager.kt
│   └── ObstacleManager.kt
├── res/
│   ├── drawable/       # Graphics: rocket, heart, coins, obstacles
│   ├── layout/         # XML layouts for all screens
│   ├── raw/            # Sound files (e.g., crash, coin, boost)
│   └── values/         # Strings and themes
├── MainActivity.kt     # Core game loop
├── MenuActivity.kt     # Entry screen
├── GameOverActivity.kt # After losing the game
├── HighScoresActivity.kt # High score screen with fragments
├── ScoresFragment.kt   # Shows top 10 scores in table
├── MapFragment.kt      # Displays score locations on map
└── utils/              # Constants and ToastUtil

---

## 📍 Permissions

- Location access for saving high score location

---

## 🔊 Sound Effects

- Step
- Crash
- Boost
- Brake
- Coin
- Heart

