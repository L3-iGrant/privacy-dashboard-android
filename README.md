<h1 align="center">
    Privacy Dashboard (Android SDK)
</h1>

<p align="center">
    <a href="/../../commits/" title="Last Commit"><img src="https://img.shields.io/github/last-commit/L3-iGrant/privacy-dashboard-android?style=flat"></a>
    <a href="/../../issues" title="Open Issues"><img src="https://img.shields.io/github/issues/L3-iGrant/privacy-dashboard-android?style=flat"></a>
    <a href="./LICENSE" title="License"><img src="https://img.shields.io/badge/License-Apache%202.0-yellowgreen?style=flat"></a>
</p>

<p align="center">
  <a href="#about">About</a> •
  <a href="#about">Configuration</a> •
  <a href="#about">Integration</a> •
  <a href="#release-status">Release Status</a> •
  <a href="#contributing">Contributing</a> •
  <a href="#licensing">Licensing</a>
</p>

## About

This repository hosts source code for the reference implementation of the Privacy Dashboard for individuals.

## Release Status

Released. Refer to the [releases](https://github.com/L3-iGrant/privacy-dashboard-android/releases) for the latest status of the deliverables.

## Configuration

Project level Gradle repository:
```gradle
repositories {
    maven { url "https://jitpack.io" }
}
```

Gradle:
```gradle
android {
    buildFeatures {
        dataBinding true
    }
}
dependencies {
    implementation "com.github.L3-iGrant:privacy-dashboard-android:2024.4.1"
    implementation "org.greenrobot:eventbus:3.2.0"
    implementation "com.squareup.okhttp3:okhttp:4.9.1"
    implementation "com.squareup.okhttp3:logging-interceptor:4.3.1"
    implementation "com.squareup.retrofit2:retrofit:2.8.0"
    implementation "com.squareup.retrofit2:converter-gson:2.8.0"
    implementation "com.github.bumptech.glide:glide:4.16.0"
    implementation "com.github.markomilos:paginate:1.0.0"
    implementation "com.github.marlonlom:timeago:4.0.3"
}
```

Maven:
```xml
<dependency>
    <groupId>com.github.L3-iGrant</groupId>
    <artifactId>privacy-dashboard-android</artifactId>
    <version><latest release></version>
</dependency>
```

## Integration

#### Authentication with a token provider (recommended)

Do not put an API key into your app. An API key in an app can be extracted,
and it gives access to all individuals of the organisation.

1. Your backend knows the logged-in user from its own session. It calls
   `POST /v3/service/individual/token` of the iGrant.io API with a
   server-only API key and gets a short-lived token (10 minutes) for that
   one individual.
2. Your app gives the token to the SDK through a token provider.

```kotlin
PrivacyDashboard.showPrivacyDashboard()
    .withBaseUrl("https://api.igrant.io/v2")
    .withOrganisationId(<Organisation ID>)
    .withTokenProvider { callback ->
        hostBackend.privacyDashboardToken { token, error -> callback(token, error) }
    }
    .start(this)
```

The SDK:
- asks the provider for a token when it starts, when the token is less than
  60 seconds from expiry, and one time after a `401` response (then it sends
  the request again one time);
- keeps the token in memory only;
- sends `Authorization: Bearer <token>` and no `X-ConsentBB-IndividualId`.
  The server takes the individual from the token.

`DataSharingUI` has the same `withTokenProvider`. The functions
`optInToDataAgreement`, `updateDataAgreementStatus` and `getDataAgreement`
have a `tokenProvider` parameter. Give the same provider object each time, so
that the SDK can use the token again.

Call `PrivacyDashboard.reset(context)` when the user logs out. It deletes the
credentials and the token from memory, and the credentials that older SDK
versions stored on the device.

The sample app (`app/`) shows a token provider. Its `SampleHostBackend` sends
`POST` to a token URL that you type in the app, and reads `access_token` from
the JSON response.

#### Authentication with an API key (deprecated)

`withApiKey` and `withUserId` keep working until a later major version of the
SDK. The SDK logs one deprecation warning for each process.

```
PrivacyDashboard.showPrivacyDashboard().withApiKey(<API key>)
                .withUserId(<User ID>)
                .withBaseUrl(<Base URL>)
                .withOrganisationId(<Organisation ID>).start(this)
```
We can also show the privacy dashboard with `accessToken`. For that use the below
```
.withAccessToken(<accessToken>)
```
> **_Note:_** If we have `accessToken` then no need to pass `API key` and `User ID`.
> A token provider has precedence over `accessToken`, and `accessToken` has
> precedence over the API key.

#### Security notes

- The SDK keeps the credentials in memory only. It does not write them to
  SharedPreferences or to other storage, so they are not in backups.
- Because the credentials are not stored on disk, an SDK screen cannot
  continue after Android stops the process of the app. When Android restores
  an SDK screen without credentials, the screen closes at once (no request, no
  error message) and the user goes back to the host app. The host app starts
  the SDK again.
- The SDK logs HTTP requests only in a debug build of the SDK, at level
  `BASIC`, with `Authorization` and `X-ConsentBB-IndividualId` redacted.
- Logo and cover image requests have no authorisation headers.
- The policy WebView opens only pages of the policy host. Other links open in
  a Custom Tab.

To set the language we just need to add the following before the `start(this)`
```
.withLocale(<language code>)
```

To enable user requests we just need to add the following before the `start(this)`
```
.enableUserRequest()
```

To enable Ask me we just need to add the following before the `start(this)`
```
.enableAskMe()
```
#### Data Sharing UI

Register activity for result to get the response back from the Data sharing UI
```
var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    Log.d("Data Agreement Record", data.getStringExtra("data_agreement_record") ?: "")
                }
            }
        }
```
To initiate the Data sharing UI
```
 val intent = DataSharingUI.showDataSharingUI()
                .withApiKey(<API key>)
                .withUserId(<User ID)
                .withDataAgreementId(<Data Agreement ID>)
                .withThirdPartyApplication(<Third party application name>,<Third party application logo>)
                .withBaseUrl(<Base URL>)
                .withOrganisationId(<Organisation ID>)
                .get(this)

resultLauncher.launch(intent)
```
We can also show the privacy dashboard with `accessToken`. For that use the below
```
.withAccessToken(<accessToken>)
```
> **_Note:_** If we have `accessToken` then no need to pass `API key` and `User ID`

To set the secondary button's text. Use the following before the `start(this)`
```
.secondaryButtonText(<Button text>)
```
In response, it will return a json string as follows. `Null` if the process failed
```
        {
            "id": "********************",
            "dataAgreementId": "********************",
            "dataAgreementRevisionId": "********************",
            "dataAgreementRevisionHash": "*******************************",
            "individualId": "********************",
            "optIn": Boolean,
            "state": "*********",
            "signatureId": ""
        }
```
#### Opt-in to Data Agreement
This function is used to provide the 3PP developer to opt-in to a data agreement.
```
 PrivacyDashboard.optInToDataAgreement(
                            dataAgreementId = <Data argeement ID>,
                            baseUrl = <baseUrl>,
                            apiKey = <apiKey>,
                            userId = <userId>
                        )
```
We can also use `accessToken` to opt-in to data agreement. For that use
```
accessToken = <Access token>
```
> **_Note:_** If we have `accessToken` then no need to pass `API key` and `User ID`

In response, it will return a json string as follows. `Null` if the process failed
```
        {
            "id": "********************",
            "dataAgreementId": "********************",
            "dataAgreementRevisionId": "********************",
            "dataAgreementRevisionHash": "*******************************",
            "individualId": "********************",
            "optIn": Boolean,
            "state": "*********",
            "signatureId": ""
        }
```
#### Fetch Data Agreement
This function is used to fetch the data agreement using `dataAgreementId`
```
  PrivacyDashboard.getDataAgreement(
                            dataAgreementId = <dataAgreementID>,
                            baseUrl = <baseUrl>,
                            apiKey = <apiKey>,
                            userId = <userId>
                        )
```
We can also use `accessToken` to opt-in to data agreement. For that use
```
accessToken = <Access token>
```
> **_Note:_** If we have `accessToken` then no need to pass `API key` and `User ID`

In response, it will return a json string.
#### Show data agreement policy
To show data agreement policy, fetch the data agreement with the above API, pass the response in this
```
PrivacyDashboard.showDataAgreementPolicy()
                        .withDataAgreement(<dataAgreementResponse>)
                        .withLocale("en")
                        .start(this)
```
#### Individual Functions

> **_Deprecated:_** `createAnIndividual`, `fetchTheIndividual`,
> `updateTheIndividual`, `getAllIndividuals` and `deleteTheIndividual` need an
> organisation API key. Use your backend for these operations. The functions
> keep working with an API key until a later major version of the SDK.
##### To Create an Individual
```
PrivacyDashboard.createAnIndividual(
                            baseUrl = <baseUrl>,
                            apiKey = <apiKey>,
                        )
```
there is also optional fields to pass `name`, `email` and `phone`
##### To fetch an Individual
```
PrivacyDashboard.fetchTheIndividual(
                            baseUrl = <baseUrl>,
                            apiKey = <apiKey>,
                            individualId = <Individual id>
                        )
```
##### To update an Individual
```
PrivacyDashboard.updateTheIndividual(
                            baseUrl = <baseUrl>,
                            apiKey = <apiKey>,
                            name = <name>,
                            email = <email>,
                            phone = <phone>,
                            individualId = <Individual id>
                        )
```
##### To fetch all individuals
```
PrivacyDashboard.updateTheIndividual(
                            baseUrl = <baseUrl>,
                            apiKey = <apiKey>,
                            offset = <offset(Int)>,
                            limit = <limit(Int)>
                        )
```

## Release Status

Refer to the [releases](https://github.com/L3-iGrant/privacy-dashboard-android/releases) for the latest status of the deliverables. 

## Contributing

Feel free to improve the plugin and send us a pull request. If you find any problems, please create an issue in this repo.

## Licensing
Copyright (c) 2026 iGrant Technologies AB (iGrant.io), Sweden

Licensed under the Apache 2.0 License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the LICENSE for the specific language governing permissions and limitations under the License.
