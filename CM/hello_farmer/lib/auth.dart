/// Authentication (login) and Registration Pages
/// File: auth.dart
/// Author: José Oliveira 202300558
/// Version: 1.0.0
/// 2025-07-21
library;

import 'package:cloud_firestore/cloud_firestore.dart'; // For error handling
import 'package:flutter/material.dart';
import 'utils.dart';
import 'auth_utils.dart';
import 'forms_fields.dart';
import 'constants.dart';
import 'l10n/app_localizations.dart';

/// Login page widget that allows users to log in with email and password, or Google/Apple/Meta accounts.
/// It includes a "Remember me" checkbox and a reset password option.
class LoginPage extends StatefulWidget {
  const LoginPage({super.key});

  @override
  State<LoginPage> createState() => _LoginPageState();
}

/// State class for the LoginPage widget.
class _LoginPageState extends State<LoginPage> {
  final _formKey = GlobalKey<FormState>();
  final TextEditingController emailController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();
  bool rememberMe = false;
  AppLocalizations? _l10n;

  @override
  void initState() {
    super.initState();
    _loadSavedLogin();
  }

  /// Loads saved login credentials from secure storage if "Remember me" was previously enabled.
  void _loadSavedLogin() async {
    final loginData = await loadSavedLoginData();
    if (loginData['rememberMe'] == true && loginData['registeredWithEmail'] == true) {
      setState(() {
        rememberMe = true;
        emailController.text = loginData['email'] ?? '';
        passwordController.text = loginData['password'] ?? '';
      });
    }
  }

  @override
  void dispose() {
    emailController.dispose();
    passwordController.dispose();
    super.dispose();
  }

