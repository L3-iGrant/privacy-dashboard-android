# mobileSDK-android

This contains all iGrant.io mobile SDK that can be incorporated to any third party app for android.

## Download
Gradle:

```groovy
repositories {
    jcenter()
}

dependencies {
    implementation 'io.igrant.igrant_sdk:igrant_org_sdk:1.0.8'
}
```

## Required Dependencies

```groovy
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## How to use

Add the following meta data to the manifest inside the application tag
```groovy
<meta-data android:value=<ORG ID> 
           android:name="io.igrant.igrant_org_sdk.orgid"></meta-data>
```
 And start the LoginActivity.java activity whereever needed.
 
```groovy
Intent intent = new Intent(this, LoginActivity.class);
startActivity(intent);
```
