package com.app.admin.sellah.view.CustomDialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.app.admin.sellah.R;
import com.app.admin.sellah.controller.WebServices.WebService;
import com.app.admin.sellah.controller.utils.Global;
import com.app.admin.sellah.controller.utils.HelperPreferences;
import com.app.admin.sellah.model.extra.GetCardDetailModel.GetCardDetailModel;
import com.app.admin.sellah.model.extra.PromotePackages.PackagesList;
import com.app.admin.sellah.model.extra.PromotePackages.PromotePackageModel;
import com.app.admin.sellah.view.adapter.PromoteOfferAdapter;
import com.app.admin.sellah.view.fragments.ShowCreditCardDetailFragment;
import com.cooltechworks.creditcarddesign.CreditCardView;
import com.stripe.android.view.CardMultilineWidget;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static com.app.admin.sellah.controller.WebServices.ApisHelper.auth;
import static com.app.admin.sellah.controller.WebServices.ApisHelper.token;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.UID;

public class PromoteDialog extends AlertDialog implements PaymentDialog.PaymentCallBack {

    @BindView(R.id.img_tick_1)
    ImageView imgTick1;
    @BindView(R.id.rv_offer_list)
    RecyclerView rvOfferList;

    Context context;
    Button btnProceed;

    String CardNumber, Cvc, Currency;
    Integer ExpMonth, ExpYear;
    public static final String PUBLISHABLE_KEY = "pk_test_3eq8eJ4CcA0kgn0JN9AG0fHQ";

    String cardNumberStr;

    CardMultilineWidget cardMultilineWidget;
    CreditCardView creditCardView;
    EditText edtCvv;
    Button btnCancel;
    @BindView(R.id.pb_root)
    ProgressBar pbRoot;
    @BindView(R.id.sv_root)
    ScrollView svRoot;
    @BindView(R.id.cd_root)
    LinearLayout cdRoot;
    @BindView(R.id.btn_promote_cancel)
    ImageView btnPromoteCancel;
    @BindView(R.id.promote_btn)
    Button promoteBtn;
    @BindView(R.id.promote_laterbtn)
    Button promoteLaterbtn;
    private BottomSheetDialog bottomSheetDialog;
    WebService webService;
    private Dialog dialog;
    private boolean isCardBack;
    private LinearLayout liCardDetail;
    private LinearLayout liCardDetailError;
    private ProgressBar pbLoading;
    private ViewGroup transitionsContainer;
    PromoteCallback callback;
    String productId = "";
    String selected_id = "";

    public static String promote_selected_id = "";

    public PromoteDialog(Context context, String productId, PromoteCallback callback) {
        super(context);
        this.context = context;
        this.callback = callback;
        this.productId = productId;
    }

    public static PromoteDialog create(Context context, String productId, PromoteCallback callback) {
        return new PromoteDialog(context, productId, callback);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webService = Global.WebServiceConstants.getRetrofitinstance();
        dialog = S_Dialogs.getLoadingDialog(context);
        setContentView(R.layout.layout_promote_product);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ButterKnife.bind(this);

        getPromotePackages();

    }

    private void hideProgress(List<PackagesList> packagesList) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                svRoot.setVisibility(View.VISIBLE);
                pbRoot.setVisibility(GONE);

                PromoteOfferAdapter adapter = new PromoteOfferAdapter(context, packagesList, (id) -> {

                    selected_id = id;

                });

                LinearLayoutManager horizontalLayoutManager1 = new GridLayoutManager(context, 2);
                rvOfferList.setLayoutManager(horizontalLayoutManager1);
                rvOfferList.setAdapter(adapter);
                Global.getTotalHeightofLinearRecyclerView(context, rvOfferList, R.layout.layout_promote_offer_list_design, 0);
            }

        }, 1000);
    }

    @OnClick({R.id.btn_promote_cancel, R.id.promote_btn, R.id.promote_laterbtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.btn_promote_cancel:

                dismiss();

                break;

            case R.id.promote_btn:

                if (selected_id.equals("")) {

                    Toast.makeText(context, "Please select a promote plan.", Toast.LENGTH_SHORT).show();

                } else {

                    ShowCreditCardDetailFragment.paymentCallBack(context, this, productId, selected_id);
                    dismiss();

                }

                break;

            case R.id.promote_laterbtn:

                dismiss();

                break;

        }

    }

    @Override
    public void onPaymentSuccess() {

        callback.onPromoteSuccess();
        Toast.makeText(context, "Product promoted successfully.", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPaymentFail(String message) {

        callback.onPromoteFailure();
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCancelDialog() {

     //   Log.e("onCancelDialog: ", "cancel");

    }

    public interface PromoteCallback {
        void onPromoteSuccess();

        void onPromoteFailure();
    }


    private void hideCCview() {
        liCardDetail.setVisibility(View.GONE);
        liCardDetailError.setVisibility(View.VISIBLE);
        btnProceed.setVisibility(View.GONE);
        pbLoading.setVisibility(GONE);

    }


    private void showCCview(GetCardDetailModel body) {

        pbLoading.setVisibility(GONE);
        liCardDetail.setVisibility(View.VISIBLE);
        liCardDetailError.setVisibility(GONE);
        btnProceed.setVisibility(View.VISIBLE);
        try {
            creditCardView.setCardHolderName(body.getRecord().get(body.getRecord().size() - 1).getCardHolderName());
            creditCardView.setCardExpiry(body.getRecord().get(body.getRecord().size() - 1).getExpDate());
            creditCardView.setCardNumber(body.getRecord().get(body.getRecord().size() - 1).getCardNumber());
        } catch (Exception e) {
          //  Log.e("CardDataSetupException", e.getMessage());
        }
    }

    private void getPromotePackages() {

        Call<PromotePackageModel> promotePackageCall = webService.getPromotePackagesApi(auth, token, HelperPreferences.get(context).getString(UID));
        promotePackageCall.enqueue(new Callback<PromotePackageModel>() {
            @Override
            public void onResponse(Call<PromotePackageModel> call, Response<PromotePackageModel> response) {
                if (response.isSuccessful()) {
                  //  Log.e("PromoteProductApi", "onResponse: success" + response.body().getStatus());
                    if (response.body().getStatus().equalsIgnoreCase("1")) {
                        hideProgress(response.body().getPackagesList());
                    } else {
                        Toast.makeText(context, "Unable to get promote packages at this movement.", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                } else {
                    Toast.makeText(context, "Unable to get promote packages at this movement.", Toast.LENGTH_SHORT).show();
                    dismiss();
                    try {
                        Log.e("PromoteProductApi_error", "onResponse: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<PromotePackageModel> call, Throwable t) {
                Toast.makeText(context, "Something went's wrong.", Toast.LENGTH_SHORT).show();
                dismiss();
              //  Log.e("PromoteProductApi", "onFailure: " + t.getMessage());
            }
        });
    }

}
