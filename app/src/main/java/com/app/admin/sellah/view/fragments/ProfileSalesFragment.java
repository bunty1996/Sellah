package com.app.admin.sellah.view.fragments;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.admin.sellah.R;
import com.app.admin.sellah.controller.WebServices.WebService;
import com.app.admin.sellah.controller.utils.Global;
import com.app.admin.sellah.controller.utils.HelperPreferences;
import com.app.admin.sellah.model.extra.SaleModelClass;
import com.app.admin.sellah.model.extra.commonResults.Common;
import com.app.admin.sellah.model.extra.getProductsModel.GetProductList;
import com.app.admin.sellah.view.CustomDialogs.S_Dialogs;
import com.app.admin.sellah.view.adapter.SalesAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.admin.sellah.controller.WebServices.ApisHelper.auth;
import static com.app.admin.sellah.controller.WebServices.ApisHelper.token;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.UID;

public class ProfileSalesFragment extends Fragment {


    ArrayList<SaleModelClass> saleList = new ArrayList<>();
    RecyclerView saleRecycler;
    SalesAdapter salesAdapter;

    View view;
    WebService service;
    GetProductList forSaleProducts;

    SalesAdapter.TabTextController controller;
    @BindView(R.id.img_no_data)
    ImageView imgNoData;
    Unbinder unbinder;
    @BindView(R.id.ll_no_product)
    LinearLayout llNoProduct;
    @BindView(R.id.reload)
    Button reload;
    @BindView(R.id.ll_no_network)
    LinearLayout llNoNetwork;


    public void onAttachToParentFragment(Fragment fragment) {

        try {
            controller = (SalesAdapter.TabTextController) fragment;

        } catch (ClassCastException e) {
            throw new ClassCastException(
                    fragment.toString() + " must implement OnPlayerSelectionSetListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onAttachToParentFragment(getParentFragment());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sales_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        saleRecycler = view.findViewById(R.id.salesRecycler);
        service = Global.WebServiceConstants.getRetrofitinstance();

        if (Global.NetworStatus.isOnline(getActivity()))
        {
            getSaledata();
        }
        else
        {
            saleRecycler.setVisibility(View.GONE);
            llNoNetwork.setVisibility(View.VISIBLE);
            llNoProduct.setVisibility(View.GONE);
        }

        return view;
    }

    private void setSalesData(GetProductList list) {
        salesAdapter = new SalesAdapter(list, getActivity(), true, (productid, pos) -> {
            S_Dialogs.getConfirmation(getActivity(),getActivity().getResources().getString(R.string.dialog_title_mark_as_sold), ((dialog1, which) -> {
                removeSaledata(productid, pos);
            })).show();
        }, (productid, pos) -> {
            S_Dialogs.getConfirmation(getActivity(), getActivity().getResources().getString(R.string.dialog_title_delete_product), ((dialog1, which) -> {
                deleteSaledata(productid, pos);
            })).show();
        }, null);
        LinearLayoutManager horizontalLayoutManager = new GridLayoutManager(getActivity(), 2);
        saleRecycler.setLayoutManager(horizontalLayoutManager);
        saleRecycler.setAdapter(salesAdapter);
        controller.tabTextController(forSaleProducts != null ? forSaleProducts.getResult().size() : 0);
        if (!(forSaleProducts.getResult().size() > 0)) {
            showErrorMsg();
        } else {
            hideErrorMsg();
        }
    }

    public int getitemCount() {
        if (forSaleProducts != null) {
            return forSaleProducts.getResult().size();
        } else {
            return 0;
        }
    }

    Call<GetProductList> recordsCall;

    private void getSaledata() {

        recordsCall = service.getForSalelistApi(auth,token,HelperPreferences.get(getActivity()).getString(UID));
        recordsCall.enqueue(new Callback<GetProductList>() {
            @Override
            public void onResponse(Call<GetProductList> call, Response<GetProductList> response) {

                if (response.isSuccessful()) {

                    if (response.body().getStatus().equalsIgnoreCase("1")) {
                        forSaleProducts = response.body();
                  //      Log.e("resultValue",forSaleProducts.getResult().size()+"");



                        if (getActivity()!=null)

                                setSalesData(forSaleProducts);
                    }
                } else {
                    showErrorMsg();
                }
            }

            @Override
            public void onFailure(Call<GetProductList> call, Throwable t) {
            //    Log.e("successMethod",t.getMessage()+"  error");
            }
        });
    }

    Call<Common> markAsSoldCall;

    private void removeSaledata(String productId, int pos) {
        markAsSoldCall = service.markProductAsSoldApi(auth,token,HelperPreferences.get(getActivity()).getString(UID), productId);
        markAsSoldCall.enqueue(new Callback<Common>() {
            @Override
            public void onResponse(Call<Common> call, Response<Common> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("1")) {
                        forSaleProducts.getResult().remove(pos);
                        salesAdapter.notifyItemRemoved(pos);
                        salesAdapter.notifyItemRangeChanged(pos, forSaleProducts.getResult().size());
                        controller.tabTextController(forSaleProducts != null ? forSaleProducts.getResult().size() : 0);
                        if (!(forSaleProducts.getResult().size() > 0)) {
                            showErrorMsg();
                        } else {
                            hideErrorMsg();
                        }
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<Common> call, Throwable t) {

            }
        });
    }

    private void deleteSaledata(String productId, int pos) {
        Call<Common> deleteItemCall = service.deleteProductApi(auth,token,HelperPreferences.get(getActivity()).getString(UID), productId);
        deleteItemCall.enqueue(new Callback<Common>() {
            @Override
            public void onResponse(Call<Common> call, Response<Common> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("1")) {
                        forSaleProducts.getResult().remove(pos);
                        salesAdapter.notifyItemRemoved(pos);
                        salesAdapter.notifyItemRangeChanged(pos, forSaleProducts.getResult().size());
                        controller.tabTextController(forSaleProducts != null ? forSaleProducts.getResult().size() : 0);
                        if (!(forSaleProducts.getResult().size() > 0)) {
                            showErrorMsg();
                        } else {
                            hideErrorMsg();
                        }
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<Common> call, Throwable t) {

            }
        });
    }

    public void showErrorMsg() {
        if (view != null) {
            saleRecycler.setVisibility(View.GONE);
           llNoNetwork.setVisibility(View.GONE);
           llNoProduct.setVisibility(View.VISIBLE);
        }
    }

    public void hideErrorMsg() {
        if (view != null) {
            saleRecycler.setVisibility(View.VISIBLE);
            llNoNetwork.setVisibility(View.GONE);
            llNoProduct.setVisibility(View.GONE);

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recordsCall != null) {
            recordsCall.cancel();
        }
        if (markAsSoldCall != null) {
            markAsSoldCall.cancel();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.ll_no_network)
    public void onViewClicked() {

        if (Global.NetworStatus.isOnline(getActivity()))
        {
            getSaledata();
        }
        else
        {
            saleRecycler.setVisibility(View.GONE);
            llNoNetwork.setVisibility(View.VISIBLE);
            llNoProduct.setVisibility(View.GONE);
        }

    }

}
