package com.app.admin.sellah.view.fragments;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.app.admin.sellah.R;
import com.app.admin.sellah.controller.WebServices.ApisHelper;
import com.app.admin.sellah.controller.WebServices.WebService;
import com.app.admin.sellah.controller.utils.Global;
import com.app.admin.sellah.model.extra.FeaturedPosts.FeaturedPosts;
import com.app.admin.sellah.model.extra.FeaturedPosts.ResultFeatured;
import com.app.admin.sellah.view.CustomDialogs.S_Dialogs;
import com.app.admin.sellah.view.CustomViews.TouchDetectableScrollView;
import com.app.admin.sellah.view.activities.MainActivity;
import com.app.admin.sellah.view.adapter.FeaturedPostsAdapter;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import static com.app.admin.sellah.controller.utils.Global.getTotalHeightofGridRecyclerView;

public class ShowFeaturedPosts extends Fragment {

    @BindView(R.id.nolivevideo_text)
    LinearLayout nolivevideoText;
    @BindView(R.id.tag_recycler)
    RecyclerView tagRecycler;
    @BindView(R.id.pb_home)
    ProgressBar pbHome;
    @BindView(R.id.txt_no_more_item)
    TextView txtNoMoreItem;
    @BindView(R.id.img_data_error)
    ImageView imgDataError;
    @BindView(R.id.sv_root)
    TouchDetectableScrollView svRoot;
    @BindView(R.id.swipe_container)
    LinearLayout swipeContainer;

    Call<FeaturedPosts> getProductsCall;
    FeaturedPosts productList;
    private Dialog dialog;
    private ArrayList<ResultFeatured> resultList;
    private int TOTAL_PAGES = 0;
    private boolean isFeedsFetchInProgress = false;
    WebService service;
    FeaturedPostsAdapter mainCategoriesAdapter;
    private int currentPage = PAGE_START;
    private static final int PAGE_START = 1;
    private int previousPage = 0;
    Unbinder unbinder;
    String tag_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View aboutUsView = inflater.inflate(R.layout.show_tag_result, container, false);
        unbinder = ButterKnife.bind(this, aboutUsView);

        Bundle bundle = this.getArguments();
        service = Global.WebServiceConstants.getRetrofitinstance();
        dialog = S_Dialogs.getLoadingDialog(getActivity());
        hideSearch();
        productList = new FeaturedPosts();
        resultList = new ArrayList<>();
        productList.setResult(resultList);


        setMainProductList();

        new ApisHelper().getFeaturedPosts("1", new ApisHelper.OnFeaturedPosts() {
            @Override
            public void onFeaturedPostsSuccess(FeaturedPosts featuredPosts) {

                resultList.addAll(featuredPosts.getResult());
                productList.setResult(resultList);
                mainCategoriesAdapter.notifyItemRangeInserted(productList.getResult().size() > 0 ? productList.getResult().size() - 1 : 0, productList.getResult().size());

            }

            @Override
            public void onFeaturedPostsFailure() {

            }

        });

        return aboutUsView;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private void setMainProductList() {

        try {

            tagRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            mainCategoriesAdapter = new FeaturedPostsAdapter(getActivity(), productList);
            tagRecycler.setAdapter(mainCategoriesAdapter);
            imgDataError.setVisibility(View.GONE);

            if (productList.getResult().size() > 0) {
                getTotalHeightofGridRecyclerView(getActivity(), tagRecycler, R.layout.main_categories_adapter, 0);
            }

        } catch (Exception e) {

        }

    }


    public void hideSearch() {

        ((MainActivity) getActivity()).rel_search.setVisibility(View.GONE);
        ((MainActivity) getActivity()).rlFilter.setVisibility(View.GONE);
        ((MainActivity) getActivity()).text_sell.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).rlBack.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).rloptions.setVisibility(View.GONE);
        ((MainActivity) getActivity()).findViewById(R.id.relativeLayout).setVisibility(View.GONE);
        ((MainActivity) getActivity()).rlMenu.setVisibility(View.GONE);
        ((MainActivity) getActivity()).changeOptionColor(0);
        ((MainActivity) getActivity()).text_sell.setText("Featured posts");
        ((MainActivity) getActivity()).findViewById(R.id.relativeLayout).setVisibility(View.VISIBLE);


    }

}
