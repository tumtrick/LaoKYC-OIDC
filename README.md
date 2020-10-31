# LaoKYC-OIDC

[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger)

### 1. Get Request SMS OTP 

Request & Response https://gateway.sbg.la/api/ 

|  | Name | Description
| ------ | ------ |  ------ |
| Route | /login |  |
| Request | phone | Phone is phone number register 3 grab successful  (205xxxxxxx,305xxxxxx,209xxxxxxx,309xxxxxx)|
|  | device | Device is Device name of Mobile phone |
| Response | code | code is integer response status call  |
|  | message | message is string response message success or failure  |

![github-small](https://i.ibb.co/vVsBnWb/Screen-Shot-2020-10-29-at-14-26-38.jpg)

### 2.	Render Image ( https://gateway.sbg.la/api/render/MyPhoto/+ Phonenumber + "?") 


### 3.	Edit file “ BuildConfigs” in package : “com.gov.mpt.laokyclib.utils”

```kotlin
public static final String AUTHORIZSTION_END_POINT_URI = "https://login.oneid.sbg.la/connect/authorize";
public static final String CLIENT_ID = "---- Please enter your Client ID -----";
public static final String CLIENT_SECRET = "--- Please enter your Client Secret ---";
public static final String REDIRECT_URI = "io.identityserver.YOUR_CLIENT_ID://signin-oidc";
public static final String REGISTRATION_END_POINT_URI = "";
public static final String RESPONSE_TYPE = "code";
public static final String SCOPE = "profile openid LaoKYC mkyc_api";
public static final String TOKEN_END_POINT_URI = "https://login.oneid.sbg.la/connect/token";

```

