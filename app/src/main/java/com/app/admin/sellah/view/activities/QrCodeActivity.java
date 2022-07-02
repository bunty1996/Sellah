package com.app.admin.sellah.view.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.app.admin.sellah.view.fragments.PaymentFragment;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QrCodeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PaymentFragment.qr_scan_id="";

        scanBarcodeCustomLayout();


    }



    public void scanBarcodeCustomLayout() {
        IntentIntegrator integrator = new IntentIntegrator(this);

        integrator.setPrompt("Scanning");
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {

              //  Log.e("ResultPrint: ","Null");
                finish();
            } else {

             //   Log.e("ResultPrint: ",result.getContents());
                PaymentFragment.qr_scan_id = result.getContents();
                finish();
            }

        }
    }

}
