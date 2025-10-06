/// Utility functions for authentication and login management
/// File: auth_utils.dart
/// Author: José Oliveira 202300558
/// Version: 1.0.0
/// 2025-07-17
library;

import 'package:flutter/material.dart';
// Utility functions
import 'utils.dart';
import 'dart:convert';
import 'l10n/app_localizations.dart';
import 'package:flutter/services.dart';
// Packages for secure storage and validation
import 'package:crypto/crypto.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:email_validator/email_validator.dart';
// Social Sign-In packages
import 'package:google_sign_in/google_sign_in.dart';
import 'package:extension_google_sign_in_as_googleapis_auth/extension_google_sign_in_as_googleapis_auth.dart';
import 'package:googleapis_auth/googleapis_auth.dart' as auth show AccessCredentials;
import 'package:flutter_facebook_auth/flutter_facebook_auth.dart';
import 'package:sign_in_with_apple/sign_in_with_apple.dart';
// Firebase
import 'package:firebase_auth/firebase_auth.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_messaging/firebase_messaging.dart';

/// Secure storage instance for saving login credentials. Private to this file.
final FlutterSecureStorage _secureStorage = FlutterSecureStorage();

/// Check the email format using the email_validator package.
bool isValidEmail(String email) {
  return EmailValidator.validate(email);
}

/// Validates the email format and returns an error message if invalid.
String? validateEmail(AppLocalizations? l10n, String? value) {
  if (value == null || value.trim().isEmpty) {
    return l10n!.email_required;
  } else if (!isValidEmail(value)) {
    return l10n!.email_invalid;
  }
  return null;
}

/// Check the password strength based on specific criteria:
/// - At least 12 characters long
/// - Contains at least one uppercase letter
/// - Contains at least one lowercase letter
/// - Contains at least one digit
/// - Contains at least one special character
/// Returns true if the password meets all criteria, false otherwise.
bool isValidPassword(String password) {
  final passwordRegex = RegExp(r'^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_\-+=\[\]{};:,.<>?/\\|~`])[A-Za-z\d!@#$%^&*()_\-+=\[\]{};:,.<>?/\\|~`]{12,}$');
  return passwordRegex.hasMatch(password);
}

/// Validates the password strength and returns an error message if invalid.
String? validatePassword(AppLocalizations? l10n, String? value) {
  if (value == null || value.trim().isEmpty) {
    return l10n!.password_required;
  } else if (!isValidPassword(value)) {
    return l10n!.password_invalid;
  }
  return null;
}

/// Loads saved login data from secure storage.
Future<Map<String, dynamic>> loadSavedLoginData() async {
  String? remember = await _secureStorage.read(key: 'rememberMe');
  String? registeredWithEmail = await _secureStorage.read(key: 'registeredWithEmail');
  String? savedEmail = await _secureStorage.read(key: 'email');
  String? savedPassword = await _secureStorage.read(key: 'password');
  return {
    'rememberMe': remember == 'true',
    'registeredWithEmail': registeredWithEmail == 'true',
    'email': savedEmail,
    'password': savedPassword,
  };
}

/// Saves the user's credentials securely or deletes them based on the rememberMe flag.
Future<void> saveCredentials({required bool rememberMe, required bool registeredWithEmail, String? email, String? password, String? name, String? dateOfBirth, String? gender, String? userType}) async {
  if (rememberMe) {
    await _secureStorage.write(key: 'rememberMe', value: 'true');
    await _secureStorage.write(key: 'registeredWithEmail', value: registeredWithEmail ? 'true' : 'false');
    if (email != null) await _secureStorage.write(key: 'email', value: email);
    if (password != null) await _secureStorage.write(key: 'password', value: password);
    if (name != null) await _secureStorage.write(key: 'name', value: name);
    if (dateOfBirth != null) await _secureStorage.write(key: 'dateOfBirth', value: dateOfBirth);
    if (gender != null) await _secureStorage.write(key: 'gender', value: gender);
    if (userType != null) await _secureStorage.write(key: 'userType', value: userType);
  } else {
    await _secureStorage.write(key: 'rememberMe', value: 'false');
    await _secureStorage.delete(key: 'email');
    await _secureStorage.delete(key: 'password');
  }
}

/// Generates a SHA-256 hash of the given input string.
String sha256ofString(String input) {
  return (sha256.convert(utf8.encode(input))).toString();
}

