package com.app.admin.sellah.view.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.app.admin.sellah.R;
import com.app.admin.sellah.controller.WebServices.ApisHelper;
import com.app.admin.sellah.controller.utils.Global;
import com.app.admin.sellah.controller.utils.MainActivityInterface;
import com.app.admin.sellah.controller.utils.HelperPreferences;
import com.app.admin.sellah.model.extra.Notification.NotificationModel;
import com.google.android.gms.common.api.GoogleApiClient;

import static com.app.admin.sellah.controller.utils.Global.getUser.isLogined;
import static com.app.admin.sellah.controller.utils.PermissionCheckUtil.PERMISSION_REQUEST_CODE;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.DEEP_LINKING;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.UID;
import static com.app.admin.sellah.controller.utils.Global.makeTransperantStatusBar;
import static com.app.admin.sellah.controller.utils.SAConstants.NotificationKeys.NT_ACCEPT_REJECT;
import static com.app.admin.sellah.controller.utils.SAConstants.NotificationKeys.NT_CHAT;
import static com.app.admin.sellah.controller.utils.SAConstants.NotificationKeys.NT_COMMENT_ADDED;
import static com.app.admin.sellah.controller.utils.SAConstants.NotificationKeys.NT_DATA;
import static com.app.admin.sellah.controller.utils.SAConstants.NotificationKeys.NT_FOLLOW;
import static com.app.admin.sellah.controller.utils.SAConstants.NotificationKeys.NT_OFFER_MAKE;
import static com.app.admin.sellah.controller.utils.SAConstants.NotificationKeys.NT_PAYMENT;
import static com.app.admin.sellah.controller.utils.SAConstants.NotificationKeys.NT_PRODUCT_ADDED;

public class SplashScreen extends AppCompatActivity {
    //Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;
    ImageView imageView;
    SharedPreferences sharedPreferences;
    private String user_id;
    Animation animation;
    MainActivityInterface mainActivityInterface;
    private String TAG = SplashScreen.class.getSimpleName();
    SharedPreferences mPrefs;
    final String welcomeScreenShownPref = "welcomeScreenShown";
    Boolean welcomeScreenShown;
    GoogleApiClient apiClient;
    public String product_id = "", product_type = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeTransperantStatusBar(this, true);
        setContentView(R.layout.splash_screen);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        welcomeScreenShown = mPrefs.getBoolean(welcomeScreenShownPref, false);



