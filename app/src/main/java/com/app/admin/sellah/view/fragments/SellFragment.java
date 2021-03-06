package com.app.admin.sellah.view.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.admin.sellah.R;
import com.app.admin.sellah.controller.WebServices.ApisHelper;
import com.app.admin.sellah.controller.stripe.StripeApp;
import com.app.admin.sellah.controller.stripe.StripeButton;
import com.app.admin.sellah.controller.stripe.StripeConnectListener;
import com.app.admin.sellah.controller.utils.HelperPreferences;
import com.app.admin.sellah.view.CustomDialogs.S_Dialogs;
import com.app.admin.sellah.view.CustomDialogs.Stripe_dialogfragment;
import com.app.admin.sellah.view.CustomDialogs.Stripe_image_verification_dialogfragment;
import com.app.admin.sellah.view.CustomDialogs.VideoDialog;
import com.app.admin.sellah.view.activities.AddNewVideos;
import com.app.admin.sellah.view.activities.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.admin.sellah.controller.stripe.StripeSession.API_ACCESS_TOKEN;
import static com.app.admin.sellah.controller.stripe.StripeSession.STRIPE_VERIFIED;

public class SellFragment extends Fragment {

    @BindView(R.id.txt_sell)
    TextView txtSell;
    @BindView(R.id.txt_sub)
    TextView txtSub;
    @BindView(R.id.lin1)
    LinearLayout lin1;
    @BindView(R.id.cardView)
    CardView cardView;
    @BindView(R.id.lin2)
    LinearLayout lin2;
    @BindView(R.id.cardView2)
    CardView cardView2;
    Unbinder unbinder;

