import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:intl/intl.dart' as intl;

import 'app_localizations_en.dart';
import 'app_localizations_pt.dart';

// ignore_for_file: type=lint

/// Callers can lookup localized strings with an instance of AppLocalizations
/// returned by `AppLocalizations.of(context)`.
///
/// Applications need to include `AppLocalizations.delegate()` in their app's
/// `localizationDelegates` list, and the locales they support in the app's
/// `supportedLocales` list. For example:
///
/// ```dart
/// import 'l10n/app_localizations.dart';
///
/// return MaterialApp(
///   localizationsDelegates: AppLocalizations.localizationsDelegates,
///   supportedLocales: AppLocalizations.supportedLocales,
///   home: MyApplicationHome(),
/// );
/// ```
///
/// ## Update pubspec.yaml
///
/// Please make sure to update your pubspec.yaml to include the following
/// packages:
///
/// ```yaml
/// dependencies:
///   # Internationalization support.
///   flutter_localizations:
///     sdk: flutter
///   intl: any # Use the pinned version from flutter_localizations
///
///   # Rest of dependencies
/// ```
///
/// ## iOS Applications
///
/// iOS applications define key application metadata, including supported
/// locales, in an Info.plist file that is built into the application bundle.
/// To configure the locales supported by your app, you’ll need to edit this
/// file.
///
/// First, open your project’s ios/Runner.xcworkspace Xcode workspace file.
/// Then, in the Project Navigator, open the Info.plist file under the Runner
/// project’s Runner folder.
///
/// Next, select the Information Property List item, select Add Item from the
/// Editor menu, then select Localizations from the pop-up menu.
///
/// Select and expand the newly-created Localizations item then, for each
/// locale your application supports, add a new item and select the locale
/// you wish to add from the pop-up menu in the Value field. This list should
/// be consistent with the languages listed in the AppLocalizations.supportedLocales
/// property.
abstract class AppLocalizations {
  AppLocalizations(String locale)
    : localeName = intl.Intl.canonicalizedLocale(locale.toString());

  final String localeName;

  static AppLocalizations? of(BuildContext context) {
    return Localizations.of<AppLocalizations>(context, AppLocalizations);
  }

  static const LocalizationsDelegate<AppLocalizations> delegate =
      _AppLocalizationsDelegate();

  /// A list of this localizations delegate along with the default localizations
  /// delegates.
  ///
  /// Returns a list of localizations delegates containing this delegate along with
  /// GlobalMaterialLocalizations.delegate, GlobalCupertinoLocalizations.delegate,
  /// and GlobalWidgetsLocalizations.delegate.
  ///
  /// Additional delegates can be added by appending to this list in
  /// MaterialApp. This list does not have to be used at all if a custom list
  /// of delegates is preferred or required.
  static const List<LocalizationsDelegate<dynamic>> localizationsDelegates =
      <LocalizationsDelegate<dynamic>>[
        delegate,
        GlobalMaterialLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
      ];

  /// A list of this localizations delegate's supported locales.
  static const List<Locale> supportedLocales = <Locale>[
    Locale('en'),
    Locale('pt'),
  ];

  /// No description provided for @welcome.
  ///
  /// In pt, this message translates to:
  /// **'Bem-vindo! '**
  String get welcome;

  /// No description provided for @welcome_message.
  ///
  /// In pt, this message translates to:
  /// **'Descubra produtos frescos diretamente do produtor.'**
  String get welcome_message;

  /// No description provided for @register.
  ///
  /// In pt, this message translates to:
  /// **'Criar Conta'**
  String get register;

  /// No description provided for @login_success.
  ///
  /// In pt, this message translates to:
  /// **'Login efetuado com sucesso.'**
  String get login_success;

  /// No description provided for @login_error.
  ///
  /// In pt, this message translates to:
  /// **'Login falhou. Verifica o email e a password.'**
  String get login_error;

  /// No description provided for @login_remember.
  ///
  /// In pt, this message translates to:
  /// **'Lembrar-me'**
  String get login_remember;

  /// No description provided for @login_google.
  ///
  /// In pt, this message translates to:
  /// **'Login com Google'**
  String get login_google;

  /// No description provided for @login_facebook.
  ///
  /// In pt, this message translates to:
  /// **'Login com Facebook'**
  String get login_facebook;

  /// No description provided for @login_apple.
  ///
  /// In pt, this message translates to:
  /// **'Login com Apple'**
  String get login_apple;

