package com.app.admin.sellah.view.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.app.admin.sellah.R;
import com.app.admin.sellah.controller.utils.Global;
import com.app.admin.sellah.controller.utils.HelperPreferences;
import com.app.admin.sellah.controller.utils.SAConstants;
import com.app.admin.sellah.model.extra.commonResults.Common;
import com.app.admin.sellah.view.CustomDialogs.S_Dialogs;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.app.admin.sellah.controller.WebServices.ApisHelper.auth;
import static com.app.admin.sellah.controller.WebServices.ApisHelper.token;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.CARDHOLDER_NAME;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.CARD_EXP_MONTH;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.CARD_EXP_YEAR;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.CARD_NUMBER;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.UID;

public class AddCreditCardDetailFragment extends AppCompatActivity {

    Unbinder unbinder;
    View view;

    @BindView(R.id.li_card_detail)
    LinearLayout liCardDetail;
    @BindView(R.id.li_bottom_root)
    RelativeLayout liBottomRoot;
    ArrayList<TextWatcher> textWatchers;
    boolean isCardBack = false;
    boolean isCardNumberAvailable = false;
    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.rl_menu)
    RelativeLayout rlMenu;
    @BindView(R.id.title_sell)
    TextView titleSell;
    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;
    String cardHolderName, cardNumber, cardExp;
    @BindView(R.id.onnewcardnumber)
    TextView onnewcardnumber;
    @BindView(R.id.onnewcardholdername)
    TextView onnewcardholdername;
    @BindView(R.id.onnewcard_expire)
    TextView onnewcardExpire;
    @BindView(R.id.onnewcard_cvv)
    TextView onnewcardCvv;
    @BindView(R.id.edt_card_name)
    EditText edtCardName;
    @BindView(R.id.edt_card_number)
    EditText edtCardNumber;
    @BindView(R.id.edt_card_exp)
    EditText edtCardExp;
    @BindView(R.id.edt_cvv)
    EditText edtCvv;
    @BindView(R.id.btn_Addcard)
    Button btnAddcard;
    private boolean lock;
    private TextWatcher nameTextWatcher;
    private TextWatcher cardnumberTextWatcher;
    private TextWatcher cardExpTextWatcher;
    private TextWatcher cardCvvTextWatcher;

    String lastInput = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_credit_card_detail);
        unbinder = ButterKnife.bind(this);
        Global.StatusBarLightMode(this);
        getIntenData();

        edtCardName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                onnewcardholdername.setText(editable.toString());
            }
        });

        edtCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (lock || s.length() > 19) {
                    return;
                }
                lock = true;
                for (int i = 4; i < s.length(); i += 5) {
                    if (s.toString().charAt(i) != ' ') {
                        s.insert(i, " ");
                    }
                }
                lock = false;
                onnewcardnumber.setText(s.toString());
             //   Log.e("Lock_bool", lock + "");
            }
        });

        edtCardExp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Remove spacing char

                String input = s.toString();

                SimpleDateFormat formatter = new SimpleDateFormat("MM/yy", Locale.GERMANY);
                Calendar expiryDateDate = Calendar.getInstance();
                try {
                    expiryDateDate.setTime(formatter.parse(input));
                } catch (ParseException e) {

                    if (s.length() == 2 && !lastInput.endsWith("/")) {
                        int month = Integer.parseInt(input.replace("/", ""));
                        if (month <= 12) {
                            edtCardExp.setText(edtCardExp.getText().toString() + "/");
                            edtCardExp.setSelection(edtCardExp.getText().length());
                        } else {
                            edtCardExp.setError("Invalid month");
                        }
                    } else if (s.length() == 2 && lastInput.endsWith("/")) {
                        int month = Integer.parseInt(input.replace("/", ""));
                        if (month <= 12) {
                            edtCardExp.setText(edtCardExp.getText().toString().substring(0, 1));
                        }
                    }
                    lastInput = edtCardExp.getText().toString();
                }


                onnewcardExpire.setText(s.toString());
            }
        });

        edtCvv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                onnewcardCvv.setText(editable.toString());

            }

        });

    }

    private void getIntenData() {

        Intent in = getIntent();
        cardHolderName = in.hasExtra(SAConstants.Keys.CARDHOLDER_NAME) ? in.getStringExtra(CARDHOLDER_NAME) : "";
        cardNumber = in.hasExtra(SAConstants.Keys.CARD_NUMBER) ? "XXXXXXXXXXXX" + in.getStringExtra(CARD_NUMBER) : "";
        String expYear = "", expMonth = "";
        expYear = in.hasExtra(SAConstants.Keys.CARD_EXP_YEAR) ? in.getStringExtra(CARD_EXP_YEAR) : "";
        expMonth = in.hasExtra(SAConstants.Keys.CARD_EXP_MONTH) ? in.getStringExtra(CARD_EXP_MONTH) : "";
        cardExp = !TextUtils.isEmpty((expMonth + "/" + expYear)) ? (expMonth + "/" + expYear) : "";

    }

    @OnClick({R.id.btn_Addcard})
    public void onViewClicked(View view) {

        switch (view.getId()) {

            case R.id.btn_Addcard:

                if (edtCardName.getText().toString().equalsIgnoreCase("")) {

                    Snackbar.make(liBottomRoot, "Please enter card holder name.", Snackbar.LENGTH_SHORT).setAction("", null).show();

                } else if (edtCardNumber.getText().toString().equalsIgnoreCase("")) {

                    Snackbar.make(liBottomRoot, "Please enter card number.", Snackbar.LENGTH_SHORT).setAction("", null).show();

                } else if (edtCardExp.getText().toString().equalsIgnoreCase("")) {

                    Snackbar.make(liBottomRoot, "Please enter expiry date.", Snackbar.LENGTH_SHORT).setAction("", null).show();

                } else if (edtCvv.getText().toString().equalsIgnoreCase("")) {

                    Snackbar.make(liBottomRoot, "Please enter CVV code.", Snackbar.LENGTH_SHORT).setAction("", null).show();

                } else {

                    updateCreditCardApi();

                }

                break;

        }

    }

    private void updateCreditCardApi() {

        List<String> items = Arrays.asList(edtCardExp.getText().toString().split("\\s*/\\s*"));
      //  Log.e("List", "updateCreditCardApi: " + items.size());
        String expMonth = "", expYear = "";

        for (int i = 0; i < items.size(); i++) {

            if (i == 0) {

                expMonth = items.get(i);

            }

            if (i == 1) {

                expYear = items.get(i);

            }

        }

       // Log.e("Expiry", "updateCreditCardApi: " + expMonth + "/" + expYear);

        Dialog dialog = S_Dialogs.getLoadingDialog(AddCreditCardDetailFragment.this);
        dialog.show();
        Call<Common> updateCall = Global.WebServiceConstants.getRetrofitinstance().addCardApi(auth, token, HelperPreferences.get(AddCreditCardDetailFragment.this).getString(UID)
                , edtCardName.getText().toString(), edtCardNumber.getText().toString(), expMonth, expYear, edtCvv.getText().toString());
        updateCall.enqueue(new Callback<Common>() {
            @Override
            public void onResponse(Call<Common> call, Response<Common> response) {

                if (dialog != null && dialog.isShowing()) {

                    dialog.dismiss();

                }

                try {

                   // Log.e("onResponse: ", response.code() + "//");

                    if (response.isSuccessful()) {

                     //   Log.e("onResponse: ", response.body().toString());

                        if (response.body().getStatus().equalsIgnoreCase("1")) {

                            Toast.makeText(AddCreditCardDetailFragment.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                            Intent bi = new Intent("data");
                            bi.putExtra("dd", "dd");
                            LocalBroadcastManager.getInstance(AddCreditCardDetailFragment.this).sendBroadcast(bi);
                            onBackPressed();

                            Global.HITCARD = "hit_card";


                        } else {

                            Snackbar.make(liBottomRoot, "Something went's wrong", Snackbar.LENGTH_SHORT).setAction("", null).show();

                        }

                    } else {

                        String errorMessage = response.errorBody().string();
                        //Log.e("cardaddres", response.errorBody().string() + "//");
                        JSONObject object = new JSONObject(errorMessage);
                        Snackbar.make(liBottomRoot, object.optString("message"), Snackbar.LENGTH_SHORT).setAction("", null).show();

                    }

                } catch (Exception e) {
                 //   Log.e("onResponse: ", "" + e.getLocalizedMessage());
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<Common> call, Throwable t) {

                Snackbar.make(liBottomRoot, "Please try again latter.", Snackbar.LENGTH_SHORT).setAction("", null).show();

                if (dialog != null && dialog.isShowing()) {

                    dialog.dismiss();

                }

            }

        });

    }

    @OnClick(R.id.menu)
    public void onViewClicked() {
        onBackPressed();
    }


}
