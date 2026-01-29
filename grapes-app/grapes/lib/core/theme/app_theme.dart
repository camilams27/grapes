import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

/// Classe que define o tema visual Pixel Art / Retrô do Grapes 🍇
/// Inspirado em SNES/GameBoy com paleta roxo, lilás e verde
class AppTheme {
  // ═══════════════════════════════════════════════════════════
  // CORES PRINCIPAIS
  // ═══════════════════════════════════════════════════════════

  // Roxos
  static const Color primaryDark = Color(0xFF1A0F2E); // Fundo principal
  static const Color primaryPurple = Color(0xFF2D1B4E); // Cards/containers
  static const Color accentPurple = Color(0xFF9D4EDD); // Destaques
  static const Color lightPurple = Color(0xFFBB86FC); // Texto secundário

  // Verdes (XP/Sucesso)
  static const Color neonGreen = Color(0xFF39FF14); // XP, sucesso
  static const Color darkGreen = Color(0xFF00A86B); // Verde alternativo

  // Lilás/Rosa
  static const Color lilac = Color(0xFFE0AAFF); // Hover states
  static const Color softPink = Color(0xFFF4A4BA); // Alertas suaves
  static const Color cyan = Color(
    0xFF7FFFD4,
  ); // Ciano para XP (combina com heart_xp)

  // Utilitárias
  static const Color white = Color(0xFFFFFFFF);
  static const Color darkGray = Color(0xFF2A2A2A);
  static const Color error = Color(0xFFFF5555);

  // ═══════════════════════════════════════════════════════════
  // ESTILOS DE TEXTO PIXEL ART
  // ═══════════════════════════════════════════════════════════

  static TextStyle get pixelTitle =>
      GoogleFonts.pressStart2p(fontSize: 24, color: white, height: 1.5);

  static TextStyle get pixelHeading =>
      GoogleFonts.pressStart2p(fontSize: 16, color: white, height: 1.5);

  static TextStyle get pixelBody =>
      GoogleFonts.pressStart2p(fontSize: 10, color: lightPurple, height: 1.8);

  static TextStyle get pixelSmall =>
      GoogleFonts.pressStart2p(fontSize: 8, color: lightPurple, height: 1.8);

  static TextStyle get pixelButton =>
      GoogleFonts.pressStart2p(fontSize: 12, color: white, height: 1.5);

  // ═══════════════════════════════════════════════════════════
  // DECORAÇÕES RETRÔ
  // ═══════════════════════════════════════════════════════════

  /// Borda grossa estilo pixel/retrô
  static BoxDecoration get retroBorder => BoxDecoration(
    color: primaryPurple,
    border: Border.all(color: accentPurple, width: 4),
    boxShadow: [
      BoxShadow(
        color: accentPurple.withValues(alpha: 0.3),
        offset: const Offset(4, 4),
        blurRadius: 0,
      ),
    ],
  );

  /// Container com borda neon verde
  static BoxDecoration get greenGlowBorder => BoxDecoration(
    color: primaryPurple,
    border: Border.all(color: neonGreen, width: 3),
    boxShadow: [
      BoxShadow(
        color: neonGreen.withValues(alpha: 0.4),
        blurRadius: 8,
        spreadRadius: 1,
      ),
    ],
  );

