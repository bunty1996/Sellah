package com.app.admin.sellah.view.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.admin.sellah.R;
import com.app.admin.sellah.controller.WebServices.WebService;
import com.app.admin.sellah.controller.utils.ChatActivityController;
import com.app.admin.sellah.controller.utils.Global;
import com.app.admin.sellah.controller.utils.HelperPreferences;
import com.app.admin.sellah.controller.utils.ImageUploadHelper;
import com.app.admin.sellah.controller.utils.SAConstants;
import com.app.admin.sellah.model.extra.ChatModel.ChatMessageModel;
import com.app.admin.sellah.model.extra.MakeOffer.MakeOfferModel;
import com.app.admin.sellah.model.extra.MessagesModel.ChatListModel;
import com.app.admin.sellah.model.extra.Notification.NotificationModel;
import com.app.admin.sellah.model.extra.UploadChatImage.UploadChatImageModel;
import com.app.admin.sellah.view.CustomDialogs.AddTestoimonialDailog;
import com.app.admin.sellah.view.CustomDialogs.MakeOfferDialog;
import com.app.admin.sellah.view.CustomDialogs.PaymentDialog;
import com.app.admin.sellah.view.CustomDialogs.S_Dialogs;
import com.app.admin.sellah.view.activities.ChatActivity;
import com.app.admin.sellah.view.adapter.ChatAdapter;
import com.app.admin.sellah.view.adapter.Pay_offer_adapter;
import com.app.admin.sellah.view.adapter.Pay_offer_adapter2;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.app.admin.sellah.controller.WebServices.ApisHelper.auth;
import static com.app.admin.sellah.controller.WebServices.ApisHelper.token;
import static com.app.admin.sellah.controller.stripe.StripeSession.STRIPE_VERIFIED;
import static com.app.admin.sellah.controller.utils.Global.playMessageTone;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.EVENT_CREATEROOM;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.EVENT_NEW_MESSAGE;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.EVENT_READMESSAGE;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.EVENT_READMESSAGE_FOR_CHATLIST;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.PUSH_NOTIFICATION;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.UID;
import static com.app.admin.sellah.controller.utils.SAConstants.NotificationKeys.NT_DATA;

public class ChatDetailFragment extends Fragment implements ChatActivityController, Pay_offer_adapter.OnCardOptionSelection {

    Unbinder unbinder;
    View view;
    Socket mSocket;
    @BindView(R.id.txt_review)
    public TextView txtReview;
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
    @BindView(R.id.linear)
    LinearLayout linear;
    @BindView(R.id.card_bottom_view)
    CardView cardBottomView;
    @BindView(R.id.fr_root)
    FrameLayout frRoot;
    @BindView(R.id.pay_recycler)
    RecyclerView payRecycler;
    @BindView(R.id.pay_recycler2)
    RecyclerView payRecycler2;
    @BindView(R.id.btn_collapsed_items)
    Button btnCollapsedItems;
    @BindView(R.id.pay_newbtn)
    Button payNewbtn;
    @BindView(R.id.pay_layout)
    LinearLayout payLayout;
    @BindView(R.id.pay_layout2)
    LinearLayout payLayout2;

    @BindView(R.id.mark_ascomplete_recycler)
    RecyclerView markAscompleteRecycler;
    @BindView(R.id.mark_ascomplete_recycler2)
    RecyclerView markAscompleteRecycler2;

    @BindView(R.id.markas_complete_btn_collapsed_items)
    Button markasCompleteBtnCollapsedItems;
    @BindView(R.id.markas_complete_btn_collapsed_items2)
    Button markasCompleteBtnCollapsedItems2;
    @BindView(R.id.markascomplete)
    Button markascomplete;
    @BindView(R.id.markascomplete2)
    Button markascomplete2;

    @BindView(R.id.pay_linLay)
    LinearLayout payLinLay;

    @BindView(R.id.accept_layout)
    LinearLayout acceptLayout;
    @BindView(R.id.accept_layout2)
    LinearLayout acceptLayout2;
    @BindView(R.id.pay_cancelbtn)
    Button payCancelbtn;
    public static NestedScrollView chatScrollview;
    @BindView(R.id.pb_chat)
    ProgressBar pbChat;
    private WebService service;
    private boolean isConnected;
    String otherUserId;
    private List<ChatMessageModel> models;
    private ChatAdapter adapter;
    private Pay_offer_adapter payoffer_adapter;
    private Pay_offer_adapter2 payoffer_adapter2;
    private Pay_offer_adapter mark_adapter;
    private Pay_offer_adapter2 mark_adapter2;
    private int GALLERY = 1213;
    private int CAMERA_PIC_REQUEST = 1212;
    String otherUserName = "";
    String otherUserImage = "";
    private String imagePath;
    private MakeOfferModel makeOfferResult;
    private Global app;
    private boolean isOffer;
    private boolean from_api;
    String roomName = "";
    int message_count;
    String rec_id = "";
    Call<ChatListModel> chatListCall;
    Call<JsonObject> chatoffercall;
    ArrayList<Map<String, String>> list = new ArrayList<>();
    ArrayList<Map<String, String>> main_offer_list = new ArrayList<>();

    ArrayList<Map<String, String>> mark_list = new ArrayList<>();
    ArrayList<Map<String, String>> mark_main_offer_list = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    ArrayList<Map<String, String>> extra_list = new ArrayList<>();
    Map<String, String> map;
    Map<String, String> mark_map;
    String latitudeList;
    String friendName;
    Dialog dialog;

    final int PAGE_START = 1;
    boolean isLoading = false;
    boolean isLastPage = false;
    int TOTAL_PAGES = 1;
    int currentPage = PAGE_START;
    private int offerStatus = 0;
    String getChatStatus = "onCreate";
    List<ChatMessageModel> chatModel = new ArrayList<>();
    List<ChatMessageModel> chatModelTemp = new ArrayList<>();
    ChatListModel chatListModel = new ChatListModel();
    private boolean offer = false;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 35;
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSIONREQUEST = 36;
    private Uri camera_FileUri;
    private String friends_stripe_is_verified="";

    public ChatDetailFragment() {

    }