        getCategories();
        imageView = (ImageView) findViewById(R.id.splashLogo);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadeon);

        user_id = HelperPreferences.get(this).getString(UID);

        imageView.setAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    private void getCategories() {

        new ApisHelper().getCategories("", new ApisHelper.GetCategoryCallback() {
            @Override
            public void onGetCategorySuccess() {

                launchActivityHandler();
            }

            @Override
            public void onGetCategoryFailure() {
                getCategories();
            }
        });

    }



    @SuppressLint("LongLogTag")
    private void handleNotificationData() {

        try {

            Intent notificationIntent = getIntent();
            NotificationModel message;

            Bundle bundle = notificationIntent.getExtras();

            if (bundle != null) {

              //  Log.e("splashBundle", "hasData");
                message = bundle.getParcelable(NT_DATA);
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.frameLayout);

                if (message != null) {
                    Intent resultIntent;
                 //   Log.e("splashBundle", message.getNotiType() + "");

                    switch (message.getNotiType()) {

                        case NT_ACCEPT_REJECT:
                            resultIntent = new Intent(getApplicationContext(), ChatActivity.class);
                            resultIntent.putExtra("otherUserId", message.getOtherUserId());
                            resultIntent.putExtra("otherUserImage", message.getUserimage());
                            resultIntent.putExtra("otherUserName", message.getUsername());
                            break;
                        case NT_FOLLOW:
                            resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                            resultIntent.putExtras(bundle);
                            break;
                        case NT_COMMENT_ADDED:
                            resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                            resultIntent.putExtras(bundle);
                            break;
                        case NT_PRODUCT_ADDED:
                            resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                            resultIntent.putExtras(bundle);
                            break;
                        case NT_CHAT:
                            resultIntent = new Intent(getApplicationContext(), ChatActivity.class);
                            resultIntent.putExtra("otherUserId", message.getOtherUserId());
                            resultIntent.putExtra("otherUserImage", message.getUserimage());
                            resultIntent.putExtra("otherUserName", message.getUsername());
                            break;
                        case NT_PAYMENT:
                            resultIntent = new Intent(getApplicationContext(), ChatActivity.class);
                            resultIntent.putExtra("otherUserId", message.getOtherUserId());
                            resultIntent.putExtra("otherUserImage", message.getUserimage());
                            resultIntent.putExtra("otherUserName", message.getUsername());
                            break;
                        case NT_OFFER_MAKE:
                            resultIntent = new Intent(getApplicationContext(), ChatActivity.class);
                            resultIntent.putExtra("otherUserId", message.getOtherUserId());
                            resultIntent.putExtra("otherUserImage", message.getUserimage());
                            resultIntent.putExtra("otherUserName", message.getUsername());
                            break;
                        default:
                            resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                            resultIntent.putExtras(bundle);
                            break;
                    }
               //     Log.e("NotificationIntentData", ": Splash" + message.getMessage());
                    startActivity(resultIntent);

                } else {
               //     Log.e("NotificationIntentData", ": Splash No data found");
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                }

            } else {
                Log.e("splashBundle", "noData");
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();


              //  Log.e("NotificationIntentData1", ": Splash No data found");
            }

        } catch (Exception e) {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish();

         //   Log.e("NotificationIntentFailure", ": Splash" + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {

        switch (permsRequestCode) {

            case PERMISSION_REQUEST_CODE:



                launchActivityHandler();


                break;

        }

    }

    private void launchActivityHandler() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                if (!welcomeScreenShown) {

                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putBoolean(welcomeScreenShownPref, true);
                    editor.commit(); // Very important to save the preference
                    Intent intent = new Intent(SplashScreen.this, WelcomeActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    //----------deep linking data-----------------------
                    Intent in = getIntent();
                    Uri data = in.getData();
                    if (data != null) {
                     //   Log.e("valuePrintFired", "deepLinking");
                        String action = in.getAction();
                        String data_ = in.getDataString();

                        if (Intent.ACTION_VIEW.equals(action) && data != null) {
                        }

                        try {

                            product_id = "";
                            product_type = "";

                            String[] val = data_.split("/");
                            int len = val.length;
                            if (val.length > 1) {
                                product_id = val[len - 1];
                                product_type = val[len - 2];
                            }

                            Global.DEEP_LINKING_STATUS = "enable";
                            Global.DEEP_LINKING_PRODUCT_ID = product_id;
                            Global.DEEP_LINKING_PRODUCT_TYPE = product_type;

                            //---------not sign in check-----------
                            if (isLogined(SplashScreen.this)) {

                                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                                intent.putExtra(DEEP_LINKING, "deeplink");
                                intent.putExtra("product_id", product_id);
                                intent.putExtra("product_type", product_type);
                                startActivity(intent);
                                finish();

                            } else {


                                new MaterialDialog.Builder(SplashScreen.this)
                                        .content("You are not currently signed In. Kindly sign in to proceed.")
                                        .positiveText(SplashScreen.this.getString(R.string.dialog_action_continue))
                                        .negativeText(SplashScreen.this.getString(R.string.dialog_action_cancel))
                                        .onPositive((dialog, which) -> {
                                            HelperPreferences.get(SplashScreen.this).clear();

                                            Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                                            intent.putExtra(DEEP_LINKING, "deeplink");
                                            intent.putExtra("product_id", product_id);
                                            intent.putExtra("product_type", product_type);
                                            startActivity(intent);
                                            finish();

                                        })
                                        .onNegative((dialog, which) ->
                                                {
                                                    dialog.dismiss();
                                                    finish();
                                                }

                                        ).build().show();

                            }

                        } catch (Exception e) {
                        }

                    } else {
                   //     Log.e("valuePrintFired", "notification");
                        Global.DEEP_LINKING_STATUS = "disable";
                        Global.DEEP_LINKING_PRODUCT_ID = "";
                        Global.DEEP_LINKING_PRODUCT_TYPE = "";

                        handleNotificationData();
                    }

                }

            }

        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new ApisHelper().getcategoriesmodel1_cancel();
    }

}
