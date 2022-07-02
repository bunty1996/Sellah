package com.app.admin.sellah.controller.WebServices;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.app.admin.sellah.model.extra.BannerModel.BannerModel;
import com.app.admin.sellah.model.extra.CardDetails.CardDetailModel;
import com.app.admin.sellah.model.extra.Categories.GetCategoriesModel;
import com.app.admin.sellah.model.extra.ChatHeadermodel.ChattedListModel;
import com.app.admin.sellah.model.extra.DeleteChatModel.DeleteChat;
import com.app.admin.sellah.model.extra.EditProfileModel;
import com.app.admin.sellah.controller.utils.ExpandableListData;
import com.app.admin.sellah.controller.utils.Global;
import com.app.admin.sellah.controller.utils.HelperPreferences;
import com.app.admin.sellah.model.extra.FeaturedPosts.FeaturedPosts;
import com.app.admin.sellah.model.extra.LikeModel.LikeModel;
import com.app.admin.sellah.model.extra.LiveVideoModel.LiveVideoModel;
import com.app.admin.sellah.model.extra.ResendCode.ResendCode;
import com.app.admin.sellah.model.extra.SendOffer.SendOfferModel;
import com.app.admin.sellah.model.extra.commonResults.Common;
import com.app.admin.sellah.model.extra.getProductsModel.GetProductList;
import com.app.admin.sellah.view.CustomDialogs.S_Dialogs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.admin.sellah.controller.utils.SAConstants.Keys.UID;

public class ApisHelper {

    private WebService service;

    public static String auth = "application/json";
    //public static String token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImU4Y2M2MDUyMWM5MjEwMDc0NWU5NGRkNDI4MzI1MmFhZmEwNjIyZWQwMzRkYjRhMDMwMjhhOTE1ODZiOTY1ZThhODc3MDRjOTJjNmM4NDRjIn0.eyJhdWQiOiI1ZGNiYjE1NTY4YzVjZTFmNjQ0Yjk0NzIiLCJqdGkiOiJlOGNjNjA1MjFjOTIxMDA3NDVlOTRkZDQyODMyNTJhYWZhMDYyMmVkMDM0ZGI0YTAzMDI4YTkxNTg2Yjk2NWU4YTg3NzA0YzkyYzZjODQ0YyIsImlhdCI6MTU3MzYzMDMwNCwibmJmIjoxNTczNjMwMzA0LCJleHAiOjE2MDUyNTI3MDQsInN1YiI6IjVkY2JiMTYwNjhjNWNlMWVlZDFmY2IyMiIsInNjb3BlcyI6W119.TK6_tZ8XAbVGh73cM-iRl0Za4VIXYgsMCfXXt_eKfqCYmOVz5xuaYWyR2qXGWDoB7SjB1JCxpal3SmlqbXQUynsArA-yLNE4JSfTiTNSSHZPRvfEFLHxEc1Urnse9OeNGsHztejcb4v4LusayNK-f2QkTp4eXHaxUTEwTwk-OlKzsu4A8hNLSuuiCawgvWoB9oMZkc1JqfdLHZ_4bT64bV5gbWvjt-szMwsqftQARgb-HD9K9WGwz4KcaOLvY0hcjqmSI8ynGrcGAvFnm-vSKnX2uE4FCdRnwi4wGLq7aB5x6sy6p2IwibytAO7eXNZ814JPxJ_AXePP9NbdOKk8tpsdZwoMpTajRtxM98RASqqfF0RHUii2H4H1LG82T62n35EnpO6C9e9b706SWe49-arNLOyykPmaC5dujUFYsRIU_kHr0MkRncsbU1JKQ0LTb3DB1jiyjFXQmEt_Z86HaT90IDPwTmxptQPtcdYH1G3PA0KZRqzLryj-c4nit3yRyAIfGKMOZNJC9bUro3QRYL2YwvuvXr9i0i_0v0ZKBXVpeNZQj5rrskFIcjeE1j5tXS1BeTOm4a47WnQ-UU7k9UXpndKB5zAtaQ8p5yQ0NPo4K-zpMWhKJqEJEyu8Oh2QO2vLN5MWaXFljeVVeyEho6lxNsEgbaCs6d_mXsqXNKg";