/// Handles Email and Password Sign-In.
Future<void> signInWithEmail(BuildContext context, bool rememberMe, String email, String password) async {
  AppLocalizations l10n = AppLocalizations.of(context)!;
  final userCredential = await FirebaseAuth.instance.signInWithEmailAndPassword(
      email: email,
      password: password
  );

  final user = userCredential.user!;
  if (!user.emailVerified) {
    await FirebaseAuth.instance.signOut();
    if (context.mounted) showSnackBarError(context, l10n.email_not_verified);
    await Future.delayed(const Duration(seconds: 3));
    return;
  } else {
    if (context.mounted) showSnackBarConfirm(context, l10n.login_success);
    // Update local user data with the server user data
    await updateLocalUserData(
        rememberMe: rememberMe,
        registeredWithEmail: true,
        uid: user.uid,
        password: password
    );
    await Future.delayed(const Duration(seconds: 3));
    if (context.mounted) Navigator.pushReplacementNamed(context, '/home');
  }
}

/// Handles Apple Sign-In.
Future<void> signInWithApple(BuildContext context) async {
  AppLocalizations? l10n = AppLocalizations.of(context);
  try {
    final rawNonce = generateNonce();
    final nonce = sha256ofString(rawNonce);

    final credential = await SignInWithApple.getAppleIDCredential(
      scopes: [AppleIDAuthorizationScopes.email, AppleIDAuthorizationScopes.fullName],
      nonce: nonce,
    );
    final appleCredential = OAuthProvider('apple.com').credential(
      idToken: credential.identityToken,
      rawNonce: rawNonce,
    );

    final userCredential = await FirebaseAuth.instance.signInWithCredential(appleCredential);
    final user = userCredential.user;
    final isNewUser = userCredential.additionalUserInfo?.isNewUser ?? false;
    final email = credential.email ?? user?.email;

    if (isNewUser) {
      if (context.mounted) Navigator.pushReplacementNamed(context, '/complete-registration', arguments: {'email': email, 'uid': user!.uid});
    } else {
      if (context.mounted) showSnackBarConfirm(context, l10n!.login_apple_success);
      await updateLocalUserData(
          rememberMe: true,
          registeredWithEmail: false,
          uid: user!.uid
      );
      await Future.delayed(const Duration(seconds: 3));
      if (context.mounted) Navigator.pushReplacementNamed(context, '/home');
    }
  } catch (error) {
    if (context.mounted) showSnackBarError(context, l10n!.login_apple_error);
  }
}

/// Retrieves the server client ID from platform-specific code using MethodChannel.
Future<String?> getServerClientId() async {
  const platform = MethodChannel('com.hellofarmer.hello_farmer/secrets');
  return await platform.invokeMethod<String>('getServerClientId');
}

/// Handles Google Sign-In.
Future<void> signInWithGoogle(BuildContext context) async {
  final AppLocalizations l10n = AppLocalizations.of(context)!;
  final GoogleSignIn signIn = GoogleSignIn.instance;

  try {
    final String? serverClientId = await getServerClientId();
    await signIn.initialize(
      serverClientId: serverClientId,
    );
    GoogleSignInAccount? googleUser = await signIn.attemptLightweightAuthentication();
    if (googleUser == null) {
      // Fallback to full sign-in flow
      if (GoogleSignIn.instance.supportsAuthenticate()) {
        googleUser = await GoogleSignIn.instance.authenticate();
      }
      if (googleUser == null) return; // Failed to sign in
    }

    final GoogleSignInClientAuthorization googleAuth = await googleUser.authorizationClient.authorizeScopes(['email', 'profile']);
    final auth.AccessCredentials credentials = (googleAuth.authClient(scopes: ['email', 'profile'])).credentials;

    final credential = GoogleAuthProvider.credential(
      accessToken: credentials.accessToken.data,
      idToken: credentials.idToken,
    );

    UserCredential userCredential = await FirebaseAuth.instance.signInWithCredential(credential);
    final isNewUser = userCredential.additionalUserInfo?.isNewUser ?? false;

    if (isNewUser) {
      if (context.mounted) Navigator.pushReplacementNamed(context, '/complete-registration', arguments: {'email': googleUser.email, 'uid': userCredential.user!.uid});
    } else {
      if (context.mounted) showSnackBarConfirm(context, l10n.login_google_success);
      await updateLocalUserData(
          rememberMe: true,
          registeredWithEmail: false,
          uid: userCredential.user!.uid
      );
      await Future.delayed(const Duration(seconds: 3));
      if (context.mounted) Navigator.pushReplacementNamed(context, '/home');
    }
  } catch (error) {
    if (context.mounted) showSnackBarError(context, l10n.login_google_error);
  }
}

