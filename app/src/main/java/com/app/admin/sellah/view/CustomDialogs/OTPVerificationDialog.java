package com.app.admin.sellah.view.CustomDialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.admin.sellah.R;
import com.app.admin.sellah.controller.WebServices.WebService;
import com.app.admin.sellah.controller.utils.Global;
import com.app.admin.sellah.model.extra.commonResults.Common;
import com.goodiebag.pinview.Pinview;

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
import static com.app.admin.sellah.controller.utils.Global.deleted_account;
import static com.app.admin.sellah.controller.utils.Global.makeLinks;

public class OTPVerificationDialog extends AlertDialog {

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
    @BindView(R.id.otp_view)
    Pinview otpView;
    @BindView(R.id.btn_verify)
    Button btnVerify;
    @BindView(R.id.txt_sign_in)
    TextView txtSignIn;
    @BindView(R.id.rootLayout)
    RelativeLayout rootLayout;

    private Context mContext;

    private String mMobileNumber;

    private int mGeneratedOTP;

    private int mEnteredOTP = 0;

    private OTPVerificationListener mOTPVerificationListener;

    private int totalTime = 1 * 60 * 1000;

    private CountDownTimer mCountDownTimer;

    private String countryCode = "";

    private WebService service;

    private String uid;
    private String phone_type;


    public OTPVerificationDialog(Context context)
    {
        super(context);

    }

    private OTPVerificationDialog(@NonNull Context context, String uid, String countryCode, String phone_type, String mobileNumber,
                                  int verificationCode, OTPVerificationListener otpVerificationListener) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.mContext = context;
        this.mOTPVerificationListener = otpVerificationListener;
        this.mGeneratedOTP = verificationCode;
        service = Global.WebServiceConstants.getRetrofitinstance();
        this.uid = uid;
        this.phone_type = phone_type;
    }
    private void KeepStatusBar(){
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    public static OTPVerificationDialog create(Context context, String uid, String countryCode, String phone_type, String mobileNumber, int verificationCode,
                                               OTPVerificationListener otpVerificationListener) {
        return new OTPVerificationDialog(context, uid, countryCode,phone_type,mobileNumber, verificationCode, otpVerificationListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_vcerification);
        ButterKnife.bind(this);
        handleKeyBoard();
        setUpPinView();
        KeepStatusBar();




    }


    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(listener);

    }

    @Override
    public void setOnShowListener(@Nullable OnShowListener listener) {
        super.setOnShowListener(listener);
    }

    private void setUpPinView() {
        otpView.setPinViewEventListener((pinView, fromUser) -> {
            Global.hideKeyboard(getWindow().getDecorView(), mContext);
            String enteredPin = pinView.getValue();
            mEnteredOTP = Integer.valueOf(enteredPin);
        });

        txtSignIn.setLinkTextColor(mContext.getResources().getColor(R.color.colorRed)); // default link color for clickable span, we can also set it in xml by android:textColorLink=""
        ClickableSpan normalLinkClickSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                reSendCode();
            }

        };
        makeLinks(txtSignIn, new String[]{
                "Resend SMS"
        }, new ClickableSpan[]{
                normalLinkClickSpan
        });

    }

    @OnClick(R.id.btn_verify)
    void onClickVerify() {
        if (mEnteredOTP == 0) {
            Toast.makeText(mContext, "Enter OTP Received", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mEnteredOTP == mGeneratedOTP/*123456*/) {
            Global.hideKeyboard(getWindow().getDecorView(), mContext);
            if (deleted_account)
            {
               restore_account_otpverification();
            }
            else
            {
                verifyOTP();
            }


        } else {
            Toast.makeText(mContext, "Enter correct OTP", Toast.LENGTH_SHORT).show();

        }
    }

    private void verifyOTP() {
     //   Log.e("Uid_vCode", uid + " : " + mEnteredOTP);

        Call<Common> verifyOTPcall = service.varifyOTPApi(auth,token,uid, mEnteredOTP, phone_type);
        verifyOTPcall.enqueue(new Callback<Common>() {
            @Override
            public void onResponse(Call<Common> call, Response<Common> response) {
            //    Log.e("VerfyCode_response", response.isSuccessful() + "");
                Common result = response.body();
                if (response.isSuccessful()) {

                    if (result.getStatus().equalsIgnoreCase("1")) {
                        mOTPVerificationListener.onOTPVerified();
                        dismiss();
                    } else {
                        mOTPVerificationListener.onOTPNotVerified();
                        Toast.makeText(mContext, result.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(mContext, "Something went's wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Common> call, Throwable t) {
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                //Log.e("verifyOtp_failure", t.getMessage());
            }
        });
    }

    private void reSendCode() {
        Dialog dialog=S_Dialogs.getLoadingDialog(mContext);
        dialog.show();
        Call<ResponseBody> recendCodeCall = service.reSendOTPApi(auth,token,uid, phone_type);
        recendCodeCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("ResendCode_response", response.isSuccessful() + "");

                if(dialog!=null&&dialog.isShowing()){
                 dialog.dismiss();
                }

                if (response.isSuccessful()) {

                    try {

                        JSONObject jsonObject = new JSONObject(response.body().string());
               //         Log.e("otpres", jsonObject.toString() + "//");
                        if (jsonObject.optString("status").equalsIgnoreCase("1")) {
                            Toast.makeText(mContext, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();

                            JSONObject object=jsonObject.optJSONObject("result");
                            mGeneratedOTP = object.optInt("verification_code");
                        } else {
                            Toast.makeText(mContext, jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                        }

                    }catch (JSONException je){

                    }catch (IOException io){

                    }catch (Exception e){

                    }
                } else {
                    Toast.makeText(mContext, "Something went's wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if(dialog!=null&&dialog.isShowing()){
                    dialog.dismiss();
                }
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void setOnCancelListener(@Nullable OnCancelListener listener) {
        super.setOnCancelListener(listener);

       // Log.e("Dialog_Canceled", "setOnCancelListener: ");
        mOTPVerificationListener.onOTPNotVerified();

    }


    public interface OTPVerificationListener {
        void onOTPVerified();

        void onOTPNotVerified();
    }

    private void handleKeyBoard() {
        if (getWindow() != null) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void restore_account_otpverification() {

     //   Log.e("Uid_vCode", uid + " : " + mEnteredOTP);

        Call<Common> verifyOTPcall = service.restore_account(auth,token,uid, mEnteredOTP);
        verifyOTPcall.enqueue(new Callback<Common>() {
            @Override
            public void onResponse(Call<Common> call, Response<Common> response) {
              //  Log.e("VerfyCode_response", response.isSuccessful() + "");
                Common result = response.body();
                if (response.isSuccessful()) {

                    if (result.getStatus().equalsIgnoreCase("1")) {
                        mOTPVerificationListener.onOTPVerified();
                        dismiss();
                    } else {
                        mOTPVerificationListener.onOTPNotVerified();
                        Toast.makeText(mContext, result.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(mContext, "Something went's wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Common> call, Throwable t) {
                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
             //   Log.e("verifyOtp_failure", t.getMessage());
            }
        });
    }



}
