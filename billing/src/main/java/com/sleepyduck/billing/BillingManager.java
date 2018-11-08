package com.sleepyduck.billing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public interface BillingManager {

    interface OnSetupCompleteListener {
        void onSetupComplete();
    }

    class Factory {
        public static BillingManager getInstance(@NonNull Activity activity, @NonNull OnSetupCompleteListener listener) {
            return new BillingManagerImplementation(activity, listener);
        }
    }

    void destroy(@NonNull Context context);

    boolean hasDonated();

    void purchase(@NonNull Activity activity, @NonNull String productId, int requestCode);

    void consumePurchase(@NonNull Activity activity, @NonNull Intent data);

    @NonNull
    ArrayList<BillingItem> getBillingItems();

    class BillingItem {
        public String price_currency_code;
        public String productId;
        public int price_amount_micros;

        String type;
        String price;
        String title;
        String description;

        BillingItem(String jsonString) {
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
