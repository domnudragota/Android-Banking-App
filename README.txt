https://github.com/domnudragota/Android-Banking-App


# Mobile Banking App

## Description
This Mobile Banking App is a comprehensive financial application built for Android devices. The app offers a variety of features to enhance the user experience, including:

- **User Authentication**: Secure login and registration using Firebase Authentication.
- **Credit Card Management**: Add, view, and manage multiple credit cards.
- **Dynamic Balance Updates**: Real-time balance updates after transactions.
- **Transfers**: Make seamless money transfers using IBAN between users.
- **Currency Converter**: Convert between various currencies using a third-party API.
- **Budget Visualization**: View spending patterns using a dynamically generated pie chart.
- **Notification System**: Receive notifications for completed transfers.
- **Broadcasts**: Validate transfer operations using a custom broadcast receiver.
- **Dynamic UI Updates**: Update the main dashboard based on the selected credit card.

---

## Components Used

### 1. **Foreground Services**
- **Role**: Used to run the pie chart generation process for budget visualization.
- **Implementation**: The `VisualizeSpendingService` is started as a foreground service when the user views spending.

### 2. **Background Services**
- **Role**: Handles transfer notifications and validation in the background using `TransferService`.
- **Implementation**: Runs silently to notify users of successful transfers.

### 3. **Intents**
- **Role**: Navigate between various screens and pass data.
- **Example**: Clicking the "View Profile" button launches the `ProfileActivity` using an explicit intent.

### 4. **Activities**
- **Role**: Provides the user interface for different workflows.
- **Examples**:
  - `MainActivity`: Serves as the app's home screen.
  - `TransferActivity`: Handles money transfer operations.
  - `CurrencyConverterActivity`: Allows currency conversion.

### 5. **Broadcast Receivers**
- **Role**: Validates money transfers and logs the status of transfer operations.
- **Implementation**: A custom broadcast receiver (`TransferValidationReceiver`) listens for `TRANSFER_COMPLETE` events and checks Firebase for balance consistency.

### 6. **Database Integration**
- **Role**: Store and retrieve structured data for users, credit cards, and transactions.
- **Implementation**: Firebase Firestore is used as the primary database for real-time data management.

### 7. **Usage of External APIs**
- **Role**: Fetch exchange rates for currency conversion.
- **Implementation**: Integrated with the ExchangeRate-API for real-time currency data.

### 8. **Notifications**
- **Role**: Inform users of completed transfers.
- **Implementation**: Notifications are sent via `NotificationManager` using the `TransferService`.

---

## Getting Started
1. Clone this repository.
2. Set up Firebase for the app.
3. Add your API key for the ExchangeRate-API in the project.
4. Build and run the app on an Android device or emulator.

---

## Future Enhancements
- Add fingerprint or biometric authentication.
- Enhance UI with animations.
- Add support for more payment gateways.

---

## License
This project is licensed under the MIT License.
