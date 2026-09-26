# 🍽️ Campus Eats

A modern Android campus food ordering application built with **Kotlin +
Jetpack Compose + Firebase**.

> **Note:** This README is based on the parts of the Campus Eats project
> reviewed so far. Some sections describe the **current
> implementation**, while the proposed project structure and future
> improvements are clearly marked so they can be edited later.

------------------------------------------------------------------------

## 📱 Project Overview

**Campus Eats** is a campus-focused food ordering application where
students can browse food/menu items, place orders, and manage their
account. An admin side is also being implemented for managing the
application and menu-related data.

The project uses:

-   **Kotlin**
-   **Jetpack Compose**
-   **Material 3**
-   **Firebase Authentication**
-   **Firebase Realtime Database**
-   **Navigation Compose**
-   **Coil**
-   **Kotlin Coroutines**
-   **Android Jetpack ViewModel**

The application package is:

``` text
com.mad.campuseats
```

------------------------------------------------------------------------

# ✨ Main Features

## 👨‍🎓 Student Features

The student side is designed around the following functionality:

-   Student registration
-   Student login
-   Email/password authentication
-   Student profile stored in Firebase Realtime Database
-   Food/menu browsing
-   Product/menu selection
-   Order flow
-   Order-related UI
-   Navigation between application screens
-   User logout

### Student account structure

A registered student is stored under:

``` text
users/{firebaseUid}
```

Example:

``` text
users
└── zLIXopc2i2hK70Jg22j469EwnC92
    ├── email: "rudrakaiser@gmail.com"
    ├── id: "zLIXopc2i2hK70Jg22j469EwnC92"
    ├── name: "Rudra Kaiser"
    ├── role: "STUDENT"
    └── studentId: "0222410005101037"
```

------------------------------------------------------------------------

# 👨‍💼 Admin Features

The project includes a separate admin login flow.

Admin authentication is **not based on a hardcoded email/password inside
the app**.

Instead, the current implementation checks:

1.  The email/password exists in Firebase Authentication.
2.  The authenticated user's UID is obtained.
3.  The app loads:

``` text
users/{uid}
```

4.  The user's role is checked.
5.  Only users whose role is `ADMIN` are allowed through the admin login
    flow.

The important condition is effectively:

``` kotlin
if (user.role != UserRole.ADMIN) {
    // Not an admin account
}
```

### Admin database structure

Example:

``` text
users
└── grAGopc2i2hK70Jg22j469EwnF88
    ├── email: "example@gmail.com"
    ├── id: "grAGopc2i2hK70Jg22j469EwnF88"
    ├── name: "Rudra Kaiser"
    ├── role: "ADMIN"
    └── studentId: ""
```

### Creating an admin

1.  Open Firebase Console.
2.  Go to **Authentication → Users**.
3.  Create an email/password user.
4.  Copy that user's UID.
5.  Open **Realtime Database → Data**.
6.  Find/create:

``` text
users/{UID}
```

7.  Set:

``` text
role = ADMIN
```

The `id` field should contain the same Firebase Authentication UID.

------------------------------------------------------------------------

# 🏗️ Current Architecture

The current project is primarily **ViewModel-centric**.

A major part of the application logic is handled by:

``` text
AppViewModel
```

The ViewModel currently handles areas such as:

-   Firebase Authentication
-   Student login
-   Student registration
-   Admin login
-   Current user state
-   Loading user profile from Realtime Database
-   Logout
-   Admin/menu management operations
-   Authentication error state

Firebase instances are created through:

``` kotlin
FirebaseAuth.getInstance()
```

and:

``` kotlin
FirebaseDatabase.getInstance()
```

The user database reference currently follows:

``` kotlin
FirebaseDatabase.getInstance()
    .getReference("users")
```

------------------------------------------------------------------------

# 🧩 Suggested Project Structure

The following is a **recommended structure for the project as it
grows**. It is not meant to imply that every folder already exists.