  /// Validates the form and submits the login credentials.
  void _validateAndSubmit(BuildContext context) async {
    if (_formKey.currentState!.validate()) {
      try {
        final email = emailController.text.trim();
        final password = passwordController.text;

        // Ensure the user has confirmed their email before allowing login
        await signInWithEmail(context, rememberMe, email, password);
      } catch (error) {
        if (context.mounted) showSnackBarError(context, _l10n!.login_error);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    _l10n = AppLocalizations.of(context)!;
    return Scaffold(
      appBar: AppBar(title: Text("Login", style: const TextStyle(fontFamily: 'Manrope'))),
      body: Center(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(16.0),
          child: Form(
            key: _formKey,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Image.asset(
                  'img/logoIcon2.png',
                  width: 70,
                  height: 70,
                ),
                const SizedBox(height: 10),
                const Text("Login", style: TextStyle(fontSize: 32, fontWeight: FontWeight.bold, fontFamily: 'Manrope')),
                const SizedBox(height: 20),
                TextFormField(
                  controller: emailController,
                  decoration: const InputDecoration(labelText: "Email"),
                  keyboardType: TextInputType.emailAddress,
                  validator: (value) => validateEmail(_l10n, value),
                ),
                const SizedBox(height: 10),
                PasswordField(
                  controller: passwordController,
                  validator: (value) => validatePassword(_l10n, value),
                ),
                Row(
                  children: [
                    Checkbox(
                      value: rememberMe,
                      onChanged: (val) {
                        setState(() => rememberMe = val ?? false);
                      },
                    ),
                    Text(_l10n!.login_remember, style: TextStyle(fontSize: 16, fontFamily: 'Manrope')),
                    const Spacer(),
                    TextButton(
                        onPressed: () => Navigator.pushNamed(context, '/reset_password', arguments: {'email' : emailController.text}),
                        child: const Text("Reset Password", style: TextStyle(fontSize: 16, fontFamily: 'Manrope'))
                    ),
                  ],
                ),
                const SizedBox(height: 10),
                FilledButton(
                  onPressed: () => _validateAndSubmit(context),
                  child: const Text("Login", style: TextStyle(fontSize: 18, fontFamily: 'Manrope'))
                ),
                const SizedBox(height: 10),
                const Divider(),
                const SizedBox(height: 10),
                FilledButton.icon(
                  onPressed: () => signInWithGoogle(context),
                  icon: Image.asset('img/google.png', width: 24, height: 24),
                  label: Text(_l10n!.login_google, style: TextStyle(fontSize: 16, fontFamily: 'Manrope')),
                ),
                const SizedBox(height: 10),
                FilledButton.icon(
                  onPressed: () => signInWithApple(context),
                  icon: Icon(Icons.apple, size: 24),
                  label: Text(_l10n!.login_apple, style: TextStyle(fontSize: 16, fontFamily: 'Manrope')),
                ),
                const SizedBox(height: 10),
                FilledButton.icon(
                  onPressed: () => signInWithFacebook(context),
                  icon: Icon(Icons.facebook, size: 24, color: Colors.blue),
                  label: Text(_l10n!.login_facebook, style: TextStyle(fontSize: 16, fontFamily: 'Manrope')),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

/// Reset Password page widget that allows users to request a password reset link via email
class ResetPasswordPage extends StatefulWidget {
  const ResetPasswordPage({super.key});

  @override
  State<ResetPasswordPage> createState() => _ResetPasswordPageState();
}

/// State class for the ResetPasswordPage widget.
class _ResetPasswordPageState extends State<ResetPasswordPage> {
  final _formKey = GlobalKey<FormState>();
  final TextEditingController _emailController = TextEditingController();
  AppLocalizations? _l10n;

  bool _initialized = false;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    // Initialize the email controller with the email argument if provided
    // The flag is used to avoid resetting the email field on every rebuild
    if (!_initialized) {
      final args = ModalRoute.of(context)?.settings.arguments as Map<String, dynamic>?;
      final email = args?['email'] as String?;
      if (email != null) {
        _emailController.text = email;
      }
      _initialized = true;
    }
  }

  /// Submits the reset password request by validating the form and sending a reset email.
  Future<void> _submitResetRequest() async {
    if (_formKey.currentState!.validate()) {
      final email = _emailController.text.trim();

      try {
        await sendPasswordResetEmail(_l10n!, email);
        if (mounted) showSnackBarConfirm(context, _l10n!.password_reset_success);
        await Future.delayed(const Duration(seconds: 3));
        if (mounted) Navigator.pop(context); // Return to login
      } catch (e) {
        if (mounted) showSnackBarError(context, _l10n!.password_reset_error);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    _l10n = AppLocalizations.of(context)!;
    return Scaffold(
      appBar: AppBar(title: Text('Reset Password', style: const TextStyle(fontFamily: 'Manrope'))),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: Column(
            children: [
              Text(_l10n!.password_reset_message, style: TextStyle(fontSize: 18, fontFamily: 'Manrope')),
              const SizedBox(height: 20),
              TextFormField(
                controller: _emailController,
                decoration: InputDecoration(labelText: "Email"),
                keyboardType: TextInputType.emailAddress,
                validator: (value) => validateEmail(_l10n, value),
              ),
              const SizedBox(height: 24),
              ElevatedButton(
                onPressed: _submitResetRequest,
                style: ElevatedButton.styleFrom(
                  backgroundColor: Theme.of(context).colorScheme.primary,
                  foregroundColor: Theme.of(context).colorScheme.onPrimary,
                ),
                child: Text(_l10n!.password_reset_send, style: TextStyle(fontFamily: 'Manrope')),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

/// Registration page widget that allows users to create a new account with email and password, or via Google/Apple/Meta accounts.
class RegisterPage extends StatefulWidget {
  const RegisterPage({super.key});

  @override
  State<RegisterPage> createState() => _RegisterPageState();
}

/// State class for the RegisterPage widget.
class _RegisterPageState extends State<RegisterPage> {
  final _formKey = GlobalKey<FormState>();

  final nameController = TextEditingController();
  final emailController = TextEditingController();
  final passwordController = TextEditingController();
  final repeatPasswordController = TextEditingController();

  DateTime? _dateOfBirth;
  Gender? _gender;
  UserType? _userType;

  bool _isLoading = false;
  AppLocalizations? _l10n;

  @override
  void dispose() {
    nameController.dispose();
    emailController.dispose();
    passwordController.dispose();
    repeatPasswordController.dispose();
    super.dispose();
  }

  Future<void> _registerWithEmail() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() => _isLoading = true);

    try {
      final email = emailController.text.trim();
      final password = passwordController.text.trim();
      final name = nameController.text.trim();
      final dateOfBirth = _dateOfBirth!.toString();

      await registerWithEmail(
        l10n: _l10n!,
        email: email,
        password: password,
        name: name,
        dateOfBirth: dateOfBirth,
        gender: _gender!.toString(),
        userType: _userType!.toString(),
      );

      if (mounted) showSnackBarConfirm(context, _l10n!.register_success);
      await Future.delayed(const Duration(seconds: 3));
      if (mounted) Navigator.pushReplacementNamed(context, '/login');
    } on FirebaseException catch (e) {
      if (e.code == 'email-already-in-use') {
        if (mounted) showSnackBarError(context, _l10n!.email_already_in_use);
      } else {
        if (mounted) showSnackBarError(context, _l10n!.register_error);
      }
    } finally {
      setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    _l10n = AppLocalizations.of(context)!;
    return Scaffold(
      appBar: AppBar(title: Text(_l10n!.register, style: const TextStyle(fontFamily: 'Manrope'))),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: Column(
            children: [
              TextFormField(
                controller: nameController,
                decoration: InputDecoration(labelText: _l10n!.name),
                validator: (v) => v == null || v.isEmpty ? _l10n!.name_required : null,
              ),
              const SizedBox(height: 10),
              TextFormField(
                controller: emailController,
                decoration: const InputDecoration(labelText: "Email"),
                keyboardType: TextInputType.emailAddress,
                validator: (value) => validateEmail(_l10n, value),
              ),
              const SizedBox(height: 10),
              PasswordField(
                controller: passwordController,
                validator: (value) => validatePassword(_l10n, value),
              ),
              const SizedBox(height: 10),
              PasswordField(
                controller: repeatPasswordController,
                validator: (value) {
                  if (value == null || value.isEmpty) {
                    return _l10n!.password_repeat_required;
                  }
                  if (value != passwordController.text) {
                    return _l10n!.password_mismatch;
                  }
                  return null;
                },
                labelText: _l10n!.password_repeat,
              ),
              const SizedBox(height: 10),
              DateOfBirthFormField(onDateSelected: (date) => _dateOfBirth = date),
              const SizedBox(height: 16),
              Container(
                alignment: Alignment.centerLeft,
                child: Text(_l10n!.gender, style: TextStyle(fontSize: 16, fontFamily: 'Manrope', fontWeight: FontWeight.bold))
              ),
              GenderRadioGroup(
                  selectedGender: _gender,
                  onChanged: (gender) => setState(() => _gender = gender),
              ),
              const SizedBox(height: 16),
              Container(
                alignment: Alignment.centerLeft,
                child: Text(_l10n!.userType, style: TextStyle(fontSize: 16, fontFamily: 'Manrope', fontWeight: FontWeight.bold)),
              ),
              UserTypeRadioGroup(
                selectedUserType: _userType,
                onChanged: (userType) => setState(() => _userType = userType),
              ),
              const SizedBox(height: 16),
              _isLoading ? const CircularProgressIndicator() : FilledButton(
                onPressed: _registerWithEmail,
                child: Text(_l10n!.register, style: TextStyle(fontSize: 18, fontFamily: 'Manrope')),
              ),
              const SizedBox(height: 10),
              const Divider(),
              const SizedBox(height: 10),
              FilledButton.icon(
                onPressed: () => signInWithGoogle(context),
                icon: Image.asset('img/google.png', width: 24, height: 24),
                label: Text(_l10n!.login_google, style: TextStyle(fontSize: 16, fontFamily: 'Manrope')),
              ),
              const SizedBox(height: 10),
              FilledButton.icon(
                onPressed: () => signInWithApple(context),
                icon: const Icon(Icons.apple, size: 24),
                label: Text(_l10n!.login_apple, style: TextStyle(fontSize: 16, fontFamily: 'Manrope')),
              ),
              const SizedBox(height: 10),
              FilledButton.icon(
                onPressed: () => signInWithFacebook(context),
                icon: const Icon(Icons.facebook, size: 24, color: Colors.blue),
                label: Text(_l10n!.login_facebook, style: TextStyle(fontSize: 16, fontFamily: 'Manrope')),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

/// Complete Registration page widget that allows users to complete their registration after signing in with Apple/Google/Facebook.
class CompleteRegistrationPage extends StatefulWidget {
  const CompleteRegistrationPage({super.key});

  @override
  State<CompleteRegistrationPage> createState() => _CompleteRegistrationPageState();
}

/// State class for the CompleteRegistrationPage widget.
class _CompleteRegistrationPageState extends State<CompleteRegistrationPage> {
  final _formKey = GlobalKey<FormState>();
  final TextEditingController _nameController = TextEditingController();
  String? _email;
  String? _uid;
  DateTime? _dateOfBirth;
  Gender? _gender;
  UserType? _userType;

  bool _initialized = false;
  AppLocalizations? _l10n;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    // Get the email argument if provided
    // The flag is used to avoid resetting the email field on every rebuild
    if (!_initialized) {
      final args = ModalRoute.of(context)?.settings.arguments as Map<String, dynamic>?;
      _email = args?['email'] as String?;
      _uid = args?['uid'] as String?;
      _initialized = true;
    }
  }

  @override
  void dispose() {
    _nameController.dispose();
    super.dispose();
  }

  void _submit() async{
    if (_formKey.currentState?.validate() ?? false) {
        final name = _nameController.text.trim();
        try {
          await handleUserPostAuth(
            uid: _uid!,
            name: name,
            email: _email!,
            dateOfBirth: _dateOfBirth!.toString(),
            gender: _gender!.toString(),
            userType: _userType!.toString(),
          );
          if (mounted) showSnackBarConfirm(context, _l10n!.register_success);
          await Future.delayed(const Duration(seconds: 3));
          if (mounted) Navigator.pushReplacementNamed(context, '/login');
        } catch (e) {
          if (mounted) showSnackBarError(context, _l10n!.register_error);
        }
    } else {
      if (mounted) showSnackBarError(context, _l10n!.form_invalid);
    }
  }

  @override
  Widget build(BuildContext context) {
    _l10n = AppLocalizations.of(context)!;
    return Scaffold(
      appBar: AppBar(title: Text(_l10n!.register_continuation, style: const TextStyle(fontFamily: 'Manrope'))),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: ListView(
            children: [
              TextFormField(
                controller: _nameController,
                decoration: InputDecoration(labelText: _l10n!.name),
                validator: (value) => value == null || value.trim().isEmpty ? _l10n!.name_required : null,
              ),
              const SizedBox(height: 16),
              DateOfBirthFormField(
                  onDateSelected: (date) => _dateOfBirth = date
              ),
              const SizedBox(height: 16),
              GenderRadioGroup(
                selectedGender: Gender.m,
                onChanged: (gender) => setState(() => _gender = gender),
              ),
              const SizedBox(height: 16),
              UserTypeRadioGroup(
                selectedUserType: _userType,
                onChanged: (userType) => setState(() => _userType = userType),
              ),
              const SizedBox(height: 32),
              FilledButton(
                onPressed: _submit,
                child: Text(_l10n!.continue_on, style: TextStyle(fontSize: 18, fontFamily: 'Manrope'))
              ),
            ],
          ),
        ),
      ),
    );
  }
}