/// Handles Facebook Sign-In.
Future<void> signInWithFacebook(BuildContext context) async {
  AppLocalizations? l10n = AppLocalizations.of(context);
  try {
    final LoginResult result = await FacebookAuth.instance.login();
    if (result.status == LoginStatus.success) {
      final AccessToken accessToken = result.accessToken!;
      final facebookCredential = FacebookAuthProvider.credential(accessToken.tokenString);

      final userCredential = await FirebaseAuth.instance.signInWithCredential(facebookCredential);
      final isNewUser = userCredential.additionalUserInfo?.isNewUser ?? false;
      final userData = await FacebookAuth.instance.getUserData(fields: "email,name");
      final email = userData['email'];

      if (isNewUser) {
        if (context.mounted) Navigator.pushReplacementNamed(context, '/complete-registration', arguments: {'email': email, 'uid': userCredential.user!.uid});
      } else {
        if (context.mounted) showSnackBarConfirm(context, l10n!.login_facebook_success);
        await updateLocalUserData(
            rememberMe: true,
            registeredWithEmail: false,
            uid: userCredential.user!.uid
        );
        await Future.delayed(const Duration(seconds: 3));
        if (context.mounted) Navigator.pushReplacementNamed(context, '/home');
      }
    } else {
      if (context.mounted) showSnackBarError(context, l10n!.login_facebook_error);
    }
  } catch (error) {
    if (context.mounted) showSnackBarError(context, l10n!.login_facebook_error);
  }
}

/// Handles Email and Password Registration
Future<void> registerWithEmail({
  required AppLocalizations l10n,
  required String email,
  required String password,
  required String name,
  required String dateOfBirth,
  required String gender,
  required String userType,
}) async {
  final userCredential = await FirebaseAuth.instance.createUserWithEmailAndPassword(
    email: email,
    password: password,
  );

  // Send verification email
  FirebaseAuth.instance.setLanguageCode(l10n.localeName);
  await userCredential.user?.sendEmailVerification();

  await handleUserPostAuth(
    uid: userCredential.user!.uid,
    name: name,
    email: email,
    dateOfBirth: dateOfBirth,
    gender: gender,
    userType: userType,
    registeredWithEmail: true,
    password: password,
  );
}

/// Sends a password reset email to the specified email address.
Future<void> sendPasswordResetEmail(AppLocalizations l10n, String email) async {
  FirebaseAuth.instance.setLanguageCode(l10n.localeName);
  await FirebaseAuth.instance.sendPasswordResetEmail(email: email);
}

/// Handles user post-authentication actions such as saving user data to Firestore and secure storage.
Future<void> handleUserPostAuth({
  required String uid,
  required String name,
  required String email,
  required String dateOfBirth,
  required String gender,
  required String userType,
  bool rememberMe = true,
  bool registeredWithEmail = false,
  String? password,
}) async {
  await FirebaseFirestore.instance.collection('users').doc(uid).set({
    'name': name,
    'email': email,
    'dateOfBirth': dateOfBirth,
    'gender': gender,
    'userType': userType,
    'createdAt': FieldValue.serverTimestamp(),
  });

  await saveCredentials(
    rememberMe: rememberMe,
    registeredWithEmail: registeredWithEmail,
    email: email,
    password: password,
    name: name,
    dateOfBirth: dateOfBirth,
    gender: gender,
    userType: userType,
  );
}

/// Update local user data with the server user data to ensure consistency.
Future<void> updateLocalUserData({
  required bool rememberMe,
  required bool registeredWithEmail,
  required String uid,
  String? password
}) async {
  final userDoc = await FirebaseFirestore.instance.collection('users').doc(uid).get();
  if (userDoc.exists) {
    final userData = userDoc.data()!;
    await saveCredentials(
      rememberMe: rememberMe,
      registeredWithEmail: registeredWithEmail,
      name: userData['name'],
      email: userData['email'],
      dateOfBirth: userData['dateOfBirth'],
      gender: userData['gender'],
      userType: userData['userType'],
      password: password,
    );
  }
}

/// Request notification permission from the user and store the status in secure storage.
void requestNotificationPermission() async {
  FirebaseMessaging messaging = FirebaseMessaging.instance;

  NotificationSettings settings = await messaging.requestPermission(
    alert: true,
    badge: true,
    sound: true,
  );

  await _secureStorage.write(
    key: 'notifications_permission',
    value: settings.authorizationStatus.name, // "authorized"
  );
}

/// Check if notifications are allowed based on the stored permission status.
Future<bool> isNotificationAllowed() async {
  final status = await _secureStorage.read(key: 'notifications_permission');
  return status == 'authorized' || status == 'provisional';
}