# Fitpal: A CS-125-Project
## Developers
* Klim Rayskiy
* Kevin Huang
* Scottie Hyunh

## About the app
Fitpal is an Android fitness lifestyle app which recommends workout sessions
and potential fitness partners based on the users' previous workout sessions.
The app also recommends certain locations and venues based on the users' reported
interestst as well as their proximity to that location.

Fitpal is developed in Java, supporting at minimum Android SDK version 26. We however
recommend Android SDK version 30, as that is the version we developed using.
It uses the Google Maps and Places SDKs to display the map and autocomplete place queries
respectively as well as Google's Firebase Realtime Database to store user profiles and reports.

## Instructions on how to deploy the app
1. Install Android Studio on your computer.
2. Set up the Android Virtual Device Emulator (AVD)
  - Our reference device:
    Pixel XL API 30, 1440 x 2560; 560 dpi, Android 11.0 (Google APIs), x86
3. Set up Google Play Services
  - Click **Tools** in the top menu bar of Android Studio 
  - Click **SDK Manager** in the drop-down menu that appears
  - If Android SDK is selected, go to **SDK Tools** tab
  - Check **Google Play services** and click Apply
3. Open project root directory in Android Studio
4. Press the Play icon. If there are any issues, check the logs and follow
the corresponding directions.
  - Google Places SDK
      - Refer to ![this link](https://developers.google.com/maps/documentation/android-sdk/get-api-key)
  - Google Maps SDK
      - Refer to ![this link](https://developers.google.com/maps/documentation/places/android-sdk/get-api-key)
      - Note: We ended up using the same API key for both Places/Maps.
      So if you end up setting up your own API key, check our setup for
      refrence first. The SHA-1 hash might also need to be set up again.
  - "Waiting for target device to come online"
      - Refer to ![this link](https://stackoverflow.com/questions/42816127/waiting-for-target-device-to-come-online)

## Updates
* First draft uploaded - Kevin Huang (3/9/2021)
