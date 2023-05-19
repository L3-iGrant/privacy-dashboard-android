# mobileSDK-android

This contains all iGrant.io mobile SDK that can be incorporated to any third party app for android.

## Download
Gradle:

```groovy
repositories {
    maven { url "https://jitpack.io" }
    jcenter()
}

dependencies {
    implementation 'com.github.L3-iGrant:mobileSDK-android:1.12.11'
}
```

## Required Dependencies

```groovy
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## How to use

```groovy
IgrantSdk().withApiKey(API_KEY)
        .withUserId(USER_ID)
        .withOrgId(ORG_ID).start(this)
```
