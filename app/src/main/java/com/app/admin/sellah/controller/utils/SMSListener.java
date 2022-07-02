package com.app.admin.sellah.controller.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSListener extends BroadcastReceiver {

    private static OTPListener mListener; // this listener will do the magic of throwing the extracted OTP to all the bound views.
    String abcd,xyz;
    SMSListener smsListener;
    @Override
    public void onReceive(Context context, Intent intent) {

    }

    public static void bindListener(OTPListener listener) {
        mListener = listener;
    }

    public static void unbindListener() {
        mListener = null;
    }
}