import 'package:flutter/material.dart';

class SettingsPage extends StatelessWidget {
  const SettingsPage({super.key});
  @override
  Widget build(BuildContext context) {
    return ListView(
      padding: EdgeInsets.all(16),
      children: [
        ListTile(title: Text("App Settings"), onTap: () {}),
        ListTile(title: Text("Store Settings"), onTap: () {}),
        ListTile(title: Text("Manage Client List"), onTap: () {}),
      ],
    );
  }
}

class ProfilePage extends StatelessWidget {
  const ProfilePage({super.key});
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text("Profile")),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text("Username: Farmer123", style: TextStyle(fontSize: 20)),
            SizedBox(height: 10),
            Text("Points: 1200", style: TextStyle(fontSize: 18)),
            SizedBox(height: 10),
            Text("Badges:", style: TextStyle(fontSize: 18)),
            Wrap(
              spacing: 8,
              children: [
                Chip(label: Text("Top Seller")),
                Chip(label: Text("Fast Delivery")),
                Chip(label: Text("Eco Friendly")),
              ],
            )
          ],
        ),
      ),
    );
  }
}