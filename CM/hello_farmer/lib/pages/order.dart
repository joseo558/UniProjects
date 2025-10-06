/// Order Page with a list of user orders
/// File: pages/order.dart
/// Author: José Oliveira 202300558
/// Version: 1.0.0
/// 2025-07-25
library;

import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:hello_farmer/pages/store_model.dart';
import 'package:intl/intl.dart';

import '../l10n/app_localizations.dart';

class OrdersPage extends StatelessWidget {
  const OrdersPage({super.key});

  Future<List<StoreOrder>> _fetchOrders() async {
    final uid = FirebaseAuth.instance.currentUser?.uid;
    if (uid == null) return [];

    final snapshot = await FirebaseFirestore.instance
        .collection('orders')
        .where('userId', isEqualTo: uid)
        .orderBy('createdAt', descending: true)
        .get();

    return snapshot.docs
        .map((doc) => StoreOrder.fromMap(doc.data()))
        .toList();
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;

    return Scaffold(
      appBar: AppBar(title: Text(l10n.orders)),
      body: FutureBuilder<List<StoreOrder>>(
        future: _fetchOrders(),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }

          if (!snapshot.hasData || snapshot.data!.isEmpty) {
            return Center(child: Text(l10n.no_orders));
          }

          final orders = snapshot.data!;

          return ListView.separated(
            padding: const EdgeInsets.all(16),
            itemCount: orders.length,
            separatorBuilder: (_, _) => const Divider(),
            itemBuilder: (context, index) {
              final order = orders[index];
              final formattedDate = DateFormat.yMMMd().add_Hm().format(order.createdAt);

              return ListTile(
                title: Text('${l10n.order} #${index + 1}'),
                subtitle: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text('${l10n.status}: ${order.status.name}'),
                    Text('${l10n.date}: $formattedDate'),
                    Text('${l10n.delivery_type}: ${order.deliveryType.name}'),
                  ],
                ),
              );
            },
          );
        },
      ),
    );
  }
}
