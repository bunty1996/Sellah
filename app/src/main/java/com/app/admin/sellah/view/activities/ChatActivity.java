package com.app.admin.sellah.view.activities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GestureDetectorCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.admin.sellah.R;
import com.app.admin.sellah.controller.WebServices.ApisHelper;
import com.app.admin.sellah.controller.WebServices.ReportApi;
import com.app.admin.sellah.controller.WebServices.WebService;
import com.app.admin.sellah.controller.utils.ChatActivityController;
import com.app.admin.sellah.controller.utils.DetectSwipeGestureListener;
import com.app.admin.sellah.controller.utils.Global;
import com.app.admin.sellah.controller.utils.HelperPreferences;
import com.app.admin.sellah.controller.utils.RecyclerViewClickListener;
import com.app.admin.sellah.model.extra.ChatHeadermodel.Record;
import com.app.admin.sellah.model.extra.ChatModel.ChatMessageModel;
import com.app.admin.sellah.model.extra.MakeOffer.MakeOfferModel;
import com.app.admin.sellah.model.extra.commonResults.Common;
import com.app.admin.sellah.view.CustomDialogs.S_Dialogs;
import com.app.admin.sellah.view.fragments.ChatDetailFragment;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.Socket;
import me.crosswall.lib.coverflow.core.CoverTransformer;
import me.crosswall.lib.coverflow.core.PagerContainer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.admin.sellah.controller.WebServices.ApisHelper.auth;
import static com.app.admin.sellah.controller.WebServices.ApisHelper.token;
import static com.app.admin.sellah.controller.utils.Global.StatusBarLightMode;
import static com.app.admin.sellah.controller.utils.SAConstants.ConstValues.SCREEN_STATUS;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.MAKE_OFFER_DATA;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.UID;