``` text
CampusEats/
│
├── app/
│   ├── build.gradle.kts
│   │
│   └── src/
│       └── main/
│           │
│           ├── AndroidManifest.xml
│           │
│           ├── java/
│           │   └── com/
│           │       └── mad/
│           │           └── campuseats/
│           │
│           │               ├── MainActivity.kt
│           │               │
│           │               ├── data/
│           │               │   ├── Models.kt
│           │               │   └── SampleData.kt
│           │               │
│           │               ├── ui/
│           │               │   ├── components/
│           │               │   │   ├── Components.kt
│           │               │   │
│           │               │   ├── screens/
│           │               │   │   ├── auth/
│           │               │   │   │   ├── AdminScreens.kt
│           │               │   │   │   ├── AuthScreens.kt
│           │               │   │   │   ├── CartScreen.kt
│           │               │   │   │   ├── CheckoutScreen.kt
│           │               │   │   │   ├── HomeScreen.kt
│           │               │   │   │   ├── OrdersScreen.kt
│           │               │   │   │   ├── Order TrackingScreen.kt
│           │               │   │   │   ├── ProfileScreen.kt
│           │               │   │   │   ├── RestaurantDetailScreen.kt
│           │               │   │   │   └── ReviewComplaintScreens.kt
│           │               │   │   │
│           │               │   └── theme/
│           │               │       ├── Color.kt
│           │               │       ├── Theme.kt
│           │               │       └── Type.kt
│           │               │
│           │               ├── navigation/
│           │               │   ├── NavGraph.kt
│           │               │   └── Screen.kt
│           │               │
│           │               ├── viewmodel/
│           │                   └── AppViewModel.kt
│           │
│           └── res/
│               ├── drawable/
│               ├── mipmap/
│               ├── values/
│               └── xml/
│
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
│
├── google-services.json
│
└── README.md
```

------------------------------------------------------------------------

# 🎨 UI / Design

Campus Eats uses a **modern food-ordering style UI** with a pink-based
visual identity.

The design direction includes:

-   Pink primary accent
-   Light pink backgrounds
-   White cards
-   Rounded corners
-   Dashed borders in selected UI areas
-   Material 3 components
-   Food/product cards
-   Clean typography
-   Student-friendly interface

The application icon is designed around the Campus Eats concept without
text, using food + campus/student visual elements.

------------------------------------------------------------------------

# 🛠️ Technology Stack

  Technology                   Usage
  ---------------------------- -----------------------------
  Kotlin                       Main programming language
  Jetpack Compose              UI framework
  Material 3                   UI components/design system
  ViewModel                    State & application logic
  Kotlin Coroutines            Asynchronous operations
  Firebase Authentication      Login/registration
  Firebase Realtime Database   User/menu/order data
  Navigation Compose           Screen navigation
  Coil                         Image loading
  Gradle Kotlin DSL            Build configuration

------------------------------------------------------------------------

# 📦 Current Gradle Configuration

The project configuration reviewed so far contains:

### Android Gradle Plugin

``` kotlin
id("com.android.application") version "8.6.1" apply false
```

### Kotlin

The reviewed root configuration contained:

``` kotlin
id("org.jetbrains.kotlin.android") version "2.1.10" apply false
id("org.jetbrains.kotlin.plugin.compose") version "2.1.10" apply false
```

> During development, a Kotlin/Firebase compatibility issue was
> encountered because a Firebase dependency contained Kotlin metadata
> `2.3.0` while the compiler expected `2.1.0`. The final Kotlin version
> should therefore be kept consistent with the versions actually
> resolved by the project.

### Google Services

``` kotlin
id("com.google.gms.google-services") version "4.5.0" apply false
```

### Android SDK

``` kotlin
compileSdk = 35
targetSdk = 35
minSdk = 24
```

### Java

``` kotlin
sourceCompatibility = JavaVersion.VERSION_17
targetCompatibility = JavaVersion.VERSION_17
```

