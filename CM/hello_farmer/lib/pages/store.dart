/// Store Page with Tabs for Info, Products, and Reviews
/// File: pages/store.dart
/// Author: José Oliveira 202300558
/// Version: 1.0.0
/// 2025-07-25
library;

import 'dart:async';
import 'dart:convert';
import 'package:flutter_rating_bar/flutter_rating_bar.dart';

import '../constants.dart';
import 'common.dart';
import 'package:hello_farmer/utils.dart';
import 'package:url_launcher/url_launcher.dart';
import '../l10n/app_localizations.dart';
import 'store_model.dart';
// UI
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:carousel_slider/carousel_slider.dart';
import 'package:dropdown_search/dropdown_search.dart';
// GPS
import 'package:google_maps_flutter/google_maps_flutter.dart';
// Firebase
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';

/// StorePage is a widget that displays a tabbed interface for a store.
class StorePage extends StatefulWidget {
  const StorePage({super.key});

  @override
  State<StorePage> createState() => _StorePageState();
}

/// State class for StorePage, which manages the tabs for store information, products, and reviews.
class _StorePageState extends State<StorePage> {
  String? selectedStoreId;
  late Future<bool> _isFarmerFuture;

  @override
  void initState() {
    super.initState();
    _isFarmerFuture = isFarmer();
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;

    // Use FutureBuilder to handle the asynchronous check for farmer status
    return FutureBuilder<bool>(
      future: _isFarmerFuture,
      builder: (context, snapshot) {
        if (!snapshot.hasData) {
          return const Center(child: CircularProgressIndicator());
        }

        final isFarmer = snapshot.data!;
        final storeTabs = [
          if (!isFarmer) Tab(text: l10n.stores),
          Tab(text: l10n.store),
          Tab(text: l10n.products),
          Tab(text: l10n.reviews),
        ];
        final storeTabViews = [
          if(!isFarmer) StoresInfoTab(
              storeId: selectedStoreId,
              onStoreSelected: (id) {
                setState(() {
                  selectedStoreId = id;
                });
              },
          ),
          StoreInfoTab(storeId: selectedStoreId),
          StoreProductsTab(storeId: selectedStoreId),
          StoreReviewsTab(storeId: selectedStoreId),
        ];

        return DefaultTabController(
          length: storeTabs.length,
          child: Column(
            children: [
              TabBar(
                tabs: storeTabs,
                labelColor: Theme.of(context).colorScheme.primary,
                unselectedLabelColor: Colors.grey,
                indicatorColor: Theme.of(context).colorScheme.primary,
              ),
              Expanded(
                child: TabBarView(children: storeTabViews),
              ),
            ],
          ),
        );
      },
    );
  }
}

/// StoreInfoTab displays the store's information, including name, images, description, location, markets, and products.
class StoreInfoTab extends StatefulWidget {
  const StoreInfoTab({super.key, this.storeId});
  final String? storeId;

  @override
  State<StoreInfoTab> createState() => _StoreInfoTabState();
}

/// State class for StoreInfoTab, which fetches and displays the store's information.
class _StoreInfoTabState extends State<StoreInfoTab> {
  StoreInfo? storeInfo;
  bool _isFarmer = false;
  bool _showContent = false;

