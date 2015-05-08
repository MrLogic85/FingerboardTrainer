package com.sleepyduck.fingerboardtrainer;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.android.vending.billing.IInAppBillingService;

public class BillingManager {
	public static final String TAG = "fingerboardtrainer";
	private IInAppBillingService mBillingService;
	private MainActivity mActivity;
	private ArrayList<BillingItem> mBillingItems = new ArrayList<BillingManager.BillingItem>();

	public BillingManager(MainActivity activity, IInAppBillingService service) {
		mActivity = activity;
		mBillingService = service;
	}

	public void handleBillingConnectionOperations() {
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				if (checkInAppBillingSupport()) {
					Bundle skuDetails = getInAppItems();
					int response = skuDetails.getInt("RESPONSE_CODE");
					Log.d(TAG, "skuBundle result: " + response);
					if (response == 0) {
						ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
						Log.d(TAG, "responseList: " + responseList);
						for (String responseItem : responseList) {
							mBillingItems.add(new BillingItem(responseItem));
						}
						return true;
					}
				} else {
					Log.d(TAG, "In app billing V3 is not supported");
				}
				return false;
			}

			protected void onPostExecute(Boolean result) {
				if (result) {
					mActivity.activateInAppPurchases();
				}
			};
		}.execute();
	}

	private boolean checkInAppBillingSupport() {
		if (mBillingService != null) {
			try {
				int billingSupport = mBillingService.isBillingSupported(3, mActivity.getPackageName(), "inapp");
				Log.d(TAG, "In app billing V3 support = " + billingSupport);
				return billingSupport == 0;
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private Bundle getInAppItems() {
		ArrayList<String> skuList = new ArrayList<String>();
		skuList.add("donate_1");
		Bundle querySkus = new Bundle();
		querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
		try {
			return mBillingService.getSkuDetails(3, mActivity.getPackageName(), "inapp", querySkus);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return new Bundle();
	}

	class BillingItem {
		String productId;
		String type;
		String price;
		int price_amount_micros;
		String price_currency_code;
		String title;
		String description;

		public BillingItem(String jsonString) {
			JSONObject object;
			try {
				object = new JSONObject(jsonString);
				productId = object.getString("productId");
				type = object.getString("type");
				price = object.getString("price");
				price_amount_micros = object.getInt("price_amount_micros");
				price_currency_code = object.getString("price_currency_code");
				title = object.getString("title");
				description = object.getString("description");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public ArrayList<BillingItem> getBillingItems() {
		return mBillingItems;
	}
}
