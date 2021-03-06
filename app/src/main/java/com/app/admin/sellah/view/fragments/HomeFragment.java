package com.app.admin.sellah.view.fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.admin.sellah.R;
import com.app.admin.sellah.controller.Database.UserSearch;
import com.app.admin.sellah.controller.WebServices.ApisHelper;
import com.app.admin.sellah.controller.WebServices.WebService;
import com.app.admin.sellah.controller.utils.ExpandableListData;
import com.app.admin.sellah.controller.utils.Global;
import com.app.admin.sellah.controller.utils.HelperPreferences;
import com.app.admin.sellah.controller.utils.SAConstants;
import com.app.admin.sellah.model.extra.BannerModel.BannerModel;
import com.app.admin.sellah.model.extra.LiveVideoModel.LiveVideoModel;
import com.app.admin.sellah.model.extra.Notification.NotificationModel;
import com.app.admin.sellah.model.extra.NotificationList.NotificationListModel;
import com.app.admin.sellah.model.extra.getProductsModel.GetProductList;
import com.app.admin.sellah.model.extra.getProductsModel.Result;
import com.app.admin.sellah.view.CustomDialogs.AllCategoryDialog;
import com.app.admin.sellah.view.CustomDialogs.Notification_dialog;
import com.app.admin.sellah.view.CustomDialogs.S_Dialogs;
import com.app.admin.sellah.view.CustomDialogs.Stripe_dialogfragment;
import com.app.admin.sellah.view.CustomViews.TouchDetectableScrollView;
import com.app.admin.sellah.view.activities.MainActivity;
import com.app.admin.sellah.view.adapter.HomeCategoryAdapter;
import com.app.admin.sellah.view.adapter.HomeProductAdapter;
import com.app.admin.sellah.view.adapter.LiveVideoPaggerAdapter;
import com.app.admin.sellah.view.adapter.SuggestedPostAdapterHome;
import com.app.admin.sellah.view.adapter.ViewPagerAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.admin.sellah.controller.WebServices.ApisHelper.auth;
import static com.app.admin.sellah.controller.WebServices.ApisHelper.token;
import static com.app.admin.sellah.controller.utils.Global.BackstackConstants.HOMETAG;
import static com.app.admin.sellah.controller.utils.Global.BackstackConstants.LIVETAG;
import static com.app.admin.sellah.controller.utils.Global.getTotalHeightofGridRecyclerView;
import static com.app.admin.sellah.controller.utils.Global.getUser.isLogined;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.PUSH_NOTIFICATION;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.UID;
import static com.app.admin.sellah.controller.utils.SAConstants.NotificationKeys.NT_ACCEPT_REJECT;
import static com.app.admin.sellah.controller.utils.SAConstants.NotificationKeys.NT_CHAT;
import static com.app.admin.sellah.controller.utils.SAConstants.NotificationKeys.NT_COMMENT_ADDED;
import static com.app.admin.sellah.controller.utils.SAConstants.NotificationKeys.NT_DATA;
import static com.app.admin.sellah.controller.utils.SAConstants.NotificationKeys.NT_FOLLOW;
import static com.app.admin.sellah.controller.utils.SAConstants.NotificationKeys.NT_OFFER_MAKE;
import static com.app.admin.sellah.controller.utils.SAConstants.NotificationKeys.NT_PAYMENT;
import static com.app.admin.sellah.controller.utils.SAConstants.NotificationKeys.NT_PRODUCT_ADDED;