  @override
  void initState() {
    super.initState();
    loadStoreInfo(docId: widget.storeId).then(
      (data) {
        if (data != null) {
          setState(() {
            storeInfo = data['storeInfo'];
            _isFarmer = data['userType'] == UserType.farmer;
          });
        }
      },
    );
    Future.delayed(const Duration(seconds: 2), () {
      if (mounted) setState(() => _showContent = true);
    });
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;
    if (!_showContent) {
      return const Center(child: CircularProgressIndicator());
    }
    if (storeInfo == null) {
      if (_isFarmer) {
        return Padding(
          padding: const EdgeInsets.all(20),
          child: Center(
            child: Column(
              children: [
                const SizedBox(height: 50),
                IconButton(
                  icon: const Icon(Icons.edit, size: 50, color: Colors.grey),
                  onPressed: () => Navigator.pushNamed(context, '/store-edit', arguments: {'storeInfo': StoreInfo.fromMap({}, null) }),
                ),
                const SizedBox(height: 10),
                Text(l10n.no_store_info, style: const TextStyle(fontSize: 16, color: Colors.grey, fontFamily: "Manrope")),
              ]
            )
          ),
        );
      } else {
        return Padding(
          padding: const EdgeInsets.all(20),
          child: Center(
            child: Text(l10n.no_store_info, style: const TextStyle(fontSize: 16, color: Colors.grey, fontFamily: "Manrope")),
          ),
        );
      }
    }

    final decodedImages = storeInfo!.images.map((b64) => base64Decode(b64)).toList();
    final products = storeInfo!.products.where((p) => p.highlighted == true).toList();
    LatLng storePosition = geoPointToLatLng(storeInfo!.geoPoint) ?? const LatLng(defaultLat, defaultLong);

    return Stack(
        children: [
          SingleChildScrollView(
            padding: const EdgeInsets.all(20),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(storeInfo!.name, style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold, fontFamily: "Manrope")),
                    _isFarmer ?
                      IconButton(
                        icon: const Icon(Icons.edit, color: Colors.black),
                        onPressed: () => Navigator.pushNamed(context, '/store-edit', arguments: {'storeInfo': storeInfo}),
                      )
                    :
                      IconButton(
                        icon: const Icon(Icons.navigate_before, color: Colors.black),
                        onPressed: () => Navigator.pop(context),
                      )
                  ],
                ),
                const SizedBox(height: 10),
                decodedImages.isNotEmpty ? CarouselSlider(
                  options: CarouselOptions(height: 180, enlargeCenterPage: true, autoPlay: true),
                  items: decodedImages
                    .map((img) => ClipRRect(
                      borderRadius: BorderRadius.circular(12),
                      child: Image.memory(img, fit: BoxFit.cover, width: double.infinity),
                    ))
                    .toList(),
                )
                  : SizedBox(height: 30, child: Center( child: Text(l10n.no_images, style: const TextStyle(fontSize: 16, fontFamily: "Manrope")))),
                const SizedBox(height: 10),
                Text(l10n.store_description, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold, fontFamily: "Manrope")),
                Text(storeInfo!.description, style: const TextStyle(fontSize: 16, fontFamily: "Manrope")),
                const SizedBox(height: 10),
                Text(l10n.deliveryType, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold, fontFamily: "Manrope")),
                Wrap(
                  spacing: 8,
                  children: storeInfo!.deliveryTypes.map((type) =>
                    Chip(
                      label: Text(type.localized(l10n), style: TextStyle(fontFamily: "Manrope", color: Theme.of(context).colorScheme.onPrimaryContainer)),
                      backgroundColor: Theme.of(context).colorScheme.primaryContainer,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(8),
                        side: BorderSide(color: Theme.of(context).colorScheme.primary, width: 1),
                      ),
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                    )
                  ).toList(),
                ),
                const SizedBox(height: 10),
                Text(l10n.localization, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold, fontFamily: "Manrope")),
                Text(storeInfo!.address, style: const TextStyle(fontSize: 16, fontFamily: "Manrope")),
                const SizedBox(height: 10),
                SizedBox(
                  height: 300,
                  width: 300,
                  child: GoogleMap(
                    mapType: MapType.normal,
                    initialCameraPosition: CameraPosition(
                      target: storePosition,
                      zoom: 14.0,
                    ),
                    onTap: (LatLng storePosition) async {
                      final lat = storePosition.latitude;
                      final lng = storePosition.longitude;
                      final uri = Uri.parse('https://www.google.com/maps/search/?api=1&query=$lat,$lng');
                      if (await canLaunchUrl(uri)) await launchUrl(uri, mode: LaunchMode.externalApplication);
                    },
                    markers: {
                      Marker(
                        markerId: const MarkerId('store'),
                        position: storePosition,
                        draggable: false,
                      ),
                    },
                  ),
                ),
                GestureDetector(
                  onTap: () async {
                    final lat = storePosition.latitude;
                    final lng = storePosition.longitude;
                    final uri = Uri.parse('https://www.google.com/maps/search/?api=1&query=$lat,$lng');
                    if (await canLaunchUrl(uri)) await launchUrl(uri, mode: LaunchMode.externalApplication);
                  },
                  child: Text(l10n.open_gps_app, style: TextStyle(fontSize: 16, color: Theme.of(context).primaryColor, decoration: TextDecoration.underline, fontFamily: "Manrope")),
                ),
                const SizedBox(height: 10),
                Text(l10n.markets_usual, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold, fontFamily: "Manrope")),
                if (storeInfo!.markets.isEmpty)
                  Text(l10n.no_markets, style: const TextStyle(fontSize: 16, fontStyle: FontStyle.italic, color: Colors.grey, fontFamily: "Manrope"))
                else
                  ...storeInfo!.markets.map((market) {
                    final List<String> images = List<String>.from(market.images);
                    final decodedImages = images.map(base64Decode).toList();
                    final location = market.geoPoint;

                    return Card(
                      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                      margin: const EdgeInsets.symmetric(vertical: 6),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          decodedImages.isNotEmpty ? CarouselSlider(
                            options: CarouselOptions(
                              height: 100,
                              enlargeCenterPage: true,
                              autoPlay: true,
                              viewportFraction: 1,
                            ),
                            items: decodedImages.map((img) {
                              return ClipRRect(
                                borderRadius: const BorderRadius.vertical(top: Radius.circular(12)),
                                child: Image.memory(
                                  img,
                                  fit: BoxFit.cover,
                                  width: double.infinity,
                                ),
                              );
                            }).toList(),
                          )
                              : SizedBox(height: 30, child: Center( child: Text(l10n.no_images, style: const TextStyle(fontSize: 16, fontFamily: "Manrope")))),
                          ListTile(
                            title: Text(
                              market.name,
                              style: const TextStyle(fontWeight: FontWeight.bold, fontFamily: "Manrope"),
                            ),
                            subtitle: Text(
                              market.schedule,
                              style: const TextStyle(fontFamily: "Manrope"),
                            ),
                            trailing: Row(
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                if (location != null)
                                  GestureDetector(
                                    onTap: () async {
                                      final lat = location.latitude;
                                      final lng = location.longitude;
                                      final uri = Uri.parse('https://www.google.com/maps/search/?api=1&query=$lat,$lng');
                                      if (await canLaunchUrl(uri)) await launchUrl(uri, mode: LaunchMode.externalApplication);
                                    },
                                    child: Icon(Icons.gps_fixed, color: Theme.of(context).colorScheme.primary),
                                  ),
                              ],
                            ),
                          ),
                        ],
                      ),
                    );
                  }),
                const SizedBox(height: 10),
                Text(l10n.highlighted_products, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold, fontFamily: "Manrope")),
                Flexible(
                  fit: FlexFit.loose,
                  child: products.isEmpty
                      ? Center(child: Text(l10n.no_highlighted_products, style: const TextStyle(fontSize: 16, fontStyle: FontStyle.italic, color: Colors.grey, fontFamily: "Manrope")))
                      : ListView.builder(
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    itemCount: products.length,
                    itemBuilder: (context, index) {
                      final product = products[index];
                      final decodedImages = product.images.map(base64Decode).toList();

                      return ListTile(
                        contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                        leading: decodedImages.isEmpty
                            ? const Icon(Icons.image_not_supported, size: 48)
                            : SizedBox(
                          width: 80,
                          height: 80,
                          child: PageView.builder(
                            itemCount: decodedImages.length,
                            itemBuilder: (context, i) {
                              return Image.memory(decodedImages[i], fit: BoxFit.cover);
                            },
                            scrollDirection: Axis.horizontal,
                            pageSnapping: true,
                            allowImplicitScrolling: true,
                          ),
                        ),
                        title: Text(product.name, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold, fontFamily: "Manrope")),
                        subtitle: Text("${product.category.localized(l10n)} • ${product.price.toStringAsFixed(2)}€/ ${product.quantityUnit}", style: const TextStyle(fontSize: 14, fontFamily: "Manrope")),
                      );
                    },
                  ),
                ),
              ],
            ),
          ),
        ],
    );
  }
}

/// StoresInfoTab displays the stores available in the app
class StoresInfoTab extends StatefulWidget {
  final String? storeId;
  final ValueChanged<String> onStoreSelected;
  const StoresInfoTab({super.key, required this.storeId, required this.onStoreSelected});

  @override
  State<StoresInfoTab> createState() => _StoresInfoTabState();
}

/// State class for StoresInfoTab, which fetches and displays the list of stores.
class _StoresInfoTabState extends State<StoresInfoTab> {
  List<StoreInfo> stores = [];
  bool _showContent = false;

  @override
  void initState() {
    super.initState();
    loadStoresInfo().then((data) {
      setState(() {
        stores = data;
      });
    });
    Future.delayed(const Duration(seconds: 2), () {
      if (mounted) setState(() => _showContent = true);
    });
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;
    if (!_showContent) {
      return const Center(child: CircularProgressIndicator());
    }
    if (stores.isEmpty) {
      return Padding(
        padding: const EdgeInsets.all(20),
        child: Center(
          child: Text(l10n.no_stores, style: const TextStyle(fontSize: 16, color: Colors.grey, fontFamily: "Manrope")),
        ),
      );
    }

    return ListView.builder(
        itemCount: stores.length,
        itemBuilder: (context, index) {
          final store = stores[index];
          final decodedImages = store.images.map((b64) => base64Decode(b64)).toList();

          return Card(
            margin: const EdgeInsets.symmetric(vertical: 8),
            child: ListTile(
              contentPadding: const EdgeInsets.all(12),
              leading: decodedImages.isNotEmpty
                  ? ClipRRect(
                      borderRadius: BorderRadius.circular(8),
                      child: Image.memory(decodedImages.first, width: 80, height: 80, fit: BoxFit.cover),
                    )
                  : const Icon(Icons.store, size: 48),
              title: Text(store.name, style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold, fontFamily: "Manrope")),
              subtitle: Text(store.description, style: const TextStyle(fontSize: 14, fontFamily: "Manrope")),
              onTap: () {
                widget.onStoreSelected(store.id);
                DefaultTabController.of(context).animateTo(1); // Switch to StoreInfoTab
              }
            ),
          );
        },
    );
  }
}