and:

``` kotlin
kotlinOptions {
    jvmTarget = "17"
}
```

------------------------------------------------------------------------

# 🔥 Firebase

Campus Eats currently uses Firebase for backend-related functionality.

## Firebase Authentication

Used for:

-   Student registration
-   Student login
-   Admin login
-   Email/password authentication
-   Firebase UID generation

The app uses:

``` kotlin
FirebaseAuth.getInstance()
```

------------------------------------------------------------------------

## Firebase Realtime Database

Used for application/user data.

Main user reference:

``` text
users
```

User records are organized by Firebase Authentication UID:

``` text
users/{uid}
```

This is important because the admin login logic uses the authenticated
UID to find the corresponding database profile.

------------------------------------------------------------------------

# 🗄️ Database Structure

A basic current structure is:

``` text
Realtime Database
│
└── users
    │
    ├── {student_uid}
    │   ├── id
    │   ├── name
    │   ├── email
    │   ├── studentId
    │   └── role: STUDENT
    │
    └── {admin_uid}
        ├── id
        ├── name
        ├── email
        ├── studentId
        └── role: ADMIN
```

As the project grows, additional nodes can be introduced for:

``` text
products
orders
categories
cart
notifications
settings
```

> These additional nodes should only be added if they are actually
> implemented in the application.

------------------------------------------------------------------------

# 🔐 Authentication Flow

## Student Registration

Current flow:

``` text
Register Screen
      ↓
AppViewModel.register()
      ↓
Firebase Authentication
      ↓
createUserWithEmailAndPassword()
      ↓
Firebase UID
      ↓
Create User object
      ↓
users/{uid}
      ↓
role = STUDENT
```

------------------------------------------------------------------------

## Student Login

``` text
Login Screen
      ↓
AppViewModel.login()
      ↓
Firebase Authentication
      ↓
signInWithEmailAndPassword()
      ↓
Get UID
      ↓
users/{uid}
      ↓
Load User Profile
      ↓
Set currentUser
```

------------------------------------------------------------------------

## Admin Login

``` text
Admin Login Screen
        ↓
AppViewModel.loginAdmin()
        ↓
Firebase Authentication
        ↓
Get UID
        ↓
users/{uid}
        ↓
Check User Profile
        ↓
Check role
        ↓
role == ADMIN
        ↓
Allow Admin Access
```

------------------------------------------------------------------------

# 🌐 Internet Permission

Firebase/network functionality requires Internet access.

The Android Manifest contains:

``` xml
<uses-permission android:name="android.permission.INTERNET" />
```

This should be placed directly inside the `<manifest>` element.

Example:

``` xml
<manifest ...>

    <uses-permission android:name="android.permission.INTERNET" />

    <application
        ...>
    </application>

</manifest>
```

No runtime permission dialog is required for normal Internet access.

------------------------------------------------------------------------

# 🧱 Dependencies

The reviewed project currently contains dependencies including:

``` kotlin
dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(platform("com.google.firebase:firebase-bom:34.19.0"))
    implementation("com.google.firebase:firebase-analytics")
}
```

------------------------------------------------------------------------

# 🖼️ Image Loading

The project uses **Coil Compose**:

``` kotlin
implementation("io.coil-kt:coil-compose:2.6.0")
```

This can be used for remote/local product images in Compose.

Example:

``` kotlin
AsyncImage(
    model = imageUrl,
    contentDescription = null
)
```

------------------------------------------------------------------------

# 🧭 Navigation

The project uses:

``` kotlin
androidx.navigation:navigation-compose
```

for Compose-based screen navigation.

A future navigation structure can be organized around:

``` text
Auth
├── Login
├── Register
└── Admin Login

Student
├── Home
├── Menu
├── Cart
├── Orders
└── Profile

Admin
├── Dashboard
├── Menu Management
└── Order Management
```

------------------------------------------------------------------------

# 🧠 State Management

The application uses Android `ViewModel` for application state.

