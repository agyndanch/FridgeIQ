# FridgeIQ 🥗📱

**Food Inventory Management App**

FridgeIQ is an Android app designed to help users efficiently manage their food inventory, reduce waste, and optimize grocery shopping by tracking expiration dates, categorizing items, and providing insightful analytics.

## 📱 Android Studio Installation & Setup

### **Setup Instructions**

1. **Download the Project**
   - Go to the GitHub repository page
   - Click the green "Code" button
   - Select "Download ZIP"
   - Extract the ZIP file to your desired location

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory and select it

3. **Run the Application**
   - Connect an Android device or start an emulator (API 32+)
   - Click "Run"
  
## 📺 Demo Video
https://github.com/user-attachments/assets/2b576d51-ff3e-48bf-97fb-9c44ed970845

## 🌟 Features

### 📦 **Inventory Management**
- Add, edit, and manage food items with detailed information
- Enter items using barcode scanner
- Organize items by category (Dairy, Meat, Vegetables, etc.)
- Track items across different storage locations (Refrigerator, Freezer, Pantry)
- Visual indicators for expiring items
- Mark items as consumed or wasted

### 🛒 **Shopping List Management**
- Create and manage shopping lists with quantity and unit tracking
- Check off items as purchased
- Clear purchased items with a single action

### 📊 **Analytics & Insights**
- Track monthly waste costs and weekly waste item counts
- Visual pie charts showing waste distribution by category

### 🔔 **Smart Notifications**
- **Expiration Alerts**: Automated notifications for items expiring within 2 days

## ⚠️ Known Issues & Limitations

### **Current Limitations**
- **Offline Mode**: Data is stored locally only
- **Date Validation**: No validation for unrealistic expiration dates
- **Multi-language**: Currently supports English only

### **Known Issues**
- **Barcode Scanning**: Scanned barcodes may not register
- **Large Datasets**: Performance slows with larger amounts of items
- **Memory**: Image processing may cause minor delays

## 🛠️ Technology Stack

### **Core Framework**
- **Language**: Kotlin
- **Platform**: Android (API 32+)
- **Architecture**: MVVM (Model-View-ViewModel)

### **Database & Storage**
- **Local Database**: Room Database with TypeConverters
- **Data Flow**: Kotlin Flows

### **Camera & ML**
- **Camera Integration**: CameraX for camera functionality
- **Barcode Scanning**: Google ML Kit Barcode Scanning

### **Navigation & UI**
- **Navigation**: Android Navigation Component with Safe Args
- **UI Components**: Material Design Components, RecyclerView, SwipeRefreshLayout
- **Charts**: MPAndroidChart for data visualization

### **Background Processing**
- **WorkManager**: Periodic notification scheduling
- **Coroutines**: Asynchronous programming
- **Lifecycle Awareness**: ViewModel integration

## 📚 API Documentation
- **ML Kit Barcode Scanning:** com.google.mlkit:barcode-scanning:17.2.0
- **CameraX:** androidx.camera:camera-*:1.3.1
- **Room database:** androidx.room:room-*:2.6.1
- **MPAndroidChart:** com.github.PhilJay:MPAndroidChart:v3.1.0
- **WorkManager:** androidx.work:work-runtime-ktx:2.9.0

## 🔮 Future Development Roadmap

### Short Term
- [ ] Family Sharing: Multi-user support for households
- [ ] Cloud Sync: Backup and sync data across devices
- [ ] Smart Notifications: Improved expiration and shopping reminders

### Long Term
- [ ] Smart Shopping: Auto-generate shopping lists from usage patterns
- [ ] Better Analytics: Enhanced waste tracking and cost optimization
- [ ] Recipe Suggestions: Recommend recipes based on available ingredients

## 📸 Screenshots
![inventory](https://github.com/user-attachments/assets/df0a3589-2518-4110-b484-6dbedcc779a0)
![inventory_empty](https://github.com/user-attachments/assets/faa11931-da9e-489b-bd5c-1095cf6fe0b0)
![add_food](https://github.com/user-attachments/assets/09227d1a-887c-42fc-8a5c-a09eee904027)
![filter_inventory](https://github.com/user-attachments/assets/1d638ddd-3a68-4ab0-ac4b-5c3051c22ea5)
![shopping_empty](https://github.com/user-attachments/assets/746cdb6d-d166-4426-809a-52492fde45b6)
![shopping](https://github.com/user-attachments/assets/e980de81-ade7-47fc-b6b2-09b2539320d7)
![barcode_scanner](https://github.com/user-attachments/assets/1abd2c28-88a8-4144-bada-646115a5de73)
![analytics](https://github.com/user-attachments/assets/1e41b82b-2ed9-42a8-ac7a-20b06c8ad075)