  /// Input field estilo terminal
  static InputDecoration inputDecoration(String label, {IconData? icon}) {
    return InputDecoration(
      labelText: label,
      labelStyle: pixelSmall.copyWith(color: lightPurple),
      prefixIcon: icon != null
          ? Icon(icon, color: accentPurple, size: 20)
          : null,
      filled: true,
      fillColor: primaryDark,
      contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 16),
      border: OutlineInputBorder(
        borderRadius: BorderRadius.zero,
        borderSide: BorderSide(color: accentPurple, width: 3),
      ),
      enabledBorder: OutlineInputBorder(
        borderRadius: BorderRadius.zero,
        borderSide: BorderSide(color: accentPurple, width: 3),
      ),
      focusedBorder: OutlineInputBorder(
        borderRadius: BorderRadius.zero,
        borderSide: BorderSide(color: neonGreen, width: 3),
      ),
      errorBorder: OutlineInputBorder(
        borderRadius: BorderRadius.zero,
        borderSide: BorderSide(color: error, width: 3),
      ),
      focusedErrorBorder: OutlineInputBorder(
        borderRadius: BorderRadius.zero,
        borderSide: BorderSide(color: error, width: 3),
      ),
      errorStyle: pixelSmall.copyWith(color: error),
    );
  }

  // ═══════════════════════════════════════════════════════════
  // BOTÕES RETRÔ
  // ═══════════════════════════════════════════════════════════

  /// Botão primário estilo arcade
  static ButtonStyle get retroButtonPrimary => ButtonStyle(
    backgroundColor: WidgetStateProperty.resolveWith((states) {
      if (states.contains(WidgetState.pressed)) {
        return accentPurple.withValues(alpha: 0.8);
      }
      if (states.contains(WidgetState.hovered)) {
        return lilac;
      }
      return accentPurple;
    }),
    foregroundColor: WidgetStateProperty.all(white),
    padding: WidgetStateProperty.all(
      const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
    ),
    shape: WidgetStateProperty.all(
      RoundedRectangleBorder(
        borderRadius: BorderRadius.zero,
        side: BorderSide(color: white, width: 3),
      ),
    ),
    elevation: WidgetStateProperty.all(0),
    textStyle: WidgetStateProperty.all(pixelButton),
  );

  /// Botão secundário (outline)
  static ButtonStyle get retroButtonSecondary => ButtonStyle(
    backgroundColor: WidgetStateProperty.all(Colors.transparent),
    foregroundColor: WidgetStateProperty.resolveWith((states) {
      if (states.contains(WidgetState.hovered)) {
        return neonGreen;
      }
      return lightPurple;
    }),
    padding: WidgetStateProperty.all(
      const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
    ),
    shape: WidgetStateProperty.all(
      RoundedRectangleBorder(
        borderRadius: BorderRadius.zero,
        side: BorderSide(color: accentPurple, width: 2),
      ),
    ),
    elevation: WidgetStateProperty.all(0),
    textStyle: WidgetStateProperty.all(pixelSmall),
  );

  /// Botão verde (ação positiva/XP)
  static ButtonStyle get retroButtonGreen => ButtonStyle(
    backgroundColor: WidgetStateProperty.resolveWith((states) {
      if (states.contains(WidgetState.pressed)) {
        return darkGreen;
      }
      return neonGreen;
    }),
    foregroundColor: WidgetStateProperty.all(primaryDark),
    padding: WidgetStateProperty.all(
      const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
    ),
    shape: WidgetStateProperty.all(
      RoundedRectangleBorder(
        borderRadius: BorderRadius.zero,
        side: BorderSide(color: white, width: 3),
      ),
    ),
    elevation: WidgetStateProperty.all(0),
    textStyle: WidgetStateProperty.all(
      pixelButton.copyWith(color: primaryDark),
    ),
  );

  /// Botão ciano (XP - combina com heart_xp)
  static ButtonStyle get retroButtonCyan => ButtonStyle(
    backgroundColor: WidgetStateProperty.resolveWith((states) {
      if (states.contains(WidgetState.pressed)) {
        return cyan.withValues(alpha: 0.8);
      }
      return cyan;
    }),
    foregroundColor: WidgetStateProperty.all(primaryDark),
    padding: WidgetStateProperty.all(
      const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
    ),
    shape: WidgetStateProperty.all(
      RoundedRectangleBorder(
        borderRadius: BorderRadius.zero,
        side: BorderSide(color: white, width: 3),
      ),
    ),
    elevation: WidgetStateProperty.all(0),
    textStyle: WidgetStateProperty.all(
      pixelButton.copyWith(color: primaryDark),
    ),
  );

  // ═══════════════════════════════════════════════════════════
  // THEME DATA COMPLETO
  // ═══════════════════════════════════════════════════════════

  static ThemeData get darkTheme => ThemeData(
    useMaterial3: true,
    brightness: Brightness.dark,
    scaffoldBackgroundColor: primaryDark,
    primaryColor: accentPurple,
    colorScheme: ColorScheme.dark(
      primary: accentPurple,
      secondary: neonGreen,
      surface: primaryPurple,
      error: error,
    ),

    // AppBar
    appBarTheme: AppBarTheme(
      backgroundColor: primaryPurple,
      foregroundColor: white,
      elevation: 0,
      centerTitle: true,
      titleTextStyle: pixelHeading,
    ),

    // Elevated Button
    elevatedButtonTheme: ElevatedButtonThemeData(style: retroButtonPrimary),

    // Text Button
    textButtonTheme: TextButtonThemeData(style: retroButtonSecondary),

    // Input
    inputDecorationTheme: InputDecorationTheme(
      filled: true,
      fillColor: primaryDark,
      labelStyle: pixelSmall,
      hintStyle: pixelSmall.copyWith(color: darkGray),
    ),

    // Snackbar
    snackBarTheme: SnackBarThemeData(
      backgroundColor: primaryPurple,
      contentTextStyle: pixelSmall.copyWith(color: white),
      shape: RoundedRectangleBorder(
        side: BorderSide(color: accentPurple, width: 2),
      ),
    ),

    // Progress Indicator
    progressIndicatorTheme: ProgressIndicatorThemeData(color: neonGreen),
  );
}
