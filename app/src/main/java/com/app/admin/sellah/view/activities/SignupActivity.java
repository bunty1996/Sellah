package com.app.admin.sellah.view.activities;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.admin.sellah.R;
import com.app.admin.sellah.controller.WebServices.WebService;
import com.app.admin.sellah.controller.utils.DeviceRegistaration;
import com.app.admin.sellah.controller.utils.Global;
import com.app.admin.sellah.controller.utils.HelperPreferences;
import com.app.admin.sellah.controller.utils.PermissionCheckUtil;
import com.app.admin.sellah.model.extra.RegisterPojo.RegisterResult;
import com.app.admin.sellah.view.CustomDialogs.OTPVerificationDialog;
import com.app.admin.sellah.view.CustomDialogs.S_Dialogs;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.admin.sellah.controller.stripe.StripeSession.API_ACCESS_TOKEN;
import static com.app.admin.sellah.controller.stripe.StripeSession.STRIPE_VERIFIED;
import static com.app.admin.sellah.controller.utils.Global.makeLinks;
import static com.app.admin.sellah.controller.utils.Global.setStatusBarColor;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.ADVERTISEMENTSTATUS;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.DEEP_LINKING;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.PROFILESTATUS;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.UID;

public class SignupActivity extends AppCompatActivity {

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
    @BindView(R.id.t_confirmPass)
    TextView tConfirmPass;
    @BindView(R.id.rl_spinner)
    RelativeLayout rlSpinner;
    @BindView(R.id.t_phoneNum)
    TextView tPhoneNum;
    @BindView(R.id.txt_sign_in)
    TextView txtSignIn;
    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.ccp1)
    CountryCodePicker ccp1;
    @BindView(R.id.et_username)
    EditText etUsername;

    private String TAG = SignupActivity.class.getSimpleName();
    @BindView(R.id.etSignup_email)
    EditText email;
    @BindView(R.id.etSignup_pass)
    EditText pass;
    @BindView(R.id.et_confirmPass)
    EditText confirmPass;
    @BindView(R.id.et_phoneNum)
    EditText phoneNum;
    @BindView(R.id.b_signUp)
    Button signup;
    @BindView(R.id.rootLayout)
    RelativeLayout rel_root;
    @BindView(R.id.spinner_city)
    Spinner spinCity;


    WebService webService;
    @BindView(R.id.ccp)
    CountryCodePicker ccp;
    private int phoneLength = 8;
    String country = "";
    String product_id="",product_type="";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_signup_new);
        setStatusBarColor(this, R.color.colorFormBg);
        ButterKnife.bind(SignupActivity.this);
        webService = Global.WebServiceConstants.getRetrofitinstance();

        Intent intent = getIntent();
        if (intent!=null && intent.hasExtra(DEEP_LINKING))
        {
            product_id = intent.getStringExtra("product_id");
            product_type = intent.getStringExtra("product_type");
        }

        PermissionCheckUtil.create(this, () -> {
        });

        ccp.registerCarrierNumberEditText(phoneNum);
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                if (ccp.getSelectedCountryCode().equalsIgnoreCase("91")) {
                    phoneLength = 10;
                } else {
                    phoneLength = 8;
                }
            }
        });

        ccp1.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country = ccp1.getSelectedCountryNameCode();

            }
        });

        txtSignIn.setLinkTextColor(Color.BLACK); // default link color for clickable span, we can also set it in xml by android:textColorLink=""
        ClickableSpan normalLinkClickSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                if (!product_id.equalsIgnoreCase("")  && !product_type.equalsIgnoreCase(""))
                {
                    intent.putExtra(DEEP_LINKING,"deeplink");
                    intent.putExtra("product_id",product_id);
                    intent.putExtra("product_type",product_type);
                }
                startActivity(intent);
                finish();
            }

        };
        makeLinks(txtSignIn, new String[]{
                "Sign In"
        }, new ClickableSpan[]{
                normalLinkClickSpan
        });

    }

    @OnClick({R.id.back})
    void imageClick() {
        onBackPressed();
    }

    @OnClick({R.id.b_signUp})
    void signupClick() {

        if (getText(etUsername).equalsIgnoreCase("")) {
            Snackbar.make(rel_root, "Please enter UserName ", Snackbar.LENGTH_SHORT)
                    .setAction("", null).show();
        }
       else if (getText(email).equalsIgnoreCase("")) {
            Snackbar.make(rel_root, "Please enter E-mail ", Snackbar.LENGTH_SHORT)
                    .setAction("", null).show();
        } else if (country.equalsIgnoreCase("")) {
            country = ccp.getDefaultCountryNameCode();
        } else if (getText(pass).equalsIgnoreCase("")) {
            Snackbar.make(rel_root, "Please enter password ", Snackbar.LENGTH_SHORT)
                    .setAction("", null).show();
        } else if (getText(confirmPass).equalsIgnoreCase("")) {
            Snackbar.make(rel_root, "Please enter confirmation password ", Snackbar.LENGTH_SHORT)
                    .setAction("", null).show();
        }  else if (!getText(confirmPass).equals((getText(pass)))) {
            Snackbar.make(rel_root, "Password not Matched", Snackbar.LENGTH_SHORT)
                    .setAction("", null).show();

        } else if (getText(pass).length() < 6 || getText(pass).isEmpty()) {
            Snackbar.make(rel_root, "Password must be 6 Characters", Snackbar.LENGTH_SHORT)
                    .setAction("", null).show();

        } else if (!ccp.isValidFullNumber()) {
            Snackbar.make(rel_root, "Invalid phone number", Snackbar.LENGTH_SHORT)
                    .setAction("", null).show();

        }  else if ((!Patterns.EMAIL_ADDRESS.matcher(getText(email)).matches())) {
            Snackbar.make(rel_root, "Invalid E-mail Address", Snackbar.LENGTH_SHORT)
                    .setAction("", null).show();

        } else  {

            Dialog dialog = S_Dialogs.getLoadingDialog(this);
            dialog.show();

            String actualPhoneNo = ccp.getFullNumberWithPlus().substring(ccp.getSelectedCountryCodeWithPlus().length());
            Log.e("ccp_number", "signupClick: " + actualPhoneNo);
            Log.e("CountryCode", ccp.getSelectedCountryCodeWithPlus());
            Log.e("Ccp_number", ccp.getFullNumber());
            Log.e("Ccp_number", ccp.getFullNumberWithPlus());
            Log.e("Ccp_number", getText(phoneNum));
            Log.e("Ccp_number", country);
            Call<RegisterResult> registerCall = webService.registrationApi(getText(etUsername),getText(email), getText(pass), getText(confirmPass), actualPhoneNo, ccp.getSelectedCountryCodeWithPlus(), "", country);
            registerCall.enqueue(new Callback<RegisterResult>() {
                @Override
                public void onResponse(Call<RegisterResult> call, Response<RegisterResult> response) {

//                    try {
//                        JSONObject jsonObject=new JSONObject(response.body().toString());
//                        JSONObject object=jsonObject.optJSONObject("result");
//
//                        Log.e("TAG",object.toString());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }

                    RegisterResult registerResult = response.body();
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Log.e("Register_Respone", response.isSuccessful() + "");
                    Log.e("Register_Respone", response.message() + "");
                  Log.e("Register_Respone",response.message()+"");
                    if (response.isSuccessful()) {
                        if (registerResult.getStatus().equalsIgnoreCase("1")) {
                            Snackbar.make(rel_root, registerResult.getMessage(), Snackbar.LENGTH_SHORT).setAction("", null).show();
//                            "id": "5ef30a8cfe7e8007824f95f2",
//                                    "username": "test",
//                                    "email": "testg12h63t@gmail.com",
//                                    "country_code": "+91",
//                                    "phone_number": "8894121343",
//                                    "city": "Singapore",
//                                    "verification_code": 5343,
//                                    "is_profile_completed": "N",
//                                    "isLogged": "Y",
//                                    "stripe_verified": "N"
                            Log.e("test",registerResult.getResult().getVerificationCode()+"//"+registerResult.getResult().getStripe_verified());
                            Log.e("Register_Response", registerResult.getResult().getVerificationCode() + ""+registerResult.getResult().getId());
                            OTPVerificationDialog.create(SignupActivity.this, registerResult.getResult().getId(), registerResult.getResult().getCountryCode(), "", getText(phoneNum), registerResult.getResult().getVerificationCode(),
                                    new OTPVerificationDialog.OTPVerificationListener() {
                                        @Override
                                        public void onOTPVerified() {
                                            HelperPreferences.get(SignupActivity.this).saveString(UID, registerResult.getResult().getId());
                                            HelperPreferences.get(SignupActivity.this).saveString(PROFILESTATUS, registerResult.getResult().getIsProfileCompleted());
                                            HelperPreferences.get(SignupActivity.this).saveString(ADVERTISEMENTSTATUS, "show");
                                            HelperPreferences.get(SignupActivity.this).saveString(STRIPE_VERIFIED, "N");
                                            Global.ProfileStatusCheck.checkProfileStatus(SignupActivity.this, new Global.ProfileStatusCheck.ProfileStatusCallback() {
                                                @Override
                                                public void onIfProfileUpdated() {
                                                //    Global.from_register = true;
                                                    new DeviceRegistaration().registerDevice(SignupActivity.this, registerResult.getResult().getId());
                                                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finishAffinity();
                                                }

                                                @Override
                                                public void onIfProfileNotUpdated() {
                                     //               Log.e(TAG, "onIfProfileNotUpdated:Profile not updated");
                                                  //  Global.from_register = true;
                                                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                                    intent.putExtra(PROFILESTATUS, "abc");
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onOTPNotVerified() {

                                        }
                                    }
                                    ).show();



                        } else {
                            Snackbar.make(rel_root, registerResult.getMessage(), Snackbar.LENGTH_SHORT).setAction("", null).show();
                        }
                    } else {


                        Snackbar.make(rel_root, "Please try with different credentials", Snackbar.LENGTH_SHORT).setAction("", null).show();

                    }

                }

                @Override
                public void onFailure(Call<RegisterResult> call, Throwable t) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Snackbar.make(rel_root, t.getMessage(), Snackbar.LENGTH_SHORT)
                            .setAction("", null).show();
                //    Log.e("RegisterFailure", t.getMessage());
                }
            });

        }

    }

    public String getText(EditText view) {
        String result = view.getText().toString().trim();
        return result;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        setStatusBarColor(this, R.color.colorWhite);
    }

}

