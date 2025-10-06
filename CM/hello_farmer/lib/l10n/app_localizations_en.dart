// ignore: unused_import
import 'package:intl/intl.dart' as intl;
import 'app_localizations.dart';

// ignore_for_file: type=lint

/// The translations for English (`en`).
class AppLocalizationsEn extends AppLocalizations {
  AppLocalizationsEn([String locale = 'en']) : super(locale);

  @override
  String get welcome => 'Welcome! ';

  @override
  String get welcome_message =>
      'Discover fresh products directly from the producer.';

  @override
  String get register => 'Create Account';

  @override
  String get login_success => 'Login successful';

  @override
  String get login_error =>
      'Login failed. Please check your email and password.';

  @override
  String get login_remember => 'Remember me';

  @override
  String get login_google => 'Login with Google';

  @override
  String get login_facebook => 'Login with Facebook';

  @override
  String get login_apple => 'Login with Apple';

  @override
  String get login_google_success => 'Login with Google successful.';

  @override
  String get login_facebook_success => 'Login with Facebook successful.';

  @override
  String get login_apple_success => 'Login with Apple successful.';

  @override
  String get login_google_error =>
      'Error logging in with Google. Please try again.';

  @override
  String get login_facebook_error =>
      'Error logging in with Facebook. Please try again.';

  @override
  String get login_apple_error =>
      'Error logging in with Apple. Please try again.';

  @override
  String get password_reset_message =>
      'Enter your email to receive a password reset link.';

  @override
  String get password_reset_send => 'Send reset link';

  @override
  String get password_reset_success => 'Reset link sent successfully.';

  @override
  String get password_reset_error =>
      'Error sending reset link. Please try again.';

  @override
  String get register_success => 'Registration successful.';

  @override
  String get register_error => 'Registration failed. Please try again.';

  @override
  String get name => 'Name';

  @override
  String get name_required => 'Please enter your name.';

  @override
  String get email_required => 'Please enter your email.';

  @override
  String get email_invalid => 'Invalid email address.';

  @override
  String get email_already_in_use => 'This email is already registered.';

  @override
  String get email_not_verified =>
      'Please verify your email before continuing.';

  @override
  String get password_required => 'Please enter your password.';

  @override
  String get password_invalid =>
      'Password must be at least 12 characters long, with at least one uppercase letter, one lowercase letter, one number, and one special character.';

  @override
  String get password_repeat => 'Repeat Password';

  @override
  String get password_repeat_required => 'Please repeat your password.';

  @override
  String get password_mismatch => 'Passwords do not match.';

  @override
  String get form_invalid => 'Please fill out all fields correctly.';

  @override
  String get register_continuation => 'Complete Registration';

  @override
  String get continue_on => 'Continue';

  @override
  String get date_of_birth => 'Date of Birth';

  @override
  String get date_of_birth_required => 'Please enter your date of birth.';

  @override
  String get date_of_birth_minor => 'You must be at least 18 years old.';

  @override
  String get date_of_birth_invalid => 'Invalid date of birth.';

  @override
  String get gender => 'Gender';

  @override
  String get gender_m => 'Male';

  @override
  String get gender_f => 'Female';

  @override
  String get gender_o => 'Non-binary';

  @override
  String get userType => 'User Type';

  @override
  String get userType_farmer => 'Farmer';

  @override
  String get userType_consumer => 'Consumer';

  @override
  String get home => 'Home';

  @override
  String get orders => 'Orders';

  @override
  String get store => 'Store';

  @override
  String get stores => 'Stores';

  @override
  String get settings => 'Settings';

  @override
  String get profile => 'Profile';

  @override
  String get notifications => 'Notifications';

  @override
  String get no_notifications => 'No new notifications.';

  @override
  String get error_loading_data =>
      'Error loading data. Please try again later.';

  @override
  String get notification => 'Notification';

  @override
  String get product => 'Product';

  @override
  String get products_recommended => 'Recommended Products';

  @override
  String get news_single => 'News';

  @override
  String get news => 'News';

  @override
  String get products => 'Products';

  @override
  String get reviews => 'Reviews';

  @override
  String get localization => 'Localization';

  @override
  String get open_gps_app => 'Open in GPS App';

  @override
  String get markets_usual => 'Usual Markets';

  @override
  String get highlighted_products => 'Highlighted Products';

  @override
  String get no_store_info => 'No store information available.';

  @override
  String get store_edit => 'Create/Edit Store';

  @override
  String get store_update_success => 'Store updated successfully.';

  @override
  String get store_name => 'Store Name';

  @override
  String get store_name_required => 'Please enter your store name.';

  @override
  String get store_description => 'Store Description';

  @override
  String get store_description_required =>
      'Please enter a description for your store.';

  @override
  String get store_address => 'Store Address';

  @override
  String get store_address_required => 'Please enter your store address.';

  @override
  String get market_add => 'Add Market';

  @override
  String get store_images => 'Store Images';

  @override
  String get use_current_location => 'Use Current Location';

  @override
  String get market_name => 'Market Name';

  @override
  String get market_name_required => 'Please enter the market name.';

  @override
  String get market_schedule => 'Market Schedule';

  @override
  String get market_schedule_required =>
      'Please enter the store schedule at the market.';

  @override
  String get market_images => 'Market Images';

