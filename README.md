# IndiCab - Android Ride-Booking Application

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Kotlin Version](https://img.shields.io/badge/kotlin-1.9.0-blue)
![Android API](https://img.shields.io/badge/API-21%2B-brightgreen)

IndiCab is a modern Android application for ride-hailing services, built with cutting-edge Android technologies and following best practices in mobile development.

## Table of Contents
- [Project Overview](#project-overview)
- [Screenshots & Demo](#screenshots--demo)
- [System Requirements](#system-requirements)
- [Environment Setup](#environment-setup)
- [Project Structure](#project-structure)
- [Architecture Overview](#architecture-overview)
- [Key Features](#key-features)
- [Tech Stack](#tech-stack)
- [Building and Running](#building-and-running)
- [Testing](#testing)
- [Debugging Tips](#debugging-tips)
- [Common Issues](#common-issues)
- [Git Workflow](#git-workflow)
- [Contribution Guidelines](#contribution-guidelines)
- [Code Style Guide](#code-style-guide)
- [Release Process](#release-process)
- [License](#license)
- [Contact & Support](#contact--support)

## Project Overview

IndiCab is a feature-rich ride-hailing application that provides:
- Real-time location tracking
- Ride booking and management
- Multiple payment options
- Driver-rider communication
- Trip history and analytics

## Screenshots & Demo

[Include screenshots here]

## System Requirements

### Windows
| Requirement | Minimum | Recommended |
|-------------|---------|-------------|
| OS | 64-bit Windows 8 | Latest 64-bit Windows |
| RAM | 8 GB | 16 GB or more |
| CPU | x86_64 CPU architecture; 2nd generation Intel Core or newer | Latest Intel Core processor |
| Disk space | 8 GB | 16 GB or more (SSD preferred) |
| Screen resolution | 1280 x 800 | 1920 x 1080 |

## Environment Setup

### Prerequisites
- Android Studio Flamingo or later
- Java Development Kit (JDK) 17
- Google Maps API key
- Firebase configuration file

### Installation Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/[organization]/IndiCab.git
   cd IndiCab
   ```

2. Open the project in Android Studio

3. Create `local.properties` file in the root directory with:
   ```properties
   MAPS_API_KEY=your_google_maps_api_key_here
   ```

4. Sync project with Gradle files

5. Build and run the application

## Project Structure

```
app/
├── src/
│   ├── main/               # Main application code
│   │   ├── java/            # Kotlin source files
│   │   ├── res/             # Resources
│   ├── androidTest/        # Instrumented tests
│   └── test/                # Unit tests
```

## Architecture Overview

The application follows MVVM (Model-View-ViewModel) architecture with the following layers:

- **UI Layer**: Jetpack Compose components
- **Domain Layer**: Use cases and business logic
- **Data Layer**: Repositories and data sources

## Key Features

- Real-time location tracking
- Ride booking system
- Multiple payment options
- Driver-rider communication
- Trip history and analytics
- Push notifications

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Dependency Injection**: Hilt
- **Networking**: Retrofit, OkHttp
- **Database**: Room
- **Authentication**: Firebase Auth
- **Analytics**: Firebase Analytics
- **Push Notifications**: Firebase Cloud Messaging
- **Maps**: Google Maps API

## Building and Running

### Build Variants
- **Debug**: For development and testing
- **Release**: For production deployment

### Gradle Commands
```bash
./gradlew assembleDebug    # Debug build
./gradlew assembleRelease  # Release build
./gradlew test             # Run all tests
./gradlew clean            # Clean project
```

## Testing

### Unit Tests
Located in `src/test/` directory

Run unit tests:
```bash
./gradlew test
```

### Instrumented Tests
Located in `src/androidTest/` directory

Run instrumented tests:
```bash
./gradlew connectedAndroidTest
```

## Debugging Tips

- Use Log statements with appropriate tags
- Leverage Android Studio's Debug tools
- Use Profiler for performance monitoring
- Enable strict mode for development

## Common Issues

### Gradle Sync Failed
**Solution**: Check internet connection and Gradle version

### Emulator Not Starting
**Solution**: Verify virtualization is enabled in BIOS

### Missing Dependencies
**Solution**: Verify repository URLs in build.gradle files

## Git Workflow

1. Create a new branch: `git checkout -b feature/your-feature`
2. Make changes and commit: `git commit -m "Your message"`
3. Push changes: `git push origin feature/your-feature`
4. Create Pull Request
5. Address review comments
6. Merge after approval

## Contribution Guidelines

We welcome contributions! Please follow these steps:
1. Fork the repository
2. Create a feature branch
3. Implement your changes
4. Write tests for new functionality
5. Submit a Pull Request

## Code Style Guide

We follow official Kotlin coding conventions:
- 4 spaces indentation
- Max line length: 100 characters
- Use KDoc for documentation
- Follow Jetpack Compose best practices

## Release Process

1. Create release branch: `release/vX.Y.Z`
2. Update version code and name
3. Run all tests
4. Create signed APK
5. Merge to main
6. Create Git tag

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact & Support

For support or questions, please contact:
- **Project Maintainer**: [Your Name]
- **Email**: [Your Email]
- **Issue Tracker**: [GitHub Issues]
