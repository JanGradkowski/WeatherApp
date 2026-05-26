🌤️ Weather 24/7 (JavaFX Desktop App)
(Note: Record a 10-second GIF of you searching a city and the UI updating, and put the link here)

A modern, asynchronous desktop weather application built with Java, JavaFX, and the OpenWeatherMap API.

This project demonstrates core software engineering principles, including multithreading for responsive user interfaces, secure environment configuration, and native local storage.

🚀 Key Features & Engineering Decisions
Asynchronous API Calls: Network requests to the OpenWeatherMap API are executed on dedicated background threads, ensuring the UI remains perfectly fluid and never freezes while fetching data.

Native Local Storage: Utilizes java.util.prefs.Preferences to persist the user's saved cities directly to the OS node. This removes the need for clunky local text files or a heavy SQLite database just to save a few strings.

Secure Configuration: API keys are loaded via a .gitignored config.properties file, ensuring sensitive credentials are never leaked into version control.

Smart UI/UX: Features glassmorphism design, debounced search suggestions, and defensive programming (disabling inputs during network calls to prevent rate-limiting).

🛠️ Tech Stack
Language: Java 17+

GUI Framework: JavaFX

Networking: java.net.http.HttpClient

Data Parsing: org.json

API: OpenWeatherMap (Geocoding API & 5-Day Forecast API)

💻 Running the Project Locally
To run this project on your local machine, you will need to provide your own OpenWeatherMap API key.

1. Clone the repository

Bash
git clone https://github.com/yourusername/weather-app.git
cd weather-app
2. Configure your API Key

Create a file named config.properties in the root directory.

Add your OpenWeather API key to the file like this:

Properties
OPENWEATHER_API_KEY=your_actual_api_key_here
(A config.example.properties file is included in the repo for reference).

3. Run the App
Build and run the project using your preferred IDE (IntelliJ/Eclipse) or via Maven/Gradle.
