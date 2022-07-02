package com.app.admin.sellah.view.adapter;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.admin.sellah.R;
import com.app.admin.sellah.controller.utils.RecyclerViewClickListener;
import com.app.admin.sellah.controller.WebServices.WebService;
import com.app.admin.sellah.controller.utils.Global;
import com.app.admin.sellah.controller.utils.HelperPreferences;
import com.app.admin.sellah.controller.utils.SAConstants;
import com.app.admin.sellah.model.extra.commonResults.Common;
import com.app.admin.sellah.view.CustomDialogs.S_Dialogs;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.admin.sellah.controller.WebServices.ApisHelper.auth;
import static com.app.admin.sellah.controller.WebServices.ApisHelper.token;
import static com.app.admin.sellah.controller.utils.Global.getUser.isLogined;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.UID;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.USER_PROFILE_PIC;

/**
 * Created by Admin on 11/30/2018.
 */

public class LiveStreamChatAdapter extends RecyclerView.Adapter {


    ArrayList<HashMap<String, String>> messagelist;
    Context context;
    private RecyclerViewClickListener onBottomReachedListener;
    private WebService service;
    private final int[] IMAGES = {R.drawable.unpined_icon, R.drawable.pined_icon};
    private boolean isClicedR;
    private String sellerId="";
    OptionsClickCallBack makeOfferController;
    private String productname_1 = "";


    public LiveStreamChatAdapter(ArrayList<HashMap<String, String>> models, Context activity,String sellerId,OptionsClickCallBack controller) {
        this.messagelist = models;
        this.context = activity;
        this.makeOfferController=controller;
        this.sellerId=sellerId;
      //  Log.e("liveStreamChatApt_list:", messagelist + "");
       // Log.e("seller_id ", sellerId + "  <-");
    }

    public static class SendMessageViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userImage;
        TextView txtMessage, txtMsgTime;
        ImageView imagePin, imgoptions;