The current `AppViewModel` manages important states such as:

-   Current user
-   Loading state
-   Authentication errors
-   User authentication
-   Registration
-   Admin authentication
-   Logout
-   Menu/admin operations

As the application becomes larger, splitting the large ViewModel into
feature-specific ViewModels would make the project easier to maintain.

Suggested future approach:

``` text
AuthViewModel
MenuViewModel
CartViewModel
OrderViewModel
AdminViewModel
```

------------------------------------------------------------------------

# 📊 Recommended Future Architecture

For a larger production-ready version, the following architecture can be
adopted:

``` text
UI
 │
 ▼
ViewModel
 │
 ▼
Repository
 │
 ▼
Firebase
```

For example:

``` text
LoginScreen
    ↓
AuthViewModel
    ↓
AuthRepository
    ↓
FirebaseAuth
```

and:

``` text
MenuScreen
    ↓
MenuViewModel
    ↓
MenuRepository
    ↓
Firebase Realtime Database
```

This is a recommended improvement, not necessarily the current
implementation.

------------------------------------------------------------------------

# 🔒 Security

Firebase Security Rules should be configured before production
deployment.

A basic conceptual rule structure would be:

``` text
users
 ├── Students → access to their own profile
 └── Admins   → administrative access
```

Admin privileges should **not** be trusted merely because the client
application displays an admin screen.

Firebase Database Rules should enforce authorization on the backend.

> Production security rules should be designed according to the exact
> database structure and operations used by the final application.

------------------------------------------------------------------------

# ⚙️ Setup Instructions

## 1. Clone/Open the Project

Open the project in Android Studio.

------------------------------------------------------------------------

## 2. Configure Firebase

Create/open a Firebase project.

Enable:

-   Authentication
-   Email/Password sign-in
-   Realtime Database

Add the Android application using:

``` text
com.premieru.campuseats
```

Download:

``` text
google-services.json
```

and place it inside:

``` text
app/google-services.json
```

------------------------------------------------------------------------

## 3. Enable Authentication

Firebase Console:

``` text
Authentication
→ Sign-in method
→ Email/Password
→ Enable
```

------------------------------------------------------------------------

## 4. Create Realtime Database

Firebase Console:

``` text
Realtime Database
→ Create Database
```

The application expects a `users` node.

------------------------------------------------------------------------

## 5. Create an Admin

Create the account under:

``` text
Authentication → Users
```

Then create/update:

``` text
users/{admin_uid}
```

with:

``` text
id        = admin_uid
name      = Admin
email     = admin@email.com
studentId = ""
role      = ADMIN
```

------------------------------------------------------------------------

# ▶️ Running the Project

From Android Studio:

``` text
Run → Run 'app'
```

Or from the terminal:

``` bash
./gradlew assembleDebug
```

On Windows:

``` bash
gradlew.bat assembleDebug
```

------------------------------------------------------------------------

# 🧹 Common Build Troubleshooting

## Kotlin metadata mismatch

If an error similar to this appears:

``` text
Module was compiled with an incompatible version of Kotlin.

The binary version of its metadata is 2.3.0,
expected version is 2.1.0.
```

it means the Kotlin compiler version and one or more dependencies are
not aligned.

Check:

-   Kotlin plugin version
-   Firebase BOM/dependency versions
-   Compose/Kotlin plugin versions
-   Android Studio version
-   Gradle version

Then clean/rebuild the project.

------------------------------------------------------------------------

## SDK XML version mismatch

If you see:

``` text
SDK processing.
This version only understands SDK XML versions up to 3
but an SDK XML file of version 4 was encountered.
```

check that Android Studio and Android SDK Command-line Tools are
compatible/up to date.

------------------------------------------------------------------------

# 📁 Important Files

The important project files currently reviewed include:

``` text
build.gradle.kts
settings.gradle.kts
app/build.gradle.kts
app/src/main/AndroidManifest.xml
AppViewModel.kt
```