/// EditStorePage allows farmers to edit their store information, including name, description, address, markets, and highlighted products.
class EditStorePage extends StatefulWidget {
  const EditStorePage({super.key});

  @override
  State<EditStorePage> createState() => _EditStorePageState();
}

/// State class for EditStorePage, which handles the form for editing store information.
class _EditStorePageState extends State<EditStorePage> {
  final _formKey = GlobalKey<FormState>();
  late StoreInfo storeData;
  late LatLng pickedPosition;
  final Completer<GoogleMapController> _controller = Completer<GoogleMapController>();

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    final args = ModalRoute.of(context)?.settings.arguments as Map?;

    if (args != null) {
      final StoreInfo initialData = args['storeInfo'];

      setState(() {
        storeData = initialData;
      });
    }
  }

  Future<void> _saveStore() async {
    final l10n = AppLocalizations.of(context)!;
    if (!_formKey.currentState!.validate()) return;
    _formKey.currentState!.save();

    final uid = FirebaseAuth.instance.currentUser?.uid;
    if (uid == null) return;

    final query = await FirebaseFirestore.instance
        .collection('stores')
        .where('userId', isEqualTo: uid)
        .limit(1)
        .get();

    final docId = query.docs.isEmpty ? null : query.docs.first.id;
    storeData.geoPoint = latLngToGeoPoint(pickedPosition);
    storeData.userId = uid; // For database rules checks

    await FirebaseFirestore.instance
        .collection('stores')
        .doc(docId)
        .set(storeData.toMap()); // Create or update

    if (mounted) showSnackBarConfirm(context, l10n.store_update_success);
    if (mounted) Navigator.pop(context);
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;
    if (storeData.geoPoint != null) {
      pickedPosition = geoPointToLatLng(storeData.geoPoint)!;
    } else {
      pickedPosition = const LatLng(defaultLat, defaultLong); // Lisbon
    }

    return Scaffold(
      appBar: AppBar(
          title: Text(l10n.store_edit, style: const TextStyle(fontFamily: "Manrope")),
          actions: [
            IconButton(
              icon: Icon(Icons.save, color: Theme.of(context).colorScheme.primary, size: 30),
              onPressed: _saveStore,
            ),
          ]
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              TextFormField(
                initialValue: storeData.name,
                decoration: InputDecoration(labelText: l10n.store_name),
                validator: (value) => value == null || value.isEmpty ? l10n.store_name_required : null,
                onSaved: (value) => storeData.name = value ?? '',
              ),
              const SizedBox(height: 10),
              TextFormField(
                initialValue: storeData.description,
                decoration: InputDecoration(labelText: l10n.store_description),
                validator: (value) => value == null || value.isEmpty ? l10n.store_description_required : null,
                maxLines: 3,
                onSaved: (value) => storeData.description = value ?? '',
              ),
              const SizedBox(height: 10),
              Text(l10n.deliveryType, style: const TextStyle(fontFamily: "Manrope")),
              Wrap(
                spacing: 4,
                children: DeliveryType.values.map((type) {
                  return Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Checkbox(
                        value: storeData.deliveryTypes.contains(type),
                        onChanged: (selected) {
                          setState(() {
                            if (selected == true) {
                              storeData.deliveryTypes.add(type);
                            } else {
                              storeData.deliveryTypes.remove(type);
                            }
                          });
                        },
                      ),
                      Text(type.localized(l10n), style: const TextStyle(fontFamily: "Manrope")),
                    ],
                  );
                }).toList(),
              ),
              const SizedBox(height: 10),
              TextFormField(
                initialValue: storeData.address,
                decoration: InputDecoration(labelText: l10n.store_address),
                validator: (value) => value == null || value.isEmpty ? l10n.store_address_required : null,
                onSaved: (value) => storeData.address = value ?? '',
              ),
              const SizedBox(height: 10),
              Center(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.center,
                  children: [
                    SizedBox(
                      height: 300,
                      width: 300,
                      child: GoogleMap(
                        mapType: MapType.normal,
                        initialCameraPosition: CameraPosition(
                          target: pickedPosition,
                          zoom: 14.0,
                        ),
                        onMapCreated: (GoogleMapController controller) {
                          _controller.complete(controller);
                        },
                        onTap: (pos) => setState(() => pickedPosition = pos),
                        markers: {
                          Marker(
                            markerId: const MarkerId('store'),
                            position: pickedPosition,
                            draggable: true,
                            onDragEnd: (pos) => setState(() => pickedPosition = pos),
                          ),
                        },
                      ),
                    ),
                    const SizedBox(height: 10),
                    ElevatedButton.icon(
                        style: ElevatedButton.styleFrom(
                          backgroundColor: Theme.of(context).colorScheme.primaryContainer,
                          foregroundColor: Theme.of(context).colorScheme.onPrimaryContainer,
                        ),
                        onPressed: () async {
                          final geoPoint = await getLocation();
                          final GoogleMapController controller = await _controller.future;
                          setState(() {
                            storeData.geoPoint = geoPoint;
                            pickedPosition = geoPointToLatLng(geoPoint)!;
                          });
                          controller.animateCamera(CameraUpdate.newLatLngZoom(pickedPosition, 15));
                        },
                        icon: Icon(Icons.my_location, color: Theme.of(context).colorScheme.onPrimaryContainer),
                        label: Text(l10n.use_current_location, style: TextStyle(fontFamily: "Manrope", color: Theme.of(context).colorScheme.onPrimaryContainer))
                    ),
                    const SizedBox(height: 10),
                    Text("Lat: ${pickedPosition.latitude}, Long: ${pickedPosition.longitude}", style: const TextStyle(fontFamily: "Manrope")),
                  ],
                ),
              ),
              const Divider(),
              Text(l10n.markets_usual, style: const TextStyle(fontWeight: FontWeight.bold)),
              SizedBox(height: 10),
              ...storeData.markets.map((market) {
                final List<String> images = List<String>.from(market.images);
                final decodedImages = images.map(base64Decode).toList();
                final location = market.geoPoint;

                return Card(
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                  margin: const EdgeInsets.symmetric(vertical: 6),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      decodedImages.isNotEmpty ? CarouselSlider(
                        options: CarouselOptions(
                          height: 100,
                          enlargeCenterPage: true,
                          autoPlay: true,
                          viewportFraction: 1,
                        ),
                        items: decodedImages.map((img) {
                          return ClipRRect(
                            borderRadius: const BorderRadius.vertical(top: Radius.circular(12)),
                            child: Image.memory(
                              img,
                              fit: BoxFit.cover,
                              width: double.infinity,
                            ),
                          );
                        }).toList(),
                      )
                        : SizedBox(height: 30, child: Center( child: Text(l10n.no_images, style: const TextStyle(fontSize: 16, fontFamily: "Manrope")))),
                      ListTile(
                        title: Text(
                          market.name,
                          style: const TextStyle(fontWeight: FontWeight.bold, fontFamily: "Manrope"),
                        ),
                        subtitle: Text(
                          market.schedule,
                          style: const TextStyle(fontFamily: "Manrope"),
                        ),
                        trailing: Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            if (location != null)
                              GestureDetector(
                                onTap: () async {
                                  final lat = location.latitude;
                                  final lng = location.longitude;
                                  final uri = Uri.parse('https://www.google.com/maps/search/?api=1&query=$lat,$lng');
                                  if (await canLaunchUrl(uri)) await launchUrl(uri, mode: LaunchMode.externalApplication);
                                },
                                child: Icon(Icons.gps_fixed, color: Theme.of(context).colorScheme.primary),
                              ),
                            IconButton(
                              icon: const Icon(Icons.edit),
                              onPressed: () async {
                                final updatedMarket = await showDialog<Market>(
                                  context: context,
                                  builder: (_) => _MarketDialog(initialMarket: market),
                                );
                                if (updatedMarket != null) {
                                  setState(() {
                                    storeData.markets.remove(market);
                                    storeData.markets.add(updatedMarket);
                                  });
                                }
                              },
                            ),
                            IconButton(
                              icon: const Icon(Icons.delete),
                              onPressed: () => setState(() => storeData.markets.remove(market)),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                );
              }),
              const SizedBox(height: 10),
              Center(
                child: ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Theme.of(context).colorScheme.primaryContainer,
                    foregroundColor: Theme.of(context).colorScheme.onPrimaryContainer,
                  ),
                  onPressed: () async {
                    final market = await showDialog<Market>(
                      context: context,
                      builder: (_) => _MarketDialog(),
                    );
                    if (market != null) setState(() => storeData.markets.add(market));
                  },
                  child: Text(l10n.market_add, style: const TextStyle(fontFamily: "Manrope"))
                ),
              ),
              const Divider(),
              const SizedBox(height: 10),
              Text(l10n.store_images, style: const TextStyle(fontWeight: FontWeight.bold, fontFamily: "Manrope")),
              Wrap(
                spacing: 8,
                children: [
                  ...storeData.images.map((b64) => Stack(
                    children: [
                      Image.memory(base64Decode(b64), width: 100, height: 100, fit: BoxFit.cover),
                      Positioned(
                        right: 0,
                        top: 0,
                        child: IconButton(
                          icon: const Icon(Icons.close),
                          onPressed: () => setState(() => storeData.images.remove(b64)),
                        ),
                      )
                    ],
                  )),
                  IconButton(
                    icon: const Icon(Icons.add_a_photo),
                    onPressed: () async {
                      final picker = ImagePicker();
                      final picked = await picker.pickImage(source: ImageSource.gallery);
                      if (picked != null) {
                        final bytes = await picked.readAsBytes();
                        setState(() => storeData.images.add(base64Encode(bytes)));
                      }
                    },
                  )
                ],
              ),
              const SizedBox(height: 10),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(l10n.save, style: const TextStyle(fontWeight: FontWeight.bold, fontFamily: "Manrope")),
                  const SizedBox(width: 10),
                  IconButton(
                    icon: Icon(Icons.save, color: Theme.of(context).colorScheme.primary, size: 30),
                    onPressed: _saveStore,
                  ),
                ],
              )
            ],
          ),
        ),
      ),
    );
  }
}