  /// No description provided for @login_google_success.
  ///
  /// In pt, this message translates to:
  /// **'Login com Google efetuado com sucesso.'**
  String get login_google_success;

  /// No description provided for @login_facebook_success.
  ///
  /// In pt, this message translates to:
  /// **'Login com Facebook efetuado com sucesso.'**
  String get login_facebook_success;

  /// No description provided for @login_apple_success.
  ///
  /// In pt, this message translates to:
  /// **'Login com Apple efetuado com sucesso.'**
  String get login_apple_success;

  /// No description provided for @login_google_error.
  ///
  /// In pt, this message translates to:
  /// **'Erro no login com Google. Tenta novamente.'**
  String get login_google_error;

  /// No description provided for @login_facebook_error.
  ///
  /// In pt, this message translates to:
  /// **'Erro no login com Facebook. Tenta novamente.'**
  String get login_facebook_error;

  /// No description provided for @login_apple_error.
  ///
  /// In pt, this message translates to:
  /// **'Erro no login com Apple. Tenta novamente.'**
  String get login_apple_error;

  /// No description provided for @password_reset_message.
  ///
  /// In pt, this message translates to:
  /// **'Insere o email para receber o link de reset da password.'**
  String get password_reset_message;

  /// No description provided for @password_reset_send.
  ///
  /// In pt, this message translates to:
  /// **'Enviar link de reset'**
  String get password_reset_send;

  /// No description provided for @password_reset_success.
  ///
  /// In pt, this message translates to:
  /// **'Link de reset enviado.'**
  String get password_reset_success;

  /// No description provided for @password_reset_error.
  ///
  /// In pt, this message translates to:
  /// **'Erro ao enviar o link de reset. Tenta novamente.'**
  String get password_reset_error;

  /// No description provided for @register_success.
  ///
  /// In pt, this message translates to:
  /// **'Registo efetuado com sucesso.'**
  String get register_success;

  /// No description provided for @register_error.
  ///
  /// In pt, this message translates to:
  /// **'Erro no registo. Tenta novamente.'**
  String get register_error;

  /// No description provided for @name.
  ///
  /// In pt, this message translates to:
  /// **'Nome'**
  String get name;

  /// No description provided for @name_required.
  ///
  /// In pt, this message translates to:
  /// **'Insere o teu nome.'**
  String get name_required;

  /// No description provided for @email_required.
  ///
  /// In pt, this message translates to:
  /// **'Insere o teu email.'**
  String get email_required;

  /// No description provided for @email_invalid.
  ///
  /// In pt, this message translates to:
  /// **'Email inválido.'**
  String get email_invalid;

  /// No description provided for @email_already_in_use.
  ///
  /// In pt, this message translates to:
  /// **'Este email já está registado.'**
  String get email_already_in_use;

  /// No description provided for @email_not_verified.
  ///
  /// In pt, this message translates to:
  /// **'Por favor, verifique o seu email antes de continuar.'**
  String get email_not_verified;

  /// No description provided for @password_required.
  ///
  /// In pt, this message translates to:
  /// **'Insere a tua password.'**
  String get password_required;

  /// No description provided for @password_invalid.
  ///
  /// In pt, this message translates to:
  /// **'A password deve ter pelo menos 12 caracteres, com no mínimo uma letra maiúscula, uma minúscula, \num número e um símbolo especial.'**
  String get password_invalid;

  /// No description provided for @password_repeat.
  ///
  /// In pt, this message translates to:
  /// **'Repetir Password'**
  String get password_repeat;

  /// No description provided for @password_repeat_required.
  ///
  /// In pt, this message translates to:
  /// **'Repete a tua password.'**
  String get password_repeat_required;

  /// No description provided for @password_mismatch.
  ///
  /// In pt, this message translates to:
  /// **'As passwords não coincidem.'**
  String get password_mismatch;

  /// No description provided for @form_invalid.
  ///
  /// In pt, this message translates to:
  /// **'Por favor, preenche todos os campos corretamente.'**
  String get form_invalid;

  /// No description provided for @register_continuation.
  ///
  /// In pt, this message translates to:
  /// **'Completar Registo'**
  String get register_continuation;

  /// No description provided for @continue_on.
  ///
  /// In pt, this message translates to:
  /// **'Continuar'**
  String get continue_on;