    //todo for production token (Live Token)
    public static String token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImY0MGNmMGQxMjZlYjdhNzA4ZDc4MDVkYTVkYTNhY2E0NGNmOTQwYzJhZWQ3ZmQ3YmJiOGQ0MDI2NTMwM2YzNDQyYjU4NWE1Y2U0MjNkNjFhIn0.eyJhdWQiOiI1ZGNiYjE1NTY4YzVjZTFmNjQ0Yjk0NzIiLCJqdGkiOiJmNDBjZjBkMTI2ZWI3YTcwOGQ3ODA1ZGE1ZGEzYWNhNDRjZjk0MGMyYWVkN2ZkN2JiYjhkNDAyNjUzMDNmMzQ0MmI1ODVhNWNlNDIzZDYxYSIsImlhdCI6MTYwNjgwNDAyNiwibmJmIjoxNjA2ODA0MDI2LCJleHAiOjE2MzgzNDAwMjYsInN1YiI6IjVmYzVlMjNhM2NjY2IwMGQxMjdmMGExMiIsInNjb3BlcyI6W119.l-QGQ3Wjuf4FshH7C0o41zXito3kcfFwmzN75bRJB4Nba_Lz8EdCOaTg2bD4zleQhxmlHnTONBb7Eo4iaMC3LxRJ5WEx7nDVl1hR2oE8s6nyLmCze63ye1g0dQcALRiAiWuoIH4tCuWx2_nUv9T_c5Gff7yB4JkQfhDEUf3GQakgsjz5oUGoODLFdLSkC7NSkJenEfzsZ1gyB9t8PpZw1tjKVxelo1KCnHITEPy_WngfRbq_rNR9-tNdNGgiwO52ugy6xULcJlQP7J_Lj5Ds5xZznppki55CmByDT5-oiUyi_3AZpcfmsHuSYHgPiZgL2GSMUomZWicV_jzWhRS2fIe7i-qZQ-J2xrZAbwkoTksmQQqYmeKtVNY-uOto7AyxW4zSdK9ar5vsJE-OGe0kyQe87OQBo2ZcnBm4krU2zHgEvnKiQutdHTmCRQoibbu5VX_zqcx4ueFUADCxtYyclvooVZO2dVVuYRsKBF2TC_H4OO4Oh4TPpRzBsqGkGqsdKT_S7HxnvThXAvYo46hjp-2uoBtZCWOTrZi-UaDYw_9wlLSpW6QK-n9c0WbGFnxEzxGaTbn9vua0CypebqOnOSka1dN4B-S3BHXpY8oS8yB2Iaow5M8YOvWI_sB8Q3ywmxjLZYdMf8FS9Vx_YHAwX_zqIiE5ROo3EoLJNSKbt04";