/// Dialog for adding a market to the store.
class _MarketDialog extends StatefulWidget {
  final Market? initialMarket;

  const _MarketDialog({this.initialMarket});

  @override
  State<_MarketDialog> createState() => _MarketDialogState();
}

/// State class for _MarketDialog, which handles the form for adding a market.
class _MarketDialogState extends State<_MarketDialog> {
  final nameController = TextEditingController();
  final scheduleController = TextEditingController();
  List<String> images = [];
  LatLng? pickedPosition;

  @override
  void initState() {
    super.initState();
    final market = widget.initialMarket;
    if (market != null) {
      nameController.text = market.name;
      scheduleController.text = market.schedule;
      images = List<String>.from(market.images);
      pickedPosition = geoPointToLatLng(market.geoPoint);
    }
  }

  @override
  void dispose() {
    nameController.dispose();
    scheduleController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;
    return AlertDialog(
      title: Text(l10n.market_add, style: const TextStyle(fontFamily: "Manrope")),
      content: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          TextFormField(
              controller: nameController,
              decoration: InputDecoration(labelText: l10n.market_name),
              validator: (value) => value == null || value.isEmpty ? l10n.market_name_required : null,
          ),
          const SizedBox(height: 10),
          TextFormField(
              controller: scheduleController,
              decoration: InputDecoration(labelText: l10n.market_schedule),
              validator: (value) => value == null || value.isEmpty ? l10n.market_schedule_required : null,
          ),
          const SizedBox(height: 10),
          Text(l10n.market_images, style: const TextStyle(fontWeight: FontWeight.bold, fontFamily: "Manrope")),
          const SizedBox(height: 4),
          Wrap(
            spacing: 8,
            children: [
              ...images.map((b64) => Stack(
                children: [
                  Image.memory(base64Decode(b64), width: 80, height: 80, fit: BoxFit.cover),
                  Positioned(
                    right: 0,
                    top: 0,
                    child: IconButton(
                      icon: const Icon(Icons.close),
                      onPressed: () => setState(() => images.remove(b64)),
                    ),
                  )
                ],
              )),
              IconButton(
                icon: const Icon(Icons.add_a_photo),
                onPressed: () async {
                  final picker = ImagePicker();
                  final picked = await picker.pickImage(source: ImageSource.gallery);
                  if (picked != null) {
                    final bytes = await picked.readAsBytes();
                    setState(() => images.add(base64Encode(bytes)));
                  }
                },
              )
            ],
          ),
          const SizedBox(height: 10),
          Text(l10n.market_location, style: const TextStyle(fontWeight: FontWeight.bold, fontFamily: "Manrope")),
          const SizedBox(height: 4),
          SizedBox(
            height: 200,
            width: 200,
            child: GoogleMap(
              mapType: MapType.normal,
              initialCameraPosition: CameraPosition(
                target: pickedPosition ?? const LatLng(defaultLat, defaultLong),
                zoom: 13.0,
              ),
              onTap: (pos) => setState(() => pickedPosition = pos),
              markers: pickedPosition == null
                ? {}
                : {
                  Marker(
                    markerId: const MarkerId('market'),
                    position: pickedPosition!,
                    draggable: true,
                    onDragEnd: (pos) => setState(() => pickedPosition = pos),
                  ),
                },
            ),
          ),
          if (pickedPosition != null)
            Text("Lat: ${pickedPosition!.latitude}, Long: ${pickedPosition!.longitude}", style: const TextStyle(fontFamily: "Manrope")),
        ],
      ),
      actions: [
        TextButton(
          style: TextButton.styleFrom(
            foregroundColor: Theme.of(context).colorScheme.onSurface,
          ),
          onPressed: () => Navigator.pop(context), child: Text(l10n.cancel, style: TextStyle(fontFamily: "Manrope", color: Theme.of(context).colorScheme.primary))
        ),
        ElevatedButton(
          style: ElevatedButton.styleFrom(
            backgroundColor: Theme.of(context).colorScheme.primaryContainer,
            foregroundColor: Theme.of(context).colorScheme.onPrimaryContainer,
          ),
          onPressed: () {
            Navigator.pop(context, Market(
              name: nameController.text.trim(),
              schedule: scheduleController.text.trim(),
              images: images,
              geoPoint: latLngToGeoPoint(pickedPosition),
            ));
          },
          child: Text(l10n.continue_on, style: TextStyle(fontFamily: "Manrope", color: Theme.of(context).colorScheme.onPrimaryContainer))
        )
      ],
    );
  }
}

