/// Utility functions for the application.
/// File: utils.dart
/// Author: José Oliveira 202300558
/// Version: 1.0.0
/// 2025-07-17
library;

import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';

/// Builds a SnackBar content widget with a progress indicator.
/// [message] is the text to display.
/// [textColor] sets the color of the message text.
/// [durationSeconds] sets the duration for the progress animation.
class ProgressSnackBarContent extends StatefulWidget {
  final String message;
  final Color textColor;
  final int durationSeconds;

  const ProgressSnackBarContent({
    super.key,
    required this.message,
    required this.textColor,
    required this.durationSeconds,
  });

  @override
  State<ProgressSnackBarContent> createState() => _ProgressSnackBarContentState();
}

/// State class for ProgressSnackBarContent that manages the progress animation.
class _ProgressSnackBarContentState extends State<ProgressSnackBarContent> with SingleTickerProviderStateMixin {
  late final Ticker _ticker;
  double _progress = 0;

  @override
  void initState() {
    super.initState();
    _ticker = Ticker((elapsed) {
      final newProgress = elapsed.inMilliseconds / (widget.durationSeconds * 1000);
      if (newProgress >= 1) {
        _ticker.stop();
        if (mounted) setState(() => _progress = 1.0);
      } else {
        if (mounted) setState(() => _progress = newProgress);
      }
    })..start();
  }

  @override
  void dispose() {
    _ticker.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        LinearProgressIndicator(
          value: _progress,
          backgroundColor: Colors.transparent,
          color: Theme.of(context).colorScheme.onSurfaceVariant,
        ),
        const SizedBox(width: 5),
        Text(widget.message, style: TextStyle(color: widget.textColor, fontSize: 16, fontFamily: "Manrope")),
      ],
    );
  }
}

/// Shows a error message in a SnackBar.
void showSnackBarError(BuildContext context, String message) {
  ScaffoldMessenger.of(context).showSnackBar(
    SnackBar(
      content: ProgressSnackBarContent(
        message: message,
        textColor: Theme.of(context).colorScheme.onError,
        durationSeconds: 3,
      ),
      backgroundColor: Theme.of(context).colorScheme.error,
      duration: const Duration(seconds: 3),
    ),
  );
}

/// Shows a confirmation message in a SnackBar.
void showSnackBarConfirm(BuildContext context, String message) {
  ScaffoldMessenger.of(context).showSnackBar(
    SnackBar(
      content: ProgressSnackBarContent(
        message: message,
        textColor: Theme.of(context).colorScheme.onPrimary,
        durationSeconds: 3,
      ),
      backgroundColor: Theme.of(context).colorScheme.primary,
      duration: const Duration(seconds: 3),
    ),
  );
}

/// Shows a generic message in a SnackBar.
void showSnackBarMessage(BuildContext context, String message) {
  ScaffoldMessenger.of(context).showSnackBar(
    SnackBar(
      content: ProgressSnackBarContent(
        message: message,
        textColor: Theme.of(context).colorScheme.onSurface,
        durationSeconds: 3,
      ),
      backgroundColor: Theme.of(context).colorScheme.surface,
      duration: const Duration(seconds: 3),
    ),
  );
}

/// Shows a dialog with a title and message.
void showDialogMessage(BuildContext context, String title, String message) {
  showDialog(
    context: context,
    builder: (context) => AlertDialog(
      title: Text(title),
      content: Text(message),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: Text(MaterialLocalizations.of(context).okButtonLabel),
        ),
      ],
    ),
  );
}