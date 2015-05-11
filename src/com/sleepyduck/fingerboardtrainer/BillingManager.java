package com.sleepyduck.fingerboardtrainer;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;

import com.android.vending.billing.IInAppBillingService;

public class BillingManager {
	private IInAppBillingService mBillingService;
	private MainActivity mActivity;
	private ArrayList<BillingItem> mBillingItems = new ArrayList<BillingManager.BillingItem>();
	private boolean mHasDonated = false;

	public BillingManager(MainActivity activity, IInAppBillingService service) {
		mActivity = activity;
		mBillingService = service;
	}

	public void handleBillingConnectionOperations() {
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				if (checkInAppBillingSupport()) {
					if (hasInAppItems()) {
						getPurchasedItems();
						return true;
					}
				} else {
					Log.d("In app billing V3 is not supported");
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
				Log.d("In app billing V3 support = " + billingSupport);
				return billingSupport == 0;
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private boolean hasInAppItems() {
		Bundle skuDetails = getInAppItems();
		int response = skuDetails.getInt("RESPONSE_CODE");
		Log.d("skuBundle result: " + response);
		if (response == 0) {
			ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
			Log.d("responseList: " + responseList);
			for (String responseItem : responseList) {
				mBillingItems.add(new BillingItem(responseItem));
			}
			return mBillingItems.size() > 0;
		}
		return false;
	}

	private void getPurchasedItems() {
		try {
			Bundle ownedItems = mBillingService.getPurchases(3, mActivity.getPackageName(), "inapp", null);
			int response = ownedItems.getInt("RESPONSE_CODE");
			if (response == 0) {
				ArrayList<String> ownedSkus = ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
				setHasDonated(ownedSkus.size() > 0);
				Log.d("User has " + (hasDonated() ? "" : "not ") + "donated");
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
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

	public boolean hasDonated() {
		return mHasDonated;
	}

	public void setHasDonated(boolean b) {
		mHasDonated = b;
	}

	public ArrayList<BillingItem> getBillingItems() {
		return mBillingItems;
	}

	static class BillingItem {
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
}