/// CartManager is a singleton class that manages the shopping cart for the application.
class CartManager {
  static final CartManager _instance = CartManager._internal();
  factory CartManager() => _instance;
  CartManager._internal();

  final Cart cart = Cart();
}

/// StoreProductsTab displays a list of products available in the store, with options to add, edit, or highlight products.
class StoreProductsTab extends StatefulWidget {
  const StoreProductsTab({super.key, this.storeId});
  final String? storeId;

  @override
  State<StoreProductsTab> createState() => _StoreProductsTabState();
}

/// State class for StoreProductsTab, which handles the product list and filtering.
class _StoreProductsTabState extends State<StoreProductsTab> {
  String _storeName = '';
  List<Product> _allProducts = [];
  List<Product> _filteredProducts = [];
  String _searchQuery = '';
  ProductCategory? _selectedCategory;
  bool _showContent = false;
  bool _isFarmer = false;
  final TextEditingController _searchController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _initializeData();
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  Future<void> _initializeData() async {
    final isFarmerUser = await isFarmer();
    _isFarmer = isFarmerUser;
    late Map<String, dynamic>? data;

    if (isFarmerUser) {
      data = await loadStoreInfo();
    } else {
      final storeId = widget.storeId;
      if (storeId != null) {
        data = await loadStoreInfo(docId: storeId);
      } else {
        data = null;
      }
    }

    if (data != null && mounted) {
      setState(() {
        _storeName = data!['storeInfo'].name;
        _allProducts = List<Product>.from(data['storeInfo'].products);
        _applyFilter();
      });
    }

    await Future.delayed(const Duration(seconds: 2));
    if (mounted) setState(() => _showContent = true);
  }

  void _applyFilter() {
    if (mounted) {
      setState(() {
        _filteredProducts = _allProducts.where((product) {
          final matchesName = _searchQuery.isEmpty || product.name.toLowerCase().contains(_searchQuery.toLowerCase());
          final matchesCategory = _selectedCategory == null || product.category == _selectedCategory;
          return matchesName && matchesCategory;
        }).toList();
      });
    }
  }

  void _showProductDetailsDialog(Product product) {
    final l10n = AppLocalizations.of(context)!;
    final decodedImages = product.images.map(base64Decode).toList();

    showDialog(
      context: context,
      builder: (context) {
        return Dialog(
          insetPadding: const EdgeInsets.all(16),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: SingleChildScrollView(
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  if (decodedImages.isNotEmpty)
                    SizedBox(
                      height: 200,
                      child: PageView.builder(
                        itemCount: decodedImages.length,
                        itemBuilder: (context, i) {
                          return ClipRRect(
                            borderRadius: BorderRadius.circular(8),
                            child: Image.memory(decodedImages[i], fit: BoxFit.cover),
                          );
                        },
                      ),
                    ),
                  const SizedBox(height: 12),
                  Text(product.name,
                      style: const TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.bold,
                          fontFamily: "Manrope"
                      )),
                  const SizedBox(height: 8),
                  Text(product.description,
                      style: const TextStyle(fontSize: 14, fontFamily: "Manrope")),
                  const SizedBox(height: 12),
                  Wrap(
                    spacing: 8,
                    children: product.deliveryTypes.map((type) {
                      return Chip(label: Text(type.localized(l10n)));
                    }).toList(),
                  ),
                  const SizedBox(height: 12),
                  Text("${l10n.category}: ${product.category.localized(l10n)}"),
                  Text("${l10n.product_price}: ${product.price.toStringAsFixed(2)} €/ ${product.quantityUnit}"),
                  Text(product.stock > product.minStock ? l10n.inStock : l10n.outOfStock),
                  Text("${l10n.min_quantity}: ${product.minQuantity} ${product.quantityUnit}"),
                  const SizedBox(height: 16),
                  Align(
                    alignment: Alignment.centerRight,
                    child: TextButton(
                      onPressed: () => Navigator.pop(context),
                      child: Text(l10n.close),
                    ),
                  )
                ],
              ),
            ),
          ),
        );
      },
    );
  }

  Future<void> _updateProductVisibility(Product product) async {
    final l10n = AppLocalizations.of(context)!;
    final uid = FirebaseAuth.instance.currentUser?.uid;
    if (uid == null) return;

    final query = await FirebaseFirestore.instance
        .collection('stores')
        .where('userId', isEqualTo: uid)
        .limit(1)
        .get();

    if (query.docs.isEmpty) return;

    final docId = query.docs.first.id;
    final storeData = StoreInfo.fromMap(query.docs.first.data(), docId);

    final index = storeData.products.indexWhere((p) => p.name == product.name);
    if (index != -1) {
      storeData.products[index].highlighted = product.highlighted;
      await FirebaseFirestore.instance
          .collection('stores')
          .doc(docId)
          .set(storeData.toMap());
      if (mounted) showSnackBarConfirm(context, l10n.product_visibility_updated);
    }
  }

  @override
  Widget build(BuildContext context) {
    final categories = _allProducts.map((p) => p.category).toSet().toList();
    final l10n = AppLocalizations.of(context)!;

    if (!_showContent) {
      return const Center(child: CircularProgressIndicator());
    }

    if (_allProducts.isEmpty) {
      return Padding(
        padding: const EdgeInsets.all(20),
        child: Center(
            child: Text(l10n.no_products, style: const TextStyle(fontSize: 16, fontFamily: "Manrope"))
        ),
      );
    }

    return Padding(
      padding: const EdgeInsets.all(12),
        child: Column(
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(_storeName, style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold, fontFamily: "Manrope")),
                if (_isFarmer) IconButton(
                  color: Theme.of(context).primaryColor,
                  style: IconButton.styleFrom(
                    shape: const CircleBorder(),
                    padding: const EdgeInsets.all(4),
                    backgroundColor: Theme.of(context).colorScheme.primaryContainer,
                  ),
                  icon: const Icon(Icons.add, size: 30),
                  onPressed: () {
                    Navigator.pushNamed(context, '/edit-product', arguments: {'product': Product.fromMap({})})
                      .then((res) {
                        // Reload products after adding
                        if (res == true) _initializeData();
                      });
                  },
                ),
                if (!_isFarmer) IconButton(
                  color: Theme.of(context).primaryColor,
                  style: IconButton.styleFrom(
                    shape: const CircleBorder(),
                    padding: const EdgeInsets.all(4),
                    backgroundColor: Theme.of(context).colorScheme.primaryContainer,
                  ),
                  icon: const Icon(Icons.shopping_cart_checkout, size: 30),
                  onPressed: () => Navigator.pushNamed(context, '/cart'),
                ),
              ],
            ),
            Row(
              children: [
                DropdownButton<ProductCategory>(
                  value: _selectedCategory,
                  hint: Text(l10n.category, style: const TextStyle(fontFamily: "Manrope")),
                  items: categories.map((cat) {
                    return DropdownMenuItem(
                      value: cat,
                      child: Text(cat.localized(l10n), style: const TextStyle(fontFamily: "Manrope"))
                    );
                  }).toList(),
                  onChanged: (value) {
                    _selectedCategory = value; // if null, show all
                    _applyFilter();
                  },
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: TextField(
                    controller: _searchController,
                    decoration: InputDecoration(
                      isDense: true,
                      labelText: l10n.search_by_name,
                      border: const OutlineInputBorder(
                        borderRadius: BorderRadius.all(Radius.circular(8))
                      ),
                    ),
                    onChanged: (value) {
                      _searchQuery = value;
                      _applyFilter();
                    },
                  ),
                ),
                IconButton(
                  icon: const Icon(Icons.clear),
                  onPressed: () {
                    _searchController.clear();
                    _searchQuery = '';
                    _selectedCategory = null;
                    _applyFilter();
                  },
                )
              ],
            ),
            Expanded(
              child: _filteredProducts.isEmpty
                ? Center(child: Text(l10n.no_products, style: const TextStyle(fontSize: 16, fontFamily: "Manrope")))
                : ListView.builder(
                  itemCount: _filteredProducts.length,
                  itemBuilder: (context, index) {
                    final product = _filteredProducts[index];
                    final decodedImages = product.images.map(base64Decode).toList();

                    return ListTile(
                      onTap: () => _showProductDetailsDialog(product),
                      contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                      leading: decodedImages.isEmpty
                        ? const Icon(Icons.image_not_supported, size: 48)
                        : SizedBox(
                          width: 80,
                          height: 80,
                          child: PageView.builder(
                            itemCount: decodedImages.length,
                            itemBuilder: (context, i) {
                              return Image.memory(decodedImages[i], fit: BoxFit.cover);
                            },
                            scrollDirection: Axis.horizontal,
                            pageSnapping: true,
                            allowImplicitScrolling: true,
                          ),
                        ),
                      title: Text(product.name, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold, fontFamily: "Manrope")),
                      subtitle: Text("${product.category.localized(l10n)} • ${product.price.toStringAsFixed(2)}€/ ${product.quantityUnit}", style: const TextStyle(fontSize: 14, fontFamily: "Manrope")),
                      trailing: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          if (!_isFarmer) IconButton(
                            icon: const Icon(Icons.add_shopping_cart),
                            onPressed: () {
                              showDialog<double>(
                                context: context,
                                builder: (context) {
                                  double quantity = product.minQuantity;
                                  return AlertDialog(
                                    title: Text(l10n.select_quantity),
                                    content: Column(
                                      mainAxisSize: MainAxisSize.min,
                                      children: [
                                        Text("${l10n.min_quantity}: ${product.minQuantity} ${product.quantityUnit}"),
                                        TextFormField(
                                          initialValue: product.minQuantity.toString(),
                                          decoration: InputDecoration(labelText: l10n.quantity),
                                          keyboardType: TextInputType.number,
                                          onChanged: (val) => quantity = double.tryParse(val) ?? 0.0,
                                          validator: (val) {
                                            if (val == null || val.isEmpty) { return l10n.quantity_required; }
                                            final parsed = double.tryParse(val);
                                            if (parsed == null || parsed < product.minQuantity) { return l10n.quantity_invalid; }
                                            if (parsed > product.stock) { return l10n.quantity_exceeds_stock; }
                                            return null;
                                          },
                                        ),
                                      ],
                                    ),
                                    actions: [
                                      TextButton(
                                        onPressed: () => Navigator.pop(context),
                                        child: Text(l10n.cancel),
                                      ),
                                      ElevatedButton(
                                        onPressed: () => Navigator.pop(context, quantity),
                                        child: Text(l10n.add_to_cart),
                                      ),
                                    ],
                                  );
                                },
                              ).then((selectedQuantity) {
                                if (selectedQuantity != null) {
                                  CartManager().cart.add(product, selectedQuantity);
                                  if (context.mounted) {
                                    ScaffoldMessenger.of(context).showSnackBar(
                                      SnackBar(content: Text(l10n.added_to_cart)),
                                    );
                                  }
                                }
                              });
                            },
                          ),
                          if (_isFarmer) IconButton(
                            icon: const Icon(Icons.edit),
                            onPressed: () {
                              Navigator.pushNamed(context, '/edit-product', arguments: {'product': product}).then((res) {
                                if (res == true) {
                                  // Reload products after editing
                                  _initializeData();
                                }
                              });
                            },
                          ),
                          if (_isFarmer) IconButton(
                            icon: Icon(product.highlighted ? Icons.visibility : Icons.visibility_off),
                            onPressed: () async {
                              setState(() {
                                product.highlighted = !product.highlighted;
                              });
                              await _updateProductVisibility(product);
                            },
                          ),
                        ],
                      ),
                    );
                  },
                ),
            ),
          ],
        )
    );
  }
}

