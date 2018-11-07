
package se.sleepyduckstudio.billing;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import timber.log.Timber;

class BillingManagerImplementation implements BillingManager {

    private IInAppBillingService mBillingService;

    private ArrayList<BillingItem> mBillingItems = new ArrayList<>();

    private boolean mHasDonated = false;

    private String mPackageName;

    @NonNull
    private OnSetupCompleteListener mSetupCompleteListener;

    private ServiceConnection mBillingServiceConn = new ServiceConnection() {
        @Override
        public void onBindingDied(ComponentName name) {
            Timber.d("Binding died");
        }

        @Override
        public void onNullBinding(ComponentName name) {
            Timber.d("Null Binding");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBillingService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBillingService = IInAppBillingService.Stub.asInterface(service);
            handleBillingConnectionOperations(BillingManagerImplementation.this);
        }
    };

    BillingManagerImplementation(@NonNull Activity activity, @NonNull OnSetupCompleteListener listener) {
        mPackageName = activity.getPackageName();
        mSetupCompleteListener = listener;
        Intent billingServiceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        billingServiceIntent.setPackage("com.android.vending");
        activity.bindService(billingServiceIntent, mBillingServiceConn, Context.BIND_AUTO_CREATE);
    }

    private static void handleBillingConnectionOperations(final BillingManagerImplementation billingManager) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                if (billingManager.checkInAppBillingSupport()) {
                    if (billingManager.hasInAppItems()) {
                        billingManager.getPurchasedItems();
                        return true;
                    }
                } else {
                    Timber.d("In app billing V3 is not supported");
                }
                return false;
            }

            protected void onPostExecute(Boolean result) {
                if (result) {
                    billingManager.mSetupCompleteListener.onSetupComplete();
                }
            }

            ;
        }.execute();
    }

    private boolean checkInAppBillingSupport() {
        if (mBillingService != null) {
            try {
                int billingSupport = mBillingService.isBillingSupported(3, mPackageName, "inapp");
                Timber.d("In app billing V3 support = %s", billingSupport);
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
        Timber.d("skuBundle result: %s", response);
        if (response == 0) {
            ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
            Timber.d("responseList: %s", responseList);
            for (String responseItem : responseList) {
                mBillingItems.add(new BillingItem(responseItem));
            }
            return mBillingItems.size() > 0;
        }
        return false;
    }

    private void getPurchasedItems() {
        try {
            Bundle ownedItems = mBillingService.getPurchases(3, mPackageName,
                    "inapp", null);
            int response = ownedItems.getInt("RESPONSE_CODE");
            if (response == 0) {
                ArrayList<String> ownedSkus = ownedItems
                        .getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
                setHasDonated(ownedSkus.size() > 0);
                Timber.d("User has %sdonated", (hasDonated() ? "" : "not "));
                Timber.d("User had bought: %s", ownedSkus);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private Bundle getInAppItems() {
        ArrayList<String> skuList = new ArrayList<String>();
        skuList.add("donate_1");
        skuList.add("donate_2");
        skuList.add("donate_3");
        skuList.add("donate_repeat");
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
        try {
            return mBillingService.getSkuDetails(3, mPackageName, "inapp", querySkus);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return new Bundle();
    }

    @Override
    public boolean hasDonated() {
        return mHasDonated;
    }

    private void setHasDonated(boolean b) {
        mHasDonated = b;
    }

    @NonNull
    @Override
    public ArrayList<BillingItem> getBillingItems() {
        return mBillingItems;
    }

    @Override
    public void destroy(@NonNull Context context) {
        if (mBillingService != null) {
            context.unbindService(mBillingServiceConn);
        }
    }

    @Override
    public void purchase(@NonNull Activity activity, @NonNull String productId, int requestCode) {
        try {
            Bundle buyIntentBundle = mBillingService.getBuyIntent(3, mPackageName, productId, "inapp", "");
            if (buyIntentBundle.getInt("RESPONSE_CODE") == 7) {

                if (productId.equals("donate_repeat")) {
                    String purchaseToken = "inapp:" + mPackageName + ":" + productId;

                    try {
                        mBillingService.consumePurchase(3, mPackageName, purchaseToken);
                        buyIntentBundle = mBillingService.getBuyIntent(3, mPackageName, productId, "inapp", "");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(activity, "There was an error trying to process your request, please contact the developer.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
            activity.startIntentSenderForResult(pendingIntent.getIntentSender(), requestCode, new Intent(), 0, 0, 0);
        } catch (RemoteException | IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void consumePurchase(@NonNull Activity activity, @NonNull Intent data) {
        int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
        String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
        try {
            if (responseCode == 0) {
                JSONObject jo = new JSONObject(purchaseData);
                Timber.d("JSON RESULT: %s", jo.toString());
                String productId = jo.has("productId") ? jo.getString("productId") : null;

                if (productId != null && (productId.equals("donate_repeat"))) {
                    String purchaseToken = "inapp:" + activity.getPackageName() + ":" + productId;

                    try {
                        mBillingService.consumePurchase(3, activity.getPackageName(), purchaseToken);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                } else {
                    setHasDonated(true);
                    activity.invalidateOptionsMenu();
                }
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Toast.makeText(activity, "Failed to process donation.", Toast.LENGTH_LONG).show();
    }

}
