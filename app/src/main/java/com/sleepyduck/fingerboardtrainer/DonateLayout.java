package com.sleepyduck.fingerboardtrainer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DonateLayout extends FrameLayout {
    public DonateLayout(Context context) {
        super(context);
    }

    public DonateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DonateLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setHasDonated(boolean hasDonated) {
        findViewById(R.id.donate_basic_container).setVisibility(hasDonated ? GONE : VISIBLE);
        findViewById(R.id.donate_repeat_container).setVisibility(hasDonated ? VISIBLE : GONE);
    }

    public void setupDonationData(BillingManager billingManager) {
        for (BillingManager.BillingItem item : billingManager.getBillingItems()) {
            String code = item.price_currency_code;
            int price = item.price_amount_micros;
            float priceF = ((float) (price)) / 1000000.f;
            String priceS = "";
            if (priceF == (int) priceF) {
                priceS += String.format("%d", (int) priceF);
            } else {
                priceS += String.format("%.2f", priceF);
            }
            priceS += " " + code;
            switch (item.productId) {
                case "donate_1":
                    ((TextView) findViewById(R.id.small_donation_text)).setText(priceS);
                    break;
                case "donate_2":
                    ((TextView) findViewById(R.id.medium_donation_text)).setText(priceS);
                    break;
                case "donate_3":
                    ((TextView) findViewById(R.id.large_donation_text)).setText(priceS);
                    break;
                case "donate_repeat":
                    ((TextView) findViewById(R.id.repeat_donation_text)).setText(priceS);
                    break;
            }
        }
    }
}