public class HomeFragment extends Fragment {

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.SliderDots)
    LinearLayout SliderDots;
    @BindView(R.id.rel_adds)
    RelativeLayout relAdds;
    @BindView(R.id.txt_live)
    TextView txtLive;
    @BindView(R.id.vp_live)
    ViewPager vpLive;
    @BindView(R.id.vp_live_dots)
    LinearLayout vpLiveDots;
    @BindView(R.id.txt_category)
    TextView txtCategory;
    @BindView(R.id.see_more)
    TextView seeMore;
    @BindView(R.id.rv_categories)
    RecyclerView rvCategories;
    @BindView(R.id.recycler_sub_categories)
    RecyclerView recyclerSubCategories;
    @BindView(R.id.txt_recomandation)
    TextView txtRecomandation;
    @BindView(R.id.rv_recomandation)
    RecyclerView rvRecomandation;
    @BindView(R.id.txt_more_products)
    TextView txtMoreProducts;
    @BindView(R.id.rv_products)
    RecyclerView rvProducts;
    @BindView(R.id.pb_home)
    ProgressBar pbHome;
    @BindView(R.id.txt_no_more_item)
    TextView txtNoMoreItem;
    @BindView(R.id.img_data_error)
    ImageView imgDataError;
    @BindView(R.id.sv_root)
    TouchDetectableScrollView svRoot;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;
    @BindView(R.id.txt_search_sella)
    TextView txtSearchSella;
    Unbinder unbinder1;
    @BindView(R.id.nolivevideo_text)
    LinearLayout nolivevideoText;
    @BindView(R.id.see_featured_posts)
    TextView seeFeaturedPosts;
    private View view;
    Unbinder unbinder;
    View rootTag;
    HomeCategoryAdapter categoriesAdapter;
    //Recyclerlist3
    public static HomeProductAdapter mainCategoriesAdapter;
    WebService service;
    GetProductList productList;
    GetProductList productListFiltered;
    private int numberOfColumns;
    private Dialog dialog;
    private TextWatcher searchWacher;
    View.OnKeyListener onKeyListener;
    TextView.OnEditorActionListener editorActionListener;
    private ArrayList<String> searchSuggestions;
    //  private ArrayAdapter<String> searchSuggessionAdapter;
    private boolean inAnimation;
    private String TAG = HomeFragment.class.getSimpleName();

    private static final int PAGE_START = 1;
    // Indicates if footer ProgressBar is shown (i.e. next page is loading)
    private boolean isLoading = false;
    // If current page is the last page (Pagination will stop after this page load)
    private boolean isLastPage = false;
    // total no. of pages to load. Initial load is page 0, after which 2 more pages will load.
    public static int TOTAL_PAGES = 0;
    // indicates the current page which Pagination is fetching.
    public static int currentPage = PAGE_START;
    private int previousPage = 0;
    private boolean isFeedsFetchInProgress = false;
    private ArrayList<Result> resultList;
    private boolean isSearching;
    private int dotscount;
    private int dotscount_;
    private ImageView[] dots;
    private Timer timer;
    int timerDelay = 5000;
    private ImageView[] dotsLive;
    private long timeVisibleDelay = 300;
    NotificationListModel notificationListModel;
    Call<GetProductList> addCommentCall;
    Call<GetProductList> getProductsCall;
    Call<NotificationListModel> notificationListCall;
    GridLayoutManager gridLayoutManager;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter(PUSH_NOTIFICATION));

        if (Global.from_register) {
            Stripe_dialogfragment stripe_dialogfragment = new Stripe_dialogfragment();
            stripe_dialogfragment.show(getActivity().getFragmentManager(), "");
        }

        dialog = S_Dialogs.getLoadingDialog(getActivity());
        dialog.setOwnerActivity(getActivity());
        dialog.show();
        service = Global.WebServiceConstants.getRetrofitinstance();
        currentPage = 1;
        if (view == null) {

            view = inflater.inflate(R.layout.fragment_home_fragment_new, container, false);
            unbinder = ButterKnife.bind(this, view);

            new ApisHelper().getBanner(HelperPreferences.get(getActivity()).getString(UID), new ApisHelper.OnGetDataListners() {
                @Override
                public void onGetDataSuccess(BannerModel body) {
                    ArrayList<HashMap<String, String>> list1 = new ArrayList<>();
                    List<String> list2 = new ArrayList<>();
                    List<String> list3 = new ArrayList<>();
                    List<String> list4 = new ArrayList<>();

                    for (int i = 0; i < body.getHomebanners().size(); i++) {

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("bannerImage", body.getHomebanners().get(i).getBannerImage());
                        hashMap.put("bannerLink", body.getHomebanners().get(i).getBannerLink());

                        list1.add(hashMap);

                    }

                    if (viewPager != null)
                        setupBannerAdds(list1);

                }

                @Override
                public void onGetDataFailure() {
                    List<String> images = new ArrayList<>();


                }
            });

            try {
                getVideoListOther();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            unbinder = ButterKnife.bind(this, view);

        }

        if (((MainActivity) getActivity()).rlResetSearch != null) {
            hideSearch();
        }

        rootTag = ((MainActivity) getActivity()).relRoot;
        productList = new GetProductList();
        resultList = new ArrayList<>();
        productList.setResult(resultList);
        searchSuggestions = new ArrayList<>();
        getNotificationList();
        ((MainActivity) getActivity()).getProfileData();

        /*for profile updation*/

        showSearchBar();


        // Notification data=========================

        ((MainActivity) getActivity()).notificationRelativelayout.findViewById(R.id.notification_relativelayout).setOnClickListener(view1 -> {

            if (isLogined(getActivity())) {

                Bundle bundle = new Bundle();
                bundle.putParcelable(SAConstants.Keys.NOTI_KEY, notificationListModel);
                Notification_dialog dialog = new Notification_dialog();
                dialog.setArguments(bundle);
                dialog.show(getActivity().getFragmentManager(), "");

            } else {

                S_Dialogs.getLoginDialog(getActivity()).show();

            }

        });

        //list1
        categoriesAdapter = new HomeCategoryAdapter(getActivity(), ExpandableListData.getData());
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.HORIZONTAL);
        rvCategories.setLayoutManager(staggeredGridLayoutManager);
        rvCategories.setAdapter(categoriesAdapter);
        numberOfColumns = 2;

        searchProduct();
        setUpSwipeView();
        pagginationCode();
        setMainProductList();
        getProductlist();
        getSuggestedPostList();

        String first = " Sellah";
        String next = "<font color='#EE0000'>Search</font>";
        txtSearchSella.setText(Html.fromHtml(next + first));

        //---------------------------------------------------
        seeFeaturedPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ShowFeaturedPosts()).addToBackStack(null).commit();
            }
        });

        //---------------------------------------------------
        unbinder1 = ButterKnife.bind(this, view);

        return view;
    }

    private int[] getVieePostion(View view) {
        int[] location = {0, 0};
        view.getLocationOnScreen(location);
        return location;
    }

    private void hideSearchBar() {

        ((MainActivity) getActivity()).rel_search.animate()
                .translationY(-((MainActivity) getActivity()).rel_search.getHeight())
                .alpha(0.0f)
                .setDuration(timeVisibleDelay);
    }

    private void showSearchBar() {

        ((MainActivity) getActivity()).rel_search.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).rel_search.animate()
                .translationY(0)
                .alpha(1f)
                .setDuration(timeVisibleDelay);

    }

    private void pagginationCode() {

        Rect scrollBounds = new Rect();
        svRoot.getHitRect(scrollBounds);


        svRoot.setMyScrollChangeListener(new TouchDetectableScrollView.OnMyScrollChangeListener() {
            @Override
            public void onScroll() {

                ((MainActivity) getActivity()).recyclerviewSearch.setVisibility(View.GONE);
                Global.hideKeyboard(swipeContainer, getActivity());

                if (txtSearchSella.getLocalVisibleRect(scrollBounds)) {
//                    Log.e(TAG, "View Visible" );
                 //   Log.e(TAG, "onScrollUp: View Visible");

                    // Any portion of the imageView, even a single pixel, is within the visible window
                } else {

                //    Log.e(TAG, "onScrollUp: not View Visible");
//                  Log.e(TAG, "onScrollDup not View Visible" );
                    //NONE of the imageView is within the visible window
                }

            }

            @Override
            public void onScrollUp() {
              Log.e(TAG, "onScrollUp: ");
                pbHome.setVisibility(View.GONE);


            }

            @Override
            public void onScrollDown() {
                pbHome.setVisibility(View.VISIBLE);
              Log.e(TAG, "onScrollDown:");

            }

            @Override
            public void onBottomReached() {
                Log.e(TAG, "onBottomReached: ");
                if (isFeedsFetchInProgress) {
                    pbHome.setVisibility(View.VISIBLE);
                    return;
                }
                if (TOTAL_PAGES > 1) {
                    //End of list
                    previousPage = currentPage;
                    currentPage++;
                    isFeedsFetchInProgress = true;
                    pbHome.setVisibility(View.VISIBLE);
                    if ((currentPage) <= TOTAL_PAGES) {
                        getProductlist();
                        Log.e(TAG, "onBottomReached: LoadNextPage");
                    } else {
                        isFeedsFetchInProgress = false;
                        pbHome.setVisibility(View.GONE);
                        txtNoMoreItem.setVisibility(View.VISIBLE);
                    }

                } else {
                    isFeedsFetchInProgress = false;
                    pbHome.setVisibility(View.GONE);
                    txtNoMoreItem.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTopReached() {

            }
        });

    }

    private void setUpSwipeView() {

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                swipeContainer.setRefreshing(true);
                swipeContainer.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        ((MainActivity) getActivity()).loadFragment(new HomeFragment(), HOMETAG);
                        swipeContainer.setRefreshing(false);

                    }
                }, 100);
            }
        });
        swipeContainer.setColorSchemeColors(getActivity().getResources().getColor(R.color.colorRed));
        swipeContainer.setSize(SwipeRefreshLayout.DEFAULT);// LARGE also can be used
    }

    private void searchProduct() {

        searchSuggestions.clear();


        isSearching = false;
        searchWacher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                ((MainActivity) getActivity()).rlMenu.setVisibility(View.GONE);
                ((MainActivity) getActivity()).rlResetSearch.setVisibility(View.VISIBLE);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

               Log.e(TAG, "afterTextChanged: ");

                if (s.length() < 1) {

                    dialog.show();
                    isSearching = false;
                    Log.e(TAG, "resetData: ");
                    Global.hideKeyboard(((MainActivity) getActivity()).searchEditText, getActivity());
                    currentPage = 1;
                    resultList.clear();
                    productList.setResult(resultList);
                    mainCategoriesAdapter.notifyDataSetChanged();
                    getProductlist();
                    ((MainActivity) getActivity()).rlMenu.setVisibility(View.VISIBLE);
                    ((MainActivity) getActivity()).rlResetSearch.setVisibility(View.GONE);
                    ((MainActivity) getActivity()).recyclerviewSearch.setVisibility(View.GONE);

                } else {

                    if (!isSearching) {

                        isSearching = true;
                        ((MainActivity) getActivity()).rlMenu.setVisibility(View.GONE);
                        ((MainActivity) getActivity()).rlResetSearch.setVisibility(View.VISIBLE);
                        ((MainActivity) getActivity()).recyclerviewSearch.setVisibility(View.VISIBLE);

                    }

                }

            }

        };

        ((MainActivity) getActivity()).searchEditText.addTextChangedListener(searchWacher);

        ((MainActivity) getActivity()).searchEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String autoCompleteText = (String) adapterView.getItemAtPosition(i);
                ((MainActivity) getActivity()).searchEditText.setText(autoCompleteText);
                Global.hideKeyboard(((MainActivity) getActivity()).searchEditText, getActivity());
                Show_tag_result_fragment fragment = new Show_tag_result_fragment();
                Bundle bundle = new Bundle();
                bundle.putString("tag", Global.getText(((MainActivity) getActivity()).searchEditText));
                bundle.putString("root", "search");
                fragment.setArguments(bundle);
                ((MainActivity) getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).addToBackStack(null).commit();

            }
        });

        editorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    String text = Global.getText(((MainActivity) getActivity()).searchEditText);
                    if (!text.equalsIgnoreCase("")) {

                        ArrayList<String> arrayList = new ArrayList<>();
                        arrayList.add(Global.getText(((MainActivity) getActivity()).searchEditText));

                        if (MainActivity.arrayListSearched.size() > 0) {
                            if (MainActivity.arrayListSearched.size() > 0 && MainActivity.arrayListSearched.size() < 5) {
                                for (int i = 0; i < MainActivity.arrayListSearched.size(); i++) {
                                    arrayList.add(MainActivity.arrayListSearched.get(i));

                                }
                            } else {
                                for (int i = 0; i < 4; i++) {
                                    arrayList.add(MainActivity.arrayListSearched.get(i));
                                }
                            }
                        }

                        MainActivity.arrayListSearched.clear();
                        MainActivity.arrayListSearched.addAll(arrayList);
                        ((MainActivity) getActivity()).searchAdapter.notifyDataSetChanged();

                        UserSearch userSearch = new UserSearch();
                        userSearch.setSearched(Global.getText(((MainActivity) getActivity()).searchEditText));
                        ((MainActivity) getActivity()).myDatabase.myDao().addSearch(userSearch);

                        ((MainActivity) getActivity()).recyclerviewSearch.setVisibility(View.GONE);

                        Show_tag_result_fragment fragment = new Show_tag_result_fragment();

                        Bundle bundle = new Bundle();
                        bundle.putString("tag", Global.getText(((MainActivity) getActivity()).searchEditText));
                        bundle.putString("root", "search");
                        fragment.setArguments(bundle);
                        ((MainActivity) getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).addToBackStack(null).commit();

                    }

                    return true;
                }
                return false;
            }
        };

        ((MainActivity) getActivity()).searchEditText.setOnEditorActionListener(editorActionListener);


    }


    public void getVideoListOther() throws Exception {

        new ApisHelper().getLiveVideoData(getActivity(), String.valueOf(currentPage), "", new ApisHelper.GetLiveVideoCallback() {
            @Override
            public void onGetLiveVideoSuccess(LiveVideoModel body) {
                if (body.getList().isEmpty()) {
                    if (nolivevideoText != null) nolivevideoText.setVisibility(View.VISIBLE);
                    if (vpLive != null) vpLive.setVisibility(View.GONE);

                } else {
                    if (nolivevideoText != null) nolivevideoText.setVisibility(View.GONE);
                    if (vpLive != null) vpLive.setVisibility(View.VISIBLE);

                    try {
                        setLiveVideos(body);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onGetLiveVideoFailure() {


            }
        });
    }

    private void setLiveVideos(LiveVideoModel body) throws Exception {

        LiveVideoPaggerAdapter viewPagerAdapter = new LiveVideoPaggerAdapter(getActivity(), body.getList());
        vpLive.setAdapter(viewPagerAdapter);
        viewPagerAdapter.notifyDataSetChanged();

        Gson gson = new GsonBuilder().create();
   //     Log.e("setLiveVideos: ", gson.toJson(body));

        if (body.getList().size() > 3) {
            dotscount = 3/*viewPagerAdapter.getCount()*/;
        } else {
            dotscount = viewPagerAdapter.getCount();
        }
//      dotscount = 4/*viewPagerAdapter.getCount()*/;
        dotsLive = new ImageView[dotscount];
        //automatic slide
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (vpLive != null) {

                    vpLive.post(new Runnable() {
                        @Override
                        public void run() {

                            if (vpLive != null) {
                                if (vpLive.getCurrentItem() == dotscount - 1) {
                                    vpLive.setCurrentItem(0);
//                            Log.e("pagger","pos"+vPBannerAdd.getCurrentItem());
                                } else {
                                    vpLive.setCurrentItem((vpLive.getCurrentItem() + 1));
//                            Log.e("pagger_else","pos"+vPBannerAdd.getCurrentItem());
                                }
                            }
                        }
                    });

                }
            }
        };

        timer = new Timer();
        timer.schedule(timerTask, timerDelay, timerDelay);

        for (int i = 0; i < dotscount; i++) {
            dotsLive[i] = new ImageView(getActivity());
            dotsLive[i].setImageResource(R.drawable.dot_icon);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(15,
                    15);
            params.setMargins(5, 0, 8, 0);
            vpLiveDots.addView(dotsLive[i], params);
        }
        //dots[0].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.dot_icon2));
        vpLive.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                try {

                    for (int i = 0; i < dotscount; i++) {
                        dotsLive[i].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.dot_icon));
                    }
                    dotsLive[position].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.dot_icon2));

                } catch (Exception e) {

                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupBannerAdds(ArrayList<HashMap<String, String>> bannerAddList) {

        if (bannerAddList != null && bannerAddList.size() > 0) {
//           .setVisibility(View.VISIBLE);
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity(), bannerAddList);
            viewPager.setAdapter(viewPagerAdapter);
            viewPagerAdapter.notifyDataSetChanged();

            dotscount_ = viewPagerAdapter.getCount();
            dots = new ImageView[dotscount_];
            //automatic slide
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (viewPager != null) {

                        viewPager.post(new Runnable() {
                            @Override
                            public void run() {

                                if (viewPager != null) {
                                    if (viewPager.getCurrentItem() == dotscount_ - 1) {
                                        viewPager.setCurrentItem(0);
//                            Log.e("pagger","pos"+vPBannerAdd.getCurrentItem());
                                    } else {
                                        viewPager.setCurrentItem((viewPager.getCurrentItem() + 1));
//                            Log.e("pagger_else","pos"+vPBannerAdd.getCurrentItem());
                                    }
                                }
                            }
                        });

                    }
                }
            };

            timer = new Timer();
            timer.schedule(timerTask, timerDelay, timerDelay);

            for (int i = 0; i < dotscount_; i++) {
                dots[i] = new ImageView(getActivity());
                dots[i].setImageResource(R.drawable.dot_icon);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(15,
                        15);
                params.setMargins(5, 0, 8, 0);
                SliderDots.addView(dots[i], params);
            }

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    try {
                        for (int i = 0; i < dotscount_; i++) {
                            dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.dot_icon));
                        }
                        dots[position].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.dot_icon2));

                    } catch (Exception e) {

                    }

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        } else {
        }

    }

    @OnClick({R.id.see_more, R.id.vp_live})
    public void onViewClicked(View v) {
        switch (v.getId()) {

            case R.id.vp_live:
              //  Log.e("viewpgr", "ok");
                if (isLogined(getActivity())) {
                    new MainActivity().changeOptionColor(1);
                    new MainActivity().loadFragment(new LiveVideosFragment(), LIVETAG);

                } else {
                    S_Dialogs.getLoginDialog(getActivity()).show();
                }
                break;

            case R.id.see_more:
            //    Log.e("Click", "Seemore");
                AllCategoryDialog.create(getActivity()).show();
                /*loadFragment(new SeeMoreFragment(), HOMETAG);*/
                break;

        }

    }

    public boolean loadFragment(Fragment seeMoreFragment, String backStackTag) {
        if (seeMoreFragment != null) {
            Global.hideKeyboard(rootTag, getActivity());
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, seeMoreFragment).addToBackStack(null).commit();
            return true;
        }
        return false;

    }

    public void hideSearch() {

        hideSearchBar();
//      new MainActivity().hideToolbar(false);
        ((MainActivity) getActivity()).rel_search.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).rlFilter.setVisibility(View.GONE);
        ((MainActivity) getActivity()).rlMenu.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).notificationRelativelayout.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).text_sell.setVisibility(View.GONE);
        ((MainActivity) getActivity()).rlResetSearch.setVisibility(View.GONE);
        ((MainActivity) getActivity()).searchEditText.setHint("Search Sellah...");
        ((MainActivity) getActivity()).rlBack.setVisibility(View.GONE);
        ((MainActivity) getActivity()).rloptions.setVisibility(View.GONE);
        ((MainActivity) getActivity()).changeOptionColor(0);