  @override
  String get market_location => 'Market Location';

  @override
  String get no_markets => 'No markets defined.';

  @override
  String get cancel => 'Cancel';

  @override
  String get save => 'Save';

  @override
  String get no_highlighted_products => 'No highlighted products available.';

  @override
  String get no_products => 'No products available.';

  @override
  String get order_status_pending => 'Pending';

  @override
  String get order_status_processing => 'Processing';

  @override
  String get order_status_readyForDelivery => 'Ready for Delivery';

  @override
  String get order_status_completed => 'Completed';

  @override
  String get order_status_cancelled => 'Cancelled';

  @override
  String get deliveryType => 'Delivery Type';

  @override
  String get deliveryType_pickup => 'Pickup';

  @override
  String get deliveryType_transporter => 'Transporter';

  @override
  String get deliveryType_homeDelivery => 'Home Delivery';

  @override
  String get search_by_name => 'Search by Name';

  @override
  String get category => 'Category';

  @override
  String get no_stores => 'No stores available.';

  @override
  String get edit_product => 'Create/Edit Product';

  @override
  String get product_name => 'Product Name';

  @override
  String get product_name_required => 'Please enter the product name.';

  @override
  String get product_description => 'Product Description';

  @override
  String get product_description_required =>
      'Please enter a description for the product.';

  @override
  String get product_category_required => 'Please select a product category.';

  @override
  String get product_price => 'Product Price';

  @override
  String get product_price_required => 'Please enter the product price.';

  @override
  String get product_unit => 'Product Unit';

  @override
  String get product_unit_required => 'Please enter the product unit.';

  @override
  String get product_stock_required =>
      'Please enter the product stock quantity.';

  @override
  String get min_stock => 'Minimum Stock Quantity';

  @override
  String get min_stock_required => 'Please enter the minimum stock quantity.';

  @override
  String get min_quantity => 'Minimum Quantity';

  @override
  String get min_quantity_required => 'Please enter the minimum quantity.';

  @override
  String get product_images => 'Product Images';

  @override
  String get product_images_required =>
      'Please upload at least one product image.';

  @override
  String get no_images => 'No images available.';

  @override
  String get productCategory_vegetables => 'Vegetables';

  @override
  String get productCategory_fruits => 'Fruits';

  @override
  String get productCategory_grains => 'Grains';

  @override
  String get productCategory_spices => 'Spices';

  @override
  String get productCategory_herbs => 'Herbs';

  @override
  String get productCategory_nuts => 'Nuts';

  @override
  String get productCategory_seeds => 'Seeds';

  @override
  String get productCategory_dairy => 'Dairy';

  @override
  String get productCategory_meat => 'Meat';

  @override
  String get productCategory_seafood => 'Seafood';

  @override
  String get productCategory_fish => 'Fish';

  @override
  String get productCategory_cheese => 'Cheese';

  @override
  String get productCategory_charcuterie => 'Charcuterie';

  @override
  String get productCategory_beverages => 'Beverages';

  @override
  String get productCategory_bakery => 'Bakery';

  @override
  String get productCategory_baskets => 'Baskets';

  @override
  String get productCategory_oils => 'Oils';

  @override
  String get productCategory_sauces => 'Sauces';

  @override
  String get productCategory_jams => 'Jams';

  @override
  String get productCategory_sweets => 'Sweets';

  @override
  String get productCategory_eggs => 'Eggs';

  @override
  String get productCategory_flowers => 'Flowers';

  @override
  String get productCategory_plants => 'Plants';

  @override
  String get productCategory_other => 'Other';

  @override
  String get close => 'Close';

  @override
  String get inStock => 'In Stock';

  @override
  String get outOfStock => 'Out of Stock';

  @override
  String get delete_product => 'Delete Product';

  @override
  String get delete_product_confirmation =>
      'Are you sure you want to delete this product?';

  @override
  String get delete => 'Delete';

  @override
  String get product_deleted => 'Product deleted successfully.';

  @override
  String get product_visibility_updated =>
      'Product visibility updated successfully.';

  @override
  String get select_quantity => 'Select Quantity';

  @override
  String get quantity => 'Quantity';

  @override
  String get quantity_required => 'Please enter the quantity.';

  @override
  String get quantity_invalid => 'Invalid quantity.';

  @override
  String get quantity_exceeds_stock => 'Quantity exceeds available stock.';

  @override
  String get add_to_cart => 'Add to Cart';

  @override
  String get added_to_cart => 'Product added to cart.';

  @override
  String get cart => 'Cart';

  @override
  String get cart_empty => 'Your cart is empty.';

  @override
  String get checkout => 'Checkout';

  @override
  String get leave_review => 'Leave a Review';

  @override
  String get submit_review => 'Submit Review';

  @override
  String get comment => 'Comment';

  @override
  String get no_reviews => 'No reviews yet.';

  @override
  String get review_submitted => 'Review submitted successfully.';

  @override
  String get warning => 'Warning';

  @override
  String get moving_too_fast =>
      'You are moving too fast. Please don\'t use the app while driving.';

  @override
  String get no_orders => 'You have no orders yet.';

  @override
  String get order => 'Order';

  @override
  String get status => 'Status';

  @override
  String get date => 'Date';

  @override
  String get delivery_type => 'Delivery type';
}
