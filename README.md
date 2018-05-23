## eNETS_test_Android
This repository will get you a copy of the project up and running on your local machine for development and testing purposes.
**Does not** show interaction with merchant portal.

See [development portal](https://api-developer.nets.com.sg/) for a complete guide on how to deploy the project on a live system.

## Main implementation
**1. AndroidManifest.xml**

Take note of the additional permissions added.
```
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission
    android:name="android.permission.INTERACT_ACROSS_USERS_FULL"
    android:protectionLevel="normal"/>
<uses-permission android:name="com.nets.netspay.QR_TRANSACTION"/>
```
        
**2. build.gradle(Module: app)**

A few additional dependencies, namely:

```
implementation 'javax.xml.bind:jaxb-api:2.2.12'        
implementation 'com.android.support:design:26.1.0'
implementation project(':enetslib-release')
```         
**3. MainActivity.java**

Mainly for the calling of the method from within the additional SDK library. **Include the library first.**
```
sendPaymentRequest(String key, String hmac, String txnReq, final PaymentCallback var4, final AppCompatActivity var5)
```

**4. HMAC_Gen.java**

Used for generating your HMAC. In actually production environment, the HMAC should be generated on the merchant portal. However, for this sample application, the implementation is shown in the application.

## Basic Functionalities
1. Allows generation of default UAT UMID, Key, Secret Key values.

2. Allows generation of payload with the previous values and current date_time as merchantTxnRef

3. Displays generated payload and corresponding HMAC

4. Allows alteration and editing of the payload

5. Simulates sending of the payload

## Payment Options
**1. Debit Payment (Built in)**

**2. Credit Payment**

Use 4111-1111-1111-1111, any future expiry date and and 3 digits security number

**3. NETSPAY Option**

Requires UAT version of [NETSPAY](https://api-developer.nets.com.sg/downloads/netspay2_0_39u_demo.zip)
```
<uses-permission android:name="com.nets.netspay.QR_TRANSACTION"/> <!-- Ensure you have this permission -->
```

## Built With

* [Android Studio 3.0.1](https://developer.android.com/studio/)
