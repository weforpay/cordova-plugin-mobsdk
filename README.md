初始化短信
cordova.plugins.mobsdk.sms.init();
cordova.plugins.mobsdk.sms.getSupportedCountries(okCb,failCb);
cordova.plugins.mobsdk.sms.getVerificationCode(contryCode,phone,okCb,failCb);
