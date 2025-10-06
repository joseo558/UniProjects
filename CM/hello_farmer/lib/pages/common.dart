/// Common UI components and utilities for the app.
/// File: pages/common.dart
/// Author: José Oliveira 202300558
/// Version: 1.0.0
/// 2025-07-23
library;

import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'store_model.dart';
import '../constants.dart';
import '../l10n/app_localizations.dart';
import 'dart:convert';
import 'dart:math';
// Firebase
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
// Pages
import 'home.dart';
import 'order.dart';
import 'store.dart';
import 'settings.dart';

final _storage = const FlutterSecureStorage();

/// Check if user is farmer
Future<bool> isFarmer() async {
  return await _storage.read(key: 'userType') == UserType.farmer.toString();
}

/// Retrieves user profile data from secure storage or Firestore.
Future<Map<String, dynamic>> getUserProfileData() async {
  String? base64Image = await _storage.read(key: 'profile_image');
  String? pointsStr = await _storage.read(key: 'points');
  String? levelStr = await _storage.read(key: 'level');

  if (base64Image == null || pointsStr == null || levelStr == null) {
    // If not found in secure storage, fetch from Firestore
    final uid = FirebaseAuth.instance.currentUser?.uid;
    if (uid == null) throw Exception('User not authenticated');
    final doc = await FirebaseFirestore.instance.collection('users').doc(uid).get();
    final data = doc.data() ?? {};
    pointsStr ??= data['points']?.toString() ?? '0';
    base64Image ??= data['profile_image'];
    levelStr ??= data['level']?.toString() ?? '1';

    // Cache to secure storage
    await _storage.write(key: 'profile_image', value: base64Image);
    await _storage.write(key: 'points', value: pointsStr);
    await _storage.write(key: 'level', value: levelStr);
  }

  return {
    'profile_image': base64Image,
    'points': int.tryParse(pointsStr) ?? 0,
    'level': int.tryParse(levelStr) ?? 1,
  };
}

/// GamifiedProfileAvatar widget that displays the user's profile image and points in a circular avatar.
class GamifiedProfileAvatar extends StatefulWidget {
  const GamifiedProfileAvatar({super.key});

  @override
  State<GamifiedProfileAvatar> createState() => _GamifiedProfileAvatarState();
}

