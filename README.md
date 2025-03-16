# IndiCab - Ride Booking App

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![License](https://img.shields.io/badge/license-Proprietary-red)
![Version](https://img.shields.io/badge/version-1.0.0-orange)

A simple and efficient ride-booking application built with Jetpack Compose and Google Maps integration. This app allows users to easily book rides, select car types, and navigate through a user-friendly interface.

## Table of Contents
- [Features](#features)
- [Setup](#setup)
- [Project Structure](#project-structure)
- [Tech Stack](#tech-stack)
- [Booking Flow](#booking-flow)
- [Dependencies](#dependencies)
- [Building and Running](#building-and-running)
- [Testing](#testing)
- [Contribution Guidelines](#contribution-guidelines)
- [License](#license)

## Features

- **Interactive map** for location selection
- **Car type selection** with pricing
- **Booking confirmation system**
- **Material3 design** with dark mode support
- **User-friendly interface** for easy navigation

## Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/indicab.git
   ```
2. Navigate into the project directory:
   ```bash
   cd indicab
   ```
3. Add your Google Maps API key to `local.properties`:
   ```properties
   MAPS_API_KEY=your_api_key_here
   ```
4. Build and run the project:
   ```bash
   ./gradlew installDebug
   ```

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

- **Kotlin**
- **Jetpack Compose**
- **Google Maps Compose**
- **Material3 Design**
- **Navigation Compose**

## Booking Flow

1. Open app -> View map with location input
2. Enter pickup/drop locations
3. Select car type and view pricing
4. Confirm booking
5. View booking confirmation

## Dependencies

- **Android SDK 34**
- **Kotlin 1.9.0**
- **Compose BOM 2024.01.00**
- **Google Maps Compose 4.3.0**
- **Places SDK 3.3.0**

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

## Testing

To run tests, execute:
```bash
./gradlew test
```

## Contribution Guidelines

Contributions are welcome! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for details on how to contribute to this project.

## License

This project is licensed under a proprietary license. All rights reserved. No part of this code may be copied, modified, or distributed without explicit permission from the author.
