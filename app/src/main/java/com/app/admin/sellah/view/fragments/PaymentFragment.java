package com.app.admin.sellah.view.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.app.admin.sellah.Extras.FlipAnimation;
import com.app.admin.sellah.R;
import com.app.admin.sellah.controller.WebServices.ApisHelper;
import com.app.admin.sellah.controller.WebServices.WebService;
import com.app.admin.sellah.controller.stripe.StripeApp;
import com.app.admin.sellah.controller.stripe.StripeButton;
import com.app.admin.sellah.controller.stripe.StripeConnectListener;
import com.app.admin.sellah.controller.utils.Global;
import com.app.admin.sellah.controller.utils.HelperPreferences;
import com.app.admin.sellah.model.extra.CardDetails.Card;
import com.app.admin.sellah.model.extra.CardDetails.CardDetailModel;
import com.app.admin.sellah.view.CustomAnimations.MyBounceInterpolator;
import com.app.admin.sellah.view.CustomDialogs.IncomingFunds_dialog;
import com.app.admin.sellah.view.CustomDialogs.S_Dialogs;
import com.app.admin.sellah.view.CustomDialogs.Stripe_dialogfragment;
import com.app.admin.sellah.view.CustomViews.NoChangingBackgroundTextInputLayout;
import com.app.admin.sellah.view.activities.MainActivity;
import com.bumptech.glide.Glide;
import com.cooltechworks.creditcarddesign.CreditCardUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
/*import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;*/
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
/*import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;*/
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
/*import cz.msebera.android.httpclient.Header;*/
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static android.app.Activity.RESULT_OK;
import static com.app.admin.sellah.controller.WebServices.ApisHelper.auth;
import static com.app.admin.sellah.controller.WebServices.ApisHelper.token;
import static com.app.admin.sellah.controller.stripe.StripeSession.API_ACCESS_TOKEN;
import static com.app.admin.sellah.controller.stripe.StripeSession.STRIPE_VERIFIED;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.AVAILABLE_BALANCE;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.PENDING_BALANCE;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.QRCODE;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.UID;

@SuppressLint("ValidFragment")
public class PaymentFragment extends Fragment {

    @BindView(R.id.edt_card_edit)
    TextView edtCardEdit;

    @BindView(R.id.rel_stripe_connect)
    RelativeLayout relStripeConnect;

    @BindView(R.id.btn_stripe_connect)
    StripeButton btnStripeConnect;

    @BindView(R.id.p_onnewcardnumber)
    TextView pOnnewcardnumber;
    @BindView(R.id.p_onnewcardholdername)
    TextView pOnnewcardholdername;
    @BindView(R.id.p_onnewcard_expire)
    TextView pOnnewcardExpire;
    @BindView(R.id.p_onnewcard_cvv)
    TextView pOnnewcardCvv;
    @BindView(R.id.rl_addnewstrpeaccount)
    RelativeLayout rlAddnewstrpeaccount;
    @BindView(R.id.rl_sellahwallet_clicklink)
    RelativeLayout rlSellahwalletClicklink;
    @BindView(R.id.sel_txt_bal1)
    TextView selTxtBal1;
    @BindView(R.id.sel_txt_acc1)
    TextView selTxtAcc1;
    @BindView(R.id.withdraw)
    TextView withdraw;
    @BindView(R.id.sel_txt_pend1)
    TextView selTxtPend1;
    @BindView(R.id.imgview_qr_code)
    ImageView imgviewQrCode;
    @BindView(R.id.rel_cardLayout)
    RelativeLayout relCardLayout;
    @BindView(R.id.rel_addCard)
    RelativeLayout relAddCard;
    private Dialog progress;
    private Animation myAnim;
    @BindView(R.id.ll_incoming)
    LinearLayout llIncoming;
   @BindView(R.id.swipyrefreshlayout)
    SwipyRefreshLayout mSwipyRefreshLayout;

    HashMap<EditText, String> bankDetaildialogMessages;
    ArrayList<EditText> allBankDetailFields;
    ArrayList<TextInputLayout> allBankdetailInputLayouts;
    final int GET_NEW_CARD = 2;