public class ChatActivity extends AppCompatActivity implements RecyclerViewClickListener, ChatActivityController {
    @BindView(R.id.rl_menu)
    RelativeLayout rlMenu;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;
    @BindView(R.id.linear)
    LinearLayout linear;
    List<Record> list;
    @BindView(R.id.txt_user_name)
    public TextView txtUserName;
    @BindView(R.id.card_top_view)
    CardView cardTopView;
    @BindView(R.id.card_bottom_view)
    CardView cardBottomView;
    @BindView(R.id.rel_chat_root)
    public RelativeLayout relChatRoot;
    @BindView(R.id.rec_message)
    RecyclerView recMessage;
    @BindView(R.id.img_send_camera)
    ImageView imgSendCamera;
    @BindView(R.id.img_send_gallery)
    ImageView imgSendGallery;
    @BindView(R.id.edt_message)
    EditText edtMessage;
    @BindView(R.id.btn_send)
    ImageView btnSend;
    boolean isOffer = true;
    boolean isOnBottom = true;
    @BindView(R.id.btn_menu)
    public ImageButton btnMenu;
    @BindView(R.id.btn_back)
    ImageView btnBack;
    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R.id.txt_review)
    TextView txtReview;
    @BindView(R.id.vp_chat_detail)
    ViewPager vpChatDetail;
    @BindView(R.id.chat_container)
    FrameLayout chatContainer;
    @BindView(R.id.img_online)
    public CircleImageView imgOnline;
    @BindView(R.id.txt_last_seen)
    public TextView txtLastSeen;
    private List<ChatMessageModel> models;
    private PagerAdapter adapter;
    private int GALLERY = 1213;
    private int CAMERA_PIC_REQUEST = 1212;
    private BottomSheetDialog filterDialog;
    private Dialog filterDialog1;

    Socket mSocket;
    private boolean isConnected;
    String otherUserId = "";
    String otherUserName = "";
    String otherUserImage = "";
    WebService service;
    List<Record> chatedListRecord;
    private String imagePath;
    private MakeOfferModel makeOfferResult;
    private boolean isUserExistes;
    private int initChatIndex = 0;
    FragmentManager fm;
    FragmentTransaction fragmentTransaction;
    boolean isBlocked;
    private PagerContainer mContainer;
    private ViewPager pager;
    private GestureDetectorCompat gestureDetectorCompat = null;

    public ChatActivity() {

    }

    PopupMenu popup;
    public MenuItem item, item1, item3;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }
    private static final String CAPTURED_PHOTO_PATH_KEY = "mCapturedPhotoPathKey";
    private static final String CAPTURED_PHOTO_URI_KEY = "mCapturedPhotoUriKey";
    private static final String RETURN_STATUS_CODE = "mReturnStatusCode";
    private String mCapturedPhotoPath = null;
    private Uri mCapturedPhotoUri = null;
    private String mReturnStatus = "";


    public String getmReturnStatus() {
        return mReturnStatus;
    }

    public void setmReturnStatus(String mReturnStatus) {
        this.mReturnStatus = mReturnStatus;
    }

    public String getmCapturedPhotoPath() {
        return mCapturedPhotoPath;
    }

    public void setmCapturedPhotoPath(String mCapturedPhotoPath) {
        this.mCapturedPhotoPath = mCapturedPhotoPath;
    }

    public Uri getmCapturedPhotoUri() {
        return mCapturedPhotoUri;
    }

    public void setmCapturedPhotoUri(Uri mCapturedPhotoUri) {
        this.mCapturedPhotoUri = mCapturedPhotoUri;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mCapturedPhotoPath != null) {
            outState.putString(CAPTURED_PHOTO_PATH_KEY, mCapturedPhotoPath);
        }
        if (mCapturedPhotoUri != null) {
            outState.putString(CAPTURED_PHOTO_URI_KEY, mCapturedPhotoUri.toString());
        }
        if (!mReturnStatus.isEmpty()) {
            Log.d("TAG", "setReturnStatus" + "//" + mReturnStatus);
            outState.putString(RETURN_STATUS_CODE, mReturnStatus);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(CAPTURED_PHOTO_PATH_KEY)) {
            mCapturedPhotoPath = savedInstanceState.getString(CAPTURED_PHOTO_PATH_KEY);
        }
        if (savedInstanceState.containsKey(CAPTURED_PHOTO_URI_KEY)) {
            mCapturedPhotoUri = Uri.parse(savedInstanceState.getString(CAPTURED_PHOTO_URI_KEY));
        }
        if (savedInstanceState.containsKey(RETURN_STATUS_CODE)) {
            Log.d("TAG", "Restore" + "//" + savedInstanceState.getString(RETURN_STATUS_CODE));
            mReturnStatus = savedInstanceState.getString(RETURN_STATUS_CODE);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        StatusBarLightMode(this);
        SCREEN_STATUS = ChatActivity.class.getSimpleName();
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        models = new ArrayList<>();
        chatedListRecord = new ArrayList<>();


        popup = new PopupMenu(ChatActivity.this, btnMenu);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_report, popup.getMenu());
        item = popup.getMenu().findItem(R.id.menu_report);
        item1 = popup.getMenu().findItem(R.id.menu_block_user);
        item3 = popup.getMenu().findItem(R.id.menu_dispute);
        item3.setVisible(false);
        item.setTitle("Report " + otherUserName);
        item1.setTitle("Block " + otherUserName);
        if (getIntent().getExtras() != null)
            setChatView();

        DetectSwipeGestureListener gestureListener = new DetectSwipeGestureListener();

        gestureListener.setActivity(this);

        gestureDetectorCompat = new GestureDetectorCompat(this, gestureListener);

    }

    private void setChatView() {

    //    Log.e("chatact", "setChatView");
        makeOfferResult = getIntent().hasExtra(MAKE_OFFER_DATA) ? getIntent().getParcelableExtra(MAKE_OFFER_DATA) : null;
        String userId = getIntent().hasExtra("user_id") ? getIntent().getStringExtra("user_id") : "";

        if (!TextUtils.isEmpty(userId)) {
            otherUserId = userId;
        } else {
            otherUserId = getIntent().hasExtra("otherUserId") ? getIntent().getStringExtra("otherUserId") : "";

        }
      //  Log.e("c", otherUserId + "//" + userId);
        otherUserName = getIntent().hasExtra("otherUserName") ? getIntent().getStringExtra("otherUserName") : "";
        otherUserImage = getIntent().hasExtra("otherUserImage") ? getIntent().getStringExtra("otherUserImage") : "";

        Record record = new Record();
        record.setFriendName(otherUserName);
        record.setFriendImage(otherUserImage);
        record.setFriendId(otherUserId);
        txtUserName.setText(otherUserName);

        isUserExistes = false;
        initChatIndex = 0;

        for (Record rec : chatedListRecord) {

          //  Log.e("recordId", rec.getFriendName());

            if (rec.getFriendId() != null) {

                if (rec.getFriendId().equals(otherUserId)) {

                    isUserExistes = true;
                    initChatIndex = chatedListRecord.indexOf(rec);

                }

            }

        }

        if (!isUserExistes) {

            chatedListRecord.add(record);

            if (chatedListRecord.size() > 0) {

                initChatIndex = chatedListRecord.size() - 1;

            }

        }

        setUpchatHeaderList();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetectorCompat.onTouchEvent(event);
        return true;
    }

    private void getIntentData() {
        Intent in = getIntent();
        Record record;

        makeOfferResult = in.hasExtra(MAKE_OFFER_DATA) ? in.getParcelableExtra(MAKE_OFFER_DATA) : null;
        String userId = in.hasExtra("user_id") ? in.getStringExtra("user_id") : "";

        if (!TextUtils.isEmpty(userId)) {
            otherUserId = userId;
        } else {
            otherUserId = in.hasExtra("otherUserId") ? in.getStringExtra("otherUserId") : "";

        }
        otherUserName = in.hasExtra("otherUserName") ? in.getStringExtra("otherUserName") : "";
        otherUserImage = in.hasExtra("otherUserImage") ? in.getStringExtra("otherUserImage") : "";
        Log.e("intent", "getIntentData: " + otherUserId + ":" + otherUserName + ":");


        record = new Record();
        record.setFriendName(otherUserName);
        record.setFriendImage(otherUserImage);
        record.setFriendId(otherUserId);

        txtUserName.setText(otherUserName);
        isUserExistes = false;
        initChatIndex = 0;
        for (Record rec : chatedListRecord) {
         //   Log.e("recordId", rec.getFriendName());

            if (rec.getFriendId() != null) {
                if (rec.getFriendId().equals(otherUserId)) {
                    isUserExistes = true;
                    initChatIndex = chatedListRecord.indexOf(rec);
                }
            }
        }
        if (!isUserExistes) {
            chatedListRecord.add(record);


            if (chatedListRecord.size() > 0) {
                initChatIndex = chatedListRecord.size() - 1;
            } else {
            }

         //  Log.e("addUserToChat", "getIntentData: Data Added");
        } else {

        }
        setUpchatHeaderList();

    }

    @Override
    public void recyclerViewListClicked(View v, int position) {

    }

    @Override
    public void isOnline(boolean isOnline) {

       // Log.e("isOnline", isOnline + "");


    }

    @Override
    public void onBottomReached(boolean status) {
        isOnBottom = status;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       // Log.e("cameraIntentresult", "reqcode" + requestCode + "resultCode" + resultCode + "data" + data);


        for (Fragment fragment : getSupportFragmentManager().getFragments()) {

            fragment.onActivityResult(requestCode, resultCode, data);

        }

    }

    @OnClick(R.id.btn_menu)
    public void onViewClicked() {

        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.menu_report:

                        filterDialog = new BottomSheetDialog(ChatActivity.this);
                        filterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        filterDialog.setContentView(R.layout.filter_dialog);
                        LinearLayout ll_reporting_item = filterDialog.findViewById(R.id.ll_reporting_item);
                        LinearLayout l2_prohibited = filterDialog.findViewById(R.id.l2_prohibited);
                        LinearLayout l3_mispriced = filterDialog.findViewById(R.id.l3_mispriced);
                        LinearLayout l4_wrongCategroy = filterDialog.findViewById(R.id.l4_wrongCategroy);
                        LinearLayout l5_duplicate = filterDialog.findViewById(R.id.l5_duplicate);
                        LinearLayout l6_offensive = filterDialog.findViewById(R.id.l6_offensive);
                        LinearLayout l7_irrelevant = filterDialog.findViewById(R.id.l7_irrelevant);
                        LinearLayout l8_counterfeit = filterDialog.findViewById(R.id.l8_counterfeit);
                        LinearLayout l9_cancel = filterDialog.findViewById(R.id.l9_cancel);
                        filterDialog.show();
                        //------------------------------------------------------------------------
                        ll_reporting_item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                hitReportApi(ll_reporting_item);
                                filterDialog.dismiss();
                            }
                        });

                        l2_prohibited.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                hitReportApi(l2_prohibited);
                                filterDialog.dismiss();
                            }
                        });

                        l3_mispriced.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                hitReportApi(l3_mispriced);
                                filterDialog.dismiss();
                            }
                        });

                        l4_wrongCategroy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                hitReportApi(l4_wrongCategroy);
                                filterDialog.dismiss();
                            }
                        });

                        l5_duplicate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                hitReportApi(l5_duplicate);
                                filterDialog.dismiss();
                            }
                        });

                        l6_offensive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                hitReportApi(l6_offensive);
                                filterDialog.dismiss();
                            }
                        });

                        l7_irrelevant.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                hitReportApi(l7_irrelevant);
                                filterDialog.dismiss();
                            }
                        });

                        l8_counterfeit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                hitReportApi(l8_counterfeit);
                                filterDialog.dismiss();
                            }
                        });

                        l9_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                filterDialog.dismiss();
                            }
                        });
                        //--------------------------------------------------------------------------

                        break;

                    case R.id.menu_block_user:

                        S_Dialogs.getBlockUnblockConfirmation(ChatActivity.this, getResources().getString(R.string.dialog_title_block_user), ((dialog, which) -> {
                            blockUnBlockUser("block", otherUserId, new OnBlockUpnBlockUserCallback() {
                                @Override
                                public void onSuccess() {

                                    isBlocked = false;

                                    try {

                                        if (pager.getChildCount() > 0) {

                                            chatedListRecord.get(pager.getCurrentItem()).setIsBlocked("Y");
                                            chatedListRecord.get(pager.getCurrentItem()).setBlockedBy(HelperPreferences.get(ChatActivity.this).getString(UID));
                                            if ((pager.getCurrentItem() + 1) < pager.getChildCount()) {
                                                pager.setCurrentItem((pager.getCurrentItem() + 1));
                                            } else if (pager.getChildCount() < (pager.getCurrentItem() - 1)) {
                                                pager.setCurrentItem((pager.getCurrentItem() - 1));
                                            } else {
                                                onBackPressed();
                                            }

                                        } else {

                                            onBackPressed();

                                        }

                                    } catch (Exception e) {

                                    }

                                }

                            });

                        })).show();

                        break;

                }

                return true;

            }

        });

    }

    @Override
    public void updateSubTotal(String subtotal) {
        TextView text = filterDialog1.findViewById(R.id.txt_subtotal);
        text.setText("S$ " + subtotal);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        super.onStop();
        new ApisHelper().cancel_chatlist();
     //   Log.e("chatActivity", "onStop: ");
        SCREEN_STATUS = "";
    }

    @OnClick(R.id.rl_back)
    public void onBackClicked() {
        onBackPressed();
    }

    private void blockUnBlockUser(String blockStatus, String friendId, OnBlockUpnBlockUserCallback callback) {
        Dialog dialog = S_Dialogs.getLoadingDialog(ChatActivity.this);
        dialog.show();
        Call<Common> manageBlockUserCall = Global.WebServiceConstants.getRetrofitinstance().manageBlockListApi(auth, token, HelperPreferences.get(ChatActivity.this).getString(UID), friendId, blockStatus);
        manageBlockUserCall.enqueue(new Callback<Common>() {
            @Override
            public void onResponse(Call<Common> call, Response<Common> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().equalsIgnoreCase("1")) {
                        Snackbar.make(relChatRoot, response.body().getMessage(), Snackbar.LENGTH_SHORT).setAction("", null).show();
                        callback.onSuccess();
                        for (Record record : chatedListRecord) {

                        }
                    }

                } else {

                }

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call<Common> call, Throwable t) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
             //   Log.e("manageUser", "onFailure: " + t.getMessage());
                Snackbar.make(relChatRoot, "Please try again latter.", Snackbar.LENGTH_SHORT)
                        .setAction("", null).show();
            }
        });
    }

    interface OnBlockUpnBlockUserCallback {
        void onSuccess();
    }

    @OnClick(R.id.txt_review)
    public void onReviewClicked() {
    }

    private void fragmentCalling(int i) {

        fm = getSupportFragmentManager();

        fragmentTransaction = fm.beginTransaction();
//        Log.e("c21hatlst", otherUserId);
//        Log.e("chatlst", chatedListRecord + "//" + chatedListRecord.size());
        if (chatedListRecord != null)
            fragmentTransaction.replace(R.id.chat_container, new ChatDetailFragment(chatedListRecord.get(i).getFriendId(), makeOfferResult, chatedListRecord.get(i).getFriendName()));
        else if (otherUserId != null) {
            fragmentTransaction.replace(R.id.chat_container, new ChatDetailFragment(otherUserId, makeOfferResult, otherUserName));
//            Log.e("rfcrfr", otherUserId);
//            Log.e("rfcrfr", otherUserName);
        }

        fragmentTransaction.commitAllowingStateLoss();

    }

    public void setUpchatHeaderList() {

        mContainer = (PagerContainer) findViewById(R.id.pager_container);

        pager = mContainer.getViewPager();

        adapter = new MyPagerAdapter();
        pager.setAdapter(adapter);

        pager.setCurrentItem(initChatIndex);
        pager.setOffscreenPageLimit(adapter.getCount());

        if (adapter.getCount() != 0) {
            pager.setCurrentItem(initChatIndex);
            fragmentCalling(initChatIndex);
        }

        pager.setClipChildren(false);

        pager.setPageTransformer(false, new CoverTransformer(0.3f, 0f, 0f, 0f));
        Log.d("###", "pager1 width:" + 150 * getResources().getDisplayMetrics().density);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int index = 0;

            @Override
            public void onPageSelected(int position) {

                index = position;
                otherUserName = chatedListRecord.get(position).getFriendName();
                otherUserId = chatedListRecord.get(position).getFriendId();

                if (!TextUtils.isEmpty(chatedListRecord.get(position).getOnlineStatus()) && chatedListRecord.get(position).getOnlineStatus().equalsIgnoreCase("ON")) {

                    imgOnline.setVisibility(View.VISIBLE);
                    imgOnline.setBackgroundResource(R.drawable.dot_online);
                    txtLastSeen.setVisibility(View.VISIBLE);
                    txtLastSeen.setText("Online");

                } else {

                    imgOnline.setVisibility(View.VISIBLE);
                    imgOnline.setBackgroundResource(R.drawable.red_dot_icon);
                    txtLastSeen.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(chatedListRecord.get(position).getLastSeenTime()))
                        txtLastSeen.setText("Last seen " + ": " + Global.getTimeAgo(Global.convertUTCToLocal(chatedListRecord.get(position).getLastSeenTime())));

                }



                try {

                    if (!TextUtils.isEmpty(chatedListRecord.get(position).getIsBlocked()) && chatedListRecord.get(position).getIsBlocked().equalsIgnoreCase("y")) {
                        isBlocked = true;
                        if (!TextUtils.isEmpty(chatedListRecord.get(position).getBlockedBy()) && chatedListRecord.get(position).getBlockedBy().equalsIgnoreCase(HelperPreferences.get(ChatActivity.this).getString(UID))) {
//                        you have blocked user
                            S_Dialogs.getYouBlockedUserDialog(ChatActivity.this, getString(R.string.dialog_title_you_block_user), (dialog, which) -> {
                                blockUnBlockUser("unblock", chatedListRecord.get(position).getFriendId(), new OnBlockUpnBlockUserCallback() {
                                    @Override
                                    public void onSuccess() {
                                        chatedListRecord.get(position).setIsBlocked("N");
                                        isBlocked = false;
                                    }
                                });
                                dialog.dismiss();
                            }).show();
                        } else {
                            S_Dialogs.getUserBlockedYouDialog(ChatActivity.this).show();
//                        user has blocked you.
                        }

                    } else {
                        fragmentCalling(position);
                    }

                } catch (Exception e) {
                    fragmentCalling(position);
                }

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


                otherUserName = chatedListRecord.get(position).getFriendName();
                if (!TextUtils.isEmpty(otherUserName)) {
                    txtUserName.setText(otherUserName);
                } else {
                    txtUserName.setText("Sellah! user");
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

                if (state == ViewPager.SCROLL_STATE_IDLE) {
                }

            }

        });

    }

    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            CircleImageView view = new CircleImageView(ChatActivity.this);

            Glide.with(ChatActivity.this).load(chatedListRecord.get(position).getFriendImage()).apply(Global.getGlideOptions()).into(view);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return chatedListRecord.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }

    }



    public void hitReportApi(LinearLayout layout) {

        new ReportApi().hitReportApi(ChatActivity.this, layout
                , otherUserId, "", (msg) -> {

                    Toast.makeText(this, msg + "", Toast.LENGTH_SHORT).show();

                }, () -> {
                    Toast.makeText(this, "Please try again later", Toast.LENGTH_SHORT).show();

                });

    }

}