class CartPage extends StatefulWidget {
  const CartPage({super.key});

  @override
  State<CartPage> createState() => _CartPageState();
}

class _CartPageState extends State<CartPage> {

  Future<void> createOrder(BuildContext context) async {
    final uid = FirebaseAuth.instance.currentUser?.uid;
    if (uid == null) return;

    final order = StoreOrder(
      userId: uid,
      items: List<CartItem>.from(CartManager().cart.items),
      deliveryType: DeliveryType.pickup, // Default to pickup
      createdAt: DateTime.now(),
    );

    await FirebaseFirestore.instance.collection('orders').add(order.toMap());
    CartManager().cart.clear();

    if (context.mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Order placed!')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    final cart = CartManager().cart;
    final l10n = AppLocalizations.of(context)!;

    return Scaffold(
      appBar: AppBar(title: Text(l10n.cart)),
      body: cart.items.isEmpty
        ? Center(child: Text(l10n.cart_empty))
        : Column(
            children: [
              Expanded(
                child: ListView.builder(
                  itemCount: cart.items.length,
                  itemBuilder: (context, index) {
                    final item = cart.items[index];
                    return ListTile(
                      title: Text(item.product.name),
                      subtitle: Text('${item.quantity} × ${item.product.price.toStringAsFixed(2)}€'),
                      trailing: IconButton(
                        icon: Icon(Icons.delete),
                        onPressed: () {
                          cart.remove(item.product);
                          setState(() {}); // Refresh the UI
                        },
                      ),
                    );
                  },
                ),
              ),
              Padding(
                padding: const EdgeInsets.all(16),
                child: ElevatedButton(
                  onPressed: () async {
                    await createOrder(context);
                  },
                  child: Text(l10n.checkout),
                ),
              ),
            ],
          ),
    );
  }
}

/// Screen for creating a new product.
class EditProductPage extends StatefulWidget {
  const EditProductPage({super.key});

  @override
  State<EditProductPage> createState() => _EditProductPageState();
}

/// State class for CreateProductPage, which handles the form for creating a new product.
class _EditProductPageState extends State<EditProductPage> {
  final _formKey = GlobalKey<FormState>();
  late Product _product;
  bool _isUploading = false;
  bool _isEditing = false;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    final args = ModalRoute.of(context)?.settings.arguments as Map?;

    if (args != null) {
      final Product initialData = args['product'];

      setState(() {
        _product = initialData;
        _isEditing = initialData.name.isNotEmpty;
      });
    }
  }