//      ((MainActivity) getActivity()).backArrow.setVisibility(View.GONE);

        txtSearchSella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((MainActivity) getActivity()).rel_search.setVisibility(View.VISIBLE);
                showSearchBar();
                svRoot.scrollTo(100, 100);

                try {
                    ((MainActivity) getActivity()).findViewById(R.id.searchEditText).requestFocus();
                    Global.showKeyboard(((MainActivity) getActivity()).findViewById(R.id.searchEditText), getActivity());
                } catch (Exception e) {
                }

            }
        });

    }

    public void getProductlist() {
        if (!Global.NetworStatus.isOnline(getActivity()) || Global.NetworStatus.isInternetAvailable()) {
            S_Dialogs.getNetworkErrorDialog(getActivity()).show();
            if (dialog.getOwnerActivity() != null && !dialog.getOwnerActivity().isFinishing() && dialog != null && dialog.isShowing()) {

                dialog.dismiss();
            }
        } else {
         //   Log.e(TAG, "getProductlist_ID: " + HelperPreferences.get(getActivity()).getString(UID) + "");
            getProductsCall = service.getProductListApi(auth, token, HelperPreferences.get(getActivity()).getString(UID), "", "", String.valueOf(currentPage));
            getProductsCall.enqueue(new Callback<GetProductList>() {
                @Override
                public void onResponse(Call<GetProductList> call, Response<GetProductList> response) {
                    if (response.isSuccessful()) {

                  //      Log.e("responseGetProfile", "success");

                        if (dialog.getOwnerActivity() != null && !dialog.getOwnerActivity().isFinishing() && dialog != null && dialog.isShowing()) {

                            dialog.dismiss();

                        }

                        TOTAL_PAGES = Integer.parseInt(response.body().getTotalPages());

                        try {

                            productList.setStatus(response.body().getStatus());
                            productList.setMessage(response.body().getMessage());
                            resultList.addAll(response.body().getResult());
                            productList.setResult(resultList);
                            isFeedsFetchInProgress = false;
                            pbHome.setVisibility(View.GONE);
                            txtNoMoreItem.setVisibility(View.GONE);
                            mainCategoriesAdapter.notifyItemRangeInserted(productList.getResult().size() > 0 ? productList.getResult().size() - 1 : 0, productList.getResult().size());

                            if (productList.getResult().size() > 0) {
                                getTotalHeightofGridRecyclerView(getActivity(), rvProducts, R.layout.main_categories_adapter, 1);
                            }

                            //--------------------------------------------
                        //    Log.e("successsss", Global.DEEP_LINKING_PRODUCT_ID + "   outer");

                            setSearchData();

                        } catch (Exception e) {

                        }

                   //     Log.e("Getproducts", "Success" + response.body().toString());

                    } else {

                        if (dialog.getOwnerActivity() != null && !dialog.getOwnerActivity().isFinishing() && dialog != null && dialog.isShowing()) {

                            dialog.dismiss();
                        }

                        pbHome.setVisibility(View.GONE);
                        txtNoMoreItem.setVisibility(View.GONE);

                        try {

                        Log.e("Getproducts", "failure" + response.errorBody().string());

                        } catch (IOException e) {

                            e.printStackTrace();

                        }


                    }

                }

                @Override
                public void onFailure(Call<GetProductList> call, Throwable t) {

                    if (dialog.getOwnerActivity() != null && !dialog.getOwnerActivity().isFinishing() && dialog != null && dialog.isShowing()) {

                        dialog.dismiss();
                    }

                    if (pbHome != null) {
                        pbHome.setVisibility(View.GONE);
                        txtNoMoreItem.setVisibility(View.GONE);
                    }

                }

            });
        }
    }

    private void setUpSuggestedPosts(GetProductList list) {

        SuggestedPostAdapterHome suggestedPostAdapter = new SuggestedPostAdapterHome(getActivity(), list);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvRecomandation.setLayoutManager(layoutManager);
        rvRecomandation.setAdapter(suggestedPostAdapter);
    }

    private void getSuggestedPostList() {
        addCommentCall = service.suggestedProductsApi(auth, token, "");
        addCommentCall.enqueue(new Callback<GetProductList>() {
            @Override
            public void onResponse(Call<GetProductList> call, Response<GetProductList> response) {
                if (response.isSuccessful()) {

                    if (response.body().getStatus().equalsIgnoreCase("1")) {

                        if (rvRecomandation != null)
                            setUpSuggestedPosts(response.body());
                    }

                } else {
                    try {
                        Log.e("comment_error", "onResponse: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetProductList> call, Throwable t) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (timer != null) {
            timer.cancel();
        }
        ((MainActivity) getActivity()).rel_search.animate()
                .translationY(0)
                .alpha(1f)
                .setDuration(0);
        ((MainActivity) getActivity()).notificationRelativelayout.setVisibility(View.GONE);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);

        ((MainActivity) getActivity()).searchEditText.removeTextChangedListener(searchWacher);
        ((MainActivity) getActivity()).searchEditText.setText("");
        ((MainActivity) getActivity()).rlMenu.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).rlResetSearch.setVisibility(View.GONE);
        ((MainActivity) getActivity()).searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Global.hideKeyboard(swipeContainer, getActivity());
                } else {
                    Global.hideKeyboard(swipeContainer, getActivity());
                }

                return false;
            }
        });

        ((MainActivity) getActivity()).searchEditText.setText("");
        searchSuggestions = new ArrayList<>();
        unbinder.unbind();
    }

    private void setSearchData() {


    }

    private void setMainProductList() {

        try {

            setSearchData();
            gridLayoutManager = new GridLayoutManager(getActivity(), numberOfColumns);
            rvProducts.setLayoutManager(gridLayoutManager);
            mainCategoriesAdapter = new HomeProductAdapter(getActivity(), productList,"1");
            rvProducts.setAdapter(mainCategoriesAdapter);
            imgDataError.setVisibility(View.GONE);
            if (productList.getResult().size() > 0) {
                getTotalHeightofGridRecyclerView(getActivity(), rvProducts, R.layout.main_categories_adapter, 0);
            }

        } catch (Exception e) {

        }

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            try {
                NotificationModel message = intent.getParcelableExtra(NT_DATA);
                if (message != null) {
                    Fragment f = getActivity().getSupportFragmentManager().findFragmentById(R.id.frameLayout);

                    switch (message.getNotiType()) {
                        case NT_ACCEPT_REJECT:
                            break;
                        case NT_FOLLOW:
                            break;
                        case NT_COMMENT_ADDED:
                            break;
                        case NT_PRODUCT_ADDED:
                            if (f instanceof HomeFragment)
                                getProductlist();
                            break;
                        case NT_CHAT:
                            break;
                        case NT_PAYMENT:
                            break;
                        case NT_OFFER_MAKE:
                            break;
                        default:

                            break;
                    }
                }
            //    Log.e("receiver", "Got message: Home" + message.getMessage());
            } catch (Exception e) {
               // Log.e("receiver_failure", "onReceive: " + e.getMessage());
            }
        }
    };


    private void getNotificationList() {

        notificationListCall = service.getNotificationList(auth, token, HelperPreferences.get(getActivity()).getString(UID));
        notificationListCall.enqueue(new Callback<NotificationListModel>() {
            @Override
            public void onResponse(Call<NotificationListModel> call, Response<NotificationListModel> response) {

                if (response.isSuccessful()) {
                  //  Log.e("GetNotificationList", "onResponse: " + response.body().getStatus());
                    notificationListModel = response.body();
                    if (notificationListModel.getListReadStatus().equals("0")) {
                        MainActivity.homeNotidot.setVisibility(View.GONE);
                    } else {
                        if (MainActivity.homeNotidot != null) {
                            MainActivity.homeNotidot.setVisibility(View.VISIBLE);
                        }
                    }


                } else {
                    try {
                        Log.e("GetNotificationList", "onResponse: errorBody" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificationListModel> call, Throwable t) {

              //  Log.e("GetNotificationList", "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        if (addCommentCall != null) {
            addCommentCall.cancel();
        }

        if (getProductsCall != null) {
            getProductsCall.cancel();
        }
        new ApisHelper().cancel_banner_request();
        new ApisHelper().getvideodata_cancel();

        if (notificationListCall != null)
            notificationListCall.cancel();

    }


}
