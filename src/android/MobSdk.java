package com.weforpay.mobsdk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class MobSdk extends CordovaPlugin {
	final String TAG = "MobSdk";
	class Country {
		String Name = "";
		String Code = "";
		String Char = "";
		boolean isSupport = false;
	}
	boolean mInited = false;
	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		if ( action.equalsIgnoreCase("smsInit") ) {
			if(mInited){
				callbackContext.success();
				return true;
			}
			mInited = true;
            this.smsInit(args, callbackContext);
            return true;
        }else if ( action.equalsIgnoreCase("smsGetSupportedCountries") ) {
            this.smsGetSupportedCountries(args, callbackContext);
            return true;
        }else if ( action.equalsIgnoreCase("smsGetVerificationCode") ) {
            this.smsGetVerificationCode(args, callbackContext);
            return true;
        }else if ( action.equalsIgnoreCase("smsSubmitVerificationCode") ) {
            this.smsSubmitVerificationCode(args, callbackContext);
            return true;
        }
		return super.execute(action, args, callbackContext);
	}
	
	CallbackContext mGetSupportedCountriesCallback = null;
	CallbackContext mGetVerificationCodeCallback = null;
	CallbackContext mSubmitVerificationCodeCallback = null;
	Context mContext = null;
	Handler mCordovaHander  = null;
	JSONArray mSupportedCountrys = null;
	void smsInit(JSONArray args,
			CallbackContext callbackContext){
		mContext = this.cordova.getActivity().getApplicationContext();
		mCordovaHander = new Handler();
		
		SMSSDK.initSDK(this.cordova.getActivity().getApplicationContext(), "1331dbecb3d72",
				"12e4d3430943e987fff7136316c7ea27");
		
		EventHandler eh = new EventHandler() {

			@Override
			public void afterEvent(int event, int result, Object data) {

				if (result == SMSSDK.RESULT_COMPLETE) {

					if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
						if(mSubmitVerificationCodeCallback != null){
							mSubmitVerificationCodeCallback.success();
							mSubmitVerificationCodeCallback = null;
						}
						Log.d(TAG, "EVENT_SUBMIT_VERIFICATION_CODE");
						Log.d(TAG,data.toString());						
					} else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
						if(mGetVerificationCodeCallback != null){
							mGetVerificationCodeCallback.success();
							mGetVerificationCodeCallback = null;
						}
						Log.d(TAG, "EVENT_GET_VERIFICATION_CODE");
						Log.d(TAG,data.toString());
						
					} else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {						
						HashMap<Character, ArrayList<String[]>> cMap = SMSSDK
								.getGroupedCountryList();
						HashMap<String,Country> countryMap = new HashMap<String,Country>();
						ArrayList<Country> countrySortedArray = new ArrayList<Country>();
						Set<Character> keys = cMap.keySet();
						for(Character key : keys){
							ArrayList<String[]> al = cMap.get(key);
							for(String[] l : al){
								Country c = new Country();
								c.Char = key+"";
								c.Code = l[1];
								c.Name = l[0];
								countryMap.put(c.Code, c);	
								countrySortedArray.add(c);
							}
						}
						
						
						
						ArrayList<HashMap<String,Object>> ahashMap = (ArrayList<HashMap<String, Object>>) data;
						for (HashMap<String, Object> country : ahashMap) {
							String code = (String) country.get("zone");
							String rule = (String) country.get("rule");
							if (TextUtils.isEmpty(code) || TextUtils.isEmpty(rule)) {
								continue;
							}
							Country c = countryMap.get(code);
							c.isSupport = true;
						}
						
						mSupportedCountrys = new JSONArray();
						for(Country c : countrySortedArray){
							JSONObject jo = new JSONObject();
							try{
								jo.put("key",c.Char);
								jo.put("code", c.Code);
								jo.put("name", c.Name);
								mSupportedCountrys.put(jo);
							}catch(Exception e){
								
							}
							
						}
						mCordovaHander.post(new Runnable(){

							@Override
							public void run() {
								if(mGetSupportedCountriesCallback != null){									
									mGetSupportedCountriesCallback.success(mSupportedCountrys);
									mGetSupportedCountriesCallback = null;
								}
							}
							
						});
					}
				} else {
					if(mSubmitVerificationCodeCallback != null){
						mSubmitVerificationCodeCallback.error(0);
						mSubmitVerificationCodeCallback = null;
					}
					
					if(mGetSupportedCountriesCallback != null){
						mGetSupportedCountriesCallback.error(0);
						mGetSupportedCountriesCallback = null;
					}
					((Throwable) data).printStackTrace();
				}
			}
		};
		SMSSDK.registerEventHandler(eh);
	}
	void smsGetSupportedCountries(JSONArray args,
			CallbackContext callbackContext){	
		mGetSupportedCountriesCallback = callbackContext;
		if(mSupportedCountrys != null){
			if(mGetSupportedCountriesCallback != null){
				mGetSupportedCountriesCallback.success(mSupportedCountrys);
				mGetSupportedCountriesCallback = null;
			}
			return ;
		}
		SMSSDK.getSupportedCountries();
	
	}
	void smsGetVerificationCode(JSONArray args,
			CallbackContext callbackContext) throws JSONException{	
		String code = args.getString(0);
		String phone = args.getString(1);
		mGetVerificationCodeCallback = callbackContext;
		SMSSDK.getVerificationCode(code, phone);
		
	}
	void smsSubmitVerificationCode(JSONArray args,
			CallbackContext callbackContext) throws JSONException{	
		String countryCode = args.getString(0);
		String phone = args.getString(1);
		String verifyCode = args.getString(2);
		mSubmitVerificationCodeCallback = callbackContext;
		SMSSDK.submitVerificationCode(countryCode, phone, verifyCode);
		
	}
}
