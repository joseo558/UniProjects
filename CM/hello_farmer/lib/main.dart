/// Main entry point for the HelloFarmer Flutter application. Splash screen and welcome page with navigation to login and registration.
/// File: main.dart
/// Author: José Oliveira 202300558
/// Version: 1.0.0
/// 2025-07-25
library;

import 'dart:async';

import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:sensors_plus/sensors_plus.dart';
import 'dart:math';
// Pages
import 'auth.dart';
import 'pages/home.dart';
import 'pages/notifications.dart';
import 'pages/settings.dart';
import 'pages/order.dart';
import 'pages/store.dart';
// Firebase
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:firebase_core/firebase_core.dart';
import 'firebase_options.dart';
// Localization
import 'package:flutter_localizations/flutter_localizations.dart';
import 'l10n/app_localizations.dart';
// Notification permission
import 'auth_utils.dart';

/// Global key for the app's navigator to handle navigation across the app.
final GlobalKey<NavigatorState> navigatorKey = GlobalKey<NavigatorState>();

/// Firebase background message handler to initialize Firebase when the app is in the background.
Future<void> _firebaseMessagingBackgroundHandler(RemoteMessage message) async {
  await Firebase.initializeApp();
}

/// Main function to run the HelloFarmer app.
void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  AccelerationService().startListening();
  runApp(MyApp());
}

/// HelloFarmer Flutter App class.
class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      navigatorKey: navigatorKey,
      title: 'HelloFarmer',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          // use system brightness instead of sensor
          brightness: MediaQuery.platformBrightnessOf(context),
          seedColor: Color(0xFF2A815E), // Main color
        ),
        // 0xFF2A815E is 2A815E in flutter hex
        useMaterial3: true,
      ),
      localizationsDelegates: const [
        AppLocalizations.delegate,
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
      ],
      supportedLocales: const [
        Locale('pt'),
        Locale('en'),
      ],
      localeResolutionCallback: (locale, supportedLocales) {
        // Optional fallback logic
        for (var supportedLocale in supportedLocales) {
          if (supportedLocale.languageCode == locale?.languageCode) {
            return supportedLocale;
          }
        }
        return supportedLocales.first;
      },
      home: const SplashScreen(),
      routes: {
        '/welcome': (context) => WelcomePage(),
        '/login': (context) => LoginPage(),
        '/register': (context) => RegisterPage(),
        '/reset_password': (context) => ResetPasswordPage(),
        '/complete_registration': (context) => CompleteRegistrationPage(),
        '/home': (context) => HomePage(),
        '/notifications': (context) => NotificationsPage(),
        '/settings': (context) => SettingsPage(),
        '/orders': (context) => OrdersPage(),
        '/store': (context) => StorePage(),
        '/store-edit': (context) => EditStorePage(),
        '/edit-product': (context) => EditProductPage(),
        '/cart': (context) => CartPage(),
      },
    );
  }
}

/// Splash screen widget that displays the app logo and navigates to the welcome page after a delay.
class SplashScreen extends StatefulWidget {
  const SplashScreen({super.key});

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

/// State class for the SplashScreen widget.
/// Displays the app logo and navigates to the WelcomePage after a delay.
///
/// Returns a [Scaffold] with a centered logo and text.
class _SplashScreenState extends State<SplashScreen> {
  bool _languageSet = false;

  /// Initializes the state and sets a timer to navigate to the welcome page after 2 seconds.
  @override
  void initState() {
    super.initState();
    _initialize();
  }

  /// Initializes Firebase, requests notification permissions, and sets up background message handling.
  /// After initialization, navigates to the welcome page after a delay of 2 seconds.
  Future<void> _initialize() async {
    await Firebase.initializeApp(
      options: DefaultFirebaseOptions.currentPlatform,
    );
    requestNotificationPermission();
    FirebaseMessaging.onBackgroundMessage(_firebaseMessagingBackgroundHandler);
    if (!_languageSet && mounted) {
      final localeCode = AppLocalizations.of(context)?.localeName ?? 'en';
      FirebaseAuth.instance.setLanguageCode(localeCode);
      _languageSet = true;
    }
    await Future.delayed(Duration(seconds: 2));
    if (mounted) {
      Navigator.pushReplacementNamed(context, '/welcome');
    }
  }

