/// This file contains the main home page of the app, including navigation and tabs for different sections.
/// File: pages/home.dart
/// Author: José Oliveira 202300558
/// Version: 1.0.0
/// 2025-07-23
library;

import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:hello_farmer/pages/common.dart';
import '../l10n/app_localizations.dart';
// Parse HTML and launch URLs
import 'package:http/http.dart' as http;
import 'package:html/parser.dart' show parse;
import 'package:url_launcher/url_launcher.dart';

/// The main home page of the app, which includes a bottom navigation bar and different pages for Home, Orders, Store, and Settings.
/// The top app bar includes the app icon, title, and action buttons for notifications and profile navigation.
class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

/// State class for HomePage, managing the current index of the bottom navigation bar and the pages displayed.
class _HomePageState extends State<HomePage> {
  int _currentIndex = 0;
  List<NavItem>? _navItems;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    // Only initialize once, if null assign the nav items
    _navItems ??= getNavItems(context);
  }

  @override
  Widget build(BuildContext context) {
    final currentItem = _navItems![_currentIndex];

    return Scaffold(
      appBar: CustomAppBar(
        title: currentItem.title,
      ),
      body: currentItem.page,
      bottomNavigationBar: buildBottomNavigationBar(
          _currentIndex,
          _navItems!,
          (index) => setState(() => _currentIndex = index),
      ),
    );
  }
}

/// HomeScreen widget displays recommended products and news related to agriculture.
class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

/// State class for HomeScreen, managing the recommended products and news fetching.
class _HomeScreenState extends State<HomeScreen> {
  List<ProductMock> recommended = [];
  List<NewsItem> news = [];
  bool loading = true;
  late Future<bool> _isFarmerFuture;

  @override
  void initState() {
    super.initState();
    _isFarmerFuture = isFarmer();
    _loadContent();
  }

  Future<void> _loadContent() async {
    await Future.wait([_loadRecommended(), _loadNews()]);
    if (mounted) setState(() => loading = false);
  }

  Future<void> _loadRecommended() async {
    try {
      if (!await _isFarmerFuture) {
        // If not a farmer, return early
        final store = await loadStoresInfo().then((stores) => stores[0]);
        recommended = List.generate(5, (index) {
          return ProductMock(
            title: store.products[index].name,
            imageUrl: store.products[index].images[0],
            link: '',
          );
        });
        return;
      }
      final res = await http.get(Uri.parse('https://www.agriloja.pt/pt/agricultura_206.html'));
      final doc = parse(res.body);
      final items = doc.querySelectorAll('.wrapper-product-item');
      recommended = items.take(5).map((e) {
        final img = e.querySelector('img')?.attributes['src'] ?? '';
        final title = e.querySelector('.name')?.text.trim() ?? '';
        final link = e.querySelector('a')?.attributes['href'] ?? '';
        return ProductMock(title: title, imageUrl: img, link: link);
      }).toList();
    } catch (_) {}
  }

  Future<void> _loadNews() async {
    try {
      final res = await http.get(Uri.parse('https://apnews.com/hub/agriculture'));
      final doc = parse(res.body);
      final extractNews = doc.querySelectorAll('.PageList-items-item');
      news = extractNews.take(5).map((e) {
        final a = e.querySelector('.PagePromo-title a');
        final link = a?.attributes['href'] ?? '';
        final title = a?.text.trim() ?? '';
        final img = e.querySelector('img')?.attributes['src'] ?? '';
        return NewsItem(title: title, imageUrl: img, link: link);
      }).toList();
    } catch (_) {}
  }

  @override
  Widget build(BuildContext context) {
    AppLocalizations l10n = AppLocalizations.of(context)!;
    if (loading) return const Scaffold(body: Center(child: CircularProgressIndicator()));

    return Scaffold(
      body: ListView(padding: const EdgeInsets.all(16), children: [
        Text(l10n.products_recommended, style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
        const SizedBox(height: 12),
        SizedBox(
          height: 200,
          child: PageView.builder(
            controller: PageController(viewportFraction: 0.8),
            itemCount: recommended.length,
            itemBuilder: (_, i) => ProductCard(product: recommended[i]),
          ),
        ),
        const SizedBox(height: 32),
        Text(l10n.news, style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
        const SizedBox(height: 12),
        SizedBox(
          height: 220,
          child: PageView.builder(
            controller: PageController(viewportFraction: 0.8),
            itemCount: news.length,
            itemBuilder: (_, i) => NewsCard(item: news[i]),
          ),
        ),
      ]),
    );
  }
}

/// Model class for ProductMock, representing a product with a title, image URL, and link.
class ProductMock {
  final String title, imageUrl, link;
  ProductMock({required this.title, required this.imageUrl, required this.link});
}

/// Model class for NewsItem, representing a news item with a title, image URL, and link.
class NewsItem {
  final String title, imageUrl, link;
  NewsItem({required this.title, required this.imageUrl, required this.link});
}

/// Widget for displaying a product card with an image, title, and link to the product page.
class ProductCard extends StatelessWidget {
  final ProductMock product;
  const ProductCard({super.key, required this.product});

  @override
  Widget build(BuildContext c) => GestureDetector(
    onTap: () => product.link.isNotEmpty ? launchUrl(Uri.parse(product.link)) : null,
    child: Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Column(
        children: [
          Expanded(child: ClipRRect(
            borderRadius: const BorderRadius.vertical(top: Radius.circular(16)),
            child:
            product.imageUrl.startsWith('http')  ? Image.network(product.imageUrl, fit: BoxFit.cover, width: double.infinity)
            : Image.memory(base64Decode(product.imageUrl), fit: BoxFit.cover, width: double.infinity),
          )),
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Text(product.title == '' ? AppLocalizations.of(c)!.product : product.title, style: const TextStyle(fontWeight: FontWeight.w600)),
          ),
        ],
      ),
    ),
  );
}

/// Widget for displaying a news card with an image, title, and link to the news article.
class NewsCard extends StatelessWidget {
  final NewsItem item;
  const NewsCard({super.key, required this.item});

  @override
  Widget build(BuildContext c) => GestureDetector(
    onTap: () => launchUrl(Uri.parse(item.link)),
    child: Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Expanded(child: ClipRRect(
            borderRadius: const BorderRadius.vertical(top: Radius.circular(16)),
            child: Image.network(item.imageUrl, fit: BoxFit.cover, width: double.infinity),
          )),
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Text(item.title == '' ? AppLocalizations.of(c)!.news_single : item.title, maxLines: 2, overflow: TextOverflow.ellipsis, style: const TextStyle(fontWeight: FontWeight.w600)),
          ),
        ],
      ),
    ),
  );
}