  /// No description provided for @date_of_birth.
  ///
  /// In pt, this message translates to:
  /// **'Data de Nascimento'**
  String get date_of_birth;

  /// No description provided for @date_of_birth_required.
  ///
  /// In pt, this message translates to:
  /// **'Insere a tua data de nascimento.'**
  String get date_of_birth_required;

  /// No description provided for @date_of_birth_minor.
  ///
  /// In pt, this message translates to:
  /// **'Tens de ter pelo menos 18 anos.'**
  String get date_of_birth_minor;

  /// No description provided for @date_of_birth_invalid.
  ///
  /// In pt, this message translates to:
  /// **'Data de nascimento inválida.'**
  String get date_of_birth_invalid;

  /// No description provided for @gender.
  ///
  /// In pt, this message translates to:
  /// **'Género'**
  String get gender;

  /// No description provided for @gender_m.
  ///
  /// In pt, this message translates to:
  /// **'Masculino'**
  String get gender_m;

  /// No description provided for @gender_f.
  ///
  /// In pt, this message translates to:
  /// **'Feminino'**
  String get gender_f;

  /// No description provided for @gender_o.
  ///
  /// In pt, this message translates to:
  /// **'Não-binário'**
  String get gender_o;

  /// No description provided for @userType.
  ///
  /// In pt, this message translates to:
  /// **'Tipo de Utilizador'**
  String get userType;

  /// No description provided for @userType_farmer.
  ///
  /// In pt, this message translates to:
  /// **'Agricultor'**
  String get userType_farmer;

  /// No description provided for @userType_consumer.
  ///
  /// In pt, this message translates to:
  /// **'Consumidor'**
  String get userType_consumer;

  /// No description provided for @home.
  ///
  /// In pt, this message translates to:
  /// **'Início'**
  String get home;

  /// No description provided for @orders.
  ///
  /// In pt, this message translates to:
  /// **'Encomendas'**
  String get orders;

  /// No description provided for @store.
  ///
  /// In pt, this message translates to:
  /// **'Loja'**
  String get store;

  /// No description provided for @stores.
  ///
  /// In pt, this message translates to:
  /// **'Lojas'**
  String get stores;

  /// No description provided for @settings.
  ///
  /// In pt, this message translates to:
  /// **'Configurações'**
  String get settings;

  /// No description provided for @profile.
  ///
  /// In pt, this message translates to:
  /// **'Perfil'**
  String get profile;

  /// No description provided for @notifications.
  ///
  /// In pt, this message translates to:
  /// **'Notificações'**
  String get notifications;

  /// No description provided for @no_notifications.
  ///
  /// In pt, this message translates to:
  /// **'Sem novas notificações'**
  String get no_notifications;

  /// No description provided for @error_loading_data.
  ///
  /// In pt, this message translates to:
  /// **'Erro ao carregar os dados. Tenta novamente mais tarde.'**
  String get error_loading_data;

  /// No description provided for @notification.
  ///
  /// In pt, this message translates to:
  /// **'Notificação'**
  String get notification;

  /// No description provided for @product.
  ///
  /// In pt, this message translates to:
  /// **'Produto'**
  String get product;

  /// No description provided for @products_recommended.
  ///
  /// In pt, this message translates to:
  /// **'Produtos Recomendados'**
  String get products_recommended;

  /// No description provided for @news_single.
  ///
  /// In pt, this message translates to:
  /// **'Notícia'**
  String get news_single;

  /// No description provided for @news.
  ///
  /// In pt, this message translates to:
  /// **'Notícias'**
  String get news;

  /// No description provided for @products.
  ///
  /// In pt, this message translates to:
  /// **'Produtos'**
  String get products;

  /// No description provided for @reviews.
  ///
  /// In pt, this message translates to:
  /// **'Avaliações'**
  String get reviews;

  /// No description provided for @localization.
  ///
  /// In pt, this message translates to:
  /// **'Localização'**
  String get localization;

  /// No description provided for @open_gps_app.
  ///
  /// In pt, this message translates to:
  /// **'Abrir na App de GPS'**
  String get open_gps_app;

  /// No description provided for @markets_usual.
  ///
  /// In pt, this message translates to:
  /// **'Mercados Usuais'**
  String get markets_usual;

  /// No description provided for @highlighted_products.
  ///
  /// In pt, this message translates to:
  /// **'Produtos em Destaque'**
  String get highlighted_products;

