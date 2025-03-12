package com.example.indicab.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Color(0xFF0066CC),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D36),
    
    secondary = Color(0xFF535F70),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD7E3F7),
    onSecondaryContainer = Color(0xFF101C2B),
    
    tertiary = Color(0xFF6B5778),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF2DAFF),
    onTertiaryContainer = Color(0xFF251431),
    
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    
    background = Color(0xFFFDFCFF),
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFFDFCFF),
    onSurface = Color(0xFF1A1C1E),
    
    surfaceVariant = Color(0xFFDFE2EB),
    onSurfaceVariant = Color(0xFF43474E),
    outline = Color(0xFF73777F),
    outlineVariant = Color(0xFFC3C7CF)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9FCAFF),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF004881),
    onPrimaryContainer = Color(0xFFD1E4FF),
    
    secondary = Color(0xFFBBC7DB),
    onSecondary = Color(0xFF253140),
    secondaryContainer = Color(0xFF3B4858),
    onSecondaryContainer = Color(0xFFD7E3F7),
    
    tertiary = Color(0xFFD7BDE4),
    onTertiary = Color(0xFF3B2948),
    tertiaryContainer = Color(0xFF523F5F),
    onTertiaryContainer = Color(0xFFF2DAFF),
    
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    
    background = Color(0xFF1A1C1E),
    onBackground = Color(0xFFE2E2E6),
    surface = Color(0xFF1A1C1E),
    onSurface = Color(0xFFE2E2E6),
    
    surfaceVariant = Color(0xFF43474E),
    onSurfaceVariant = Color(0xFFC3C7CF),
    outline = Color(0xFF8D9199),
    outlineVariant = Color(0xFF43474E)
)

@Composable
fun IndiCabTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Custom colors for specific components
object IndiCabColors {
    val SuccessLight = Color(0xFF146B3A)
    val SuccessDark = Color(0xFF5AE49D)
    val WarningLight = Color(0xFFFFA000)
    val WarningDark = Color(0xFFFFB74D)
    val InfoLight = Color(0xFF0288D1)
    val InfoDark = Color(0xFF81D4FA)
    
    // Ride status colors
    val ScheduledLight = Color(0xFF6200EA)
    val ScheduledDark = Color(0xFFB388FF)
    val InProgressLight = Color(0xFF00897B)
    val InProgressDark = Color(0xFF80CBC4)
    val CompletedLight = Color(0xFF2E7D32)
    val CompletedDark = Color(0xFF81C784)
    val CancelledLight = Color(0xFFD32F2F)
    val CancelledDark = Color(0xFFE57373)
    
    // Payment status colors
    val PendingLight = Color(0xFFF57C00)
    val PendingDark = Color(0xFFFFB74D)
    val ProcessingLight = Color(0xFF0097A7)
    val ProcessingDark = Color(0xFF4DD0E1)
    val SuccessfulLight = Color(0xFF388E3C)
    val SuccessfulDark = Color(0xFF81C784)
    val FailedLight = Color(0xFFC62828)
    val FailedDark = Color(0xFFE57373)
    
    // Emergency colors
    val EmergencyLight = Color(0xFFD50000)
    val EmergencyDark = Color(0xFFFF5252)
    val SafetyLight = Color(0xFF00C853)
    val SafetyDark = Color(0xFF69F0AE)
    val AlertLight = Color(0xFFFF6D00)
    val AlertDark = Color(0xFFFFAB40)
}

@Composable
fun themeAwareColor(lightColor: Color, darkColor: Color): Color {
    return if (isSystemInDarkTheme()) darkColor else lightColor
}

// Extension functions for common color combinations
@Composable
fun successColor() = themeAwareColor(IndiCabColors.SuccessLight, IndiCabColors.SuccessDark)

@Composable
fun warningColor() = themeAwareColor(IndiCabColors.WarningLight, IndiCabColors.WarningDark)

@Composable
fun infoColor() = themeAwareColor(IndiCabColors.InfoLight, IndiCabColors.InfoDark)

@Composable
fun emergencyColor() = themeAwareColor(IndiCabColors.EmergencyLight, IndiCabColors.EmergencyDark)

@Composable
fun safetyColor() = themeAwareColor(IndiCabColors.SafetyLight, IndiCabColors.SafetyDark)

@Composable
fun alertColor() = themeAwareColor(IndiCabColors.AlertLight, IndiCabColors.AlertDark)

// Ride status colors
@Composable
fun scheduledColor() = themeAwareColor(IndiCabColors.ScheduledLight, IndiCabColors.ScheduledDark)

@Composable
fun inProgressColor() = themeAwareColor(IndiCabColors.InProgressLight, IndiCabColors.InProgressDark)

@Composable
fun completedColor() = themeAwareColor(IndiCabColors.CompletedLight, IndiCabColors.CompletedDark)

@Composable
fun cancelledColor() = themeAwareColor(IndiCabColors.CancelledLight, IndiCabColors.CancelledDark)

// Payment status colors
@Composable
fun pendingColor() = themeAwareColor(IndiCabColors.PendingLight, IndiCabColors.PendingDark)

@Composable
fun processingColor() = themeAwareColor(IndiCabColors.ProcessingLight, IndiCabColors.ProcessingDark)

@Composable
fun successfulColor() = themeAwareColor(IndiCabColors.SuccessfulLight, IndiCabColors.SuccessfulDark)

@Composable
fun failedColor() = themeAwareColor(IndiCabColors.FailedLight, IndiCabColors.FailedDark)