  /// Builds the splash screen UI.
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Theme.of(context).colorScheme.primary, // Main color
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Image.asset(
              'img/logoIcon.png',
              width: 100,
              height: 100,
            ),
            const SizedBox(height: 10),
            Image.asset(
              'img/logoText.png',
              width: 200,
              height: 50,
            ),
          ],
        ),
      ),
    );
  }
}

/// Welcome page widget that displays the app logo, a greeting message, and buttons to navigate to login and registration pages.
///
/// Returns a [Scaffold] with a centered logo, greeting text, and buttons for navigation.
class WelcomePage extends StatelessWidget {
  const WelcomePage({super.key});

  /// Builds the welcome page UI.
  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;
    return Scaffold(
      backgroundColor: Theme.of(context).colorScheme.primary, // Main color
      body: Stack(
        children: [
          Positioned(
            bottom: -25, // Adjust position
            left: 0,
            right: 0,
            child: Image.asset(
              'img/fruits.png',
              fit: BoxFit.fitWidth
            ),
          ),
          Center(
            child: Padding(
              // Extra padding for bottom image
              padding: const EdgeInsets.fromLTRB(16.0, 16.0, 16.0, 100.0),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Image.asset(
                    'img/logoHelloFarmer.png',
                    width: 300,
                    height: 100,
                    fit: BoxFit.cover, // Apply color blend mode
                  ),
                  Column(
                    children: [
                      const SizedBox(height: 10),
                      Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Text(l10n.welcome, style: TextStyle(fontSize: 28, color: Theme.of(context).colorScheme.onPrimary, fontFamily: 'Manrope')),
                            Icon(Icons.waving_hand, size: 28, color: Theme.of(context).colorScheme.onPrimary),
                          ]
                      ),
                      const SizedBox(height: 10),
                      Text(l10n.welcome_message, textAlign: TextAlign.center, style: TextStyle(fontSize: 16, color: Theme.of(context).colorScheme.onPrimary, fontFamily: 'Manrope')),
                    ],
                  ),
                  const SizedBox(height: 40),
                  Row(
                    children: [
                      Expanded(
                        child: ElevatedButton(
                          onPressed: () => Navigator.pushNamed(context, '/login'),
                          child: Text("Login", style: TextStyle(fontSize: 18, fontFamily: 'Manrope')),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 20),
                  Row(
                    children: [
                      Expanded(
                        child: ElevatedButton(
                          onPressed: () => Navigator.pushNamed(context, '/register'),
                          child: Text(l10n.register, style: TextStyle(fontSize: 18, fontFamily: 'Manrope')),
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ),
        ]
      )
    );
  }
}

/// Service to monitor user acceleration and show a warning dialog if the user is moving too fast.


class AccelerationService {
  static final AccelerationService _instance = AccelerationService._internal();
  factory AccelerationService() => _instance;
  AccelerationService._internal();

  bool _popupShown = false;
  final double _threshold = 15.0;
  StreamSubscription<UserAccelerometerEvent>? _subscription;

  void startListening() {
    _subscription = userAccelerometerEvents.listen(
      (event) {
        final double magnitude = sqrt(
          event.x * event.x + event.y * event.y + event.z * event.z,
        );

        if (magnitude > _threshold && !_popupShown) {
          _popupShown = true;

          final context = navigatorKey.currentContext;
          if (context != null && context.mounted) {
            final l10n = AppLocalizations.of(context)!;
            showDialog(
              context: context,
              builder: (ctx) =>
                  AlertDialog(
                    title: Text(l10n.warning, style: TextStyle(fontFamily: 'Manrope')),
                    content: Text(l10n.moving_too_fast, style: TextStyle(fontFamily: 'Manrope')),
                    actions: [
                      TextButton(
                        onPressed: () {
                          Navigator.of(ctx).pop();
                          _popupShown = false;
                        },
                        child: const Text('OK'),
                      ),
                    ],
                  ),
            );
          }
        }
      },
      cancelOnError: true,
    );
  }

  void dispose() {
    _subscription?.cancel();
    _subscription = null;
  }
}