  /// No description provided for @no_store_info.
  ///
  /// In pt, this message translates to:
  /// **'Não há informações da loja disponíveis.'**
  String get no_store_info;

  /// No description provided for @store_edit.
  ///
  /// In pt, this message translates to:
  /// **'Criar/Editar Loja'**
  String get store_edit;

  /// No description provided for @store_update_success.
  ///
  /// In pt, this message translates to:
  /// **'Loja atualizada com sucesso.'**
  String get store_update_success;

  /// No description provided for @store_name.
  ///
  /// In pt, this message translates to:
  /// **'Nome da Loja'**
  String get store_name;

  /// No description provided for @store_name_required.
  ///
  /// In pt, this message translates to:
  /// **'Por favor, insere o nome da loja.'**
  String get store_name_required;

  /// No description provided for @store_description.
  ///
  /// In pt, this message translates to:
  /// **'Descrição da Loja'**
  String get store_description;

  /// No description provided for @store_description_required.
  ///
  /// In pt, this message translates to:
  /// **'Por favor, insere uma descrição para a loja.'**
  String get store_description_required;

  /// No description provided for @store_address.
  ///
  /// In pt, this message translates to:
  /// **'Endereço da Loja'**
  String get store_address;

  /// No description provided for @store_address_required.
  ///
  /// In pt, this message translates to:
  /// **'Por favor, insere o endereço da loja.'**
  String get store_address_required;

  /// No description provided for @market_add.
  ///
  /// In pt, this message translates to:
  /// **'Adicionar Mercado'**
  String get market_add;

  /// No description provided for @store_images.
  ///
  /// In pt, this message translates to:
  /// **'Imagens da Loja'**
  String get store_images;

  /// No description provided for @use_current_location.
  ///
  /// In pt, this message translates to:
  /// **'Usar localização atual'**
  String get use_current_location;

  /// No description provided for @market_name.
  ///
  /// In pt, this message translates to:
  /// **'Nome do Mercado'**
  String get market_name;

  /// No description provided for @market_name_required.
  ///
  /// In pt, this message translates to:
  /// **'Por favor, insere o nome do mercado.'**
  String get market_name_required;

  /// No description provided for @market_schedule.
  ///
  /// In pt, this message translates to:
  /// **'Horário do Mercado'**
  String get market_schedule;

  /// No description provided for @market_schedule_required.
  ///
  /// In pt, this message translates to:
  /// **'Por favor, insere o horário presente no mercado.'**
  String get market_schedule_required;

  /// No description provided for @market_images.
  ///
  /// In pt, this message translates to:
  /// **'Imagens do Mercado'**
  String get market_images;

  /// No description provided for @market_location.
  ///
  /// In pt, this message translates to:
  /// **'Localização do Mercado'**
  String get market_location;

  /// No description provided for @no_markets.
  ///
  /// In pt, this message translates to:
  /// **'Nenhum mercado definido.'**
  String get no_markets;

  /// No description provided for @cancel.
  ///
  /// In pt, this message translates to:
  /// **'Cancelar'**
  String get cancel;

  /// No description provided for @save.
  ///
  /// In pt, this message translates to:
  /// **'Guardar'**
  String get save;

  /// No description provided for @no_highlighted_products.
  ///
  /// In pt, this message translates to:
  /// **'Nenhum produto em destaque de momento.'**
  String get no_highlighted_products;

  /// No description provided for @no_products.
  ///
  /// In pt, this message translates to:
  /// **'Nenhum produto disponível.'**
  String get no_products;

  /// No description provided for @order_status_pending.
  ///
  /// In pt, this message translates to:
  /// **'Pendente'**
  String get order_status_pending;

  /// No description provided for @order_status_processing.
  ///
  /// In pt, this message translates to:
  /// **'Em Processamento'**
  String get order_status_processing;

  /// No description provided for @order_status_readyForDelivery.
  ///
  /// In pt, this message translates to:
  /// **'Pronto para Entrega'**
  String get order_status_readyForDelivery;

  /// No description provided for @order_status_completed.
  ///
  /// In pt, this message translates to:
  /// **'Concluído'**
  String get order_status_completed;

  /// No description provided for @order_status_cancelled.
  ///
  /// In pt, this message translates to:
  /// **'Cancelado'**
  String get order_status_cancelled;

  /// No description provided for @deliveryType.
  ///
  /// In pt, this message translates to:
  /// **'Tipo de Entrega'**
  String get deliveryType;

