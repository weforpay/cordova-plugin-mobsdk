var exec    = require('cordova/exec');


var countries = [
	{
		code:'86',
		name:LANG.CONTRY.CHINA,
		key:'Z',
	},
];

exports.sms = {
	init:function(ok,fail){
		cordova.exec(ok, fail, 'mobsdk', 'smsInit', []);
	},
	getSupportedCountries:function(ok,fail){
		if(countries){
			if(ok){
				ok(countries);
			}
			return;
		}
		cordova.exec(function(data){
			if(ok){
				ok(data);
			}
			countries = data;
		}, fail, 'mobsdk', 'smsGetSupportedCountries', []);
	},
	getVerificationCode:function(contryCode,phone,ok,fail){
		
		cordova.exec(ok, fail, 'mobsdk', 'smsGetVerificationCode', [contryCode,phone]);
	},
	submitVerificationCode:function(contryCode,phone,verifyCode,ok,fail){
		cordova.exec(ok, fail, 'mobsdk', 'smsSubmitVerificationCode', [contryCode,phone,verifyCode]);
	}
}