    //    public static String token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImIyMjhhOGJlNzFhNWVhZjEyMTUxMTQyMWQwNzE3NTM0YTM5OWRmMTg5NjQxZmM4MzY1MzFlNzkyOWNmNmQyODk3ZDVkMGM1MmRkYWExMzI4In0.eyJhdWQiOiI1ZGNiYjE1NTY4YzVjZTFmNjQ0Yjk0NzIiLCJqdGkiOiJiMjI4YThiZTcxYTVlYWYxMjE1MTE0MjFkMDcxNzUzNGEzOTlkZjE4OTY0MWZjODM2NTMxZTc5MjljZjZkMjg5N2Q1ZDBjNTJkZGFhMTMyOCIsImlhdCI6MTU3NDkyMjI2OSwibmJmIjoxNTc0OTIyMjY5LCJleHAiOjE2MDY1NDQ2NjksInN1YiI6IjVkZGY2ODFkNjhjNWNlNGM3NDY4YWFjMiIsInNjb3BlcyI6W119.Q2jT2hDk5MvesTeQm3VLAW2BD8mDSo1y_dBeDytMnKiLmDHL01wAPkq4oNLpXn6vX56z8OqmRbXfDQ66oDw9mOjUAX2tVrTNvSdmyZp2jCiJNgWhLZMQ-R0uqcyhYeFeJKr30mRIUg7FVa6UDgdjCA2Rhys2JuG2y6wNqOZbaiqCTAfSDy_6G2REEEgTMJqG6lpEU0jeEs0L1KRMfY90dDCB_sK8D3ZwJhlPC0YslSoxI7UUfRwWO4TM7lA9f90HNI8F3rGNkH7gHK3dQ_sAA-xlC8FoCfHlIjLpktZlsRy5nZ6RbT3RJD1mFO_KtZgJqIUOD1_jjr8-MmYZ1v_SRS7Bv8O0Frh1gyGFHv0bgsmoBURCKOuYc8x4UBMmI8tSIYF7-5H8FS-Eeak9VdDFmt1bT3lLMhwBxrtRdsNlnbmvliWQqpjk3Sag4DuXJBGjA67fOY9PFPD9Kem6E7-8TL1MrrFDe_GLBj6XcyKA3AHmc3GC6M42gYafk7lb7C89gDkbiWUWxzCWD32DoMEopawemoqDZznGiu_Vqw4c3UwvwmilLRbBCDvw5Hu562t0OHrzTJYUQkJWxspDd1Fkp0nfkcpFZMTCthfOWhQ-rJo29-RWssmUZQpb5QRO1w-5htCCWkM-fFN_NAnp0sxhEwTXg1Rv3YEXnMmaxQC1nl0";
    // public static String token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImIyMjhhOGJlNzFhNWVhZjEyMTUxMTQyMWQwNzE3NTM0YTM5OWRmMTg5NjQxZmM4MzY1MzFlNzkyOWNmNmQyODk3ZDVkMGM1MmRkYWExMzI4In0.eyJhdWQiOiI1ZGNiYjE1NTY4YzVjZTFmNjQ0Yjk0NzIiLCJqdGkiOiJiMjI4YThiZTcxYTVlYWYxMjE1MTE0MjFkMDcxNzUzNGEzOTlkZjE4OTY0MWZjODM2NTMxZTc5MjljZjZkMjg5N2Q1ZDBjNTJkZGFhMTMyOCIsImlhdCI6MTU3NDkyMjI2OSwibmJmIjoxNTc0OTIyMjY5LCJleHAiOjE2MDY1NDQ2NjksInN1YiI6IjVkZGY2ODFkNjhjNWNlNGM3NDY4YWFjMiIsInNjb3BlcyI6W119.Q2jT2hDk5MvesTeQm3VLAW2BD8mDSo1y_dBeDytMnKiLmDHL01wAPkq4oNLpXn6vX56z8OqmRbXfDQ66oDw9mOjUAX2tVrTNvSdmyZp2jCiJNgWhLZMQ-R0uqcyhYeFeJKr30mRIUg7FVa6UDgdjCA2Rhys2JuG2y6wNqOZbaiqCTAfSDy_6G2REEEgTMJqG6lpEU0jeEs0L1KRMfY90dDCB_sK8D3ZwJhlPC0YslSoxI7UUfRwWO4TM7lA9f90HNI8F3rGNkH7gHK3dQ_sAA-xlC8FoCfHlIjLpktZlsRy5nZ6RbT3RJD1mFO_KtZgJqIUOD1_jjr8-MmYZ1v_SRS7Bv8O0Frh1gyGFHv0bgsmoBURCKOuYc8x4UBMmI8tSIYF7-5H8FS-Eeak9VdDFmt1bT3lLMhwBxrtRdsNlnbmvliWQqpjk3Sag4DuXJBGjA67fOY9PFPD9Kem6E7-8TL1MrrFDe_GLBj6XcyKA3AHmc3GC6M42gYafk7lb7C89gDkbiWUWxzCWD32DoMEopawemoqDZznGiu_Vqw4c3UwvwmilLRbBCDvw5Hu562t0OHrzTJYUQkJWxspDd1Fkp0nfkcpFZMTCthfOWhQ-rJo29-RWssmUZQpb5QRO1w-5htCCWkM-fFN_NAnp0sxhEwTXg1Rv3YEXnMmaxQC1nl0";
    //todo for staging token
    // public static String token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjhmZGRiM2EwN2E4YTJhZTA2YjYxMDBlMTY3NWJlMWEyMGM4ZTE0YzFiMDM5YTQ5N2E0YmQzM2E4MmFjMTQ5YmIzMTEzOTQwZjQzYzkxODgxIn0.eyJhdWQiOiI1ZTJhZGE5MDhjMDIyMzRkOTYzNjU4MjIiLCJqdGkiOiI4ZmRkYjNhMDdhOGEyYWUwNmI2MTAwZTE2NzViZTFhMjBjOGUxNGMxYjAzOWE0OTdhNGJkMzNhODJhYzE0OWJiMzExMzk0MGY0M2M5MTg4MSIsImlhdCI6MTU3OTg2Njg5NywibmJmIjoxNTc5ODY2ODk3LCJleHAiOjE2MTE0ODkyOTcsInN1YiI6IjVlMmFkYjExYTg0YTc3MDU4YTZiZjVjMiIsInNjb3BlcyI6W119.FOcBB83vO7t_oVfdBrQbbr_aiRGMp-ZLR6ItBV4hfrcwSCn6Gjc4nAUlnzUSF1jrQgRtTA7YocUQ0tt8cjVsjGvRcNU4WchB9ThAiLRrI5xQgm0iUxltWd_OR-81iQ8O3apiPhyfhNAsPyiy9wPZ2sWNXBLu-vU8Qs4rtsEwapZc-CX066PRphoK7F7gDcIpXG4sa2J_cBG2XSAZS_SR1_zQJxXZKklQiqLXoxXYncWMCQ5b03XBMDafLf1F_Pn1n5PaTB-p3BUdCP7WQL9wagKf9D2v4GSOiooJEDYzqEAZBNlsmF8mFTufF6EB8b2HvC1XCmtXr1L0aavzdVTTAbei8vcS7gpuLot54PC5wNuXx7ycFebBuLJ_sRlZYwLMecxOS9h359Um39bumFTiXfCy1KI3Xjgi0eAk6KraHf2kLn4kSQTVaRvi-xwDgrwbEB1T5Ocl0d57ct_wt8-kA6DltUzJzNPCF97E3TvdIykyNZD-27rNLgYpxboe8uM2a-GJzNDO55LO8y_xqMAtDFdm2L7weV2hqtgLY-esJv8jeOYoPZ_PR-XTP3-e_73YFxTGm_lFoYwQNM43fsQdSTyQeEZmHdZy4dk4q6Drsbf9yFyqhdRftzOynLqPoA7dUAvXMQJzlBoJ3zz2i7aEAfVyobGHWAwqlyG41AEaHp0";