  // on submit
  Future<void> _saveProduct() async {
    final l10n = AppLocalizations.of(context)!;
    if (!_formKey.currentState!.validate()) return;
    _formKey.currentState!.save();

    setState(() => _isUploading = true);

    final uid = FirebaseAuth.instance.currentUser?.uid;
    if (uid == null) return;

    final query = await FirebaseFirestore.instance
        .collection('stores')
        .where('userId', isEqualTo: uid)
        .limit(1)
        .get();

    final docId = query.docs.isEmpty ? null : query.docs.first.id;
    final storeData = StoreInfo.fromMap(query.docs.first.data(), docId);

    final index = storeData.products.indexWhere((p) => p.name == _product.name);
    if (index != -1) {
      // Update existing product
      storeData.products[index] = _product;
    } else {
      // Add new product
      storeData.products.add(_product);
    }

    await FirebaseFirestore.instance
        .collection('stores')
        .doc(docId)
        .set(storeData.toMap()); // Create or update

    if (mounted) showSnackBarConfirm(context, l10n.store_update_success);
    if (mounted) Navigator.pop(context, true); // Return true to indicate success and refresh the product list
  }

  Future<void> _deleteProduct() async {
    final l10n = AppLocalizations.of(context)!;
    final uid = FirebaseAuth.instance.currentUser?.uid;
    if (uid == null) return;

    final query = await FirebaseFirestore.instance
        .collection('stores')
        .where('userId', isEqualTo: uid)
        .limit(1)
        .get();

    if (query.docs.isEmpty) return;

    final docId = query.docs.first.id;
    final storeData = StoreInfo.fromMap(query.docs.first.data(), docId);

    storeData.products.removeWhere((p) => p.name == _product.name);

    await FirebaseFirestore.instance
        .collection('stores')
        .doc(docId)
        .set(storeData.toMap());

    if (mounted) showSnackBarConfirm(context, l10n.product_deleted);
    if (mounted) Navigator.pop(context);
    if (mounted) Navigator.pop(context, true); // Return true to indicate deletion
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;
    return Scaffold(
      appBar: AppBar(
        title: Text(l10n.edit_product, style: const TextStyle(fontFamily: "Manrope")),
        actions: [
          IconButton(
            icon: Icon(Icons.save, color: Theme.of(context).colorScheme.primary, size: 30),
            onPressed: _saveProduct,
          ),
        ]
      ),
      body: _isUploading
          ? const Center(child: CircularProgressIndicator())
          : SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Form(
          key: _formKey,
          child: Column(
            children: [
              TextFormField(
                initialValue: _product.name,
                decoration: InputDecoration(labelText: l10n.product_name),
                onChanged: (val) => _product.name = val,
                validator: (val) => val == null || val.isEmpty ? l10n.product_name_required : null,
              ),
              DropdownSearch<ProductCategory>(
                // TODO: allow user to create new ones in profile and load them here
                items: (_, _) => Future.value(ProductCategory.values),
                selectedItem: _product.category,
                compareFn: (item1, item2) => item1 == item2,
                itemAsString: (item) => item.localized(l10n),
                decoratorProps: DropDownDecoratorProps(
                  decoration: InputDecoration(
                    labelText: l10n.category,
                  ),
                ),
                popupProps: PopupProps.menu(
                  showSearchBox: true,
                  searchFieldProps: TextFieldProps(
                    decoration: InputDecoration(
                      hintText: l10n.category,
                      border: const OutlineInputBorder(),
                      contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                    ),
                  ),
                  fit: FlexFit.loose,
                ),
                onChanged: (val) => _product.category = val ?? ProductCategory.other,
                validator: (val) => val == null ? l10n.product_category_required : null,
              ),
              TextFormField(
                initialValue: _product.description,
                decoration: InputDecoration(labelText: l10n.product_description),
                maxLines: 3,
                onChanged: (val) => _product.description = val,
                validator: (val) => val == null || val.isEmpty ? l10n.product_description_required : null,
              ),
              TextFormField(
                initialValue: _product.price.toString(),
                decoration: InputDecoration(labelText: "${l10n.product_price} (€)"),
                keyboardType: TextInputType.number,
                onChanged: (val) => _product.price = double.tryParse(val) ?? 0.0,
                validator: (val) => val == null || val.isEmpty ? l10n.product_price_required : null,
              ),
              DropdownButtonFormField<String>(
                value: _product.quantityUnit.isEmpty ? null : _product.quantityUnit,
                decoration: InputDecoration(labelText: l10n.product_unit),
                items: ['unit', 'kg'].map((unit) {
                  return DropdownMenuItem(value: unit, child: Text(unit, style: const TextStyle(fontFamily: "Manrope")));
                }).toList(),
                onChanged: (val) => _product.quantityUnit = val ?? 'unit',
                validator: (val) => val == null || val.isEmpty ? l10n.product_unit_required : null,
              ),
              TextFormField(
                initialValue: _product.stock.toString(),
                decoration: InputDecoration(labelText: "Stock"),
                keyboardType: TextInputType.number,
                onChanged: (val) => _product.stock = double.tryParse(val) ?? 0.0,
                validator: (val) => val == null || val.isEmpty ? l10n.product_stock_required : null,
              ),
              TextFormField(
                initialValue: _product.minStock.toString(),
                decoration: InputDecoration(labelText: l10n.min_stock),
                keyboardType: TextInputType.number,
                onChanged: (val) => _product.minStock = double.tryParse(val) ?? 0.0,
                validator: (val) => val == null || val.isEmpty ? l10n.min_stock_required : null,
              ),
              TextFormField(
                initialValue: _product.minQuantity.toString(),
                decoration: InputDecoration(labelText: l10n.min_quantity),
                keyboardType: TextInputType.number,
                onChanged: (val) => _product.minQuantity = double.tryParse(val) ?? 0.0,
                validator: (val) => val == null || val.isEmpty ? l10n.min_quantity_required : null,
              ),
              const SizedBox(height: 10),
              Wrap(
                spacing: 10,
                children: DeliveryType.values.map((type) {
                  return Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Checkbox(
                        value: _product.deliveryTypes.contains(type),
                        onChanged: (selected) {
                          setState(() {
                            if (selected == true) {
                              _product.deliveryTypes.add(type);
                            } else {
                              _product.deliveryTypes.remove(type);
                            }
                          });
                        },
                      ),
                      Text(type.localized(l10n), style: const TextStyle(fontFamily: "Manrope")),
                    ],
                  );
                }).toList(),
              ),
              const SizedBox(height: 10),
              Text(l10n.product_images, style: const TextStyle(fontWeight: FontWeight.bold, fontFamily: "Manrope")),
              Wrap(
                spacing: 8,
                children: [
                  ..._product.images.map((b64) => Stack(
                    children: [
                      Image.memory(base64Decode(b64), width: 100, height: 100, fit: BoxFit.cover),
                      Positioned(
                        right: 0,
                        top: 0,
                        child: IconButton(
                          icon: const Icon(Icons.close),
                          onPressed: () => setState(() => _product.images.remove(b64)),
                        ),
                      )
                    ],
                  )),
                  IconButton(
                    icon: const Icon(Icons.add_a_photo),
                    onPressed: () async {
                      final picker = ImagePicker();
                      final picked = await picker.pickImage(source: ImageSource.gallery);
                      if (picked != null) {
                        final bytes = await picked.readAsBytes();
                        setState(() => _product.images.add(base64Encode(bytes)));
                      }
                    },
                  )
                ],
              ),
              const SizedBox(height: 24),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(l10n.save, style: const TextStyle(fontWeight: FontWeight.bold, fontFamily: "Manrope")),
                  const SizedBox(width: 10),
                  IconButton(
                    icon: Icon(Icons.save, color: Theme.of(context).colorScheme.primary, size: 30),
                    onPressed: _saveProduct,
                  ),
                  if (_isEditing) IconButton(
                    icon: const Icon(Icons.delete, color: Colors.red),
                    onPressed: () {
                      showDialog(
                        context: context,
                        builder: (context) {
                          return AlertDialog(
                            title: Text(l10n.delete_product),
                            content: Text(l10n.delete_product_confirmation),
                            actions: [
                              TextButton(
                                onPressed: () => Navigator.pop(context),
                                child: Text(l10n.cancel, style: TextStyle(color: Theme.of(context).colorScheme.primary)),
                              ),
                              ElevatedButton(
                                style: ElevatedButton.styleFrom(
                                  backgroundColor: Theme.of(context).colorScheme.primaryContainer,
                                  foregroundColor: Theme.of(context).colorScheme.onPrimaryContainer,
                                ),
                                onPressed: () async {
                                  await _deleteProduct();// Return true to indicate deletion
                                },
                                child: Text(l10n.delete, style: TextStyle(color: Theme.of(context).colorScheme.onPrimaryContainer)),
                              )
                            ],
                          );
                        },
                      );
                    },
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}

/// Tab for displaying store reviews.
class StoreReviewsTab extends StatefulWidget {
  const StoreReviewsTab({super.key, this.storeId});
  final String? storeId; // Optional store ID for non-farmer users

  @override
  State<StoreReviewsTab> createState() => _StoreReviewsTabState();
}

class _StoreReviewsTabState extends State<StoreReviewsTab> {
  String _storeName = '';
  List<Review> _reviews = [];
  bool _showContent = false;
  bool _isFarmer = false;
  final TextEditingController _commentController = TextEditingController();
  bool _submitting = false;
  static const double startRating = 3.0;
  Review? _currentReview;

  @override
  void initState() {
    super.initState();
    _initializeData();
  }

  @override
  void dispose() {
    _commentController.dispose();
    super.dispose();
  }

  Future<void> _initializeData() async {
    final isFarmerUser = await isFarmer();
    _isFarmer = isFarmerUser;
    late Map<String, dynamic>? data;

    if (isFarmerUser) {
      data = await loadStoreInfo(loadReviews: true);
    } else {
      final storeId = widget.storeId;
      if (storeId != null) {
        data = await loadStoreInfo(loadReviews: true, docId: storeId);
      } else {
        data = null;
      }
    }

    if (data != null && mounted) {
      setState(() {
        _storeName = data!['storeInfo'].name;
        _reviews = List<Review>.from(data['reviews']);
        _currentReview = _reviews.firstWhere(
          (r) => r.userId == FirebaseAuth.instance.currentUser?.uid,
          orElse: () => Review.fromMap({}, null),
        );
        if (_currentReview?.id != null) _commentController.text = _currentReview!.comment;
      });
    }

    await Future.delayed(const Duration(seconds: 2));
    if (mounted) setState(() => _showContent = true);
  }

  Future<void> _submitReview() async {
    final l10n = AppLocalizations.of(context)!;
    if (_currentReview == null || _commentController.text.trim().isEmpty) return;

    final user = FirebaseAuth.instance.currentUser;
    if (user == null) return;

    setState(() => _submitting = true);

    _currentReview!.userId = user.uid;
    _currentReview!.userName = user.displayName ?? 'Anonymous';
    _currentReview!.comment = _commentController.text.trim();

    String docId = await FirebaseFirestore.instance
      .collection('stores')
      .doc(widget.storeId)
      .collection('reviews')
      .add(_currentReview!.toMap()).then((doc) => doc.id);

    _currentReview!.id = docId; // Set the ID after adding to Firestore
    await FirebaseFirestore.instance
      .collection('stores')
      .doc(widget.storeId)
      .collection('reviews')
      .doc(docId).update({'id': docId});
    _reviews.add(_currentReview!);
    setState(() {
      _commentController.clear();
      _submitting = false;
    });

    if (mounted) showSnackBarMessage(context, l10n.review_submitted);
  }

  @override
  Widget build(BuildContext context) {
    final l10n = AppLocalizations.of(context)!;
    if (!_showContent) {
      return const Center(child: CircularProgressIndicator());
    }
    if (_reviews.isEmpty) {
      return Padding(
        padding: const EdgeInsets.all(20),
        child: Center(
          child: Text(l10n.no_reviews, style: const TextStyle(fontSize: 16, fontFamily: "Manrope"))
        ),
      );
    }

    return Column(
      children: [
        Text(_storeName, style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold, fontFamily: "Manrope")),
        if (!_isFarmer) Padding(
          padding: const EdgeInsets.all(12),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(l10n.leave_review, style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold, fontFamily: "Manrope")),
              const SizedBox(height: 8),
              RatingBar.builder(
                initialRating: _currentReview?.rating ?? startRating,
                minRating: 1,
                allowHalfRating: true,
                itemCount: 5,
                itemBuilder: (context, _) => const Icon(Icons.star, color: Colors.amber),
                onRatingUpdate: (rating) => setState(() => _currentReview?.rating = rating),
              ),
              const SizedBox(height: 8),
              TextField(
                controller: _commentController,
                decoration: InputDecoration(
                  labelText: l10n.comment,
                  border: const OutlineInputBorder(),
                ),
                maxLines: 3,
              ),
              const SizedBox(height: 8),
              ElevatedButton(
                onPressed: _submitting ? null : _submitReview,
                child: _submitting
                    ? const SizedBox(height: 16, width: 16, child: CircularProgressIndicator(strokeWidth: 2))
                    : Text(l10n.submit_review),
              ),
              const Divider(),
            ],
          ),
        ),
        Expanded(
          child: _reviews.isEmpty
              ? Padding(
            padding: const EdgeInsets.all(20),
            child: Center(
              child: Text(
                l10n.no_reviews,
                style: const TextStyle(fontSize: 16, fontFamily: "Manrope"),
              ),
            ),
          )
              : ListView.separated(
            padding: const EdgeInsets.all(12),
            itemCount: _reviews.length,
            separatorBuilder: (_, _) => const Divider(),
            itemBuilder: (context, index) {
              final review = _reviews[index];
              return ListTile(
                title: Text(review.userName),
                subtitle: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    RatingBarIndicator(
                      rating: review.rating,
                      itemBuilder: (context, _) => const Icon(Icons.star, color: Colors.amber),
                      itemCount: 5,
                      itemSize: 20,
                    ),
                    const SizedBox(height: 4),
                    Text(review.comment),
                  ],
                ),
                trailing: Text(
                  review.timestamp.toLocal().toString().split('.').first,
                  style: const TextStyle(fontSize: 12),
                ),
              );
            },
          ),
        ),
      ],
    );
  }
}