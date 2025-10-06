/// Store and Product Data Models
/// File: pages/store_model.dart
/// Author: José Oliveira 202300558
/// Version: 1.0.0
/// 2025-07-25
library;

import 'package:cloud_firestore/cloud_firestore.dart';
import '../constants.dart';

/// Model class for store information, including name, description, address, location, markets, and highlighted products.
class StoreInfo {
  String id; // Firestore document ID
  String name;
  List<String> images;
  String description;
  String address;
  GeoPoint? geoPoint;
  List<Market> markets;
  List<Product> products;
  List<DeliveryType> deliveryTypes;
  String? userId; // Optional user ID for the store owner

  StoreInfo({
    this.id = '', // Default to empty string for new stores
    required this.name,
    required this.images,
    required this.description,
    required this.address,
    this.geoPoint,
    required this.markets,
    required this.products,
    required this.deliveryTypes,
    this.userId,
  });

  factory StoreInfo.fromMap(Map<String, dynamic> data, String? docId) {
    return StoreInfo(
      id: docId ?? '', // Firestore document ID
      name: data['name'] ?? '',
      images: List<String>.from(data['images'] ?? []),
      description: data['description'] ?? '',
      address: data['address'] ?? '',
      geoPoint: data['geoPoint'],
      markets: (data['markets'] as List<dynamic>? ?? [])
          .map((market) => Market.fromMap(Map<String, dynamic>.from(market)))
          .toList(),
      products: (data['products'] as List<dynamic>? ?? [])
          .map((product) => Product.fromMap(Map<String, dynamic>.from(product)))
          .toList(),
      deliveryTypes: (data['deliveryTypes'] != null)
          ? List<DeliveryType>.from((data['deliveryTypes'] as List).map((e) => DeliveryType.values.firstWhere((d) => d.toString() == e, orElse: () => DeliveryType.pickup)))
          : [],
      userId: data['userId'], // Optional user ID
    );
  }

  Map<String, dynamic> toMap() => {
    'id': id, // Firestore document ID
    'name': name,
    'description': description,
    'address': address,
    'geoPoint': geoPoint,
    'markets': markets.map((m) => m.toMap()).toList(),
    'products': products.map((p) => p.toMap()).toList(),
    'images': images,
    'deliveryTypes': deliveryTypes.map((e) => e.toString()).toList(),
    'userId': userId, // Optional user ID
  };
}

/// Model class for product
class Product {
  String name;
  ProductCategory category;
  List<String> images; // base64-encoded
  String description;
  List<DeliveryType> deliveryTypes;
  double minQuantity;
  String quantityUnit; // 'kg' or 'unit'
  double stock;
  double minStock;
  double price;
  bool highlighted;

  Product({
    required this.name,
    required this.category,
    required this.images,
    required this.description,
    required this.deliveryTypes,
    required this.minQuantity,
    required this.quantityUnit,
    required this.stock,
    required this.minStock,
    required this.price,
    required this.highlighted,
  });

  factory Product.fromMap(Map<String, dynamic> map) {
    return Product(
      name: map['name'] ?? '',
      category: ProductCategory.values.firstWhere(
        (e) => e.toString() == map['category'],
        orElse: () => ProductCategory.other,
      ),
      images: List<String>.from(map['images'] ?? []),
      description: map['description'] ?? '',
      deliveryTypes: (map['deliveryTypes'] != null)
          ? List<DeliveryType>.from((map['deliveryTypes'] as List).map((e) => DeliveryType.values.firstWhere((d) => d.toString() == e, orElse: () => DeliveryType.pickup)))
          : [],
      minQuantity: (map['minQuantity'] ?? 0).toDouble(),
      quantityUnit: map['quantityUnit'] ?? 'unit', // Default to 'unit'
      stock: (map['stock'] ?? 0).toDouble(),
      minStock: (map['minStock'] ?? 0).toDouble(),
      price: (map['price'] ?? 0).toDouble(),
      highlighted: map['highlighted'] ?? false,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'name': name,
      'category': category.toString(),
      'images': images,
      'description': description,
      'deliveryTypes': deliveryTypes.map((e) => e.toString()).toList(),
      'minQuantity': minQuantity,
      'quantityUnit': quantityUnit,
      'stock': stock,
      'minStock': minStock,
      'price': price,
      'highlighted': highlighted,
    };
  }
}

/// Model class for market information, including name, schedule, images, and location.
class Market {
  final String name;
  final String schedule;
  final List<String> images; // Base64-encoded images
  final GeoPoint? geoPoint; // Optional GeoPoint for market location

  Market({
    required this.name,
    required this.schedule,
    required this.images,
    this.geoPoint,
  });