  /// No description provided for @deliveryType_pickup.
  ///
  /// In pt, this message translates to:
  /// **'Levantamento'**
  String get deliveryType_pickup;

  /// No description provided for @deliveryType_transporter.
  ///
  /// In pt, this message translates to:
  /// **'Transportadora'**
  String get deliveryType_transporter;

  /// No description provided for @deliveryType_homeDelivery.
  ///
  /// In pt, this message translates to:
  /// **'Entrega ao Domicílio'**
  String get deliveryType_homeDelivery;

  /// No description provided for @search_by_name.
  ///
  /// In pt, this message translates to:
  /// **'Pesquisar por Nome'**
  String get search_by_name;

  /// No description provided for @category.
  ///
  /// In pt, this message translates to:
  /// **'Categoria'**
  String get category;

  /// No description provided for @no_stores.
  ///
  /// In pt, this message translates to:
  /// **'Nenhuma loja disponível.'**
  String get no_stores;

  /// No description provided for @edit_product.
  ///
  /// In pt, this message translates to:
  /// **'Criar/Editar Produto'**
  String get edit_product;

  /// No description provided for @product_name.
  ///
  /// In pt, this message translates to:
  /// **'Nome do Produto'**
  String get product_name;

  /// No description provided for @product_name_required.
  ///
  /// In pt, this message translates to:
  /// **'Por favor, insere o nome do produto.'**
  String get product_name_required;

  /// No description provided for @product_description.
  ///
  /// In pt, this message translates to:
  /// **'Descrição do Produto'**
  String get product_description;

  /// No description provided for @product_description_required.
  ///
  /// In pt, this message translates to:
  /// **'Por favor, insere uma descrição para o produto.'**
  String get product_description_required;

  /// No description provided for @product_category_required.
  ///
  /// In pt, this message translates to:
  /// **'Por favor, seleciona uma categoria para o produto.'**
  String get product_category_required;

  /// No description provided for @product_price.
  ///
  /// In pt, this message translates to:
  /// **'Preço do Produto'**
  String get product_price;

  /// No description provided for @product_price_required.
  ///
  /// In pt, this message translates to:
  /// **'Por favor, insere o preço do produto.'**
  String get product_price_required;

  /// No description provided for @product_unit.
  ///
  /// In pt, this message translates to:
  /// **'Unidade do Produto'**
  String get product_unit;

  /// No description provided for @product_unit_required.
  ///
  /// In pt, this message translates to:
  /// **'Por favor, insere a unidade do produto.'**
  String get product_unit_required;

  /// No description provided for @product_stock_required.
  ///
  /// In pt, this message translates to:
  /// **'Por favor, insere a quantidade em stock do produto.'**
  String get product_stock_required;

  /// No description provided for @min_stock.
  ///
  /// In pt, this message translates to:
  /// **'Quantidade mínima em stock'**
  String get min_stock;

  /// No description provided for @min_stock_required.
  ///
  /// In pt, this message translates to:
  /// **'Por favor, insere a quantidade mínima em stock.'**
  String get min_stock_required;

  /// No description provided for @min_quantity.
  ///
  /// In pt, this message translates to:
  /// **'Quantidade Mínima'**
  String get min_quantity;

  /// No description provided for @min_quantity_required.
  ///
  /// In pt, this message translates to:
  /// **'Por favor, insere a quantidade mínima.'**
  String get min_quantity_required;

  /// No description provided for @product_images.
  ///
  /// In pt, this message translates to:
  /// **'Imagens do Produto'**
  String get product_images;

  /// No description provided for @product_images_required.
  ///
  /// In pt, this message translates to:
  /// **'Por favor, adiciona imagens do produto.'**
  String get product_images_required;

  /// No description provided for @no_images.
  ///
  /// In pt, this message translates to:
  /// **'Nenhuma imagem disponível.'**
  String get no_images;

  /// No description provided for @productCategory_vegetables.
  ///
  /// In pt, this message translates to:
  /// **'Legumes'**
  String get productCategory_vegetables;

  /// No description provided for @productCategory_fruits.
  ///
  /// In pt, this message translates to:
  /// **'Frutas'**
  String get productCategory_fruits;

  /// No description provided for @productCategory_grains.
  ///
  /// In pt, this message translates to:
  /// **'Cereais'**
  String get productCategory_grains;

  /// No description provided for @productCategory_spices.
  ///
  /// In pt, this message translates to:
  /// **'Especiarias'**
  String get productCategory_spices;

