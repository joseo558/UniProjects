/// Customized form fields for the application.
/// File: forms_fields.dart
/// Author: José Oliveira 202300558
/// Version: 1.0.0
/// 2025-07-09
library;

import 'package:flutter/material.dart';
import 'l10n/app_localizations.dart';
import 'constants.dart';

/// DateOfBirthFormField is a custom form field widget that allows users to select their date of birth using a date picker dialog.
class DateOfBirthFormField extends StatefulWidget {
  /// Callback function that is called when a date is selected.
  final void Function(DateTime) onDateSelected;

  const DateOfBirthFormField({super.key, required this.onDateSelected,});

  @override
  State<DateOfBirthFormField> createState() => _DateOfBirthFormFieldState();
}

/// State class for the DateOfBirthFormField widget.
class _DateOfBirthFormFieldState extends State<DateOfBirthFormField> {
  final TextEditingController _controller = TextEditingController();
  AppLocalizations? _l10n;
  DateTime? _selectedDate;

  static final DateTime _today = DateTime.now();
  static final DateTime _maxDate = DateTime(_today.year - 18, _today.month, _today.day);
  static final DateTime _minDate = DateTime(_today.year - 100);

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  /// Format the date as dd/MM/yyyy
  String _formatDate(DateTime date) {
    return '${date.day.toString().padLeft(2, '0')}/${date.month.toString().padLeft(2, '0')}/${date.year}';
  }

  /// Selects a date using a date picker dialog and updates the text field.
  Future<void> _selectDate() async {
    FocusScope.of(context).unfocus();
    final picked = await showDatePicker(
      context: context,
      initialDate: _selectedDate ?? _maxDate,
      firstDate: _minDate,
      lastDate: _maxDate,
    );
    if (picked != null) {
      setState(() {
        _controller.text = _formatDate(picked);
        _selectedDate = picked;
        widget.onDateSelected(picked);
      });
    }
  }

  /// Validates the date input to ensure it is in the correct format and that the user is at least 18 years old.
  String? _validate(String? value) {
    if (value == null || value.isEmpty) return _l10n!.date_of_birth_required;

    try {
      final parts = value.split('/');
      if (parts.length != 3) throw Exception();

      final day = int.parse(parts[0]);
      final month = int.parse(parts[1]);
      final year = int.parse(parts[2]);

      final date = DateTime(year, month, day);
      final age = _today.year - date.year - (_today.month < month || (_today.month == month && _today.day < day) ? 1 : 0);

      if (age < 18) return _l10n!.date_of_birth_minor;
    } catch (error) {
      return _l10n!.date_of_birth_invalid;
    }
    return null;
  }

  @override
  Widget build(BuildContext context) {
    _l10n = AppLocalizations.of(context)!;
    return TextFormField(
      controller: _controller,
      decoration: InputDecoration(labelText: _l10n!.date_of_birth),
      readOnly: true,
      onTap: _selectDate,
      validator: _validate,
    );
  }
}

/// GenderRadioGroup is a custom radio button group widget that allows users to select their
/// gender from a list of predefined options and will display the localized text for each.
class GenderRadioGroup extends StatelessWidget {
  final Gender? selectedGender;
  final ValueChanged<Gender?> onChanged;

  const GenderRadioGroup({
    super.key,
    required this.selectedGender,
    required this.onChanged,
  });

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;

    return Column(
      children: Gender.values.map((gender) {
        return RadioListTile<Gender>(
          title: Text(gender.localized(l10n)),
          value: gender,
          groupValue: selectedGender,
          onChanged: onChanged,
          visualDensity: VisualDensity.compact,
        );
      }).toList(),
    );
  }
}

/// UserTypeRadioGroup is a custom radio button group widget that allows users to select their user type
/// from a list of predefined options and will display the localized text for each.
class UserTypeRadioGroup extends StatelessWidget {
  final UserType? selectedUserType;
  final ValueChanged<UserType?> onChanged;

  const UserTypeRadioGroup({
    super.key,
    required this.selectedUserType,
    required this.onChanged,
  });

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;

    return Column(
      children: UserType.values.map((userType) {
        return RadioListTile<UserType>(
          title: Text(userType.localized(l10n)),
          value: userType,
          groupValue: selectedUserType,
          onChanged: onChanged,
          visualDensity: VisualDensity.compact,
        );
      }).toList(),
    );
  }
}

/// PasswordField is a custom form field widget that allows users to enter a password with validation.
class PasswordField extends StatefulWidget {
  final TextEditingController controller;
  final String? Function(String?)? validator;
  final String labelText;

  const PasswordField({super.key, required this.controller, this.validator, this.labelText = "Password"});

  @override
  State<PasswordField> createState() => _PasswordFieldState();
}

/// State class for the PasswordField widget.
class _PasswordFieldState extends State<PasswordField> {
  bool _obscure = true;

  void _toggleVisibility() {
    setState(() {
      _obscure = !_obscure;
    });
  }

  @override
  Widget build(BuildContext context) {
    return TextFormField(
      controller: widget.controller,
      obscureText: _obscure,
      validator: widget.validator,
      decoration: InputDecoration(
        labelText: widget.labelText,
        suffixIcon: IconButton(
          icon: Icon(
            _obscure ? Icons.visibility_off : Icons.visibility,
          ),
          onPressed: _toggleVisibility,
        ),
      ),
    );
  }
}