Firebase configuration:

``` text
app/google-services.json
```

should be present locally but should not be committed publicly if the
project's security/repository policy requires keeping configuration
files private.

------------------------------------------------------------------------

# 🧪 Testing Checklist

## Authentication

-   [ ] Student registration works
-   [ ] Student login works
-   [ ] Wrong email/password shows an error
-   [ ] Logout works
-   [ ] Admin login works
-   [ ] Student account cannot access admin flow
-   [ ] Admin profile exists under the correct UID
-   [ ] `role` is correctly stored as `ADMIN`

## Database

-   [ ] `users` node exists
-   [ ] User UID matches Firebase Authentication UID
-   [ ] User profile loads correctly
-   [ ] Student profile is saved
-   [ ] Admin profile is saved
-   [ ] Firebase security rules are configured

## UI

-   [ ] Login UI
-   [ ] Registration UI
-   [ ] Home UI
-   [ ] Menu/product UI
-   [ ] Cart/order UI
-   [ ] Admin UI
-   [ ] Loading states
-   [ ] Error states
-   [ ] Empty states
-   [ ] Responsive Compose layouts

------------------------------------------------------------------------

# 🚧 Future Improvements

Possible future features:

### Student

-   [ ] Food categories
-   [ ] Search food
-   [ ] Product details
-   [ ] Cart quantity management
-   [ ] Order confirmation
-   [ ] Order history
-   [ ] Order tracking
-   [ ] Profile editing
-   [ ] Favorites
-   [ ] Notifications

### Admin

-   [ ] Dashboard statistics
-   [ ] Add product
-   [ ] Edit product
-   [ ] Delete product
-   [ ] Product image upload
-   [ ] Category management
-   [ ] Order management
-   [ ] Change order status
-   [ ] User management
-   [ ] Admin profile management

### Backend

-   [ ] Proper Firebase Realtime Database security rules
-   [ ] Better data validation
-   [ ] Repository layer
-   [ ] Feature-specific ViewModels
-   [ ] Better error handling
-   [ ] Offline handling
-   [ ] Production logging strategy

------------------------------------------------------------------------

# 🎯 Project Goals

Campus Eats aims to provide a simple and convenient digital
food-ordering experience for students within a campus environment.

The application is designed to connect:

``` text
Students
   │
   │ Browse / Order
   ▼
Campus Eats
   │
   ▼
Firebase Backend
   │
   ├── Authentication
   └── Realtime Database
   │
   ▼
Admin
   │
   └── Manage Campus Food Service
```

------------------------------------------------------------------------

# 👥 User Roles

Currently the application distinguishes between:

``` text
STUDENT
ADMIN
```

Example:

``` kotlin
enum class UserRole {
    STUDENT,
    ADMIN
}
```

The exact enum definition should remain consistent with the project's
`User.kt`.

------------------------------------------------------------------------

# 📌 Development Notes

### Current implementation style

The application currently uses a relatively centralized `AppViewModel`.

This is perfectly workable while the application is small, but as Campus
Eats grows, separating responsibilities into repositories and
feature-specific ViewModels can improve:

-   Maintainability
-   Testability
-   Readability
-   Debugging
-   Feature isolation

### Firebase UID rule

The most important relationship in the current authentication/database
design is:

``` text
Firebase Authentication UID
            =
users/{UID}
            =
User.id
```

Keeping these three values synchronized is essential for login/profile
loading.

------------------------------------------------------------------------

# 📜 License

Add the project's license here.

Example:

``` text
MIT License
```

or replace this section with the appropriate license for the project.

------------------------------------------------------------------------

# 👨‍💻 Author

**Campus Eats**

Developed as a campus food ordering application.

------------------------------------------------------------------------

## ⭐ Project Status

``` text
Development / Active
```

The application is currently under development, with authentication,
Firebase integration, student/admin roles, Compose UI, and menu/admin
functionality being built incrementally.