        public SendMessageViewHolder(View itemView) {
            super(itemView);
            this.userImage = itemView.findViewById(R.id.img_user);
            this.txtMessage = itemView.findViewById(R.id.txt_message);
            this.txtMsgTime = itemView.findViewById(R.id.txt_msg_time);
            this.imagePin = itemView.findViewById(R.id.img_pin);
            this.imgoptions = itemView.findViewById(R.id.img_msg_options);

        }
    }


    public static class RecievedMessageViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePin,imgOption;
        CircleImageView userImage;
        TextView txtMessage, txtMsgTime;
        RelativeLayout img_sendOfferIcon;

        public RecievedMessageViewHolder(View itemView) {
            super(itemView);
            this.userImage = itemView.findViewById(R.id.img_user);
            this.txtMessage = itemView.findViewById(R.id.txt_message);
            this.txtMsgTime = itemView.findViewById(R.id.txt_msg_time);
            this.imagePin = itemView.findViewById(R.id.img_pin);
            this.imgOption = itemView.findViewById(R.id.img_msg_options);
            this.img_sendOfferIcon = itemView.findViewById(R.id.img_sendOfferIcon);

        }
    }

    public static class ImageSentViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userImage;
        ImageView imgSent;

        public ImageSentViewHolder(View itemView) {
            super(itemView);
            this.userImage = itemView.findViewById(R.id.img_user);
            this.imgSent = itemView.findViewById(R.id.img_sent_image);
        }
    }

    public static class ImageReceivedViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userImage;
        ImageView imgSent;

        public ImageReceivedViewHolder(View itemView) {
            super(itemView);
            this.userImage = itemView.findViewById(R.id.img_user);
            this.imgSent = itemView.findViewById(R.id.img_received_image);
        }
    }

    public static class PinnedReceivedViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userImage;
        TextView txtTitle, txtCategory, txtDescription;

        public PinnedReceivedViewHolder(View itemView) {
            super(itemView);
            this.userImage = itemView.findViewById(R.id.img_user);
            this.txtTitle = itemView.findViewById(R.id.txt_Title);
            this.txtCategory = itemView.findViewById(R.id.txt_category);
            this.txtDescription = itemView.findViewById(R.id.txt_description);
        }
    }

    public static class PinnedSendViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userImage;
        TextView txtTitle, txtCategory, txtDescription;

        public PinnedSendViewHolder(View itemView) {
            super(itemView);
            this.userImage = itemView.findViewById(R.id.img_user);
            this.userImage = itemView.findViewById(R.id.img_user);
            this.txtTitle = itemView.findViewById(R.id.txt_Title);
            this.txtCategory = itemView.findViewById(R.id.txt_category);
            this.txtDescription = itemView.findViewById(R.id.txt_description);
        }
    }

    public static class MakeOfferViewHolder extends RecyclerView.ViewHolder {

        TextView txtItemName, txtItemQuantity, txtItemCost, txtCanceled;
        Button btnAccept, btnCanceld, btnPay;
        LinearLayout liOffer, liPay, liOfferStatus;

        public MakeOfferViewHolder(View itemView) {
            super(itemView);
            this.txtItemName = (TextView) itemView.findViewById(R.id.txt_item_name);
            this.txtItemQuantity = itemView.findViewById(R.id.txt_item_quantity);
            this.txtItemCost = itemView.findViewById(R.id.txt_item_cost);
            this.txtCanceled = (TextView) itemView.findViewById(R.id.txt_offer_status);
            this.btnAccept = itemView.findViewById(R.id.btn_item_accept);
            this.btnPay = itemView.findViewById(R.id.btn_pay);
            this.btnCanceld = itemView.findViewById(R.id.btn_item_cancel);
            this.liOffer = itemView.findViewById(R.id.li_confirm_order);
            this.liPay = itemView.findViewById(R.id.li_pay_order);
            this.liOfferStatus = itemView.findViewById(R.id.li_offer_status);
        }
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case SAConstants.Keys.TYPE_SEND:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_live_message_sent, parent, false);
                return new LiveStreamChatAdapter.SendMessageViewHolder(view);
            case SAConstants.Keys.TYPE_RECEIVED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_live_message_recieved, parent, false);
                return new LiveStreamChatAdapter.RecievedMessageViewHolder(view);
            case SAConstants.Keys.TYPE_MAKEOFFER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_live_message_sent, parent, false);
                return new LiveStreamChatAdapter.SendMessageViewHolder(view);
            case SAConstants.Keys.TYPE_MAKEOFFER_RECEIVED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_live_message_recieved, parent, false);
                return new LiveStreamChatAdapter.RecievedMessageViewHolder(view);
            case SAConstants.Keys.TYPE_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_send_image, parent, false);
                return new LiveStreamChatAdapter.ImageSentViewHolder(view);
            case SAConstants.Keys.TYPE_RECIEVED_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_received_image, parent, false);
                return new LiveStreamChatAdapter.ImageReceivedViewHolder(view);
            case SAConstants.Keys.TYPE_PINED_SEND_MSG:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_live_pin_message, parent, false);
                return new LiveStreamChatAdapter.PinnedSendViewHolder(view);
            case SAConstants.Keys.TYPE_PINED_RECEIVED_MSG:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_live_pin_message, parent, false);
                return new LiveStreamChatAdapter.PinnedReceivedViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

      //  Log.e("onBindViewHolderLive","count");

        if (messagelist.get(position).get("senderid").equalsIgnoreCase(HelperPreferences.get(context).getString(UID))) {

            if (messagelist.get(position).get("type").equalsIgnoreCase("o")) {

                handleOfferData(holder, position);
            } else if (messagelist.get(position).get("type").equalsIgnoreCase("img")) {

                handleSentImageData(holder, position);

            } else {
                handleSentMessageData(holder, position);
            }
        } else {

            if (messagelist.get(position).get("type").equalsIgnoreCase("o")) {
                handleOfferDataReceived(holder, position);

            } else if (messagelist.get(position).get("type").equalsIgnoreCase("img")) {

                handleRecievedImageData(holder, position);

            }else {
                handleRecievMessageData(holder, position);
            }
        }

        if (position == messagelist.size() - 1) {


        } else {


        }
    }

    private void handlePinedMessageRecieved(RecyclerView.ViewHolder holder, int position) {
        ((PinnedReceivedViewHolder) holder).txtTitle.setText("Title :-" + messagelist.get(position).get("product_name"));
        ((PinnedReceivedViewHolder) holder).txtDescription.setText("Description :-" + messagelist.get(position).get("product_description"));
        ((PinnedReceivedViewHolder) holder).txtCategory.setText("Category :-" + messagelist.get(position).get("product_category"));
    }

    private void handlePinedMessageSent(RecyclerView.ViewHolder holder, int position) {
        ((PinnedSendViewHolder) holder).txtTitle.setText("Title :-" + messagelist.get(position).get("product_name"));
        ((PinnedSendViewHolder) holder).txtDescription.setText("Description :-" + messagelist.get(position).get("product_description"));
        ((PinnedSendViewHolder) holder).txtCategory.setText("Category :-" + messagelist.get(position).get("product_category"));
    }

    private void handleOfferDataReceived(RecyclerView.ViewHolder holder, int position) {
        Glide.with(context)
                .load(messagelist.get(position).get("sender_image"))
                .apply(Global.getGlideOptions())
                .into(((ImageReceivedViewHolder) holder).userImage);
        if (messagelist.get(position).get("ispined") != null && messagelist.get(position).get("ispined").equalsIgnoreCase("y")) {
            ((RecievedMessageViewHolder) holder).imagePin.setImageResource(IMAGES[1]);
        } else {
            ((RecievedMessageViewHolder) holder).imagePin.setImageResource(IMAGES[0]);
        }
        ((RecievedMessageViewHolder) holder).txtMessage.setText(messagelist.get(position).get("message"));
        isClicedR = false;

    }

    private void handleRecievMessageData(RecyclerView.ViewHolder holder, int position) {
        isClicedR = false;

        ((RecievedMessageViewHolder) holder).txtMsgTime.setText(Global.formateDateTo_HHmm(Global.convertUTCToLocal(messagelist.get(position).get("created_at"))));

        if (messagelist.get(position).get("owner_id") != null && HelperPreferences.get(context).getString(UID).equalsIgnoreCase(messagelist.get(position).get("owner_id"))) {


            ((RecievedMessageViewHolder) holder).img_sendOfferIcon.setVisibility(View.VISIBLE);

          //  Log.e("LongPress", "handleRecievMessageData: ");
            ((RecievedMessageViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    PopupMenu popup = new PopupMenu(context, ((RecievedMessageViewHolder) holder).imgOption);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.menu_livechat, popup.getMenu());
                    popup.getMenu().findItem(R.id.menu_make_offer).setVisible(true);
                    if (messagelist.get(position).get("ispined") != null && messagelist.get(position).get("ispined").equalsIgnoreCase("y")) {
                        popup.getMenu().findItem(R.id.menu_add_bookmark).setVisible(false);
                        popup.getMenu().findItem(R.id.menu_remove_bookmark).setVisible(true);
                    } else {
                        popup.getMenu().findItem(R.id.menu_add_bookmark).setVisible(true);
                        popup.getMenu().findItem(R.id.menu_remove_bookmark).setVisible(false);
                    }
                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if (isLogined(context)) {
                                switch (menuItem.getItemId()) {
                                    case R.id.menu_make_offer:
                                //        Log.e("mkoffer", "onMenuItemClick: ");
                                        makeOfferController.onMakeOfferClicked(messagelist.get(position).get("comment_id"), messagelist.get(position).get("senderid"));
                                        break;
                                    case R.id.menu_add_bookmark:
                                        pinCommentApi(messagelist.get(position).get("comment_id"), messagelist.get(position).get("group_id"), "y", new PinUnPinCommentCallback() {
                                            @Override
                                            public void onCommentPinSuccess() {
                                                isCliced = true;
                                                messagelist.get(position).put("ispined", "y");
                                            }

                                            @Override
                                            public void onCommentPinUnSuccess() {
                                                Toast.makeText(context, "Unable to add comment as bookmark at the movement.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        break;
                                    case R.id.menu_remove_bookmark:
                                        pinCommentApi(messagelist.get(position).get("comment_id"), messagelist.get(position).get("group_id"), "n", new PinUnPinCommentCallback() {
                                            @Override
                                            public void onCommentPinSuccess() {
                                                isCliced = true;
                                                messagelist.get(position).put("ispined", "n");
                                            }

                                            @Override
                                            public void onCommentPinUnSuccess() {
                                                Toast.makeText(context, "Unable to remove comment from bookmark the movement.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        break;
                                    case R.id.menu_cancel:
                                        popup.dismiss();
                                        break;
                                    case R.id.menu_pin_comment:
                           //             Log.e("pinComment", "onMenuItemClick: ");
                                        makeOfferController.onPinLiveCommentClick(messagelist.get(position).get("comment_id"));
                                        break;
                                }
                            } else {
                                S_Dialogs.getLoginDialog(context).show();
                            }
                            return false;
                        }
                    });


                }
            });
        } else {
            ((RecievedMessageViewHolder) holder).img_sendOfferIcon.setVisibility(View.GONE);
          //  Log.e("LongPress", "handleRecievMessageData: negative");
        }

        ((LiveStreamChatAdapter.RecievedMessageViewHolder) holder).txtMessage.setText(messagelist.get(position).get("message"));

        Glide.with(context)
                .load(messagelist.get(position).get("sender_image"))
                .apply(Global.getGlideOptions())
                .into(((RecievedMessageViewHolder) holder).userImage);
    }

    boolean isCliced = false;

    private void handleSentMessageData(RecyclerView.ViewHolder holder, int position) {

        isCliced = false;

        if (messagelist.get(position).get("owner_id")!=null&&messagelist.get(position).get("owner_id").equalsIgnoreCase(HelperPreferences.get(context).getString(UID))) {
         //   Log.e("longSent", "handleSentMessageData: " );
            ((SendMessageViewHolder) holder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu popup = new PopupMenu(context, ((SendMessageViewHolder) holder).imgoptions);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.menu_livechat, popup.getMenu());
                    popup.getMenu().findItem(R.id.menu_make_offer).setVisible(false);
                    if (messagelist.get(position).get("ispined") != null && messagelist.get(position).get("ispined").equalsIgnoreCase("y")) {
                        popup.getMenu().findItem(R.id.menu_add_bookmark).setVisible(false);
                        popup.getMenu().findItem(R.id.menu_remove_bookmark).setVisible(true);
                    } else {
                        popup.getMenu().findItem(R.id.menu_add_bookmark).setVisible(true);
                        popup.getMenu().findItem(R.id.menu_remove_bookmark).setVisible(false);
                    }
                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if (isLogined(context)) {
                                switch (menuItem.getItemId()) {
                                    case R.id.menu_pin_comment:
                               //         Log.e("pinComment", "onMenuItemClick: ");
                                        makeOfferController.onPinLiveCommentClick(messagelist.get(position).get("comment_id"));
                                        break;
                                    case R.id.menu_make_offer:
                                        makeOfferController.onMakeOfferClicked(messagelist.get(position).get("comment_id"),messagelist.get(position).get("senderid"));
                                        break;
                                    case R.id.menu_add_bookmark:
                                        pinCommentApi(messagelist.get(position).get("comment_id"), messagelist.get(position).get("group_id"), "y", new PinUnPinCommentCallback() {
                                            @Override
                                            public void onCommentPinSuccess() {
                                                isCliced = true;
                                                messagelist.get(position).put("ispined", "y");
                                            }

                                            @Override
                                            public void onCommentPinUnSuccess() {
                                                Toast.makeText(context, "Unable to pin comment at the movement.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        break;
                                    case R.id.menu_remove_bookmark:
                                        pinCommentApi(messagelist.get(position).get("comment_id"), messagelist.get(position).get("group_id"), "n", new PinUnPinCommentCallback() {
                                            @Override
                                            public void onCommentPinSuccess() {
                                                isCliced = true;
                                                messagelist.get(position).put("ispined", "n");
                                            }

                                            @Override
                                            public void onCommentPinUnSuccess() {
                                                Toast.makeText(context, "Unable to pin comment at the movement.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        break;
                                    case R.id.menu_cancel:
                                        popup.dismiss();
                                        break;
                                }
                            } else {
                                S_Dialogs.getLoginDialog(context).show();
                            }

                            return false;
                        }
                    });
                    return false;
                }
            });
        } else {
            Log.e("longSent", "handleSentMessageData: negative" );
        }


        ((LiveStreamChatAdapter.SendMessageViewHolder) holder).txtMessage.setText(messagelist.get(position).get("message"));
        ((SendMessageViewHolder) holder).txtMsgTime.setText(Global.formateDateTo_HHmm(Global.convertUTCToLocal(messagelist.get(position).get("created_at"))));

        Glide.with(context).load(HelperPreferences.get(context).getString(USER_PROFILE_PIC))
                .apply(Global.getGlideOptions())
                .into(((SendMessageViewHolder) holder).userImage);/*       */


    }
    private void handleSentImageData(RecyclerView.ViewHolder holder, int position) {
        Glide.with(context).load(messagelist.get(position).get("image_url")).apply(Global.getGlideOptions()).into(((ImageSentViewHolder) holder).imgSent);
        Glide.with(context).load(messagelist.get(position).get("sender_image"))
                .apply(Global.getGlideOptions())
                .into(((ImageSentViewHolder) holder).userImage);
    }
    private void handleRecievedImageData(RecyclerView.ViewHolder holder, int position) {

        Glide.with(context).load(messagelist.get(position).get("image_url")).apply(Global.getGlideOptions()).into(((ImageReceivedViewHolder) holder).imgSent);
        Glide.with(context)
                .load(messagelist.get(position).get("sender_image"))
                .apply(Global.getGlideOptions())
                .into(((ImageReceivedViewHolder) holder).userImage);
        ((LiveStreamChatAdapter.ImageReceivedViewHolder) holder).userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }

    private void handleOfferData(RecyclerView.ViewHolder holder, int position) {
        Glide.with(context).load(HelperPreferences.get(context).getString(USER_PROFILE_PIC))
                .apply(Global.getGlideOptions())
                .into(((SendMessageViewHolder) holder).userImage);
        ((SendMessageViewHolder) holder).txtMessage.setText(messagelist.get(position).get("message"));
        if (messagelist.get(position).get("ispined") != null && messagelist.get(position).get("ispined").equalsIgnoreCase("y")) {
            ((SendMessageViewHolder) holder).imagePin.setImageResource(IMAGES[1]);
        } else {
            ((SendMessageViewHolder) holder).imagePin.setImageResource(IMAGES[0]);
        }
        isCliced = false;
        ((SendMessageViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messagelist.get(position).get("ispined") != null && messagelist.get(position).get("ispined").equalsIgnoreCase("y")) {
                    pinCommentApi(messagelist.get(position).get("comment_id"), messagelist.get(position).get("group_id"), "n", new PinUnPinCommentCallback() {
                        @Override
                        public void onCommentPinSuccess() {
                            messagelist.get(position).put("ispined", "n");
                            ((SendMessageViewHolder) holder).imagePin.setImageResource(IMAGES[0]);
                        }

                        @Override
                        public void onCommentPinUnSuccess() {
                            Toast.makeText(context, "Unable to pin comment at the movement.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {

                    pinCommentApi(messagelist.get(position).get("comment_id"), messagelist.get(position).get("group_id"), "y", new PinUnPinCommentCallback() {
                        @Override
                        public void onCommentPinSuccess() {
                            messagelist.get(position).put("ispined", "y");
                            ((SendMessageViewHolder) holder).imagePin.setImageResource(IMAGES[1]);
                        }

                        @Override
                        public void onCommentPinUnSuccess() {
                            Toast.makeText(context, "Unable to pin comment at the movement.", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

    }

    @Override
    public int getItemViewType(int position) {
//        Log.e("Dscvdsfvc ", messagelist + "");
//        Log.e("Dscvdsfvc ", HelperPreferences.get(context).getString(UID) + "");
        if (messagelist.get(position).get("senderid").equalsIgnoreCase(HelperPreferences.get(context).getString(UID))) {
            if (messagelist.get(position).get("type").equalsIgnoreCase("o")) {

                return SAConstants.Keys.TYPE_MAKEOFFER;

            } else if (messagelist.get(position).get("type").equalsIgnoreCase("img")) {

                return SAConstants.Keys.TYPE_IMAGE;

            } else if (messagelist.get(position).get("type").equalsIgnoreCase("p")) {
                return SAConstants.Keys.TYPE_PINED_SEND_MSG;
            } else {
                return SAConstants.Keys.TYPE_SEND;
            }
        } else {
            if (messagelist.get(position).get("type").equalsIgnoreCase("o")) {

                return SAConstants.Keys.TYPE_MAKEOFFER_RECEIVED;

            } else if (messagelist.get(position).get("type").equalsIgnoreCase("img")) {

                return SAConstants.Keys.TYPE_RECIEVED_IMAGE;

            } else if (messagelist.get(position).get("type").equalsIgnoreCase("p")) {
                return SAConstants.Keys.TYPE_PINED_RECEIVED_MSG;
            } else {
                return SAConstants.Keys.TYPE_RECEIVED;
            }
        }

    }

    @Override
    public int getItemCount() {
        return messagelist.size();
    }

    private String formateDate(String dateToChange) {

        String formattedDate = "";
        DateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat writeFormat = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = readFormat.parse(dateToChange);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            formattedDate = writeFormat.format(date);
        }
        return formattedDate;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt;

        public ViewHolder(View itemView) {
            super(itemView);
            txt = itemView.findViewById(R.id.txt);
        }
    }

    private void pinCommentApi(String commentId, String groupId, String pinStatus, PinUnPinCommentCallback callback) {
        Dialog dialog = S_Dialogs.getLoadingDialog(context);
        dialog.show();
        Call<Common> pinCommentApi = Global.WebServiceConstants.getRetrofitinstance().pinCommentApi(auth,token,HelperPreferences.get(context).getString(UID),
                groupId, commentId, pinStatus.toUpperCase());
        pinCommentApi.enqueue(new Callback<Common>() {
            @Override
            public void onResponse(Call<Common> call, Response<Common> response) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (response.isSuccessful()) {
                    callback.onCommentPinSuccess();
                  // Log.e("pinCommentApi", "onResponse: " + response.body().toString());
                } else {
                    callback.onCommentPinUnSuccess();
                    try {
                      Log.e("pinCommentApiError", "onResponse: " + response.errorBody().string());
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
                callback.onCommentPinUnSuccess();
               // Log.e("pinCommentApiFailure", "onFailure: " + t.getMessage());
            }
        });
    }

    private interface PinUnPinCommentCallback {
        void onCommentPinSuccess();
        void onCommentPinUnSuccess();
    }
    public interface OptionsClickCallBack {
        void onMakeOfferClicked(String comment_id, String senderId);
        void onPinLiveCommentClick(String commentId);
    }
}
