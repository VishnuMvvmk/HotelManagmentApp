# 🏨 Hotel Management System (Android App)

The **Hotel Management System** is an Android application designed to simplify hotel operations such as room booking, customer management, food menu handling, and service requests. The app provides separate interfaces for **Admin** and **Customer** roles, ensuring efficient management and a smooth user experience.

---

## 📱 Features

### 👨‍💼 Admin Module
- Add, edit, and delete hotel rooms
- Upload room images (via ImgBB)
- Manage room availability
- View booked rooms (Upcoming, Occupied, Past)
- Perform check-in and check-out
- View customer reviews and ratings
- Manage food menu items
- Update hotel contact details
- View hotel statistics (bookings, occupancy, revenue)

### 👤 Customer Module
- View available rooms
- Book rooms with date selection
- View upcoming and past bookings
- Cancel upcoming bookings
- Rate rooms and submit reviews
- View food menu
- Submit service requests
- View hotel contact information
- Manage profile (update name & password)
- Secure logout

---

## 🛠️ Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose (Material Design 3)
- **Database:** Firebase Realtime Database
- **Image Hosting:** ImgBB
- **Image Loading:** Coil
- **Architecture:** MVVM (UI-driven state management)

---


## 🖼️ Image Upload Strategy

- Images are uploaded to **ImgBB** using its REST API.
- ImgBB returns a public image URL.
- The URL is saved in Firebase Realtime Database.
- Images are displayed using the **Coil** library.

**Reason:** Firebase Realtime Database does not support binary file storage.

---

## 🔐 Authentication

- Basic email & password authentication (custom Firebase DB-based).
- Login status is stored using SharedPreferences.
- Separate roles: `admin` and `customer`.


## 🎨 UI & UX Highlights

- Premium card-based layouts
- Role-based navigation
- Tab views for bookings
- Icons for better clarity
- Form validation & user feedback
- Modern Material Design 3 components

---

## 🚀 How to Run the Project

1. Clone the repository
2. Open the project in **Android Studio**
3. Sync Gradle files
4. Add your Firebase project configuration
5. Run the app on an emulator or physical device

---




