var exec    = require('cordova/exec');


var countries = [
	{
		code:'86',
		name:LANG.CONTRY.CHINA,
		key:'Z',
	},
	{
		code:'49',
		name:LANG.CONTRY.GERMANY,
		key:'D',
	},
	{
		code:'52',
		name:LANG.CONTRY.MEXICO,
		key:'M',
	},
	{
		code:'46',
		name:LANG.CONTRY.SWEDEN,
		key:'R',
	},
	{
		code:'62',
		name:LANG.CONTRY.INDONESIA,
		key:'Y',
	},
	{
		code:'44',
		name:LANG.CONTRY.BRITAIN,
		key:'Y',
	},
	{
		code:'1',
		name:LANG.CONTRY.USA,
		key:'M',
	},
	{
		code:'1',
		name:LANG.CONTRY.CANADA,
		key:'J',
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