    @BindView(R.id.btn_stripe_connect)
    StripeButton btnStripeConnect;
    private StripeApp StripeAppmApp;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.sell_product, container, false);
        unbinder = ButterKnife.bind(this, view);
        hideSearch();

        StripConnect();


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.cardView, R.id.cardView2})
    public void onViewClicked(View view) {

        switch (view.getId()) {

            case R.id.cardView:
                if ((HelperPreferences.get(getActivity()).getString(STRIPE_VERIFIED).equals("")||HelperPreferences.get(getActivity()).getString(STRIPE_VERIFIED).equals("N"))) {
                    final Dialog dialog = new Dialog(getActivity()); // Context, this, etc.
                    dialog.setContentView(R.layout.add_new_prod_dialog);

                    dialog.setCancelable(false);
                    if (dialog.getWindow() != null) {
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.getWindow().setGravity(Gravity.BOTTOM);
                        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
//                    params.gravity = Gravity.CENTER_VERTICAL;
                    }
                    TextView ok = dialog.findViewById(R.id.textOk);
                    TextView maybelater = dialog.findViewById(R.id.textCancel);
                    maybelater.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Intent intent = new Intent(getActivity(), AddNewVideos.class);
                            startActivity(intent);
                        }
                    });
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            if ((HelperPreferences.get(getActivity()).getString(STRIPE_VERIFIED).equals("") || HelperPreferences.get(getActivity()).getString(STRIPE_VERIFIED).equals("N"))) {
//                            S_Dialogs.getLiveVideoStopedDialog(getActivity(), "You are not currently connected with stripe. Press ok to connect.", ((dialog, which) -> {
                                //--------------openHere-----------------

                                Stripe_dialogfragment stripe_dialogfragment = new Stripe_dialogfragment();
                                stripe_dialogfragment.show(getActivity().getFragmentManager(), "");

//                            })).show();
                            } else if ((HelperPreferences.get(getActivity()).getString(STRIPE_VERIFIED).equalsIgnoreCase("P"))) {
                             //   S_Dialogs.getLiveVideoStopedDialog(getActivity(), "You have not uploaded you Idenitification Documents. Press ok to upload.", ((dialog, which) -> {
                                    //--------------openHere-----------------

                                    Stripe_image_verification_dialogfragment stripe_dialogfragment = new Stripe_image_verification_dialogfragment();
                                    stripe_dialogfragment.show(getActivity().getFragmentManager(), "");

                             //   })).show();
                            } else {
                                Intent intent = new Intent(getActivity(), AddNewVideos.class);
                                startActivity(intent);
                            }
                        }
                    });

                    dialog.show();

                }
                else if ((HelperPreferences.get(getActivity()).getString(STRIPE_VERIFIED).equalsIgnoreCase("P"))) {
                    final Dialog dialog = new Dialog(getActivity()); // Context, this, etc.
                    dialog.setContentView(R.layout.add_new_prod_dialog);

                    dialog.setCancelable(false);
                    if (dialog.getWindow() != null) {
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.getWindow().setGravity(Gravity.BOTTOM);
                        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
//                    params.gravity = Gravity.CENTER_VERTICAL;
                    }
                    TextView ok = dialog.findViewById(R.id.textOk);
                    TextView maybelater = dialog.findViewById(R.id.textCancel);
                    maybelater.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            Intent intent = new Intent(getActivity(), AddNewVideos.class);
                            startActivity(intent);
                        }
                    });
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            if ((HelperPreferences.get(getActivity()).getString(STRIPE_VERIFIED).equals("") || HelperPreferences.get(getActivity()).getString(STRIPE_VERIFIED).equals("N"))) {
//                            S_Dialogs.getLiveVideoStopedDialog(getActivity(), "You are not currently connected with stripe. Press ok to connect.", ((dialog, which) -> {
                                //--------------openHere-----------------

                                Stripe_dialogfragment stripe_dialogfragment = new Stripe_dialogfragment();
                                stripe_dialogfragment.show(getActivity().getFragmentManager(), "");

//                            })).show();
                            } else if ((HelperPreferences.get(getActivity()).getString(STRIPE_VERIFIED).equalsIgnoreCase("P"))) {
                                //   S_Dialogs.getLiveVideoStopedDialog(getActivity(), "You have not uploaded you Idenitification Documents. Press ok to upload.", ((dialog, which) -> {
                                //--------------openHere-----------------

                                Stripe_image_verification_dialogfragment stripe_dialogfragment = new Stripe_image_verification_dialogfragment();
                                stripe_dialogfragment.show(getActivity().getFragmentManager(), "");

                                //   })).show();
                            } else {
                                Intent intent = new Intent(getActivity(), AddNewVideos.class);
                                startActivity(intent);
                            }
                        }
                    });

                    dialog.show();

                }

                    else {
                        Intent intent = new Intent(getActivity(), AddNewVideos.class);
                        startActivity(intent);
                    }


                break;

            case R.id.cardView2:
                VideoDialog videoDialog=new VideoDialog(getActivity());
                videoDialog.show();

                break;

        }

    }

    private void paymentMehtod()
    {
        btnStripeConnect.performClick();
    }

    public void hideSearch() {

        ((MainActivity) getActivity()).rel_search.setVisibility(View.GONE);
        ((MainActivity) getActivity()).rlFilter.setVisibility(View.GONE);
        ((MainActivity) getActivity()).text_sell.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).text_sell.setText("Sell");
        ((MainActivity) getActivity()).rloptions.setVisibility(View.GONE);
        ((MainActivity) getActivity()).rlBack.setVisibility(View.GONE);
        ((MainActivity) getActivity()).rlMenu.setVisibility(View.GONE);
        ((MainActivity) getActivity()).changeOptionColor(2);
    }


    private void StripConnect() {

        StripeAppmApp = new StripeApp(getActivity(), "geniusAppDeveloper", "ca_EWK1BYRqruSX1X92DbtLY8UiV46ADGoC",
                "sk_test_HDkDbhty58uz3aaJi2TDllrR", "https://developer.android.com", "read_write");

        btnStripeConnect.setStripeApp(StripeAppmApp);
        btnStripeConnect.setConnectMode(StripeApp.CONNECT_MODE.DIALOG);
        btnStripeConnect.addStripeConnectListener(new StripeConnectListener() {

            @Override
            public void onConnected() {
           //     Log.e("Connected_as", "onConnected: " + StripeAppmApp.getUserId());
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
              //  Log.e("Disconnected", "Disconnected: ");
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
            }

        });
    }


}