  //  public static String token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6ImE2Mzg3ZWNiYzQ5MDEwMDg1N2NkYzI4ZDViMTQxZmFmY2IyYTQ5MTQ2OGRiYTVjZGUwMTA1MzQwMDZlYmVhZTA0N2ZiOWFmODFlNjY4MmM0In0.eyJhdWQiOiI1ZTUzYjE5MjZhZDQ5MzRiZTQ3ZDNmMzIiLCJqdGkiOiJhNjM4N2VjYmM0OTAxMDA4NTdjZGMyOGQ1YjE0MWZhZmNiMmE0OTE0NjhkYmE1Y2RlMDEwNTM0MDA2ZWJlYWUwNDdmYjlhZjgxZTY2ODJjNCIsImlhdCI6MTU4MjU0MzI5MywibmJmIjoxNTgyNTQzMjkzLCJleHAiOjE2MTQxNjU2OTMsInN1YiI6IjVlNTNiMWJkYTI3OTEzNDk1NDQ0NDJjMiIsInNjb3BlcyI6W119.N4NMF_xXJVz3g3sNz1ECIV7tce5yVExexlrhQnViZADqx6SOYxUeW4PwmrAWO9hnNEnXyVdATz6ElJ_fsNJoCG5RvCS1uVllHXoxzlujA1DtHcHZ3ca5OnUVK5P3CjQiKCCRO6FUxRfETCXQ4lCuxDxEJtGzU3Jg-GrU42SBHMjUYKff0vNptTuDrW0JkHUQ8DYZIpx0rrX17jGV2GIUZcK19x1lP2dBuVdHbnrwE9MLM3PsPY93KwAyWqEl-rMXdQHyWdqhbsirRUbG-dfrAxn-Gf3mpv-UHVzp8wBiTn2uxFy8aZkNH1-bsIFz5dl82SChYb-smBazhrE9g8d4ry8hQ-xhpQqSeObILoLXfdFgvf4-a1KG0lByxC2lOwsRQrJ2eJc9y5_TxogYmE2OTiyegQmgnIREDHG47_-Lh5rONf4z5RomV8nVl7-71MdQZ-9KO7jWzGsnIT_n4wbs_rjG8TrLPbV18Kck1LOC0RkhzDOdJW2UymRid9pkB9bkAVme0dCFBc4i1DVtWPOsZ5cqOdGxJSwBL9ygZ9SQq4DU4Lp18E--_azM4KF1YQGqtKrFL9dnMv2gq4SpkXIcpXeNh9MOJS40t5QN9U5c6dYpfsw_zH1jNOUbOHCDTch19vF_WMdB62JCocUEEnmYzLF4qfRCnQAcmcnJw_y5_70";

    public ApisHelper() {
        service = Global.WebServiceConstants.getRetrofitinstance();
    }

    Call<DeleteChat> deleteChatCall;

    public void getDeleteChat(String user_id, String room_id, OnDeleteChat call_back_deletechat) {

        deleteChatCall = service.deleteChatApi(auth, token, user_id, room_id);

        deleteChatCall.enqueue(new Callback<DeleteChat>() {
            @Override
            public void onResponse(Call<DeleteChat> call, Response<DeleteChat> response) {

                try {

                    if (response.isSuccessful()) {

                        call_back_deletechat.onDeleteChatSuccess(response.body());

                    } else {

                        try {

                            Log.e("deletechat", "un-Success : " + response.errorBody().string());

                        } catch (IOException e) {

                            e.printStackTrace();

                        }

                    }

                } catch (Exception e) {

                }

            }

            @Override
            public void onFailure(Call<DeleteChat> call, Throwable t) {

                call_back_deletechat.onDeleteChatFailure();
               Log.e("deletechat", "failure : " + t.getMessage());

            }

        });

    }

    Call<com.app.admin.sellah.model.extra.FeaturedPosts.FeaturedPosts> featuredPostsCall;

