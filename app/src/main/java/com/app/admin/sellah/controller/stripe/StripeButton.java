package com.app.admin.sellah.controller.stripe;

import com.app.admin.sellah.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class StripeButton extends androidx.appcompat.widget.AppCompatTextView {
	
	private StripeApp mStripeApp;
	private Context mContext;
	private StripeConnectListener mStripeConnectListener;
	private StripeApp.CONNECT_MODE mConnectMode = StripeApp.CONNECT_MODE.DIALOG;

	public StripeButton(Context context) {
		super(context);
		mContext = context;
		setupButton();
	}

	public StripeButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		setupButton();
	}

	public StripeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		setupButton();
	}

	private void setupButton() {
		
		if(mStripeApp == null) {
			setButtonText(R.string.btnConnectText);
		}
		
		setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
		
		setClickable(true);

		setPadding(3,0,3,0);
		setGravity(Gravity.CENTER);
		setTextColor(getResources().getColor(R.color.colorRed));
		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if(mStripeApp == null) {
					Toast.makeText(mContext, 
							"StripeApp obect needed. Call StripeButton.setStripeApp()", 
							Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(mStripeApp.isConnected()) {


                    if(mConnectMode == StripeApp.CONNECT_MODE.DIALOG) {
                        mStripeApp.displayDialog();
                    }
                    else {
                        Activity parent = (Activity) mContext;
                        Intent i = new Intent(getContext(), StripeActivity.class);
                        i.putExtra("url", mStripeApp.getAuthUrl());
                        i.putExtra("callbackUrl", mStripeApp.getCallbackUrl());
                        i.putExtra("tokenUrl", mStripeApp.getTokenUrl());
                        i.putExtra("secretKey", mStripeApp.getSecretKey());
                        i.putExtra("accountName", mStripeApp.getAccountName());
                        parent.startActivityForResult(i, StripeApp.STRIPE_CONNECT_REQUEST_CODE);
                    }


				}
				else {
					if(mConnectMode == StripeApp.CONNECT_MODE.DIALOG) {
						mStripeApp.displayDialog();
					}
					else {
						Activity parent = (Activity) mContext;
						Intent i = new Intent(getContext(), StripeActivity.class);
						i.putExtra("url", mStripeApp.getAuthUrl());
						i.putExtra("callbackUrl", mStripeApp.getCallbackUrl());
						i.putExtra("tokenUrl", mStripeApp.getTokenUrl());
						i.putExtra("secretKey", mStripeApp.getSecretKey());
						i.putExtra("accountName", mStripeApp.getAccountName());
						parent.startActivityForResult(i, StripeApp.STRIPE_CONNECT_REQUEST_CODE);
					}
				}

			}
			
		});
		
	}
	
	private void setButtonText(int resourceId) {
		setText(resourceId);
	}
	
	/**
	 * 
	 * @param connectMode
	 */
	public void setConnectMode(StripeApp.CONNECT_MODE connectMode) {
		mConnectMode = connectMode;
	}
	
	/**
	 * 
	 * @param stripeApp
	 */
	public void setStripeApp(StripeApp stripeApp) {
		mStripeApp = stripeApp;
		mStripeApp.setListener(getOAuthAuthenticationListener());
		
		if(mStripeApp.isConnected()) {
			setButtonText(R.string.btnDisconnectText);
		}
		else {
			setButtonText(R.string.btnConnectText);
		}
	}
	

	/**
	 * 
	 * @param stripeConnectListener
	 */
	public void addStripeConnectListener(StripeConnectListener stripeConnectListener) {
		mStripeConnectListener = stripeConnectListener;
		if(mStripeApp != null) {
			mStripeApp.setListener(getOAuthAuthenticationListener());
		}
	}
	
	private StripeApp.OAuthAuthenticationListener getOAuthAuthenticationListener() {
		
		return new StripeApp.OAuthAuthenticationListener() {

			@Override
			public void onSuccess() {
				Log.d("StripeButton", "Calling OAuthAuthenticationListener.onSuccess()");
				if(mStripeConnectListener != null) {
					if(mStripeApp.isConnected()) {
						Log.d("StripeButton", "Connected");
						setButtonText(R.string.btnDisconnectText);
						Log.d("StripeButton", "Calling mStripeConnectListener.onConnected()");
						mStripeConnectListener.onConnected();
					}
					else {
						Log.d("StripeButton", "Disconnected");
						Log.d("StripeButton", "Calling mStripeConnectListener.onDisconnected()");
						setButtonText(R.string.btnConnectText);
						mStripeConnectListener.onDisconnected();
					}
				}
				else {
					Log.d("StripeButton", "mStripeConnectListener is null");
				}
			}

			@Override
			public void onFail(String error) {
				Log.i("StripeButton", "Calling OAuthAuthenticationListener.onFail()");
				if(mStripeConnectListener != null) {
					mStripeConnectListener.onError(error);
				}
			}
		};
	}
	

}
