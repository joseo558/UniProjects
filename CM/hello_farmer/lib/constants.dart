/// This file contains constants used throughout the application.
/// File: constants.dart
/// Author: José Oliveira 202300558
/// Version: 1.0.0
/// 2025-07-09
library;

import 'package:cloud_firestore/cloud_firestore.dart';

import 'l10n/app_localizations.dart';

// Constants used in the HelloFarmer application.
enum Gender { m, f, o }
enum UserType { farmer, consumer}
enum OrderStatus { pending, processing, readyForDelivery, completed, cancelled }
enum DeliveryType { pickup, transporter, homeDelivery }
// Default categories for products.
enum ProductCategory {
  vegetables, fruits, grains, spices, herbs, nuts, seeds,
  dairy, meat, seafood, fish, cheese, charcuterie,
  beverages, bakery, baskets, oils, sauces, jams, sweets,
  eggs, flowers, plants, other,
}
// GPS coordinates for default location.
const double defaultLat = 38.7169;
const double defaultLong = -9.1399; // Lisbon
const GeoPoint defaultGeoPoint = GeoPoint(defaultLat, defaultLong);

/// Extension methods for Gender to provide localized strings.
extension GenderExtension on Gender {
  String localized(AppLocalizations l10n) {
    switch (this) {
      case Gender.m:
        return l10n.gender_m;
      case Gender.f:
        return l10n.gender_f;
      case Gender.o:
        return l10n.gender_o;
    }
  }
}

/// Extension methods for UserType to provide localized strings.
extension UserTypeExtension on UserType {
  String localized(AppLocalizations l10n) {
    switch (this) {
      case UserType.farmer:
        return l10n.userType_farmer;
      case UserType.consumer:
        return l10n.userType_consumer;
    }
  }
}

/// Extension methods for OrderStatus to provide localized strings.
extension OrderStatusExtension on OrderStatus {
  String localized(AppLocalizations l10n) {
    switch (this) {
      case OrderStatus.pending:
        return l10n.order_status_pending;
      case OrderStatus.processing:
        return l10n.order_status_processing;
      case OrderStatus.readyForDelivery:
        return l10n.order_status_readyForDelivery;
      case OrderStatus.completed:
        return l10n.order_status_completed;
      case OrderStatus.cancelled:
        return l10n.order_status_cancelled;
    }
  }
}

/// Extension methods for DeliveryType to provide localized strings.
extension DeliveryTypeExtension on DeliveryType {
  String localized(AppLocalizations l10n) {
    switch (this) {
      case DeliveryType.pickup:
        return l10n.deliveryType_pickup;
      case DeliveryType.transporter:
        return l10n.deliveryType_transporter;
      case DeliveryType.homeDelivery:
        return l10n.deliveryType_homeDelivery;
    }
  }
}

/// Extension methods for ProductCategory to provide localized strings.
extension ProductCategoryExtension on ProductCategory {
  String localized(AppLocalizations l10n) {
    switch (this) {
      case ProductCategory.vegetables: return l10n.productCategory_vegetables;
      case ProductCategory.fruits: return l10n.productCategory_fruits;
      case ProductCategory.grains: return l10n.productCategory_grains;
      case ProductCategory.spices: return l10n.productCategory_spices;
      case ProductCategory.herbs: return l10n.productCategory_herbs;
      case ProductCategory.nuts: return l10n.productCategory_nuts;
      case ProductCategory.seeds: return l10n.productCategory_seeds;
      case ProductCategory.dairy: return l10n.productCategory_dairy;
      case ProductCategory.meat: return l10n.productCategory_meat;
      case ProductCategory.seafood: return l10n.productCategory_seafood;
      case ProductCategory.fish: return l10n.productCategory_fish;
      case ProductCategory.cheese: return l10n.productCategory_cheese;
      case ProductCategory.charcuterie: return l10n.productCategory_charcuterie;
      case ProductCategory.beverages: return l10n.productCategory_beverages;
      case ProductCategory.bakery: return l10n.productCategory_bakery;
      case ProductCategory.baskets: return l10n.productCategory_baskets;
      case ProductCategory.oils: return l10n.productCategory_oils;
      case ProductCategory.sauces: return l10n.productCategory_sauces;
      case ProductCategory.jams: return l10n.productCategory_jams;
      case ProductCategory.sweets: return l10n.productCategory_sweets;
      case ProductCategory.eggs: return l10n.productCategory_eggs;
      case ProductCategory.flowers: return l10n.productCategory_flowers;
      case ProductCategory.plants: return l10n.productCategory_plants;
      case ProductCategory.other: return l10n.productCategory_other;
    }
  }
}