  /// No description provided for @productCategory_herbs.
  ///
  /// In pt, this message translates to:
  /// **'Ervas'**
  String get productCategory_herbs;

  /// No description provided for @productCategory_nuts.
  ///
  /// In pt, this message translates to:
  /// **'Nozes'**
  String get productCategory_nuts;

  /// No description provided for @productCategory_seeds.
  ///
  /// In pt, this message translates to:
  /// **'Sementes'**
  String get productCategory_seeds;

  /// No description provided for @productCategory_dairy.
  ///
  /// In pt, this message translates to:
  /// **'Laticínios'**
  String get productCategory_dairy;

  /// No description provided for @productCategory_meat.
  ///
  /// In pt, this message translates to:
  /// **'Carne'**
  String get productCategory_meat;

  /// No description provided for @productCategory_seafood.
  ///
  /// In pt, this message translates to:
  /// **'Marisco'**
  String get productCategory_seafood;

  /// No description provided for @productCategory_fish.
  ///
  /// In pt, this message translates to:
  /// **'Peixe'**
  String get productCategory_fish;

  /// No description provided for @productCategory_cheese.
  ///
  /// In pt, this message translates to:
  /// **'Queijo'**
  String get productCategory_cheese;

  /// No description provided for @productCategory_charcuterie.
  ///
  /// In pt, this message translates to:
  /// **'Charcutaria'**
  String get productCategory_charcuterie;

  /// No description provided for @productCategory_beverages.
  ///
  /// In pt, this message translates to:
  /// **'Bebidas'**
  String get productCategory_beverages;

  /// No description provided for @productCategory_bakery.
  ///
  /// In pt, this message translates to:
  /// **'Padaria'**
  String get productCategory_bakery;

  /// No description provided for @productCategory_baskets.
  ///
  /// In pt, this message translates to:
  /// **'Cestas'**
  String get productCategory_baskets;

  /// No description provided for @productCategory_oils.
  ///
  /// In pt, this message translates to:
  /// **'Óleos'**
  String get productCategory_oils;

  /// No description provided for @productCategory_sauces.
  ///
  /// In pt, this message translates to:
  /// **'Molhos'**
  String get productCategory_sauces;

  /// No description provided for @productCategory_jams.
  ///
  /// In pt, this message translates to:
  /// **'Compotas'**
  String get productCategory_jams;

  /// No description provided for @productCategory_sweets.
  ///
  /// In pt, this message translates to:
  /// **'Doces'**
  String get productCategory_sweets;

  /// No description provided for @productCategory_eggs.
  ///
  /// In pt, this message translates to:
  /// **'Ovos'**
  String get productCategory_eggs;

  /// No description provided for @productCategory_flowers.
  ///
  /// In pt, this message translates to:
  /// **'Flores'**
  String get productCategory_flowers;

  /// No description provided for @productCategory_plants.
  ///
  /// In pt, this message translates to:
  /// **'Plantas'**
  String get productCategory_plants;

  /// No description provided for @productCategory_other.
  ///
  /// In pt, this message translates to:
  /// **'Outro'**
  String get productCategory_other;

  /// No description provided for @close.
  ///
  /// In pt, this message translates to:
  /// **'Fechar'**
  String get close;

  /// No description provided for @inStock.
  ///
  /// In pt, this message translates to:
  /// **'Em Stock'**
  String get inStock;

  /// No description provided for @outOfStock.
  ///
  /// In pt, this message translates to:
  /// **'Esgotado'**
  String get outOfStock;

  /// No description provided for @delete_product.
  ///
  /// In pt, this message translates to:
  /// **'Eliminar Produto'**
  String get delete_product;

  /// No description provided for @delete_product_confirmation.
  ///
  /// In pt, this message translates to:
  /// **'Confirma que querer eliminar este produto?'**
  String get delete_product_confirmation;

  /// No description provided for @delete.
  ///
  /// In pt, this message translates to:
  /// **'Eliminar'**
  String get delete;

  /// No description provided for @product_deleted.
  ///
  /// In pt, this message translates to:
  /// **'Produto eliminado com sucesso.'**
  String get product_deleted;

  /// No description provided for @product_visibility_updated.
  ///
  /// In pt, this message translates to:
  /// **'Visibilidade do produto atualizada com sucesso.'**
  String get product_visibility_updated;