    Unbinder unbinder;

    CardDetailModel cardDetailModel;
    private String cardHolderName = "";
    private String cardNumber = "";
    private String cardExp = "";
    private String stripeId = "";
    private StripeApp StripeAppmApp;
    private Dialog dialog;
    WebService service;

    public static String card_id = "", customer_id = "", qr_scan_id = "";


    @SuppressLint("ValidFragment")
    public PaymentFragment() {

    }

    @SuppressLint("ValidFragment")
    public PaymentFragment(String stripeId) {
        this.stripeId = stripeId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_payment_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        progress = S_Dialogs.getLoadingDialog(getActivity());
        service = Global.WebServiceConstants.getRetrofitinstance();
     //   Log.e("onGetDataSuccess: ", "d" + (HelperPreferences.get(getActivity()).getString(STRIPE_VERIFIED)));


        if ((HelperPreferences.get(getActivity()).getString(STRIPE_VERIFIED).equals("") || HelperPreferences.get(getActivity()).getString(STRIPE_VERIFIED).equals("N"))) {
            rlSellahwalletClicklink.setVisibility(View.VISIBLE);
          //  Log.e("onCreateView: ", "1");
            rlAddnewstrpeaccount.setVisibility(View.GONE);
            withdraw.setVisibility(View.GONE);
        } else {
          //  Log.e("onCreateView: ", "2");
            rlSellahwalletClicklink.setVisibility(View.GONE);
            rlAddnewstrpeaccount.setVisibility(View.VISIBLE);
            // Todo changes
            selTxtBal1.setText("S$ " + HelperPreferences.get(getActivity()).getString(AVAILABLE_BALANCE));
            selTxtPend1.setText("S$ " + HelperPreferences.get(getActivity()).getString(PENDING_BALANCE));
            String acc = HelperPreferences.get(getActivity()).getString(API_ACCESS_TOKEN);
            String newacc = acc.substring(acc.length() - 4);

            Glide.with(getActivity()).load(HelperPreferences.get(getActivity()).getString(QRCODE)).into(imgviewQrCode);

            selTxtAcc1.setText("* *** " + newacc);


        }


        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    getProfileData();
                }
            }
        });

        return view;
    }

    public void getProfileData()
    {
            new ApisHelper().getProfileData(getActivity(), new ApisHelper.GetProfileCallback() {
                @Override
                public void onGetProfileSuccess(JsonObject msg) {
                  //  Log.e("onGetProfileSuccess: ", msg.toString());


                    try {
                        if(getActivity()!=null) {
                            mSwipyRefreshLayout.setRefreshing(false);
                            JSONObject jsonObject = new JSONObject(msg.toString());
                            String status = jsonObject.getString("status");
                      //      Log.e("onGetProfileSuccess: ", msg.toString() + ">>>>  " + jsonObject.toString());


                            if (status.equalsIgnoreCase("1")) {
                                JSONObject result = jsonObject.getJSONObject("result");

                                HelperPreferences.get(getActivity()).saveString(AVAILABLE_BALANCE, result.getString("available_bal"));
                                HelperPreferences.get(getActivity()).saveString(PENDING_BALANCE, result.getString("pending_bal"));

                                selTxtPend1.setText("S$ " + HelperPreferences.get(getActivity()).getString(PENDING_BALANCE));
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }


               }

                @Override
                public void onGetProfileFailure() {
                    if(getActivity()!=null)
                        mSwipyRefreshLayout.setRefreshing(false);
                }
            });
        }



    private void stripeApi() {

        new ApisHelper().getCardApi(getActivity(), new ApisHelper.OnGetCardDataListners() {
            @Override
            public void onGetDataSuccess(CardDetailModel body) {


                try {


                    Gson gson = new GsonBuilder().create();
                    relAddCard.setVisibility(View.GONE);
                    relCardLayout.setVisibility(View.VISIBLE);


                    if (body != null) {
                        setUpcards(body.getCards());//set card list adapter

                        customer_id = body.getCards().get(0).getCustomer();
                        card_id = body.getCards().get(0).getId();
                    } else {


                    }
                } catch (Exception e) {
                }

            }

            @Override
            public void onGetDataFailure() {
                try {
                    relAddCard.setVisibility(View.VISIBLE);
                    relCardLayout.setVisibility(View.GONE);
                } catch (Exception e) {
                }
            }

        });
        StripConnect();

    }


    private void setUpcards(List<Card> cards) {
        if (cards != null && cards.size() > 0) {

            for (int i = 0; i < cards.size(); i++) {
                if (cards.get(i).getDefault_card().equals("Y")) {
                    if (pOnnewcardholdername != null) {
                        pOnnewcardholdername.setText(cards.get(i).getName());
                    }
                    if (pOnnewcardnumber != null) {
                        pOnnewcardnumber.setText("**** **** **** " + cards.get(i).getLast4());
                    }
                    if (pOnnewcardExpire != null) {
                        pOnnewcardExpire.setText(cards.get(i).getExpMonth() + "/" + cards.get(i).getExpYear());
                    }


                    break;
                }
            }


        } else {

        }

    }

    private void StripConnect() {



        StripeAppmApp = new StripeApp(getActivity(), "geniusAppDeveloper", "pk_test_3eq8eJ4CcA0kgn0JN9AG0fHQ",
                "sk_test_QW9KCbQ08S6BSGogNk3XKDTa", "https://developer.android.com", "read_write");

        btnStripeConnect.setStripeApp(StripeAppmApp);
        btnStripeConnect.setConnectMode(StripeApp.CONNECT_MODE.DIALOG);
        btnStripeConnect.addStripeConnectListener(new StripeConnectListener() {

            @Override
            public void onConnected() {
             //   Log.e("Connected_as", "onConnected: " + StripeAppmApp.getUserId());
                if (StripeAppmApp.getUserId() != null) {

                    new ApisHelper().linkStripApi(getActivity(), StripeAppmApp.getUserId(), new ApisHelper.StripeConnectCallback() {
                        @Override
                        public void onStripeConnectSuccess(String msg) {
                            Snackbar.make(((MainActivity) getActivity()).relRoot, msg, Snackbar.LENGTH_SHORT)
                                    .setAction("", null).show();
                            HelperPreferences.get(getActivity()).saveString(API_ACCESS_TOKEN, StripeAppmApp.getUserId());

                        }

                        @Override
                        public void onStripeConnectFailure() {
                            Snackbar.make(getActivity().getWindow().getDecorView(), "Unable to link account with stripe at this movements", Snackbar.LENGTH_SHORT)
                                    .setAction("", null).show();

                        }
                    });
                }
            }

            @Override
            public void onDisconnected() {
            //    Log.e("Disconnected", "Disconnected: ");
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
            }

        });
    }


    public void bankDetail() {

        dialog = new Dialog(getActivity());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.edit_bank_account_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        EditText edtBankName = dialog.findViewById(R.id.edt_bank_name);
        EditText edtAccountNo = dialog.findViewById(R.id.edt_account_no);

        NoChangingBackgroundTextInputLayout ilBankName = dialog.findViewById(R.id.il_bank_name);
        NoChangingBackgroundTextInputLayout ilAccountNo = dialog.findViewById(R.id.il_account_no);

        allBankDetailFields = new ArrayList<>();
        allBankDetailFields.add(edtBankName);
        allBankDetailFields.add(edtAccountNo);


        allBankdetailInputLayouts = new ArrayList<>();
        allBankdetailInputLayouts.add(ilBankName);
        allBankdetailInputLayouts.add(ilAccountNo);

        bankDetaildialogMessages = new HashMap<>();
        bankDetaildialogMessages.put(edtBankName, "Please enter bank name");
        bankDetaildialogMessages.put(edtAccountNo, "Please enter account no.");

        Button btn_change = dialog.findViewById(R.id.btn_done);

        ImageView back = dialog.findViewById(R.id.back_img);

        myAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.1, 20);
        myAnim.setInterpolator(interpolator);
        back.startAnimation(myAnim);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });


        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isBankDetailFormValid()) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }

            }
        });
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    protected boolean isBankDetailFormValid() {

        for (EditText editText : allBankDetailFields) {
            if (editText.getText().toString().trim().isEmpty()) {
                allBankdetailInputLayouts.get(allBankDetailFields.indexOf(editText))
                        .setError(bankDetaildialogMessages.get(editText));
                return false;
            } else {
                allBankdetailInputLayouts.get(allBankDetailFields.indexOf(editText)).setErrorEnabled(false);
            }
        }
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_NEW_CARD && resultCode == RESULT_OK) {

            String cardHolderName = data.getStringExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME);
            String cardNumber = data.getStringExtra(CreditCardUtils.EXTRA_CARD_NUMBER);
            String expiry = data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY);
            String cvv = data.getStringExtra(CreditCardUtils.EXTRA_CARD_CVV);


        }

        //-----------qr code onActivity----------------------------------------
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "ResultFeatured Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    //  Log.e("contentVal",obj.getString("name"));
                    //  Log.e("contentVal",obj.getString("address"));

                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    //  Toast.makeText(getActivity(), result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }


        //---------------------------------------------------------------


    }


    @Override
    public void onStop() {
        super.onStop();
        new ApisHelper().cancel_striipe_request();
    }


    public void wirhdraw_api() {
        progress.show();
        Call<JsonObject> call = service.withdrawal_money(auth,token,HelperPreferences.get(getActivity()).getString(UID));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (progress != null) {
                    progress.dismiss();
                }

                try {
                    JSONObject obj = new JSONObject(response.body().toString());

                    S_Dialogs.getLiveVideoStopedDialog(getActivity(), obj.getString("message"), ((dialog, which) -> {


                    })).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
              //  Log.e("onResponse: ", response.body().toString());


            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }


    @OnClick({R.id.withdraw, R.id.ll_incoming, R.id.rl_sellahwallet_clicklink, R.id.rel_qr_scan, R.id.scan_rel, R.id.phone_qrScan, R.id.edt_card_edit, R.id.rel_addCard})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.withdraw:
//                wirhdraw_api();
               // getAccApi();
                break;

            case R.id.phone_qrScan:
                //  new PayDialog_QRScan(getActivity(),HelperPreferences.get(getActivity()).getString(AVAILABLE_BALANCE)).show();
                break;

            case R.id.rl_sellahwallet_clicklink:


                Stripe_dialogfragment stripe_dialogfragment = new Stripe_dialogfragment();
                stripe_dialogfragment.show(getActivity().getFragmentManager(), "");
                break;

            case R.id.rel_qr_scan:
                flip();
                break;

            case R.id.scan_rel:
                flip();
                break;
            case R.id.ll_incoming:
                IncomingFunds_dialog _dialog = new IncomingFunds_dialog();
                _dialog.show(getFragmentManager(), "");

                break;

            case R.id.edt_card_edit:
                editCardDetail();
                break;

            case R.id.rel_addCard:
                editCardDetail();
                break;


        }
    }


    public void flip() {


        View rootLayout = (View) getActivity().findViewById(R.id.rl_addnewstrpeaccount);
        View cardFace = (View) getActivity().findViewById(R.id.add_rel);
        View cardBack = (View) getActivity().findViewById(R.id.scan_rel);

        FlipAnimation flipAnimation = new FlipAnimation(cardFace, cardBack);

        if (cardFace.getVisibility() == View.GONE) {
            flipAnimation.reverse();
        }
        rootLayout.startAnimation(flipAnimation);

    }

    public void editCardDetail() {

        Intent intent = new Intent(getActivity(), ShowCreditCardDetailFragment.class);
        intent.putExtra("payment", "payment");
        startActivityForResult(intent, GET_NEW_CARD);


    }

    @Override
    public void onResume() {
        super.onResume();


        stripeApi();

    }
}
