# ✈️ Flight Tracker Android App

A modern Android application that allows users to track real-time flights by flight number or search routes between airports using IATA codes.

---

##  Features

-  **Flight Search by Flight Code**  
-  **Route Search (Departure ↔ Arrival Airports)**  
-  **View Flight Details** (departure/arrival time, duration, delays)  
-  **Average Flight Time Calculation**  
-  **Offline Support** (via Room DB caching)  
-  **Modern Material UI**  
-  **WebView Integration** for detailed views  

---

## 🛠️ Tech Stack

### 📌 Language & Frameworks
- **Kotlin** – Primary language
- **Android Jetpack Libraries**:
  - `ViewModel` – For lifecycle-aware UI data
  - `LiveData` – Observing data changes
  - `Room` – Local database
  - `Navigation Component` – In-app navigation

### ⚙️ Networking & Async
- **Retrofit** – REST API client  
- **OkHttp** – HTTP request logging  
- **Gson** – JSON parsing  
- **Coroutines** – For concurrency and async operations  

### 🔌 APIs
- **AviationStack API** – Flight data provider

---

## 📂 Project Structure & Architecture

Follows **MVVM Architecture**:

├── data │ ├── api/ # Retrofit interfaces │ ├── db/ # Room Database & DAO │ └── models/ # Data classes │ ├── repository/ # Handles data sources (API/DB) ├── ui/ │ ├── view/ # Activities/Fragments │ └── viewmodel/ # ViewModels │ └── utils/ # Constants, helpers, etc.

---

## 🧪 API Endpoints

Using AviationStack:
- `GET /flights`
  - `flight_iata=...` – Flight number search
  - `dep_iata=...&arr_iata=...` – Route search
  - `limit=...` – Limit result size

Example: GET https://api.aviationstack.com/v1/flights?flight_iata=AI101&access_key=YOUR_API_KEY

---

## 🧑‍💻 Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/flight-tracker.git
   cd flight-tracker
2. Open in Android Studio and let Gradle sync.

3. Build & Run on an emulator or Android device.

## Configuration

Get your API Key from AviationStack

1. Insert it into FlightRepository.kt:

2. private val apiKey = "YOUR_API_KEY_HERE"

⚠️ Avoid hardcoding API keys in production – use BuildConfig or environment variables for security.


## 📱 Screens Overview

### Main Screen
- Flight number input field – Search by flight IATA code  
- From/To IATA code inputs – Route-based search between airports  
- Results list – Displays matching flights with brief details  
- Average flight duration display – Shows average flight time for the route  

### WebView Screen
- Embedded flight detail view – Displays full flight information in a web-based format  