/// State class for GamifiedProfileAvatar, which fetches and displays the user's profile image and points.
class _GamifiedProfileAvatarState extends State<GamifiedProfileAvatar>
    with SingleTickerProviderStateMixin {
  String? _imageBytes;
  int _points = 0;
  int _level = 1;
  bool _isLoading = true;
  static const _maxLevel = 6;

  late AnimationController _glowController;

  @override
  void initState() {
    super.initState();
    _glowController = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 2),
    )..repeat(reverse: true);
    _loadProfileData();
  }

  @override
  void dispose() {
    _glowController.dispose();
    super.dispose();
  }

  Future<void> _loadProfileData() async {
    try {
      final data = await getUserProfileData();
      setState(() {
        _imageBytes = data['profile_image'];
        _points = data['points'];
        _level = data['level'];
        _isLoading = false;
      });
    } catch (e) {
      setState(() => _isLoading = false);
    }
  }

  Color _levelColor(int level) {
    const levelColors = [
      Colors.grey,
      Colors.blue,
      Colors.green,
      Colors.orange,
      Colors.purple,
      Colors.red,
    ];
    return levelColors[min(levelColors.length - 1, level ~/ 2)];
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) return const Center(child: CircularProgressIndicator());
    final bytes = (_imageBytes != null && _imageBytes!.isNotEmpty) ? base64Decode(_imageBytes!) : null;

    final borderColor = _levelColor(_level);

    return Container(
      margin: const EdgeInsets.only(top: 6, bottom: 6),
      child: Stack(
        alignment: Alignment.center,
        children: [
          // Glowing progress ring
          AnimatedBuilder(
            animation: _glowController,
            builder: (_, _) => Container(
              width: 70,
              height: 70,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                boxShadow: [
                  BoxShadow(
                    color: borderColor.withValues(
                        alpha: 0.3 + (_glowController.value * 0.4), // Adjust alpha for glow effect
                    ),
                    blurRadius: 2 + (_glowController.value * 5),
                    spreadRadius: 2,
                  ),
                ],
              ),
            ),
          ),
          // Progress circle
          SizedBox(
            width: 50,
            height: 70,
            child: CircularProgressIndicator(
              value: _level/_maxLevel,
              strokeWidth: 6,
              backgroundColor: Colors.grey[300],
              valueColor: AlwaysStoppedAnimation<Color>(borderColor),
            ),
          ),
          // Avatar
          CircleAvatar(
            radius: 36,
            backgroundColor: Colors.grey[200],
            backgroundImage: bytes != null ? MemoryImage(bytes) : null,
            child: bytes == null
                ? const Icon(Icons.person, size: 40, color: Colors.grey)
                : null,
          ),
          // Level badge
          Positioned(
            bottom: 0,
            left: 0,
            child: Container(
              padding:
              const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
              decoration: BoxDecoration(
                color: borderColor,
                borderRadius: BorderRadius.circular(12),
              ),
              child: Text(
                "Lv $_level",
                style: const TextStyle(
                  color: Colors.white,
                  fontWeight: FontWeight.bold,
                  fontSize: 12,
                  fontFamily: "Manrope",
                ),
              ),
            ),
          ),
          // Points badge
          Positioned(
            top: 0,
            left: 0,
            child: Container(
              padding:
              const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
              decoration: BoxDecoration(
                color: Theme.of(context).colorScheme.primary,
                borderRadius: BorderRadius.circular(12),
              ),
              child: Text(
                '$_points P',
                style: TextStyle(
                  color: Theme.of(context).colorScheme.onPrimary,
                  fontWeight: FontWeight.bold,
                  fontFamily: "Manrope",
                  fontSize: 14,
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}

/// Custom AppBar widget that includes the app logo, title, and action buttons for notifications and profile.
class CustomAppBar extends StatelessWidget implements PreferredSizeWidget {
  final String title;
  final int notificationCount;

  const CustomAppBar({
    super.key,
    required this.title,
    this.notificationCount = 0, // Default to 0 if not provided
  });

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;

    return AppBar(
      leading: IconButton(
        icon: Image.asset("img/logoIcon2.png", width: 70, height: 70),
        tooltip: l10n.home,
        onPressed: () => Navigator.pushNamed(context, '/home'),
      ),
      title: Text(title, style: const TextStyle(fontSize: 24, fontFamily: "Manrope")),
      actions: [
        StreamBuilder<int>(
          stream: getUnreadNotificationCount(),
          builder: (context, snapshot) {
            final count = snapshot.data ?? 0;
            return Stack(
              children: [
                IconButton(
                  icon: const Icon(Icons.notifications, color: Colors.amber, size: 28),
                  tooltip: l10n.notifications,
                  onPressed: () {
                    Navigator.pushNamed(context, '/notifications');
                  },
                ),
                if (count > 0)
                  Positioned(
                    right: 11,
                    top: 11,
                    child: Container(
                      padding: const EdgeInsets.all(2),
                      decoration: BoxDecoration(
                        color: Colors.red,
                        borderRadius: BorderRadius.circular(12),
                      ),
                      constraints: const BoxConstraints(
                        minWidth: 20,
                        minHeight: 20,
                      ),
                      child: Text(
                        '$count',
                        style: const TextStyle(
                          color: Colors.white,
                          fontSize: 12,
                        ),
                        textAlign: TextAlign.center,
                      ),
                    ),
                  ),
              ],
            );
          },
        ),
        SizedBox(width: 10), // Spacing between buttons
        SizedBox(
          width: 140,
          child: GamifiedProfileAvatar(),
        )
      ],
    );
  }

  @override
  Size get preferredSize => const Size.fromHeight(kToolbarHeight);
}

/// Navigation item model representing each tab in the bottom navigation bar.
class NavItem {
  final String title;
  final IconData icon;
  final Widget page;

  const NavItem({
    required this.title,
    required this.icon,
    required this.page,
  });
}

/// Returns a list of navigation items for the bottom navigation bar.
List<NavItem> getNavItems(BuildContext context) {
  final l10n = AppLocalizations.of(context)!;

  return [
    NavItem(
      title: l10n.home,
      icon: Icons.home,
      page: const HomeScreen(),
    ),
    NavItem(
      title: l10n.orders,
      icon: Icons.shopping_cart,
      page: OrdersPage(), // TODO: make const
    ),
    NavItem(
      title: l10n.store,
      icon: Icons.storefront,
      page: const StorePage(),
    ),
    NavItem(
      title: l10n.settings,
      icon: Icons.settings,
      page: SettingsPage(), // TODO
    ),
  ];
}

/// Builds the bottom navigation bar with the provided navigation items.
Widget buildBottomNavigationBar(int currentIndex, List<NavItem> navItems, void Function(int) setOnTap) {
  return BottomNavigationBar(
    currentIndex: currentIndex,
    type: BottomNavigationBarType.fixed,
    onTap: setOnTap,
    items: navItems.map((item) => BottomNavigationBarItem(icon: Icon(item.icon), label: item.title))
        .toList(),
  );
}

/// Firebase imports for notifications
Stream<int> getUnreadNotificationCount() {
  final userId = FirebaseAuth.instance.currentUser?.uid;
  return FirebaseFirestore.instance
      .collection('notifications')
      .where('userId', isEqualTo: userId)
      .where('read', isEqualTo: false)
      .orderBy('timestamp', descending: true)
      .snapshots()
      .map((snapshot) => snapshot.docs.length);
}

/// Get all notifications for the current user.
Stream<QuerySnapshot> getUserNotifications() {
  final userId = FirebaseAuth.instance.currentUser?.uid;
  return FirebaseFirestore.instance
      .collection('notifications')
      .where('userId', isEqualTo: userId)
      .orderBy('timestamp', descending: true)
      .snapshots();
}

/// Creates a notification in Firestore for the current user.
Future<void> createNotification({
  required String title,
  required String body,
}) async {
  final user = FirebaseAuth.instance.currentUser;
  if (user == null) return;

  await FirebaseFirestore.instance.collection('notifications').add({
    'userId': user.uid,
    'title': title,
    'body': body,
    'read': false,
    'timestamp': FieldValue.serverTimestamp(),
  });
}

/// Loads the store information for the current user (farmer) from Firestore.
Future<Map<String, dynamic>?> loadStoreInfo({bool loadReviews = false, String? docId}) async {
  List<Review> reviews = [];
  Map<String, dynamic>? data;

  if (docId != null) {
    final doc = await FirebaseFirestore.instance
      .collection('stores')
      .doc(docId)
      .get();
    if (!doc.exists) return null;
    data = doc.data();
  } else {
    // is farmer
    final uid = FirebaseAuth.instance.currentUser?.uid;
    if (uid == null) return null;
    final querySnapshot = await FirebaseFirestore.instance
      .collection('stores')
      .where('userId', isEqualTo: uid)
      .limit(1).get();
    if (querySnapshot.docs.isEmpty) return null;
    final doc = querySnapshot.docs.first;
    data = doc.data();
    docId = doc.id; // set id
  }

  bool isFarmerBool = await isFarmer();

  if (loadReviews) {
    final reviewsSnapshot = await FirebaseFirestore.instance
      .collection('stores')
      .doc(docId)
      .collection('reviews')
      .orderBy('timestamp', descending: true)
      .get();
    reviews = reviewsSnapshot.docs
      .map((reviewDoc) => Review.fromMap(reviewDoc.data(), reviewDoc.id))
      .toList();
  }

  return {
    'storeInfo': StoreInfo.fromMap(data!, docId),
    'userType': isFarmerBool ? UserType.farmer : UserType.consumer,
    'reviews': reviews,
  };
}

/// Loads stores information for the current user (customer) from Firestore.
Future<List<StoreInfo>> loadStoresInfo() async {
  final uid = FirebaseAuth.instance.currentUser?.uid;
  if (uid == null) return [];
  final querySnapshot = await FirebaseFirestore.instance
      .collection('stores').get();
  if (querySnapshot.docs.isEmpty) return [];
  return querySnapshot.docs.map((doc) => StoreInfo.fromMap(doc.data(), doc.id)).toList();
}

/// Get the current location of the user
Future<GeoPoint> getLocation() async {
  LocationPermission permission = await Geolocator.checkPermission();
  if (permission == LocationPermission.denied) {
    try {
      permission = await Geolocator.requestPermission();
    } catch (e) {
      return defaultGeoPoint; // Return a default GeoPoint
    }
    if (permission == LocationPermission.denied) {
      return defaultGeoPoint; // Return a default GeoPoint
    }
  }
  if (permission == LocationPermission.deniedForever) {
    return defaultGeoPoint; // Return a default GeoPoint
  }
  bool isLocationEnabled = await Geolocator.isLocationServiceEnabled();
  if (!isLocationEnabled) {
    return defaultGeoPoint;
  }
  try {
    final geoPoint = await Geolocator.getCurrentPosition(timeLimit: const Duration(seconds: 10));
    return GeoPoint(geoPoint.latitude, geoPoint.longitude);
  } catch (e) {
    return defaultGeoPoint; // Return a default GeoPoint
  }
}

/// Convert LatLng to GeoPoint
GeoPoint? latLngToGeoPoint(LatLng? latLng) {
  if (latLng == null) return null;
  return GeoPoint(latLng.latitude, latLng.longitude);
}

/// Convert GeoPoint to LatLng
LatLng? geoPointToLatLng(GeoPoint? geoPoint) {
  if (geoPoint == null) return null;
  return LatLng(geoPoint.latitude, geoPoint.longitude);
}