  /// No description provided for @select_quantity.
  ///
  /// In pt, this message translates to:
  /// **'Seleciona a quantidade'**
  String get select_quantity;

  /// No description provided for @quantity.
  ///
  /// In pt, this message translates to:
  /// **'Quantidade'**
  String get quantity;

  /// No description provided for @quantity_required.
  ///
  /// In pt, this message translates to:
  /// **'Por favor, insere a quantidade.'**
  String get quantity_required;

  /// No description provided for @quantity_invalid.
  ///
  /// In pt, this message translates to:
  /// **'Quantidade inválida.'**
  String get quantity_invalid;

  /// No description provided for @quantity_exceeds_stock.
  ///
  /// In pt, this message translates to:
  /// **'Quantidade selecionada excede o stock disponível.'**
  String get quantity_exceeds_stock;

  /// No description provided for @add_to_cart.
  ///
  /// In pt, this message translates to:
  /// **'Adicionar ao Carrinho'**
  String get add_to_cart;

  /// No description provided for @added_to_cart.
  ///
  /// In pt, this message translates to:
  /// **'Produto adicionado ao carrinho.'**
  String get added_to_cart;

  /// No description provided for @cart.
  ///
  /// In pt, this message translates to:
  /// **'Carrinho'**
  String get cart;

  /// No description provided for @cart_empty.
  ///
  /// In pt, this message translates to:
  /// **'O carrinho está vazio.'**
  String get cart_empty;

  /// No description provided for @checkout.
  ///
  /// In pt, this message translates to:
  /// **'Finalizar Compra'**
  String get checkout;

  /// No description provided for @leave_review.
  ///
  /// In pt, this message translates to:
  /// **'Deixar uma Avaliação'**
  String get leave_review;

  /// No description provided for @submit_review.
  ///
  /// In pt, this message translates to:
  /// **'Submeter Avaliação'**
  String get submit_review;

  /// No description provided for @comment.
  ///
  /// In pt, this message translates to:
  /// **'Comentário'**
  String get comment;

  /// No description provided for @no_reviews.
  ///
  /// In pt, this message translates to:
  /// **'Sem avaliações ainda.'**
  String get no_reviews;

  /// No description provided for @review_submitted.
  ///
  /// In pt, this message translates to:
  /// **'Avaliação submetida com sucesso.'**
  String get review_submitted;

  /// No description provided for @warning.
  ///
  /// In pt, this message translates to:
  /// **'Aviso'**
  String get warning;

  /// No description provided for @moving_too_fast.
  ///
  /// In pt, this message translates to:
  /// **'Estás a mover-se muito rápido. Por favor, não utilizes a aplicação enquanto conduzes.'**
  String get moving_too_fast;

  /// No description provided for @no_orders.
  ///
  /// In pt, this message translates to:
  /// **'Não tens encomendas ainda.'**
  String get no_orders;

  /// No description provided for @order.
  ///
  /// In pt, this message translates to:
  /// **'Encomenda'**
  String get order;

  /// No description provided for @status.
  ///
  /// In pt, this message translates to:
  /// **'Estado'**
  String get status;

  /// No description provided for @date.
  ///
  /// In pt, this message translates to:
  /// **'Data'**
  String get date;

  /// No description provided for @delivery_type.
  ///
  /// In pt, this message translates to:
  /// **'Tipo de Entrega'**
  String get delivery_type;
}

class _AppLocalizationsDelegate
    extends LocalizationsDelegate<AppLocalizations> {
  const _AppLocalizationsDelegate();

  @override
  Future<AppLocalizations> load(Locale locale) {
    return SynchronousFuture<AppLocalizations>(lookupAppLocalizations(locale));
  }

  @override
  bool isSupported(Locale locale) =>
      <String>['en', 'pt'].contains(locale.languageCode);

  @override
  bool shouldReload(_AppLocalizationsDelegate old) => false;
}

AppLocalizations lookupAppLocalizations(Locale locale) {
  // Lookup logic when only language code is specified.
  switch (locale.languageCode) {
    case 'en':
      return AppLocalizationsEn();
    case 'pt':
      return AppLocalizationsPt();
  }

  throw FlutterError(
    'AppLocalizations.delegate failed to load unsupported locale "$locale". This is likely '
    'an issue with the localizations generation tool. Please file an issue '
    'on GitHub with a reproducible sample app and the gen-l10n configuration '
    'that was used.',
  );
}