  /// Convert to Firestore-compatible map
  Map<String, dynamic> toMap() {
    return {
      'name': name,
      'schedule': schedule,
      'images': images,
      'geoPoint': geoPoint,
    };
  }

  /// Create Market from Firestore map
  factory Market.fromMap(Map<String, dynamic> map) {
    return Market(
      name: map['name'] ?? '',
      schedule: map['schedule'] ?? '',
      images: List<String>.from(map['images'] ?? []),
      geoPoint: map['geoPoint'],
    );
  }
}

/// Model class for CartItem, representing a product in the cart with its quantity.
class CartItem {
  final Product product;
  double quantity;

  CartItem({required this.product, required this.quantity});
}

/// Model class for Cart, representing a shopping cart containing multiple CartItems.
class Cart {
  final List<CartItem> items = [];

  void add(Product product, double quantity) {
    final existing = items.firstWhere(
      (item) => item.product.name == product.name,
      orElse: () => CartItem(product: product, quantity: 0),
    );
    if (existing.quantity == 0) {
      items.add(CartItem(product: product, quantity: quantity));
    } else {
      existing.quantity += quantity;
    }
  }

  void remove(Product product) {
    items.removeWhere((item) => item.product.name == product.name);
  }

  void updateQuantity(Product product, double quantity) {
    final existing = items.firstWhere(
      (item) => item.product.name == product.name,
      orElse: () => CartItem(product: product, quantity: 0),
    );
    if (existing.quantity > 0) {
      existing.quantity = quantity;
    }
  }

  void clear() {
    items.clear();
  }

  double get total => items.fold(0, (cartSum, item) => cartSum + item.product.price * item.quantity);
}

/// Model class for Order, representing a user's order with items, status, delivery type, and optional delivery address or transporter ID.
class StoreOrder {
  final String userId;
  final List<CartItem> items;
  final DateTime createdAt;
  OrderStatus status;
  DeliveryType deliveryType;
  String? deliveryAddress; // Optional delivery address for home delivery
  String? transporterId; // Optional transporter ID for transporter delivery
  String? notes; // Optional notes for the order

  StoreOrder({required this.userId, required this.items, required this.deliveryType, required this.createdAt, this.status = OrderStatus.pending, this.deliveryAddress, this.transporterId, this.notes});

  void updateStatus(OrderStatus newStatus) {
    status = newStatus;
  }

  Map<String, dynamic> toMap() => {
    'userId': userId,
    'items': items.map((item) => {
      'product': item.product.toMap(),
      'quantity': item.quantity,
    }).toList(),
    'createdAt': createdAt.toIso8601String(),
    'status': status.toString(),
    'deliveryType': deliveryType.toString(),
    'deliveryAddress': deliveryAddress,
    'transporterId': transporterId,
    'notes': notes,
  };

  factory StoreOrder.fromMap(Map<String, dynamic> data) {
    return StoreOrder(
      userId: data['userId'] ?? '',
      items: (data['items'] as List<dynamic>? ?? [])
          .map((item) => CartItem(
                product: Product.fromMap(Map<String, dynamic>.from(item['product'])),
                quantity: (item['quantity'] ?? 0).toDouble(),
              ))
          .toList(),
      createdAt: DateTime.parse(data['createdAt'] ?? DateTime.now().toIso8601String()),
      status: OrderStatus.values.firstWhere(
        (e) => e.toString() == data['status'],
        orElse: () => OrderStatus.pending,
      ),
      deliveryType: DeliveryType.values.firstWhere(
        (e) => e.toString() == data['deliveryType'],
        orElse: () => DeliveryType.pickup,
      ),
      deliveryAddress: data['deliveryAddress'],
      transporterId: data['transporterId'],
      notes: data['notes'],
    );
  }
}

/// Model class for Review, representing a user's review of a product with rating, comment, and timestamp.
class Review {
  String? id; // Firestore document ID
  String userId;
  String userName;
  double rating;
  String comment;
  final DateTime timestamp;

  Review({
    this.id,
    required this.userId,
    required this.userName,
    required this.rating,
    required this.comment,
    required this.timestamp,
  });

  factory Review.fromMap(Map<String, dynamic> map, String? docId) {
    return Review(
      id: docId ?? '', // Firestore document ID
      userId: map['userId'] ?? '',
      userName: map['userName'] ?? 'Anonymous',
      rating: (map['rating'] ?? 0).toDouble(),
      comment: map['comment'] ?? '',
      timestamp: (map['timestamp'] as Timestamp?)?.toDate() ?? DateTime.now(),
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'id': id, // Firestore document ID
      'userId': userId,
      'userName': userName,
      'rating': rating,
      'comment': comment,
      'timestamp': Timestamp.fromDate(timestamp),
    };
  }
}
