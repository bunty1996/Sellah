package com.app.admin.sellah.view.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.admin.sellah.R;
import com.app.admin.sellah.controller.WebServices.WebService;
import com.app.admin.sellah.controller.utils.DeviceRegistaration;
import com.app.admin.sellah.controller.utils.Global;
import com.app.admin.sellah.controller.utils.HelperPreferences;
import com.app.admin.sellah.model.extra.LoginPojo.Result;
import com.app.admin.sellah.view.CustomDialogs.OTPVerificationDialog;
import com.app.admin.sellah.view.CustomDialogs.S_Dialogs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.admin.sellah.controller.WebServices.ApisHelper.auth;
import static com.app.admin.sellah.controller.WebServices.ApisHelper.token;
import static com.app.admin.sellah.controller.stripe.StripeSession.API_ACCESS_TOKEN;
import static com.app.admin.sellah.controller.stripe.StripeSession.STRIPE_VERIFIED;
import static com.app.admin.sellah.controller.utils.Global.deleted_account;
import static com.app.admin.sellah.controller.utils.Global.makeLinks;
import static com.app.admin.sellah.controller.utils.Global.setStatusBarColor;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.ADVERTISEMENTSTATUS;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.DEEP_LINKING;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.PROFILESTATUS;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.UID;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.img_logo)
    ImageView imgLogo;
    @BindView(R.id.txt_killer_quote)
    TextView txtKillerQuote;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.signUp_relative)
    RelativeLayout signUpRelative;
    @BindView(R.id.txt_header)
    TextView txtHeader;
    @BindView(R.id.t_email)
    TextView tEmail;
    @BindView(R.id.t_pass)
    TextView tPass;
    @BindView(R.id.text)
    TextView text;

    @BindView(R.id.termsConditionPolicy)
    TextView termsConditionPolicy;

    private String TAG = LoginActivity.class.getSimpleName();
    @BindView(R.id.etLogin_email)
    EditText loginEmail;
    @BindView(R.id.etLogin_pass)
    EditText loginPass;
    @BindView(R.id.signIn)
    Button loginSignIn;
    @BindView(R.id.signUp)
    TextView loginSignup;
    @BindView(R.id.forgotPass)
    TextView forgot;
    @BindView(R.id.root)
    RelativeLayout rel_root;
    WebService webService;

    String product_id = "", product_type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_signin_new);
        setStatusBarColor(this, R.color.colorFormBg);
        ButterKnife.bind(LoginActivity.this);// view ids injection with ButterKnife.
        webService = Global.WebServiceConstants.getRetrofitinstance();// Get retrofit instance.

        //------redirect deep link-login-specific item----------
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(DEEP_LINKING)) {
            product_id = intent.getStringExtra("product_id");
            product_type = intent.getStringExtra("product_type");
        } else {
            product_id = "";
            product_type = "";
        }

        //----------------------------------------------------

        forgot.setLinkTextColor(Color.BLACK); // default link color for clickable span, we can also set it in xml by android:textColorLink=""
        ClickableSpan normalLinkClickSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }

        };

        makeLinks(forgot, new String[]{
                "I forgot my password?"
        }, new ClickableSpan[]{
                normalLinkClickSpan
        });

        loginSignup.setLinkTextColor(Color.BLACK); // default link color for clickable span, we can also set it in xml by android:textColorLink=""
        ClickableSpan loginSignUp = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                if (!product_id.equalsIgnoreCase("") && !product_type.equalsIgnoreCase("")) {
                    intent.putExtra(DEEP_LINKING, "deeplink");
                    intent.putExtra("product_id", product_id);
                    intent.putExtra("product_type", product_type);
                }
                startActivity(intent);
            }

        };

        makeLinks(loginSignup, new String[]{
                "Register"
        }, new ClickableSpan[]{
                loginSignUp
        });

        //todo for clickable span
        SpannableString ss = new SpannableString(getResources().getString(R.string.terms_cond));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent privacy = new Intent(LoginActivity.this, TermsWebView.class);
                privacy.putExtra("str", "1");
                startActivity(privacy);

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {

                Intent privacy = new Intent(LoginActivity.this, TermsWebView.class);
                privacy.putExtra("str", "2");
                startActivity(privacy);

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };


        ss.setSpan(clickableSpan, 0, 20, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan2, 22, 37, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        termsConditionPolicy.setText(ss);
        termsConditionPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        termsConditionPolicy.setHighlightColor(Color.TRANSPARENT);

    }

    @OnClick({R.id.signIn})
    void signinClick() {
        if (getText(loginEmail).equalsIgnoreCase("")) {
            Snackbar.make(rel_root, "Please Enter E-mail", Snackbar.LENGTH_SHORT)
                    .setAction("", null).show();

        } else if (getText(loginPass).equals("")) {
            Snackbar.make(rel_root, "Please Enter Password ", Snackbar.LENGTH_SHORT)
                    .setAction("", null).show();


        } else if ((!Patterns.EMAIL_ADDRESS.matcher(getText(loginEmail)).matches())) {
            Snackbar.make(rel_root, "Invalid E-mail Address", Snackbar.LENGTH_SHORT)
                    .setAction("", null).show();

        }
        else if (Patterns.EMAIL_ADDRESS.matcher(getText(loginEmail)).matches() && getText(loginPass).length() > 0) {


            Dialog dialog = S_Dialogs.getLoadingDialog(this);
            dialog.show();

            Call<ResponseBody> loginCall = webService.loginApi(getText(loginEmail), getText(loginPass));
            loginCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (response.isSuccessful()) {

                        try {

                            String res = response.body().string();
                        //    Log.e("logindata123", res + "//");
                            JSONObject jsonObject = new JSONObject(res);

                            if (jsonObject.optString("status").equalsIgnoreCase("1")) {

                                JSONObject object = jsonObject.optJSONObject("result");

                                Result result = new Result();
                                result.setUserId(object.optString("user_id"));
                                result.setEmail(object.optString("email"));
                                result.setCountryCode(object.optString("country_code"));
                                result.setIsDeleted(object.optString("is_deleted"));
                                result.setIsProfileCompleted(object.optString("is_profile_completed"));
                                result.setIsVerified(object.optString("is_verified"));
                                result.setPhoneNumber(object.optString("phone_number"));
                                result.setStripe_id(object.optString("stripe_id"));
                                result.setStripe_verified(object.optString("stripe_verified"));

                                Snackbar.make(rel_root, jsonObject.optString("message"), Snackbar.LENGTH_SHORT).setAction("", null).show();

                                if (result.getIsDeleted().equalsIgnoreCase("Y")) {

                                    S_Dialogs.getLiveVideoStopedDialog(LoginActivity.this, "You have deleted your account. \n" +
                                            "To recover your account. \n" + "Please verify your phone number.", ((dialog, which) -> {
                                        //--------------openHere-----------------

                                        otpsendapi(result);
                                        deleted_account = true;

                                    })).show();

                                } else if (result.getIsVerified().equalsIgnoreCase("N")) {

                                /**
                                 * Api to complete OTP verification Process if OTP is not verified.
                                 * */

                                    otpsendapi(result);
                                    deleted_account = false;

                                } else {

                               /**
                                 *Do: Redirect user to main screen is OTP verification process is already completed.
                                 *Checks: Is profile updated by user or not.
                                            *Decisions: Redirect to edit profile mode in case profile is not updated.
                                            * */

                                        new Handler().postDelayed(new Runnable() {

                                            @Override
                                            public void run() {



                                                HelperPreferences.get(LoginActivity.this).saveString(UID, result.getUserId());
                                                HelperPreferences.get(LoginActivity.this).saveString(PROFILESTATUS, result.getIsProfileCompleted());
                                                HelperPreferences.get(LoginActivity.this).saveString(API_ACCESS_TOKEN, result.getStripe_id());
                                                HelperPreferences.get(LoginActivity.this).saveString(STRIPE_VERIFIED, result.getStripe_verified());
                                                HelperPreferences.get(LoginActivity.this).saveString(ADVERTISEMENTSTATUS, "show");

                                                Global.ProfileStatusCheck.checkProfileStatus(LoginActivity.this, new Global.ProfileStatusCheck.ProfileStatusCallback() {
                                                    @Override
                                                    public void onIfProfileUpdated() {

                                                        if (!product_id.equalsIgnoreCase("") && !product_type.equalsIgnoreCase("")) {
                                                            Bundle bundle = new Bundle();
                                                            bundle.putString(DEEP_LINKING, "deeplink");
                                                            bundle.putString("product_id", product_id);
                                                            bundle.putString("product_type", product_type);

                                                            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                                                            resultIntent.putExtras(bundle);
                                                            startActivity(resultIntent);
                                                            finish();

                                                        } else {
                                                            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                                                            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                            startActivity(resultIntent);
                                                            finish();
                                                        }


                                                        new DeviceRegistaration().registerDevice(LoginActivity.this, result.getUserId());
                                                    }

                                                    @Override
                                                    public void onIfProfileNotUpdated() {
                                               //         Log.e(TAG, "onIfProfileNotUpdated:Profile not updated");

                                                        if (!product_type.equalsIgnoreCase("") && !product_id.equalsIgnoreCase("")) {

                                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                            intent.putExtra(DEEP_LINKING, "deeplink");
                                                            intent.putExtra("product_id", product_id);
                                                            intent.putExtra("product_type", product_type);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(intent);
                                                            finish();

                                                        } else {

                                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                            intent.putExtra(PROFILESTATUS, "abc");
                                                            startActivity(intent);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            finishAffinity();

                                                        }


                                                }
                                            });
                                        }

                                }, Snackbar.LENGTH_SHORT);

                            }
                            }

                        } catch (IOException e) {

                            e.printStackTrace();

                        } catch (JSONException je) {

                        } catch (Exception ei) {

                        }

                    } else {

                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }

                        try {

                            Log.e(TAG, "onResponse: " + response.errorBody().string());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Snackbar.make(rel_root, "Invalid email or password", Snackbar.LENGTH_SHORT).setAction("", null).show();

                    }

                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
               //     Log.e("errorPrint", t.getMessage());
                    Snackbar.make(rel_root, "Something went's wrong", Snackbar.LENGTH_SHORT)
                            .setAction("", null).show();
                }

            });


        } else {
            Snackbar.make(rel_root, "Invalid field data", Snackbar.LENGTH_SHORT)
                    .setAction("", null).show();
        }

    }


    public String getText(EditText view) {
        String result = view.getText().toString().trim();
        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    public void otpsendapi(Result loginResult) {
        Call<ResponseBody> recendCodeCall = webService.reSendOTPApi(auth, token, loginResult.getUserId(), "");
        recendCodeCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
           //     Log.e("ResendCode_response", response.isSuccessful() + "");

                if (response.isSuccessful()) {

                    try {

                        JSONObject jsonObject=new JSONObject(response.body().string());
                      //  Log.e("otpres",jsonObject.toString()+"//");

                    if (jsonObject.optString("status").equalsIgnoreCase("1")) {

                     //   Log.e("ResendCode_response",  "okk");
                        JSONObject object=jsonObject.optJSONObject("result");


                        /**
                         * OTPVerificationDialog
                         * By: Raghubeer singh Virk
                         * To: verify otp from api and entered otp from user.
                         * Provide: user interface to show resend otp options,timer, Pin view to enter otp.
                           * */
                        OTPVerificationDialog.create(LoginActivity.this, loginResult.getUserId(), loginResult.getCountryCode(), "", loginResult.getPhoneNumber(), Integer.valueOf(object.optString("verification_code")),
                                new OTPVerificationDialog.OTPVerificationListener() {
                                    @Override
                                    public void onOTPVerified() {

                                        /*
                                         * OTP verification success listener
                                         * */
                                        HelperPreferences.get(LoginActivity.this).saveString(STRIPE_VERIFIED, loginResult.getStripe_verified());
                                        HelperPreferences.get(LoginActivity.this).saveString(API_ACCESS_TOKEN, loginResult.getStripe_id());

                                        HelperPreferences.get(LoginActivity.this).saveString(UID, object.optString("user_id"));
                                        HelperPreferences.get(LoginActivity.this).saveString(PROFILESTATUS, loginResult.getIsProfileCompleted());
                                        onBackPressed();
                                        new DeviceRegistaration().registerDevice(LoginActivity.this, object.optString("user_id"));


                                    }

                                    @Override
                                    public void onOTPNotVerified() {

                                    }
                                }
                                ).show();

                    } else {
                        Toast.makeText(LoginActivity.this, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                    }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Something went's wrong", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        setStatusBarColor(this, R.color.colorWhite);
    }

}