    @SuppressLint("ValidFragment")
    public ChatDetailFragment(String otherUserId, MakeOfferModel makeOfferModel, String friendName) {
        this.otherUserId = otherUserId;
        this.makeOfferResult = makeOfferModel;
        this.friendName = friendName;
        offerStatus = 1;
        offer = true;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver1, new IntentFilter(PUSH_NOTIFICATION));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_chat_dtail, container, false);
        }
        unbinder = ButterKnife.bind(this, view);
        chatScrollview = view.findViewById(R.id.chat_scrollview);
        app = (Global) getActivity().getApplicationContext();
        mSocket = app.getSocket();
        isOffer = true;
        service = Global.WebServiceConstants.getRetrofitinstance();
        Log.e("ids", otherUserId+"\\"+HelperPreferences.get(getActivity()).getString(UID) );
        models = new ArrayList<>();
        models.clear();
        setUpMessgae(models, "progressNo");
        setUpSendButton();
        getofferlist(otherUserId);
        permission();
        pagination();

        ((ChatActivity) getActivity()).item3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                disputedialog();
                return true;
            }

        });

        return view;

    }

    private void scrollToBottom() {
        recMessage.scrollToPosition(adapter.getItemCount() - 1);

    }

    private void permission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    private void pagination() {

        Rect scrollBounds = new Rect();
        recMessage.getHitRect(scrollBounds);


        loadFirstPage();

    }

    private void loadFirstPage() {


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        // Log.e("OnDestroy", "OnDestroy");
        disconnectSocket();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver1);
    }

    @Override
    public void onPause() {
        super.onPause();
        //ActivityPaused  = true;
        // Log.e("OnPause", "OnPause");
        disconnectSocket();
        isConnected = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatListCall != null)
            chatListCall.cancel();
    }

    private void ConnectToSocket() {

        this.mSocket.on(Socket.EVENT_CONNECT, onConnect);
        this.mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        this.mSocket.on(EVENT_CREATEROOM, onRoomCreation);
        this.mSocket.on(EVENT_READMESSAGE, onReadMessage);
        this.mSocket.on(EVENT_READMESSAGE_FOR_CHATLIST, BACKCHATREAD);
        this.mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        this.mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        this.mSocket.on(EVENT_NEW_MESSAGE, onNewMessage);
        this.mSocket.connect();
    }

    public void disconnectSocket() {

        try {

            this.mSocket.disconnect();
            this.mSocket.off(EVENT_CREATEROOM, onRoomCreation);
            this.mSocket.off(Socket.EVENT_CONNECT, onConnect);
            this.mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
            this.mSocket.off(EVENT_READMESSAGE, onReadMessage);
            this.mSocket.off(EVENT_READMESSAGE_FOR_CHATLIST, BACKCHATREAD);
            this.mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
            this.mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            this.mSocket.off(EVENT_NEW_MESSAGE, onNewMessage);

        } catch (Exception e) {

            //  Log.e("Exception", "disconnectSocket: " + e.getMessage());

        }

    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (getActivity() == null)
                return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (!isConnected) {

                        JSONObject jsonObject = new JSONObject();

                        try {

                            jsonObject.put("sender_id", HelperPreferences.get(getActivity()).getString(UID));
                            jsonObject.put("receiver_id", otherUserId);

                        } catch (JSONException e) {

                            e.printStackTrace();

                        }

                        //    Log.e("CreateRoom_params", jsonObject.toString());
                        mSocket.emit(EVENT_CREATEROOM, jsonObject);
                        //   Log.e("Connection", "connected");


                        /*Get chat api */
                        getChathistoryApi(otherUserId, String.valueOf(currentPage));
                        isConnected = true;

                    } else {

                        Toast.makeText(getActivity(), "Already connected", Toast.LENGTH_SHORT).show();

                    }

                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (getActivity() == null)
                return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //    Log.e("Connection", "disconnected");
                    isConnected = false;

                }
            });
        }
    };

    private Emitter.Listener onRoomCreation = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (getActivity() == null)
                return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //   Log.e("Event_createRoom", "run: created" + args[0].toString());
                    JSONObject data = (JSONObject) args[0];

                    try {

                        roomName = data.getString("room_name");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONObject jsonObject = new JSONObject();

                    try {

                        jsonObject.put("user_id", HelperPreferences.get(getActivity()).getString(UID));
                        jsonObject.put("room_name", roomName);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        // Log.e("Exception_message", "Emit message");
                    }

                    mSocket.emit(EVENT_READMESSAGE, jsonObject);
                    mSocket.emit(EVENT_READMESSAGE_FOR_CHATLIST, jsonObject);

                }
            });
        }
    };

    //todo for message read
    private Emitter.Listener onReadMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            if (getActivity() == null)
                return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //  Log.e("msgread", "okread");
                    JSONObject object = null;

                    try {

                        object = new JSONObject(args[0].toString());
                        //  Log.e("readjson", object.toString() + "//" + HelperPreferences.get(getActivity()).getString(UID));

                        if (object.has("receiver_id")) {

                            // Log.e("receiverId", "okjkl");
                            //{"status":"1","message":"Message read successfully","receiver_id":"5de549ba68c5ce3ffd6b84d4"}//5de5511368c5ce3f69413272
                            if (!object.getString("receiver_id").equalsIgnoreCase(HelperPreferences.get(getActivity()).getString(UID))) {

                                try {

                                    //  Log.e("msg12", "ok897");

                                    if (from_api) {

//                                        Log.e("msg12345", "ok89712");
//                                        Log.e("run: ", "loop");

                                        for (int i = 0; i < models.size(); i++) {

                                            models.get(i).setMessage_read_status("Y");
                                            //        Log.e("runapipos", i + "//");

                                        }

                                        for (int i = 0; i < chatModel.size(); i++) {

                                            chatModel.get(i).setMessage_read_status("Y");
                                            //           Log.e("runapipos", i + "//");

                                        }

                                        adapter.notifyDataSetChanged();
                                        from_api = false;

                                    } else {

                                        //        Log.e("msg12222", "ok222");

                                        //          Log.e("modsiz", models.size() + "//");
                                        for (int i = 0; i < models.size(); i++) {

                                            models.get(i).setMessage_read_status("Y");
                                            //           Log.e("pos", i + "//");

                                        }

                                        for (int i = 0; i < chatModel.size(); i++) {

                                            chatModel.get(i).setMessage_read_status("Y");
                                            //         Log.e("pos", i + "//");

                                        }

                                        adapter.notifyDataSetChanged();


                                    }

                                } catch (Exception e) {

                                }

                            }

                        }

                    } catch (JSONException e) {

                        e.printStackTrace();

                    }

                    //   Log.e("Event_ReadMessage", "run: created" + args[0].toString());

                }

            });

        }

    };

    private Emitter.Listener BACKCHATREAD = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (getActivity() == null)
                return;
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    //   Log.e("errorValue", args[0] + " exception");
                    Toast.makeText(getActivity(), "error_connect", Toast.LENGTH_LONG).show();

                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    JSONObject data = (JSONObject) args[0];
                    //  Log.e("onmsgReceive", data.toString());
                    playMessageTone(getActivity());
                    JSONObject roomParams = new JSONObject();
                    try {
                        roomParams.put("room_name", roomParams);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String mJsonString = String.valueOf(args[0]);
                    JsonParser parser = new JsonParser();
                    JsonElement mJson = parser.parse(mJsonString);
                    Gson gson = new Gson();

                    ChatMessageModel object = gson.fromJson(mJson, ChatMessageModel.class);
                    //    Log.e("runaaaa: ", args[0].toString());

                    //  Log.e("run: ", object.getReceiverId());
                    //   Log.e("run: ", object.getMsgId());
                    //  Log.e("run: ", roomName);
                    //    Log.e("run: ", chatModel.size() + "//");

                    if (object != null) {

                        chatModel.add(object);
                        adapter.notifyDataSetChanged();
                        if (adapter.getItemCount() > 1) {
                            recMessage.getLayoutManager().scrollToPosition(adapter.getItemCount() - 1);
                            //       Log.e("p[osition item", " >>>" + (adapter.getItemCount() - 1));
                        }
                        //    Log.e("run12:", chatModel.size() + "//");
                        if (chatModel.size() == 1) {
                            chatModel.clear();
                            models.clear();
                            recMessage.setVisibility(View.VISIBLE);
                            getChathistoryApi(otherUserId, "1");

                        }

                    } else {
                        //      Log.e("msg_object", "Error");
                    }

                    if (!object.getSenderId().equalsIgnoreCase(HelperPreferences.get(getActivity()).getString(UID))) {

                        JSONObject jsonObject = new JSONObject();
                        try {

                            jsonObject.put("receiver_id", object.getReceiverId());
                            jsonObject.put("message_id", object.getMsgId());
                            jsonObject.put("room_name", roomName);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mSocket.emit(EVENT_READMESSAGE, jsonObject);

                    }

                    //     Log.e("messageModel", "run: " + object.getType());

                }

            });

        }

    };


    private void setUpMessgae(List<ChatMessageModel> models, String progressStatus) {

        try {

            chatModel.addAll(models);
            Collections.reverse(chatModel);
            adapter = new ChatAdapter(chatModel, getActivity(), progressStatus, dialog, txtReview, this);
            linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false);
            recMessage.setLayoutManager(linearLayoutManager);
            recMessage.setItemAnimator(new DefaultItemAnimator());
            recMessage.setAdapter(adapter);
            recMessage.setNestedScrollingEnabled(false);

            if (models.size() > 0) {

                recMessage.smoothScrollToPosition(models.size() - 1);
                //    Log.e("Moedel item", " >>>" + (models.size() - 1));

            }


        } catch (Exception e) {

            // Log.e("setup_error", "setUpMessgae:");

        }

    }

    public void setUpMessgaeLoad(List<ChatMessageModel> models) {

        pbChat.setVisibility(View.GONE);
        chatModelTemp.clear();
        chatModelTemp.addAll(models);
        Collections.reverse(chatModelTemp);
        chatModelTemp.addAll(chatModel);
        chatModel.clear();
        chatModel.addAll(chatModelTemp);

        adapter.notifyDataSetChanged();


    }

    private void getChathistoryApi(String otherUserId, String curr_page) {

        if (getChatStatus.equalsIgnoreCase("onCreate")) {
            dialog = S_Dialogs.getLoadingDialog(getActivity());
        } else {
            pbChat.setVisibility(View.VISIBLE);
            dialog.dismiss();
        }

        chatListCall = service.getChatDetailApi(auth, token, HelperPreferences.get(getActivity()).getString(UID), otherUserId, curr_page);

        chatListCall.enqueue(new Callback<ChatListModel>() {
            @Override
            public void onResponse(Call<ChatListModel> call, Response<ChatListModel> response) {

                if (response.isSuccessful()) {

                    //  Log.e("sttttt", response.body().tostring);

                    if (getChatStatus.equalsIgnoreCase("onCreate")) {
                        if (dialog != null && dialog.isShowing())
                            dialog.dismiss();
                    }

                    if (response.body().getStatus().equalsIgnoreCase("1")) {
                        models.clear();
                        chatModel.clear();

                        dialog.dismiss();
                        ChatMessageModel chatMessageModel;

                        try {
                            if (response.body().getTotalPages() != null && !response.body().getTotalPages().equalsIgnoreCase(""))
                                TOTAL_PAGES = Integer.parseInt(response.body().getTotalPages());
                        } catch (Exception e) {

                        }

                        for (int i = 0; i < response.body().getRecord().size(); i++) {

                            chatMessageModel = new ChatMessageModel();
                            java.text.DateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                            if (models.isEmpty()) {

                                /*For adding time for the first time to the model*/

                                //  Log.e("date1: ", response.body().getRecord().get(i).getCreatedAt());

                                try {

                                    Date strDate = readFormat.parse(response.body().getRecord().get(i).getCreatedAt());
                                    //   Log.e("strdate", datefun(strDate) + "");
                                    chatMessageModel.setMessage("");
                                    chatMessageModel.setStatus("");
                                    chatMessageModel.setMessage("");
                                    chatMessageModel.setMsgId("1");
                                    chatMessageModel.setSenderId("");
                                    chatMessageModel.setReceiverId("");
                                    chatMessageModel.setProduct_image("");
                                    chatMessageModel.setMessage_read_status("");
                                    chatMessageModel.setType("");
                                    chatMessageModel.setToday_boolean(true);
                                    chatMessageModel.setToday(datefun(strDate));
                                    models.add(chatMessageModel);

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    //      Log.e("errorExpPrint", e.getMessage());
                                }

                            } else {

                                /*For adding time by matching it with previous time*/
                                try {

                                    Date strDate = readFormat.parse(response.body().getRecord().get(i - 1).getCreatedAt());
                                    Date strDate2 = readFormat.parse(response.body().getRecord().get(i).getCreatedAt());
                                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                    String dateString = formatter.format(new Date(strDate.getTime()));
                                    String dateString1 = formatter.format(new Date(strDate2.getTime()));

                                    if (!dateString1.equalsIgnoreCase(dateString)) {

                                        String monthName = new SimpleDateFormat("MMMM").format(strDate2.getTime());
                                        SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
                                        String goal = outFormat.format(strDate2);

                                        chatMessageModel.setMessage("");
                                        chatMessageModel.setStatus("");
                                        chatMessageModel.setMsgId("1");
                                        chatMessageModel.setSenderId("");
                                        chatMessageModel.setReceiverId("");
                                        chatMessageModel.setType("");
                                        chatMessageModel.setProduct_image("");
                                        chatMessageModel.setMessage_read_status("");
                                        chatMessageModel.setToday_boolean(true);
                                        chatMessageModel.setToday(datefun(strDate2));
                                        models.add(chatMessageModel);

                                    }

                                } catch (ParseException e) {

                                    e.printStackTrace();
                                    //       Log.e("errorExpPrint_", e.getMessage());

                                }

                            }

                            Gson gson = new GsonBuilder().create();
                            String payloadStr = gson.toJson(response.body().getRecord());
                            //Log.e("getChathistoryApi: ", payloadStr);
                            response.body().getRecord().get(i).setToday_boolean(false);
                            response.body().getRecord().get(i).setToday(response.body().getRecord().get(i).getCreatedAt());
                            models.add(response.body().getRecord().get(i));

                        }

                        /*Socket to read all the message for the first time  while user open the screen */
                        if (!models.get(models.size() - 1).getSenderId().equalsIgnoreCase((HelperPreferences.get(getActivity()).getString(UID)))) {

//                            Log.e("onResponse: ", models.get(models.size() - 1).getReceiverId());
//                            Log.e("onResponse: ", models.get(models.size() - 1).getMsgId());
                            //variable for first api loop
                            from_api = true;
                            JSONObject jsonObject = new JSONObject();

                            try {

                                jsonObject.put("receiver_id", models.get(models.size() - 1).getReceiverId());
                                jsonObject.put("room_name", roomName);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            mSocket.emit(EVENT_READMESSAGE, jsonObject);

                        }


                        //     Log.e("status", "onResponse: success" + response.body().getOnlineStatus());


                        if (!TextUtils.isEmpty(response.body().getOnlineStatus()) && response.body().getOnlineStatus().equalsIgnoreCase("ON")) {
                            //    Log.e("online: ", response.body().getOnlineStatus());
                            ((ChatActivity) getActivity()).imgOnline.setVisibility(View.VISIBLE);
                            ((ChatActivity) getActivity()).imgOnline.setBackgroundResource(R.drawable.dot_online);
                            ((ChatActivity) getActivity()).txtLastSeen.setVisibility(View.VISIBLE);
                            if (!TextUtils.isEmpty(response.body().getLastSeenTime()))
                                ((ChatActivity) getActivity()).txtLastSeen.setText("Online");

                        } else {

                            ((ChatActivity) getActivity()).imgOnline.setVisibility(View.VISIBLE);
                            ((ChatActivity) getActivity()).imgOnline.setBackgroundResource(R.drawable.red_dot_icon);
                            ((ChatActivity) getActivity()).txtLastSeen.setVisibility(View.VISIBLE);

                            if (!TextUtils.isEmpty(response.body().getLastSeenTime()))
                                if (!TextUtils.isEmpty(response.body().getLastSeenTime()))
                                    ((ChatActivity) getActivity()).txtLastSeen.setText("Last seen " + ": " + Global.getTimeAgo(Global.convertUTCToLocal(response.body().getLastSeenTime())));
                        }

                        if (getChatStatus.equalsIgnoreCase("onLoad"))
                            setUpMessgaeLoad(models);
                        else
                            setUpMessgae(models, "progressYes");
                        if (response.body().getIs_reviewed() != null && response.body().getIs_reviewed().equalsIgnoreCase("N")) {
                            //txtReview.setVisibility(View.VISIBLE);
                            //    Log.e("Review_pending", "onResponse: " + response.body().getIs_reviewed());
                        } else {
                            //   Log.e("Review_pending", "onResponse: " + "Y");
                            txtReview.setVisibility(View.GONE);
                        }

                        if (makeOfferResult != null && offer == true) {
                            offerStatus = 0;
                            offer = false;
                            //   Log.e("OfferData", "onResponse: ");
                            attemptToSendOfferData(makeOfferResult, makeOfferResult.getResult().getProductName());
                        }

                    }

                } else {

                    try {

                        if (dialog != null && dialog.isShowing())
                            dialog.dismiss();
                        Log.e("Chat_detailApi", "error: " + response.errorBody().string() + "//" + makeOfferResult);


                    } catch (IOException e) {

                        e.printStackTrace();

                    }

                }

            }

            @Override
            public void onFailure(Call<ChatListModel> call, Throwable t) {

                if (getChatStatus.equalsIgnoreCase("onCreate")) {

                    if (dialog != null && dialog.isShowing())
                        dialog.dismiss();

                }

                // Log.e("Chat_detailApiFailure", "onFailure: " + t.getMessage());

            }

        });

    }

    private void uploadChatImage(MultipartBody.Part mPart) {

        Dialog dialog = S_Dialogs.getLoadingDialog(getActivity());
        dialog.show();
        Call<UploadChatImageModel> uploadChatImageCall = service.uploadChatimageApi(auth, token, mPart);

        uploadChatImageCall.enqueue(new Callback<UploadChatImageModel>() {
            @Override
            public void onResponse(Call<UploadChatImageModel> call, Response<UploadChatImageModel> response) {
                //  Log.e("TAG", "onResponse: " + response.code());

                if (response.isSuccessful()) {

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    if (response.body().getStatus().equalsIgnoreCase("1")) {

                        JSONObject jsonObject = new JSONObject();

                        try {

                            jsonObject.put("sender_id", HelperPreferences.get(getActivity()).getString(UID));
                            jsonObject.put("receiver_id", otherUserId);
                            jsonObject.put("image_name", response.body().getImageName());
                            jsonObject.put("sender_image", "");
                            jsonObject.put("image_url", response.body().getImageUrl());
                            jsonObject.put("type", "img");

                        } catch (JSONException e) {

                            e.printStackTrace();
                            //     Log.e("Exception_message", "Emit message");

                        }

                        //    Log.e("msg_params", jsonObject.toString());
                        mSocket.emit(EVENT_NEW_MESSAGE, jsonObject);

                    }

                } else {

                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    Snackbar.make(frRoot, "Unable to send image at the movement.", Snackbar.LENGTH_SHORT).setAction("", null).show();

                }

            }

            @Override
            public void onFailure(Call<UploadChatImageModel> call, Throwable t) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                //  Log.e("UploadChatImageApi", "onFailure: " + t.getMessage());
                Snackbar.make(frRoot, "Please try again later", Snackbar.LENGTH_SHORT)
                        .setAction("", null).show();
            }

        });

    }

    /*method to send offer to other user*/
    private void attemptToSendOfferData(MakeOfferModel body, String productName) {

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("sender_id", HelperPreferences.get(getActivity()).getString(UID));
            jsonObject.put("receiver_id", otherUserId);
            jsonObject.put("sender_image", "");
            jsonObject.put("type", "o");
            jsonObject.put("offer_id", body.getResult().getOfferId());
            jsonObject.put("product_id", body.getResult().getProductId());
            jsonObject.put("product_name", body.getResult().getProductName());
            jsonObject.put("price_cost", body.getResult().getPriceCost());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //  Log.e("msg_params", jsonObject.toString());
        mSocket.emit(EVENT_NEW_MESSAGE, jsonObject);

    }

    @OnClick(R.id.txt_review)
    public void onReviewClicked() {

        AddTestoimonialDailog dailog = new AddTestoimonialDailog();
        Bundle bundle = new Bundle();
        bundle.putString("other_id", otherUserId);
        dailog.setArguments(bundle);
        dailog.show(getActivity().getFragmentManager(), "testimonial");

    }

    private void setUpBottomView() {

        try {

            MakeOfferDialog.create(getActivity(), new MakeOfferDialog.OfferController() {
                @Override
                public void onMakeOfferButtonClick(MakeOfferModel offerPrice, String itemQuantity) {
                    // Log.e("onMakeOfferclick", offerPrice + itemQuantity);

                    attemptToSendOfferData(offerPrice, itemQuantity);

                }

                @Override
                public void onMakeOffer(MakeOfferModel body, String productName) {
                    //  Log.e("onMakeOfferclick1", body + productName);
                    attemptToSendOfferData(body, productName);

                }

                @Override
                public void onErrorSelection() {

                    Toast.makeText(getActivity(), "Please select at-least one item", Toast.LENGTH_SHORT).show();

                }

            }, otherUserId).show();

        } catch (Exception e) {

            // ("makeOfferDialog", "Exception: " + e.getMessage());

        }

    }

    private void setUpSendButton() {

        edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 0) {

                    btnSend.setImageResource(R.drawable.cart_icon_96dp);
                    isOffer = true;

                } else {

                    isOffer = false;
                    btnSend.setImageResource(R.drawable.send_50dp);

                }

            }

        });

    }

    @OnClick({R.id.pay_cancelbtn, R.id.markascomplete, R.id.pay_newbtn, R.id.img_send_camera, R.id.img_send_gallery, R.id.btn_send, R.id.markascomplete2})
    public void onViewClicked(View view) {

        switch (view.getId()) {

            case R.id.pay_cancelbtn:
                if (payCancelbtn.getText().toString().equalsIgnoreCase("Pay by Cash/Others")) {
                    new AlertDialog.Builder(getActivity())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            //    .setTitle("Mark as complete")
                            .setMessage("Are you sure?")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    float amount = 0, total_amount = 0;

                                    if (extra_list != null) {

                                        for (int i = 0; i < extra_list.size(); i++) {

                                            if (extra_list.get(i).get("order_status").equalsIgnoreCase("A")) {

                                                try {

                                                    if (extra_list.get(i).get("price_cost").contains("$")) {
                                                        String amt = extra_list.get(i).get("price_cost").replace("$", "").trim();
                                                        amount = Float.parseFloat(amt);
                                                    } else if (!extra_list.get(i).get("price_cost").equalsIgnoreCase(""))
                                                        amount = Integer.parseInt(extra_list.get(i).get("price_cost"));

                                                } catch (Exception e) {
                                                }

                                                total_amount += amount;
                                            }

                                        }

                                    }

                                    //-----converting dollar into cent to send at backend-------
                                    total_amount = total_amount * 100;

                                    offlinePayment(String.valueOf((int) Math.round(total_amount)),latitudeList,otherUserId);



                                }

                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();


//                    getofferlist(otherUserId);
//
//                    for (int i = 0; i < extra_list.size(); i++) {
//
//                        manage_inventory(extra_list.get(i).get("product_id"), extra_list.get(i).get("quantity"));
//
//                    }
                } else {
                    for (int i = 0; i < extra_list.size(); i++) {


                        cancelrequest(extra_list.get(i).get("offer_id"), "r");
                        payLayout.setVisibility(View.GONE);

                    }
                }
                break;


            case R.id.markascomplete:

                if (markascomplete.getText().toString().equalsIgnoreCase("Mark as completed")) {

                    new AlertDialog.Builder(getActivity())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Mark as complete")
                            .setMessage("Are you sure you want to mark it complete?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    mark_complete(mark_main_offer_list.get(0).get("order_id"), "C");

                                }

                            })
                            .setNegativeButton("No", null)
                            .show();

                }

                if (markascomplete.getText().toString().equalsIgnoreCase("Review Pending")) {

                    AddTestoimonialDailog dailog = new AddTestoimonialDailog();
                    Bundle bundle = new Bundle();
                    bundle.putString("other_id", otherUserId);
                    bundle.putString("order_id", mark_main_offer_list.get(0).get("order_id"));
                    bundle.putString("friendName", friendName);
                    dailog.setArguments(bundle);
                    dailog.show(getActivity().getFragmentManager(), "testimonial");

                    mark_main_offer_list.clear();
                    if (mark_adapter != null)
                        mark_adapter.notifyDataSetChanged();

                    acceptLayout.setVisibility(View.GONE);

                }

                break;

            case R.id.markascomplete2:

                if (markascomplete2.getText().toString().equalsIgnoreCase("Mark as completed")) {

                    new AlertDialog.Builder(getActivity())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Mark as complete")
                            .setMessage("Are you sure you want to mark it complete?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    mark_complete1(main_offer_list.get(0).get("order_id"), "C");
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();

                }

                if (markascomplete2.getText().toString().equalsIgnoreCase("Review Pending")) {

                    AddTestoimonialDailog dailog = new AddTestoimonialDailog();
                    Bundle bundle = new Bundle();
                    bundle.putString("other_id", otherUserId);
                    bundle.putString("order_id", main_offer_list.get(0).get("order_id"));
                    bundle.putString("friendName", friendName);
                    dailog.setArguments(bundle);
                    dailog.show(getActivity().getFragmentManager(), "testimonial");

                    main_offer_list.clear();
                    if (payoffer_adapter2 != null)
                        payoffer_adapter2.notifyDataSetChanged();

                    acceptLayout2.setVisibility(View.GONE);

                }

                break;

            case R.id.pay_newbtn:

                /*Pay button  payments*/
                if (payNewbtn.getText().toString().endsWith("Card")) {
                    if ((HelperPreferences.get(getActivity()).getString(STRIPE_VERIFIED).equals("") || HelperPreferences.get(getActivity()).getString(STRIPE_VERIFIED).equals("N"))||friends_stripe_is_verified.equals("")||friends_stripe_is_verified.equals("N")) {
                        new AlertDialog.Builder(getActivity())
                                .setIcon(android.R.drawable.ic_dialog_alert)
//                                .setTitle("Mark as complete")
                                .setMessage("Seller has not enabled card payment")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }

                                })

                                .show();
                    }
                    else if ((HelperPreferences.get(getActivity()).getString(STRIPE_VERIFIED).equals("") || HelperPreferences.get(getActivity()).getString(STRIPE_VERIFIED).equals("P")||friends_stripe_is_verified.equals("P"))) {

                        new AlertDialog.Builder(getActivity())
                                .setIcon(android.R.drawable.ic_dialog_alert)
//                                .setTitle("Mark as complete")
                                .setMessage("Seller has not enabled card payment")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }

                                })

                                .show();
                    }
                    else {
                        float amount = 0, total_amount = 0;

                        if (extra_list != null) {

                            for (int i = 0; i < extra_list.size(); i++) {

                                if (extra_list.get(i).get("order_status").equalsIgnoreCase("A")) {

                                    try {

                                        if (extra_list.get(i).get("price_cost").contains("$")) {
                                            String amt = extra_list.get(i).get("price_cost").replace("$", "").trim();
                                            amount = Float.parseFloat(amt);
                                        } else if (!extra_list.get(i).get("price_cost").equalsIgnoreCase(""))
                                            amount = Integer.parseInt(extra_list.get(i).get("price_cost"));

                                    } catch (Exception e) {
                                    }

                                    total_amount += amount;
                                }

                            }

                        }

                        //-----converting dollar into cent to send at backend-------
                        total_amount = total_amount * 100;

                        //   Log.e("totalAmt", total_amount + "");

                        PaymentDialog.create(getActivity(), String.valueOf((int) Math.round(total_amount)), latitudeList, otherUserId, "", "", "", new PaymentDialog.PaymentCallBack() {
                            @Override
                      //      String price, String offerId, String sellerId, String productId, String packageId, String promoteId,
                            public void onPaymentSuccess() {

                                getofferlist(otherUserId);

                                for (int i = 0; i < extra_list.size(); i++) {

                                    manage_inventory(extra_list.get(i).get("product_id"), extra_list.get(i).get("quantity"));

                                }

                            }

                            @Override
                            public void onPaymentFail(String message) {

                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onCancelDialog() {

                            }

                        }).show();
                    }

                }
                else if (payNewbtn.getText().toString().startsWith("Pay: $")) {
                    payNewbtn.setText("Pay by Card");
                    payCancelbtn.setText("Pay by Cash/Others");
                    Log.e("TAG", friends_stripe_is_verified+ "//"+HelperPreferences.get(getActivity()).getString(STRIPE_VERIFIED));
                    if (HelperPreferences.get(getActivity()).getString(STRIPE_VERIFIED).equals("") || HelperPreferences.get(getActivity()).getString(STRIPE_VERIFIED).equals("N")||(friends_stripe_is_verified.equals("")||friends_stripe_is_verified.equals("N"))) {
//                        payNewbtn.setEnabled(false);
                        payNewbtn.setBackground(getResources().getDrawable(R.drawable.chat_pay_gray_round));
                        payCancelbtn.setBackground(getResources().getDrawable(R.drawable.chat_pay_round));
                        payCancelbtn.setTextColor(getResources().getColor(R.color.colorWhite));
                    }
                    else if ((HelperPreferences.get(getActivity()).getString(STRIPE_VERIFIED).equalsIgnoreCase("P")||friends_stripe_is_verified.equals("P"))) {
                        //  payNewbtn.setEnabled(false);
                        payNewbtn.setBackground(getResources().getDrawable(R.drawable.chat_pay_gray_round));
                        payCancelbtn.setBackground(getResources().getDrawable(R.drawable.chat_pay_round));
                        payCancelbtn.setTextColor(getResources().getColor(R.color.colorWhite));
                    }
                    else {

                        payNewbtn.setBackground(getResources().getDrawable(R.drawable.chat_pay_round));
                        payCancelbtn.setBackground(getResources().getDrawable(R.drawable.chat_pay_round));
                        payCancelbtn.setTextColor(getResources().getColor(R.color.colorWhite));
                    }
                }
                else if (payNewbtn.getText().toString().equalsIgnoreCase("Mark as completed")) {

                    mark_complete(extra_list.get(0).get("order_id"), "C");

                }
                else if (payNewbtn.getText().toString().equalsIgnoreCase("Review Pending")) {

                    AddTestoimonialDailog dailog = new AddTestoimonialDailog();
                    Bundle bundle = new Bundle();
                    bundle.putString("other_id", otherUserId);
                    bundle.putString("order_id", extra_list.get(0).get("order_id"));
                    dailog.setArguments(bundle);
                    dailog.show(getActivity().getFragmentManager(), "testimonial");

                    main_offer_list.clear();

                    if (payoffer_adapter != null)
                        payoffer_adapter.notifyDataSetChanged();

                    payLayout.setVisibility(View.GONE);

                }

                break;

            case R.id.img_send_camera:

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    openCamera();

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSIONS_REQUEST_CODE);
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_REQUEST_CODE);
                    }
                }


                break;

            case R.id.img_send_gallery:


                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    choosePhotoFromGallary();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_REQUEST_CODE);
                    }
                }
                break;

            case R.id.btn_send:

                if (isOffer) {

                    //   Log.e("onViewClicked: ", "dd");

//                    if ((HelperPreferences.get(getActivity()).getString(STRIPE_VERIFIED).equals("") || HelperPreferences.get(getActivity()).getString(STRIPE_VERIFIED).equals("N"))) {
//
//                        S_Dialogs.getLiveVideoStopedDialog(getActivity(), "You are not currently connected with stripe. Press ok to connect.", ((dialog, which) -> {
//                            //--------------openHere-----------------
//
//                            Stripe_dialogfragment stripe_dialogfragment = new Stripe_dialogfragment();
//                            stripe_dialogfragment.show(getActivity().getFragmentManager(), "");
//
//                        })).show();
//
//                    } else if ((HelperPreferences.get(getActivity()).getString(STRIPE_VERIFIED).equalsIgnoreCase("P"))) {
//
//                        S_Dialogs.getLiveVideoStopedDialog(getActivity(), "You have not uploaded you Idenitification Documents. Press ok to upload.", ((dialog, which) -> {
//                            //--------------openHere-----------------
//
//                            Stripe_image_verification_dialogfragment stripe_dialogfragment = new Stripe_image_verification_dialogfragment();
//                            stripe_dialogfragment.show(getActivity().getFragmentManager(), "");
//
//                        })).show();
//
//                    } else {
//
//                        setUpBottomView();
//
//                    }
                    setUpBottomView();

                } else {

                    attemptSend();

                }

                break;

        }

    }

    private void offlinePayment(String amount,String offerId,String sellerId) {
        Dialog dialog = S_Dialogs.getLoadingDialog(getActivity());
        dialog.show();
        WebService webService = Global.WebServiceConstants.getRetrofitinstance();

        Log.e("paymentpg", auth + "//" + token + "//" + HelperPreferences.get(getActivity()).getString(UID) + "//" + "//" + "sgd" + "//"  + "//" + offerId + "//" + sellerId + "//"  + "//" + "Y");

        Call<JsonObject> offlinePayment = webService.offlinePayment(auth, token, HelperPreferences.get(getActivity()).getString(UID), "", amount, "sgd", offerId, sellerId, "", "Y");
        offlinePayment.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        getofferlist(otherUserId);

                        for (int i = 0; i < extra_list.size(); i++) {

                            manage_inventory(extra_list.get(i).get("product_id"), extra_list.get(i).get("quantity"));

                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else {
                    try {
                        Log.e("errorBodyRes", response.errorBody().string());
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(getActivity(),"Unable to make payment against this product",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();


            }
        });

    }

    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        ChatActivity mainActivity = (ChatActivity) getActivity();
        if (cameraIntent.resolveActivity(mainActivity.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImgFile();
                //     Log.d("TAG", "photo file// " + photoFile);

            } catch (IOException ex) {
                //  Log.d("TAG", "IO exception//" + ex.getMessage());


            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(mainActivity,
                        "com.app.admin.sellah.fileprovider",
                        photoFile);
                mainActivity.setmCapturedPhotoUri(photoURI);
                mainActivity.setmCapturedPhotoPath(photoURI.getPath());
//                Log.e("TAG", "openCamera: " + photoURI.getPath());
//                Log.e("TAG", "openCamera: " + mainActivity.getmCapturedPhotoPath());
                mainActivity.setmReturnStatus("1");
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                getActivity().startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
            }
        }
    }

    private Bitmap getImageFromStorage(String path) {

        try {

            File f = new File(path);
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 512, 512);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            return b;

        } catch (FileNotFoundException e) {
            Log.d("uri", "file not found: " + e.getMessage());
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            Log.d("uri", "out of memory " + e.getMessage());
        }
        return null;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private File createImgFile() throws IOException {
        String timeStemp = new SimpleDateFormat("yyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imgFileName = "img_" + timeStemp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imgFileName, ".jpg", storageDir);
        return image;
    }

    private void choosePhotoFromGallary() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getActivity().startActivityForResult(galleryIntent, GALLERY);

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);

    }

    public String getRealPathFromURI(Uri uri) {

        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);

    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        //  Log.e("onRePermissionsResult ", "permcode" + permsRequestCode + "permission" + permissions + "grant" + grantResults);

        switch (permsRequestCode) {

            case 100:

                boolean readExternalStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                boolean camera = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean cameraPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                if (readExternalStorage && cameraPermission) {

                    openCamera();
                } else {

                    Snackbar.make(frRoot, "Unable to access camera", Snackbar.LENGTH_SHORT).setAction("", null).show();

                }

                break;
            case REQUEST_PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    choosePhotoFromGallary();
                } else {
                    Snackbar.make(frRoot, "Unable to access camera", Snackbar.LENGTH_SHORT).setAction("", null).show();

                }
            case CAMERA_PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Snackbar.make(frRoot, "Unable to access camera", Snackbar.LENGTH_SHORT).setAction("", null).show();

                }
            case WRITE_EXTERNAL_STORAGE_PERMISSIONREQUEST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Snackbar.make(frRoot, "Unable to access camera", Snackbar.LENGTH_SHORT).setAction("", null).show();

                }

                break;

        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Log.e("cameraIntentresult", "reqcode" + requestCode + "resultCode" + resultCode + "data" + data);
        if (requestCode == CAMERA_PIC_REQUEST && resultCode == RESULT_OK) {
            ChatActivity chatActivity = (ChatActivity) getActivity();
            //   Log.e("uri", "onActivityResult: " + chatActivity.getmCapturedPhotoUri());
            //  Log.e("uri", "path: " + chatActivity.getmCapturedPhotoUri().getPath());
            Uri photo = null;
            if (chatActivity != null) {
                photo = chatActivity.getmCapturedPhotoUri();
            }

            if (photo != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photo);
                    //     Log.e("TAG", bitmap.toString());
                    Uri tempUri = getImageUri(getActivity(), bitmap);
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(tempUri, filePath, null, null, null);
                    //     Log.e("onActivityResult: ", filePath.toString() + "//" + tempUri);
                    cursor.moveToFirst();
                    imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

                    uploadChatImage(ImageUploadHelper.convertImageTomultipart(imagePath, "image"));
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }


        }

        if (requestCode == GALLERY && resultCode == RESULT_OK) {

            if (data != null) {

                Uri contentURI = data.getData();

                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                    /*String imgString = Base64.encodeToString(getBytesFromBitmap(bitmap),
                            Base64.NO_WRAP);*/
                    Uri tempUri = getImageUri(getActivity(), bitmap);
                    String[] filePath = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(tempUri, filePath, null, null, null);
                    //  Log.e("onActivityResult: ", filePath.toString() + "//" + tempUri);
                    cursor.moveToFirst();
                    imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

                    uploadChatImage(ImageUploadHelper.convertImageTomultipart(imagePath, "image"));


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }


    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void attemptSend() {

        String message = edtMessage.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }

        hideKeyboard(getActivity());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = sdf.format(new Date());
        edtMessage.setText("");

        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("sender_id", HelperPreferences.get(getActivity()).getString(UID));
            jsonObject.put("receiver_id", otherUserId);
            jsonObject.put("message", message);
            jsonObject.put("sender_image", "https://ellahppdiag.blob.core.windows.net/chatimages/21112018015235_1542808355.jpg");
            jsonObject.put("created_at", str);
            jsonObject.put("image_url", "");
            jsonObject.put("type", "t");

        } catch (JSONException e) {

            e.printStackTrace();
            //   Log.e("msg_params_error", e.getMessage());

        }

        //   Log.e("msg_params", jsonObject.toString());
        mSocket.emit(EVENT_NEW_MESSAGE, jsonObject);

    }

    @Override
    public void onResume() {
        super.onResume();
        chatModel = new ArrayList<>();
        chatModelTemp = new ArrayList<>();
        chatModel.clear();
        chatModelTemp.clear();
        ConnectToSocket();

        //  Log.e("ResumeFragment", "onHiddenChanged: is visible");

    }

    private BroadcastReceiver mMessageReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Get extra data included in the Intent

            // Log.e("chatDetailFrg", "BroadCastReceiver");

            try {

                NotificationModel message = intent.getParcelableExtra(NT_DATA);
                //  Log.e("BroadCastonReceive: ", message.getNotiType());

                if (message.getNotiType().equalsIgnoreCase("orderstatus")) {

                    //   Log.e("onReceive: ", "ssss");
                    markascomplete.setText("Review Pending");
                    getofferlist(otherUserId);

                }

                if (message.getNotiType().equalsIgnoreCase(SAConstants.NotificationKeys.NT_ACCEPT_REJECT)) {
                    getofferlist(otherUserId);
                }

                if (message.getNotiType().equalsIgnoreCase(SAConstants.NotificationKeys.NT_ACCEPT_REJECT) || message.getNotiType().equalsIgnoreCase(SAConstants.NotificationKeys.NT_PAYMENT)) {

                    for (int i = 0; i < models.size(); i++) {
                        ChatMessageModel model = models.get(i);

                        if (model.getMsgId().equalsIgnoreCase(message.getMsgId())) {
                            //         Log.e("index", "onReceive: " + models.indexOf(model));
                            models.get(models.indexOf(model)).setStatus(message.getStatus());
                            if (message.getStatus().equalsIgnoreCase("s")) {
                                //txtReview.setVisibility(View.VISIBLE);
                            }
                            adapter.notifyDataSetChanged();
                            recMessage.smoothScrollToPosition(models.indexOf(model));
                        }

                    }

                    if (message.getNotiType().equalsIgnoreCase("payment")) {
                        getofferlist(otherUserId);
                    }
                    if (message.getStatus().equalsIgnoreCase("a")) {

                        getofferlist(otherUserId);
                    }

                }

            } catch (Exception e) {
                //  Log.e("Receiver", "onReceive: " + e.getMessage());
            }

        }
    };

    public static String getDate(long timestamp) {

        Calendar nowTime = Calendar.getInstance();
        Calendar neededTime = Calendar.getInstance();
        neededTime.setTimeInMillis(timestamp * 1000L);

        if (nowTime.get(Calendar.DATE) == neededTime.get(Calendar.DATE)) {
            //here return like "Today at 12:00"
            return "Today " + DateFormat.format("hh:mm:ss aa", neededTime);

        } else if (nowTime.get(Calendar.DATE) - neededTime.get(Calendar.DATE) == 1) {
            //here return like "Yesterday at 12:00"
            return "Yesterday " + DateFormat.format("hh:mm:ss aa", neededTime);

        } else {
            //here return like "May 31, 12:00"
            return DateFormat.format("dd-MM-yyyy hh:mm:ss aa", neededTime).toString();
        }

    }

    public String datefun(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String monthName = new SimpleDateFormat("MMMM").format(cal.getTime());
        SimpleDateFormat outFormat = new SimpleDateFormat("EEEE");
        String da = outFormat.format(cal.getTime());
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String overall = da + ", " + day + " " + monthName;
        return overall;

    }

    private void setupoffer(ArrayList<Map<String, String>> list) {

        try {

            payoffer_adapter2 = new Pay_offer_adapter2(getActivity(), list, this, "yes", otherUserId);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false);
            markAscompleteRecycler2.setLayoutManager(linearLayoutManager);
            markAscompleteRecycler2.setItemAnimator(new DefaultItemAnimator());
            markAscompleteRecycler2.setAdapter(payoffer_adapter2);

        } catch (Exception e) {
            // Log.e("setup_error", "setUpMessgae: ");
        }


    }

    private void setup_accepted(ArrayList<Map<String, String>> list) {

        try {

            mark_adapter = new Pay_offer_adapter(getActivity(), list, this, otherUserId, markascomplete);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, false);
            markAscompleteRecycler.setLayoutManager(linearLayoutManager);
            markAscompleteRecycler.setItemAnimator(new DefaultItemAnimator());
            markAscompleteRecycler.setAdapter(mark_adapter);

        } catch (Exception e) {

            //   Log.e("setup_error", "setUpMessgae: ");

        }

    }

    /*Get offerlist api for the bottom sticked offers for both accepted and send offers*/

    private void getofferlist(String otherUserId) {

//        Log.e("getofferlist1233: ", "" + otherUserId);
//        Log.e("getofferlist1234: ", "" + HelperPreferences.get(getActivity()).getString(UID));

        chatoffercall = service.chat_offer_list(auth, token, HelperPreferences.get(getActivity()).getString(UID), otherUserId);
        chatoffercall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                try {

                    main_offer_list.clear();
                    list.clear();
                    mark_main_offer_list.clear();
                    mark_list.clear();
                    Log.e("Chat_detailApi", "onResponse: fail" + response.body().toString());
                    JSONObject object = new JSONObject(response.body().toString());
                    String status = object.getString("status");
                    friends_stripe_is_verified=object.optString("stripe_verified");

                    if (status.equalsIgnoreCase("1")) {

                        JSONArray array = object.getJSONArray("offerList");

                        if (array.length() > 0) {

                            for (int i = 0; i < array.length(); i++) {

                                JSONObject data = array.getJSONObject(i);
                                map = new HashMap<>();

                                if (data.getString("is_reviewed").equalsIgnoreCase("N")) {

                                    map.put("order_id", data.optString("order_id"));
                                    map.put("offer_id", data.getString("offer_id"));
                                    map.put("product_owner", data.getString("product_owner"));
                                    map.put("product_owner_name", data.getString("product_owner_name"));
                                    map.put("product_id", data.getString("product_id"));
                                    map.put("product_name", data.getString("product_name"));
                                    map.put("product_image", data.getString("product_image"));
                                    map.put("price_cost", data.getString("price_cost"));
                                    map.put("quantity", data.getString("quantity"));
                                    map.put("order_status", data.getString("order_status"));
                                    map.put("is_reviewed", data.getString("is_reviewed"));
                                    map.put("dispute_days", data.getString("dispute_days"));
                                    list.add(map);

                                } else {

                                }

                            }

                            // Log.e("offer: ", "dddd" + list.size());

                        }

                        JSONArray acceptedarray = object.getJSONArray("accepted_offers");

                        if (acceptedarray.length() > 0) {

                            mark_list.clear();

                            for (int i = 0; i < acceptedarray.length(); i++) {

                                JSONObject data = acceptedarray.getJSONObject(i);
                                mark_map = new HashMap<>();

                                if (data.getString("is_reviewed").equalsIgnoreCase("N")) {

                                    mark_map.put("order_id", data.optString("order_id"));
                                    mark_map.put("offer_id", data.getString("offer_id"));
                                    mark_map.put("product_owner", data.getString("product_owner"));
                                    mark_map.put("product_owner_name", data.getString("product_owner_name"));
                                    mark_map.put("product_id", data.getString("product_id"));
                                    mark_map.put("product_name", data.getString("product_name"));
                                    mark_map.put("product_image", data.getString("product_image"));
                                    mark_map.put("price_cost", data.getString("price_cost"));
                                    mark_map.put("quantity", data.getString("quantity"));
                                    mark_map.put("is_reviewed", data.getString("is_reviewed"));
                                    mark_map.put("order_status", data.getString("order_status"));
                                    mark_list.add(mark_map);

                                } else {

                                }

                            }

                            //    Log.e("sdsss: ", "dddd" + mark_list);

                            //-----------------------------------------------------------------------------------

                            if (!mark_list.isEmpty()) {

                                acceptLayout.setVisibility(View.VISIBLE);
                                buttonstatus(mark_list);

                                if (mark_list.size() <= 1) {

                                    //    Log.e("marklist: ", "marklist");
                                    markasCompleteBtnCollapsedItems.setVisibility(View.GONE);
                                    mark_main_offer_list.add(mark_list.get(0));

                                    setup_accepted(mark_main_offer_list);


                                } else {

                                    //           Log.e("marklist: ", "marklist else");
                                    mark_main_offer_list.add(mark_list.get(0));
                                    markasCompleteBtnCollapsedItems.setVisibility(View.VISIBLE);
                                    setup_accepted(mark_main_offer_list);
                                    markasCompleteBtnCollapsedItems.setText("+ " + String.valueOf(mark_list.size() - 1) + " more products");


                                }

                            } else {

                                acceptLayout.setVisibility(View.GONE);

                            }

                            //--------------------------------------------------------------------------------

                            markasCompleteBtnCollapsedItems.setOnClickListener(view1 -> {

                                //--------------------------------------------
                                /* Top view in app*/
                                //--------------------------------------------

                                if (markasCompleteBtnCollapsedItems.getText().toString().equalsIgnoreCase("Collapse products")) {

                                    mark_main_offer_list.clear();
                                    mark_main_offer_list.add(mark_list.get(0));
                                    setup_accepted(mark_main_offer_list);
                                    markasCompleteBtnCollapsedItems.setText("+ " + String.valueOf(mark_list.size() - 1) + " more products");


                                } else {

                                    mark_main_offer_list.clear();
                                    mark_main_offer_list.addAll(mark_list);
                                    setup_accepted(mark_main_offer_list);
                                    markasCompleteBtnCollapsedItems.setText("Collapse products");

                                }

                                acceptLayout.setVisibility(View.VISIBLE);

                            });

                        }

                        //  Log.e("sizePrint", list.size() + "");
                        //-------------------------------list----------------------------------

                        if (!list.isEmpty()) {

                            acceptLayout2.setVisibility(View.VISIBLE);
                            buttonstatus1(list);

                            if (list.size() <= 1) {

                                //   Log.e("marklist: ", "marklist");
                                markasCompleteBtnCollapsedItems2.setVisibility(View.GONE);
                                extra_list.clear();
                                extra_list.addAll(list);
                                main_offer_list.add(list.get(0));
                                setupoffer(main_offer_list);

                            } else {

                                //     Log.e("marklist: ", "marklist else");
                                //mark_main_offer_list.add(mark_list.get(0));
                                markasCompleteBtnCollapsedItems2.setVisibility(View.VISIBLE);
                                extra_list.clear();
                                extra_list.addAll(list);
                                main_offer_list.add(list.get(0));
                                setupoffer(main_offer_list);
                                markasCompleteBtnCollapsedItems2.setText("+ " + String.valueOf(list.size() - 1) + " more products");

                            }

                            //----------------------to show pay button----------------------------------

                            String stat = "false";

                            for (int i = 0; i < list.size(); i++) {

                                if (list.get(i).get("order_status").equalsIgnoreCase("A"))
                                    stat = "true";

                            }

                            if (stat.equalsIgnoreCase("true")) {

                                payLinLay.setVisibility(View.VISIBLE);
                                markascomplete2.setVisibility(View.GONE);

                                //--------------to get total cost of products---------------------------------------
                                double amount = 0, total_amount = 0;

                                if (list != null) {

                                    for (int i = 0; i < list.size(); i++) {

                                        if (list.get(i).get("order_status").equalsIgnoreCase("A")) {

                                            try {

                                                if (list.get(i).get("price_cost").contains("$")) {
                                                    String amt = list.get(i).get("price_cost").replace("$", "").trim();
                                                    amount = Double.parseDouble(amt);
                                                } else if (!list.get(i).get("price_cost").equalsIgnoreCase("")) {
                                                    amount = Double.parseDouble(list.get(i).get("price_cost"));

                                                }

                                            } catch (Exception e) {

                                            }

                                            total_amount += amount;

                                        }

                                    }

                                }

                                payNewbtn.setText("Pay: $" + total_amount);
                                //------------------------------------------------------------------

                            } else {

                                payLinLay.setVisibility(View.GONE);
                                markascomplete2.setVisibility(View.VISIBLE);

                            }

                            //payLinLay.setVisibility(View.VISIBLE);
                            //-------------------------------------------------------------------

                        } else {

                            acceptLayout2.setVisibility(View.GONE);

                        }

                        markasCompleteBtnCollapsedItems2.setOnClickListener(view1 -> {

                            //--------------------------------------------
                            /* Bottom view in app*/
                            //--------------------------------------------

                            if (markasCompleteBtnCollapsedItems2.getText().toString().equalsIgnoreCase("Collapse products")) {

                                main_offer_list.clear();
                                main_offer_list.add(list.get(0));
                                setupoffer(main_offer_list);
                                markasCompleteBtnCollapsedItems2.setText("+ " + String.valueOf(list.size() - 1) + " more products");

                            } else {

                                main_offer_list.clear();
                                main_offer_list.addAll(list);
                                setupoffer(main_offer_list);
                                markasCompleteBtnCollapsedItems2.setText("Collapse products");

                            }

                            acceptLayout2.setVisibility(View.VISIBLE);

                        });

                        if (list.size() == 0 && mark_list.size() == 0)
                            chatScrollview.setVisibility(View.GONE);
                        else
                            chatScrollview.setVisibility(View.VISIBLE);


                        List<String> li;
                        List<String> list1 = new ArrayList<>();

                        for (int i = 0; i < list.size(); i++) {

                            li = new ArrayList<>();
                            li.add(list.get(i).get("offer_id"));
                            list1.addAll(li);

                        }

                        if (list1.size() != 0) {

                            StringBuilder sb1 = new StringBuilder();

                            for (String s : list1) {

                                sb1.append(",");
                                sb1.append(s);

                            }

                            latitudeList = sb1.substring(1).toString();

                        }

                        //   Log.e("sdsss: ", "ssss" + latitudeList);


                    } else {
                        payLayout.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    //    Log.e("errorPrint", e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                //  Log.e("onFailChatDetail", t.getMessage());
            }
        });

    }

    @Override
    public void updateSubTotal(String subtotal) {
        // Log.e("updateSubTotal: ", "cpomg");
        if (subtotal.equals("offer")) {
            //  Log.e("updateSubTotal: ", "cpomg");
            getofferlist(otherUserId);
        }

    }

    /*Mark as complete button*/
    private void mark_complete(String order_id, String status) {
//        Log.e("xxxc", "order: " + order_id.toString());
//        Log.e("xxxc", "order: " + status.toString());
//        Log.e("xxxc", "order: " + HelperPreferences.get(getActivity()).getString(UID));
//        Log.e("xxxc", "order: " + order_id.toString());
        WebService webService = Global.WebServiceConstants.getRetrofitinstance();
        Call<JsonObject> stripePaymentApi = webService.set_order_status(auth, token, HelperPreferences.get(getActivity()).getString(UID), order_id, status, otherUserId);
        stripePaymentApi.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (response.isSuccessful()) {

                    try {

                        //  Log.e("markcompled", "order: " + response.body().toString());
                        payNewbtn.setText("Review Pending");
                        markascomplete.setText("Review Pending");
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        //  Log.e("paymentException", "onResponse: " + e.getMessage());
                    }

                } else {

                    try {

                        Log.e("paymenterrorcomp", response.errorBody().string() + "//");

                    } catch (IOException e) {

                        e.printStackTrace();

                    }

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }

        });

    }

    private void mark_complete1(String order_id, String status) {

//        Log.e("xxxc", "order: " + order_id.toString());
//        Log.e("xxxc", "order: " + status.toString());
//        Log.e("xxxc", "order: " + HelperPreferences.get(getActivity()).getString(UID));
//        Log.e("xxxc", "order: " + order_id.toString());

        WebService webService = Global.WebServiceConstants.getRetrofitinstance();
        Call<JsonObject> stripePaymentApi = webService.set_order_status(auth, token, HelperPreferences.get(getActivity()).getString(UID), order_id, status, otherUserId);
        stripePaymentApi.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                //   Log.e("mrkcmp", response.code() + "//");

                if (response.isSuccessful()) {

                    try {

                        //  Log.e("apiii45", "" + response.body().toString());
                        markascomplete2.setText("Review Pending");
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        //     Log.e("paymentException", "onResponse: " + e.getMessage());
                    }

                } else {

                    try {
                        Log.e("mrkcmpterror", "order: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }

        });

    }

    /*Method for accepted offer buttn statuse */
    public void buttonstatus(ArrayList<Map<String, String>> t) {

        if (t.get(0).get("order_status").equalsIgnoreCase("A")) {
            markascomplete.setText("Unpaid");
            markascomplete.setBackground(getResources().getDrawable(R.drawable.grey_btn));
        }
        if (t.get(0).get("order_status").equalsIgnoreCase("S")) {
            markascomplete.setText("Mark as completed");
            markascomplete.setBackground(getResources().getDrawable(R.drawable.chat_pay_round));
        }
        if (t.get(0).get("order_status").equalsIgnoreCase("C")) {
            markascomplete.setText("Review Pending");
            markascomplete.setBackground(getResources().getDrawable(R.drawable.chat_pay_round));
        }
        if (t.get(0).get("order_status").equalsIgnoreCase("D")) {
            markascomplete.setText("Offer Disputed");
            markascomplete.setBackground(getResources().getDrawable(R.drawable.chat_pay_round));
        }
    }

    public void buttonstatus1(ArrayList<Map<String, String>> t) {

        if (t.get(0).get("order_status").equalsIgnoreCase("A")) {
            markascomplete2.setText("Unpaid");
            markascomplete2.setBackgroundColor(getActivity().getResources().getColor(R.color.grey_font));
        }
        if (t.get(0).get("order_status").equalsIgnoreCase("S")) {
            markascomplete2.setText("Mark as completed");
            markascomplete2.setBackground(getResources().getDrawable(R.drawable.chat_pay_round));
        }
        if (t.get(0).get("order_status").equalsIgnoreCase("C")) {
            markascomplete2.setText("Review Pending");
            markascomplete2.setBackground(getResources().getDrawable(R.drawable.chat_pay_round));
        }
        if (t.get(0).get("order_status").equalsIgnoreCase("D")) {
            markascomplete2.setText("Offer Disputed");
            markascomplete2.setBackground(getResources().getDrawable(R.drawable.chat_pay_round));
        }
    }

    /*Metho for send offer button statuses */
    public void mainofferliststatus_btn(ArrayList<Map<String, String>> t) {
        if (t.get(0).get("order_status").equalsIgnoreCase("A")) {

            int amount, total_amount = 0;
            if (t != null) {
                for (int i = 0; i < t.size(); i++) {

                    if (t.get(0).get("order_status").equalsIgnoreCase("A")) {
                        if (t.get(i).get("price_cost").contains("$")) {
                            String amt = t.get(i).get("price_cost").replace("$", "").trim();
                            amount = Integer.parseInt(amt);
                            total_amount += amount;
                        } else {
                            amount = Integer.parseInt(t.get(i).get("price_cost"));

                            total_amount += amount;
                        }

                    }

                }

            }


            payNewbtn.setText("Pay: $" + total_amount);
            payCancelbtn.setVisibility(View.VISIBLE);
        }

        if (t.get(0).get("order_status").equalsIgnoreCase("S")) {
            payNewbtn.setText("Mark as completed");
            payCancelbtn.setVisibility(View.GONE);
        }

        if (t.get(0).get("order_status").equalsIgnoreCase("C")) {
            payNewbtn.setText("Review Pending");
            payCancelbtn.setVisibility(View.GONE);
            payNewbtn.setBackgroundColor(getActivity().getResources().getColor(R.color.grey_font));

        }
        if (t.get(0).get("order_status").equalsIgnoreCase("D")) {
            payNewbtn.setText("Offer Disputed");
            payCancelbtn.setVisibility(View.GONE);

        }

        if (!extra_list.isEmpty()) {
            if (!extra_list.get(0).get("dispute_days").isEmpty()) {
                int days = Integer.parseInt(extra_list.get(0).get("dispute_days"));
                if (days >= 0) {
                    ((ChatActivity) getActivity()).item3.setVisible(true);
                } else {
                    ((ChatActivity) getActivity()).item3.setVisible(false);
                }
            } else {
                ((ChatActivity) getActivity()).item3.setVisible(false);
            }
        }

    }

    public void disputedialog() {

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setMessage("Dispute Reason");
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dispute_alertdiaog, null);
        EditText input = view.findViewById(R.id.disputereason);
        alert.setView(view);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                if (extra_list.get(0).get("order_id") != null) {
                    String orderid = extra_list.get(0).get("order_id");


                    dispute_api(HelperPreferences.get(getActivity()).getString(UID), otherUserId, orderid, input.getText().toString().trim());

                }

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();

    }

    /*Dispute Api*/
    public void dispute_api(String userid, String friendid, String orderid, String reason) {

        Call<JsonObject> call = service.disputeOffer(auth, token, userid, friendid, orderid, reason);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                //  Log.e("onResponse: ", response.body().toString());

                try {

                    JSONObject obj = new JSONObject(response.body().toString());

                    if (obj.getString("status").equalsIgnoreCase("1")) {

                        payNewbtn.setText("Offer Disputed");
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_LONG).show();

                    } else {

                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException e) {

                    e.printStackTrace();

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                Toast.makeText(getActivity(), "Something went's wrong", Toast.LENGTH_LONG).show();

            }
        });
    }

    /*Api to reduce product quantity after payment */
    public void manage_inventory(String productid, String qty) {
        Call<JsonObject> call = service.manage_inventory(auth, token, HelperPreferences.get(getActivity()).getString(UID), productid, qty);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                try {
                    //   Log.e("onResponse: ", response.body().toString());
                    JSONObject obj = new JSONObject(response.body().toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getActivity(), "Something went's wrong", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void cancelrequest(String offerId, String status) {

        Dialog dialog = S_Dialogs.getLoadingDialog(getActivity());
        dialog.show();
        Call<MakeOfferModel> acceptDeclineCall = service.acceptDeclineOfferApi(auth, token, HelperPreferences.get(getActivity()).getString(UID), otherUserId, offerId, status);
        acceptDeclineCall.enqueue(new Callback<MakeOfferModel>() {
            @Override
            public void onResponse(Call<MakeOfferModel> call, Response<MakeOfferModel> response) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (response.isSuccessful()) {
                    //     Log.e("AcceptDecline", "onResponse: " + response.body().getResult().getStatus());

                    Toast.makeText(getActivity(), "Offer Cancelled", Toast.LENGTH_SHORT).show();

                    getofferlist(otherUserId);


                } else {

                    try {
                        Log.e("AcceptDecline", "onResponsefaild: " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<MakeOfferModel> call, Throwable t) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

//                Log.e("AcceptDecline", "onResponsefaild: " + t.getMessage());

            }
        });
    }

    @Override
    public void removeoffer(int pos) {

//        Log.e("removeoffer: ", "" + pos);
//        Log.e("removeoffer: ", "" + extra_list);
        cancelrequest(extra_list.get(pos).get("offer_id"), "r");

    }

    private void payByCash() {

    }
}

