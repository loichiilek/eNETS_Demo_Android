## eNETS_test_Android
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.
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

A few additional dependencies are added, namely:

```
implementation 'javax.xml.bind:jaxb-api:2.2.12'        
implementation 'com.android.support:design:26.1.0'
implementation project(':enetslib-release')
```         
**3. MainActivity.java**

Mainly for the calling of the method from within the additional SDK library. **Do include the library first.**
```
sendPaymentRequest(String key, String hmac, String txnReq, final PaymentCallback var4, final AppCompatActivity var5)
```

**4. HMAC_Gen.java**

Used for generating your HMAC. In actually production environment, the HMAC should be generated on the merchant portal. However, for this sample application, the implementation is shown in the application.

## Built With

* [Android Studio](https://developer.android.com/studio/)