    public void getFeaturedPosts(String page, OnFeaturedPosts callback_featrured) {
        featuredPostsCall = service.getFeaturedPostsApi(auth, token, page);
        featuredPostsCall.enqueue(new Callback<FeaturedPosts>() {
            @Override
            public void onResponse(Call<FeaturedPosts> call, Response<FeaturedPosts> response) {
                if (response.isSuccessful()) {
                    callback_featrured.onFeaturedPostsSuccess(response.body());

                } else {

                    try {
                        Log.e("categories", "un-Success : " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<FeaturedPosts> call, Throwable t) {
                callback_featrured.onFeaturedPostsFailure();
                Log.e("categories", "failure : " + t.getMessage());
            }
        });

    }

    Call<GetCategoriesModel> categoriesModelCall1;

    public void getCategories(String user_id, GetCategoryCallback callback) {

        //ExpandableListData.setDataOnfaliur();
        categoriesModelCall1 = service.getCategoryApi(auth, token/*user_id*/);
        categoriesModelCall1.enqueue(new Callback<GetCategoriesModel>() {
            @Override
            public void onResponse(Call<GetCategoriesModel> call, Response<GetCategoriesModel> response) {

                Log.e("categryres", response.code() + "//");

                if (response.isSuccessful()) {

                    Log.e("categories_", "Success : " + response.body().getMessage());
                    callback.onGetCategorySuccess();
                    ExpandableListData.setData(response.body());

                } else {

                    ExpandableListData.setDataOnfaliur();
                    try {
                        Log.e("categories", "un-Success : " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(Call<GetCategoriesModel> call, Throwable t) {
                ExpandableListData.setDataOnfaliur();
                callback.onGetCategoryFailure();
                Log.e("categories", "failure : " + call.toString() + "//" + t.getMessage());
            }

        });

    }

    Call<BannerModel> categoriesModelCall;

    public void getBanner(String user_id, OnGetDataListners listners) {

        categoriesModelCall = service.getbannersApi(auth, token, user_id);
        categoriesModelCall.enqueue(new Callback<BannerModel>() {
            @Override
            public void onResponse(Call<BannerModel> call, Response<BannerModel> response) {
                if (response.isSuccessful()) {
                    Log.e("getBannerApi", "Success : " + response.body().getMessage());
                    if (response.body().getStatus().equalsIgnoreCase("1")) {
                        listners.onGetDataSuccess(response.body());
                    }
                } else {
                    listners.onGetDataFailure();
                    try {
                        Log.e("getBannerApi", "un-Success : " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<BannerModel> call, Throwable t) {
                listners.onGetDataFailure();
                Log.e("categories", "failure : " + t.getMessage());
            }
        });

    }

    Call<CardDetailModel> stripePaymentApi;

    public void getCardApi(Context context, OnGetCardDataListners listners) {

        stripePaymentApi = service.getCardApi(auth, token, HelperPreferences.get(context).getString(UID));

        stripePaymentApi.enqueue(new Callback<CardDetailModel>() {
            @Override
            public void onResponse(Call<CardDetailModel> call, Response<CardDetailModel> response) {

                try {

                    Gson gson = new GsonBuilder().create();

                    if (response.isSuccessful()) {

                        if (response.body().getStatus().equalsIgnoreCase("1")) {

                            listners.onGetDataSuccess(response.body());

                        }


                    } else {

                        listners.onGetDataFailure();

                        try {
                            Log.e("GetCard_error", response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                } catch (Exception e) {

                }

            }

            @Override
            public void onFailure(Call<CardDetailModel> call, Throwable t) {


                try {

                    listners.onGetDataFailure();
                    Log.e("GetCard_failure", t.getMessage());

                } catch (Exception e) {

                }

            }

        });

    }

    Call<ChattedListModel> chatedListCall;

    public void getChattedUsersListApi(Context context, OnGetChatedListDataListners listDataListners) {
        Dialog dialog = S_Dialogs.getLoadingDialog(context);
        chatedListCall = service.getChattedUsersApi(auth, token, HelperPreferences.get(context).getString(UID));
        chatedListCall.enqueue(new Callback<ChattedListModel>() {
            @Override
            public void onResponse(Call<ChattedListModel> call, Response<ChattedListModel> response) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (response.isSuccessful()) {
                    Log.e("working", "onResponse: " );
                    if (response.body().getStatus().equalsIgnoreCase("1")) {
                        listDataListners.onGetChattedListSuccess(response.body(), dialog);
                    }
                } else {
                    Log.e("working", response.code()+"" );
                    listDataListners.onGetChattedListFailure();
                }
            }

            @Override
            public void onFailure(Call<ChattedListModel> call, Throwable t) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                listDataListners.onGetChattedListFailure();
                Log.e("GetChatListApi", "onFailure: " + t.getMessage());
            }
        });
    }

    public void getSeaarchResultApi(Context context, SearchResultCallback listDataListners, String search_keyword) {
        Dialog dialog = S_Dialogs.getLoadingDialog(context);
        dialog.show();
        Call<GetProductList> chatedListCall = service.searchProductApi(auth, token, search_keyword, "name");
        chatedListCall.enqueue(new Callback<GetProductList>() {
            @Override
            public void onResponse(Call<GetProductList> call, Response<GetProductList> response) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("1")) {
                        listDataListners.onProductListSuccess(response.body());
                    }
                } else {
                    listDataListners.onProductListFailure();
                }
            }

            @Override
            public void onFailure(Call<GetProductList> call, Throwable t) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                listDataListners.onProductListFailure();
                Log.e("GetChatListApi", "onFailure: " + t.getMessage());
            }
        });
    }

    public void updateProfileApi(EditProfileModel model, Context context, UpdateProfileCallback callback) {
        Dialog dialog = S_Dialogs.getLoadingDialog(context);
        dialog.show();

        Call<Common> editProfileCall = service.editProfileApi(auth, token, model.getUser_id(), model.getUsername()
                , model.getDescription(), model.getCountry_code(), model.getPhone_number(), model.getAbout(), model.getShipping_policy()
                , model.getReturn_policy(), model.getAddress_name(), model.getAddress_1(), model.getAddress_2(), model.getPostal_code(), model.getState()
                , model.getAddress_city(), model.getImage(), model.getEdit_mode());

        editProfileCall.enqueue(new Callback<Common>() {
            @Override
            public void onResponse(Call<Common> call, Response<Common> response) {

                if (response.isSuccessful()) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    callback.onProfileUpdateSuccess(response.body().getMessage());

                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    try {
                        Log.e("EditProfile", "onFailure: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    callback.onProfileUpdateFailure();
                }
            }

            @Override
            public void onFailure(Call<Common> call, Throwable t) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Log.e("EditProfile", "onFailure: " + t.getMessage());
                callback.onProfileUpdateFailure();
            }
        });
    }

    public void editCommentApi(Context context, String commentId, String content, EditCommentCallback callback) {

        Dialog dialog = S_Dialogs.getLoadingDialog(context);
        dialog.show();
        Call<Common> editProfileCall = service.editCommentApi(auth, token, HelperPreferences.get(context).getString(UID), commentId, content);
        editProfileCall.enqueue(new Callback<Common>() {
            @Override
            public void onResponse(Call<Common> call, Response<Common> response) {

                if (response.isSuccessful()) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    callback.onCommentEditionSuccess(response.body().getMessage());

                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    callback.onCommentEditionFailure();
                }
            }

            @Override
            public void onFailure(Call<Common> call, Throwable t) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                callback.onCommentEditionFailure();
            }
        });
    }

    public void deleteCommentApi(Context context, String commentId, DeleteCommentCallback callback) {

        Dialog dialog = S_Dialogs.getLoadingDialog(context);
        dialog.show();
        Call<Common> editProfileCall = service.deleteCommentApi(auth, token, HelperPreferences.get(context).getString(UID), commentId);
        editProfileCall.enqueue(new Callback<Common>() {
            @Override
            public void onResponse(Call<Common> call, Response<Common> response) {

                if (response.isSuccessful()) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    callback.onCommentdeletionSuccess(response.body().getMessage());

                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    callback.onCommentdeletionFailure();
                }
            }

            @Override
            public void onFailure(Call<Common> call, Throwable t) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                callback.onCommentdeletionFailure();
            }
        });
    }

    public void reportCommentApi(Context context, String reason, String otherUserId, String commentId, ReportApi.ReportCallback callback, ReportApi.ReportErrorCallBack errorCallBack) {
        Dialog dialog = S_Dialogs.getLoadingDialog(context);
        dialog.show();
        Call<Common> reportCall = service.reportCommentApi(auth, token, HelperPreferences.get(context).getString(UID), reason, commentId, otherUserId);
        reportCall.enqueue(new Callback<Common>() {
            @Override
            public void onResponse(Call<Common> call, Response<Common> response) {
                if (response.isSuccessful()) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    callback.onReportSubmit(response.body().getMessage());
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    errorCallBack.onReportError();
                }
            }

            @Override
            public void onFailure(Call<Common> call, Throwable t) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                errorCallBack.onReportError();
            }
        });
    }

    public void getProfileData(Context context, GetProfileCallback callback) {
        Dialog dialog = S_Dialogs.getLoadingDialog(context);
        dialog.show();
        Log.e("user", "" + HelperPreferences.get(context).getString(UID));
        Call<JsonObject> getProfileCall = service.getProfileApi1(auth, token, HelperPreferences.get(context).getString(UID));
        getProfileCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {

                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    callback.onGetProfileSuccess(response.body());


                } else {

                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    callback.onGetProfileFailure();

                    try {
                        Log.e("Profile_error", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                callback.onGetProfileFailure();
                Log.e("Profile_error", "onFailure: " + t.getMessage());
            }
        });
    }

    Call<LiveVideoModel> getVideodataCall;

    public void getLiveVideoData(Context context, String pageNo, String catId, GetLiveVideoCallback callback) {
        Dialog dialog = S_Dialogs.getLoadingDialog(context);
        dialog.show();
        getVideodataCall = service.getVideoListApi(auth, token, HelperPreferences.get(context).getString(UID), pageNo, catId);
        getVideodataCall.enqueue(new Callback<LiveVideoModel>() {
            @Override
            public void onResponse(Call<LiveVideoModel> call, Response<LiveVideoModel> response) {
                if (response.isSuccessful()) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    callback.onGetLiveVideoSuccess(response.body());
                    Log.e("LiveVideo_success", "" + response.body().getList().toString());
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    callback.onGetLiveVideoFailure();
                    try {
                        Log.e("LiveVideo_error", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<LiveVideoModel> call, Throwable t) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                callback.onGetLiveVideoFailure();
                Log.e("Profile_error", "onFailure: " + t.getMessage());
            }
        });
    }

    Call<LiveVideoModel> streamedVideoListCall;

    public void getStreamedVideoData(Context context, String pageNo, GetLiveVideoCallback callback) {
        Dialog dialog = S_Dialogs.getLoadingDialog(context);
        dialog.show();
        streamedVideoListCall = service.getStreamedVideoList(auth, token, HelperPreferences.get(context).getString(UID), pageNo);
        streamedVideoListCall.enqueue(new Callback<LiveVideoModel>() {
            @Override
            public void onResponse(Call<LiveVideoModel> call, Response<LiveVideoModel> response) {
                if (response.isSuccessful()) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    callback.onGetLiveVideoSuccess(response.body());
                    Log.e("LiveVideo_success", "" + response.body().getList().toString());
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    callback.onGetLiveVideoFailure();
                    try {
                        Log.e("LiveVideo_error", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<LiveVideoModel> call, Throwable t) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                callback.onGetLiveVideoFailure();
                Log.e("Profile_error", "onFailure: " + t.getMessage());
            }
        });
    }

    public void sendOfferApi(Context context, String receiverId, String groupId, String productid, String productName, String productPrice, SendOfferCallback callback) {
        Dialog dialog = S_Dialogs.getLoadingDialog(context);
        dialog.show();
        Call<SendOfferModel> sendOfferApiCall = service.sendOfferApi(auth, token, HelperPreferences.get(context).getString(UID), receiverId, groupId, productid, productName, productPrice);
        sendOfferApiCall.enqueue(new Callback<SendOfferModel>() {
            @Override
            public void onResponse(Call<SendOfferModel> call, Response<SendOfferModel> response) {
                if (response.isSuccessful()) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    callback.onSendOfferSuccess(response.body().getMessage());
                    Log.e("SendOffer", "" + response.body().getMessage().toString());
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    callback.onSendOfferFailure();
                    try {
                        Log.e("SendOffer", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<SendOfferModel> call, Throwable t) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                callback.onSendOfferFailure();
                Log.e("SendOffer", "onFailure: " + t.getMessage());
            }
        });
    }

    public void readNotificationApi(Context context, String notiId, String type, ReadNotificationCallback callback) {
        Log.e("Notification_id", "readNotificationApi: " + notiId);
        Dialog dialog = S_Dialogs.getLoadingDialog(context);
        dialog.show();
        Call<Common> readNotificationApiCall = service.readNotificationApi(auth, token, HelperPreferences.get(context).getString(UID), notiId, "1", type);
        readNotificationApiCall.enqueue(new Callback<Common>() {
            @Override
            public void onResponse(Call<Common> call, Response<Common> response) {
                if (response.isSuccessful()) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    callback.onReadNotificationSuccess(response.body().getMessage());
                    Log.e("ReadNotification", "" + response.body().getMessage().toString());
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    callback.onReadNotificationFailure();
                    try {
                        Log.e("ReadNotification", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Common> call, Throwable t) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                callback.onReadNotificationFailure();
                Log.e("ReadNotification", "onFailure: " + t.getMessage());
            }
        });
    }

    public void likeCommentApi(Context context, String commentId, String likeStatus, LikeCommentCallback callback) {

        Dialog dialog = S_Dialogs.getLoadingDialog(context);
        dialog.show();
        Call<LikeModel> likeCommentApiCall = service.likeCommentApi(auth, token, HelperPreferences.get(context).getString(UID), commentId, likeStatus.toUpperCase());
        likeCommentApiCall.enqueue(new Callback<LikeModel>() {
            @Override
            public void onResponse(Call<LikeModel> call, Response<LikeModel> response) {
                if (response.isSuccessful()) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    callback.onLikeCommentSuccess(response.body().getMessage());
                    Log.e("Like_Comment", "onSuccess" + response.body().getMessage().toString());
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    callback.onLikeCommentFailure();
                    try {
                        Log.e("Like_Comment", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<LikeModel> call, Throwable t) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                callback.onLikeCommentFailure();
                Log.e("Like_Comment", "onFailure: " + t.getMessage());
            }
        });
    }

    public void LogoutApi(Context context, LogOutCallback callback) {

        Dialog dialog = S_Dialogs.getLoadingDialog(context);
        dialog.show();
        Call<Common> logoutApiCall = service.logoutApi(auth, token, HelperPreferences.get(context).getString(UID));
        logoutApiCall.enqueue(new Callback<Common>() {
            @Override
            public void onResponse(Call<Common> call, Response<Common> response) {
                if (response.isSuccessful()) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    callback.onLogOutSuccess(response.body().getMessage());
                    Log.e("logout", "onSuccess" + response.body().getMessage().toString());
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    callback.onLogOutFailure();
                    try {
                        Log.e("logout", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Common> call, Throwable t) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                callback.onLogOutFailure();
                Log.e("logout", "onFailure: " + t.getMessage());
            }
        });
    }

    public void linkStripApi(Context context, String stripeId, StripeConnectCallback callback) {
        Dialog dialog = S_Dialogs.getLoadingDialog(context);
        Call<Common> linkStripAccountCall = service.linkStripAccount(auth, token, HelperPreferences.get(context).getString(UID), stripeId);
        linkStripAccountCall.enqueue(new Callback<Common>() {
            @Override
            public void onResponse(Call<Common> call, Response<Common> response) {
                if (response.isSuccessful()) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    callback.onStripeConnectSuccess(response.body().getMessage());
                    Log.e("StripeConnect", "" + response.body().getMessage());
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    callback.onStripeConnectFailure();
                    try {
                        Log.e("StripeConnect", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Common> call, Throwable t) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                callback.onStripeConnectFailure();
                Log.e("Profile_error", "onFailure: " + t.getMessage());
            }
        });
    }

    public void getCategoryLiveVideo(Context context, String catId, SortLiveVideo callback) {
        Dialog dialog = S_Dialogs.getLoadingDialog(context);
        Call<LiveVideoModel> linkStripAccountCall = service.getliveVideoFilter(auth, token, catId);
        linkStripAccountCall.enqueue(new Callback<LiveVideoModel>() {
            @Override
            public void onResponse(Call<LiveVideoModel> call, Response<LiveVideoModel> response) {
                if (response.isSuccessful()) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    callback.onSortLiveVideoSuccess(response.body());
                    Log.e("StripeConnect", "" + response.body().getMessage());
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    callback.onSortLiveVideoFailure();
                    try {
                        Log.e("StripeConnect", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<LiveVideoModel> call, Throwable t) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                callback.onSortLiveVideoFailure();
                Log.e("Profile_error", "onFailure: " + t.getMessage());
            }
        });
    }

    public void sendOtpChangePassApi(Context context, OTPSentCallBack callBack) {
        Dialog loadingdialog = S_Dialogs.getLoadingDialog(context);
        loadingdialog.show();

        Call<ResendCode> changePassCall = service.sendOtpChangePass(auth, token, HelperPreferences.get(context).getString(UID));
        changePassCall.enqueue(new Callback<ResendCode>() {
            @Override
            public void onResponse(Call<ResendCode> call, Response<ResendCode> response) {
                if (response.isSuccessful()) {

                    if (loadingdialog != null && loadingdialog.isShowing()) {
                        loadingdialog.dismiss();
                    }
                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    callBack.onOTPSentSuccess(response.body());

                } else {
                    if (loadingdialog != null && loadingdialog.isShowing()) {
                        loadingdialog.dismiss();
                    }
                    Toast.makeText(context, "Unable to send OTP on your registered mobile number at the movement.", Toast.LENGTH_SHORT).show();
                    callBack.onOTPSentFailure();
                    try {
                        Log.e("ChagePassErrorOTP", " : " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResendCode> call, Throwable t) {
                if (loadingdialog != null && loadingdialog.isShowing()) {
                    loadingdialog.dismiss();
                }
                callBack.onOTPSentFailure();
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface OnGetDataListners {
        void onGetDataSuccess(BannerModel body);

        void onGetDataFailure();
    }

    public interface OTPSentCallBack {
        void onOTPSentSuccess(ResendCode body);

        void onOTPSentFailure();
    }

    public interface OnGetCardDataListners {
        void onGetDataSuccess(CardDetailModel body);

        void onGetDataFailure();
    }

    public interface OnDeleteChat {
        void onDeleteChatSuccess(DeleteChat body);

        void onDeleteChatFailure();
    }

    public interface OnGetChatedListDataListners {
        void onGetChattedListSuccess(ChattedListModel body, Dialog dialog);

        void onGetChattedListFailure();
    }

    public interface SearchResultCallback {
        void onProductListSuccess(GetProductList body);

        void onProductListFailure();
    }

    public interface UpdateProfileCallback {
        void onProfileUpdateSuccess(String msg);

        void onProfileUpdateFailure();
    }

    public interface EditCommentCallback {
        void onCommentEditionSuccess(String msg);

        void onCommentEditionFailure();
    }

    public interface DeleteCommentCallback {
        void onCommentdeletionSuccess(String msg);

        void onCommentdeletionFailure();
    }

    public interface GetProfileCallback {
        void onGetProfileSuccess(JsonObject msg);

        void onGetProfileFailure();
    }

    public interface GetCategoryCallback {
        void onGetCategorySuccess();

        void onGetCategoryFailure();
    }

    public interface GetLiveVideoCallback {
        void onGetLiveVideoSuccess(LiveVideoModel msg);

        void onGetLiveVideoFailure();
    }

    public interface ReadNotificationCallback {
        void onReadNotificationSuccess(String msg);

        void onReadNotificationFailure();
    }

    public interface LikeCommentCallback {
        void onLikeCommentSuccess(String msg);

        void onLikeCommentFailure();
    }

    public interface SendOfferCallback {
        void onSendOfferSuccess(String msg);

        void onSendOfferFailure();
    }

    public interface LogOutCallback {
        void onLogOutSuccess(String msg);

        void onLogOutFailure();
    }

    public interface StripeConnectCallback {
        void onStripeConnectSuccess(String msg);

        void onStripeConnectFailure();
    }

    public interface SortLiveVideo {
        void onSortLiveVideoSuccess(LiveVideoModel videoList);

        void onSortLiveVideoFailure();
    }

    public interface OnFeaturedPosts {
        void onFeaturedPostsSuccess(FeaturedPosts videoList);

        void onFeaturedPostsFailure();
    }

    public void cancel_banner_request() {
        if (categoriesModelCall != null) {
            categoriesModelCall.cancel();
        }

    }

    public void cancel_chatlist() {
        if (chatedListCall != null) {
            chatedListCall.cancel();
        }

    }

    public void getcategoriesmodel1_cancel() {
        if (categoriesModelCall1 != null) {
            categoriesModelCall1.cancel();
        }
    }

    public void cancel_striipe_request() {
        if (stripePaymentApi != null) {
            stripePaymentApi.cancel();
        }
    }

    public void getvideodata_cancel() {
        if (getVideodataCall != null) {
            getVideodataCall.cancel();
        }
    }

    public void streameedvideolist_cancel() {
        if (streamedVideoListCall != null) {
            streamedVideoListCall.cancel();
        }
    }

}
