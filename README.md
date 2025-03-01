# Fetch Rewards Coding Exercise

## Overview
This is a native Android app built in Java that fetches and displays a list of items from a remote JSON file. The app retrieves data from **[Fetch Rewards API](https://fetch-hiring.s3.amazonaws.com/hiring.json)** and organizes it for easy viewing. It supports the current release mobile OS.

## Features
- 📲 **Displays items in a RecyclerView**, grouped and sorted.
- 🚀 **Filters out empty or null names** for cleaner data presentation.
- 📶 **Checks internet connectivity** before making API calls.
- 🔄 **Progress bar** indicates data loading state.
- 🌙 **Dark Mode Support** for better user experience in low-light conditions.
- 🛠 **Material You UI design** for a modern look.
- 📡 **Uses Retrofit** for efficient network requests.

## How to Run the App
### 1️⃣ Clone the Repository
```sh
git clone https://github.com/JP109/FetchRewardsCodingExercise
cd FetchRewardsCodingExercise
```
### 2️⃣ Open in Android Studio
- Open the project in Android Studio (latest stable version).
- Ensure Gradle Sync completes successfully.
### 3️⃣ Run the App
- Use an emulator or physical device.
- Click Run in Android Studio.

## How to Enable Dark Mode
- The app automatically follows the system's dark mode settings.
- You can manually toggle Dark Mode from the ActionBar.
