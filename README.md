# IndiCab - Ride Booking App

A simple and efficient ride-booking application built with Jetpack Compose and Google Maps integration.

## Features

- Interactive map for location selection
- Car type selection with pricing
- Booking confirmation system
- Material3 design with dark mode support

## Setup

1. Clone the repository
2. Add your Google Maps API key to `local.properties`:
```properties
MAPS_API_KEY=your_api_key_here
```
3. Build and run the project

## Project Structure

```
app/src/main/
├── java/com/example/indicab/
│   ├── screens/
│   │   ├── SimpleHomeScreen.kt       # Main screen with map
│   │   ├── SimpleBookingScreen.kt    # Car selection
│   │   └── BookingConfirmationScreen.kt
│   ├── navigation/
│   │   └── SimpleNavigation.kt       # Navigation setup
│   ├── ui/theme/
│   │   └── Theme.kt                  # App theme
│   ├── MainActivity.kt
│   └── IndiCabApplication.kt
└── res/
    └── values/
        ├── strings.xml               # Text resources
        └── themes.xml               # Theme configuration
```

## Tech Stack

- Kotlin
- Jetpack Compose
- Google Maps Compose
- Material3 Design
- Navigation Compose

## Booking Flow

1. Open app -> View map with location input
2. Enter pickup/drop locations
3. Select car type and view pricing
4. Confirm booking
5. View booking confirmation

## Dependencies

- Android SDK 34
- Kotlin 1.9.0
- Compose BOM 2024.01.00
- Google Maps Compose 4.3.0
- Places SDK 3.3.0

## Building and Running

The app requires minimum SDK version 24 (Android 7.0) and targets SDK version 34.

To build:
```bash
./gradlew assembleDebug
```

To run:
```bash
./gradlew installDebug
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.
