package com.app.admin.sellah.view.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.app.admin.sellah.R;
import com.app.admin.sellah.controller.WebServices.WebService;
import com.app.admin.sellah.controller.utils.Global;
import com.app.admin.sellah.controller.utils.HelperPreferences;
import com.app.admin.sellah.view.activities.ChatActivity;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.admin.sellah.controller.WebServices.ApisHelper.auth;
import static com.app.admin.sellah.controller.WebServices.ApisHelper.token;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.UID;

public class Pay_offer_adapter2  extends RecyclerView.Adapter<Pay_offer_adapter2.Payooferholder> {

    Context context;
    ArrayList<Map<String, String>> list;
    Pay_offer_adapter.OnCardOptionSelection cardOptionSelection;
    String cross_status = "";
    PopupMenu popup;
    public MenuItem item, item1, item3;
    WebService service;
    String friend_id;
    Button markascomplete;


    public Pay_offer_adapter2(Context activity, ArrayList<Map<String, String>> list, Pay_offer_adapter.OnCardOptionSelection cardOptionSelection, String cross_status, String friend_id) {
        context = activity;
        this.list = list;
        this.cardOptionSelection = cardOptionSelection;
        this.cross_status = cross_status;
        this.friend_id = friend_id;

       // Log.e("printListAdpt", list + "");


    }

    public Pay_offer_adapter2(Context activity, ArrayList<Map<String, String>> list, Pay_offer_adapter.OnCardOptionSelection cardOptionSelection, String friend_id, Button markascomplete) {
        context = activity;
        this.list = list;
        this.cardOptionSelection = cardOptionSelection;
        this.friend_id = friend_id;
        this.markascomplete = markascomplete;

        //  Log.e("valueArray",list+"");


    }

    @Override
    public Pay_offer_adapter2.Payooferholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View groceryProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pay_adapter_view2, parent, false);

        service = Global.WebServiceConstants.getRetrofitinstance();

        Pay_offer_adapter2.Payooferholder cvh = new Pay_offer_adapter2.Payooferholder(groceryProductView);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull Pay_offer_adapter2.Payooferholder holder, int position) {


        holder.name.setText(list.get(position).get("product_name"));

        if (list.get(position).get("price_cost")!=null&&list.get(position).get("price_cost").contains("$")) {
            String amt = list.get(position).get("price_cost").replace("$", "").trim();
            holder.price.setText(amt);
        } else if (list.get(position).get("price_cost")!=null&&!list.get(position).get("price_cost").equalsIgnoreCase(""))
            holder.price.setText(list.get(position).get("price_cost"));


        holder.qty.setText(list.get(position).get("quantity"));
        Glide.with(context)
                .load(!list.get(position).get("product_image").isEmpty() ? list.get(position).get("product_image") : "")
                .apply(Global.getGlideOptions())
                .into(holder.offerimage);


        if (list.get(position).get("order_status").equalsIgnoreCase("A")) {
            holder.cancel_offer.setVisibility(View.GONE);
            holder.paid_status.setVisibility(View.GONE);
            holder.btn_menu_pay.setVisibility(View.GONE);
        } else if (list.get(position).get("order_status").equalsIgnoreCase("S")) {
            holder.btn_menu_pay.setVisibility(View.VISIBLE);
        } else {
            holder.btn_menu_pay.setVisibility(View.GONE);
            holder.cancel_offer.setVisibility(View.VISIBLE);
            holder.paid_status.setVisibility(View.VISIBLE);
        }

        if (cross_status.isEmpty()) {
            holder.cancel_offer.setVisibility(View.GONE);

        } else {
            if (list.get(position).get("order_status").equalsIgnoreCase("A")) {
                holder.cancel_offer.setVisibility(View.VISIBLE);
            } else {
                holder.cancel_offer.setVisibility(View.GONE);
            }


        }


        holder.cancel_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardOptionSelection.removeoffer(holder.getAdapterPosition());

            }
        });

        holder.btn_menu_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popup = new PopupMenu(context, holder.btn_menu_pay);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.menu_report, popup.getMenu());
                item1 = popup.getMenu().findItem(R.id.menu_dispute);
                popup.getMenu().findItem(R.id.menu_report).setVisible(false);
                popup.getMenu().findItem(R.id.menu_block_user).setVisible(false);


                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {

                            case R.id.menu_dispute:

                                disputedialog(list.get(position).get("order_id"), friend_id);
                                break;
                        }
                        return true;
                    }
                });


            }
        });

    }


    @Override
    public int getItemViewType(int position) {
        return list.size();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Payooferholder extends RecyclerView.ViewHolder {
        TextView name, price, qty, paid_status;
        ImageView offerimage, cancel_offer;
        ImageButton btn_menu_pay;

        public Payooferholder(View view) {
            super(view);
            name = view.findViewById(R.id.pay_txt_item_name);
            price = view.findViewById(R.id.pay_txt_item_cost);
            qty = view.findViewById(R.id.pay_txt_item_quantity);
            offerimage = view.findViewById(R.id.pay_offer_image);
            cancel_offer = view.findViewById(R.id.cancel_offer);
            paid_status = view.findViewById(R.id.paid_or_not);
            btn_menu_pay = view.findViewById(R.id.btn_menu_pay);


        }
    }

    public interface OnCardOptionSelection {
        void removeoffer(int pos);


    }


    public void disputedialog(String order_id, String otherUserId) {

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage("Dispute Reason");
        LayoutInflater layoutInflater = ((ChatActivity) context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dispute_alertdiaog, null);
        EditText input = view.findViewById(R.id.disputereason);
        alert.setView(view);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                if (order_id != null) {



                    dispute_api(HelperPreferences.get(context).getString(UID), otherUserId, order_id, input.getText().toString().trim());

                }

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();


    }

    /* Dispute Api */

    public void dispute_api(String userid, String friendid, String orderid, String reason) {
        Call<JsonObject> call = service.disputeOffer(auth,token,userid, friendid, orderid, reason);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                Log.e("onResponse: ", response.body().toString());

                try {

                    if (response.isSuccessful()) {


                        JSONObject obj = new JSONObject(response.body().toString());

                        if (obj.getString("status").equalsIgnoreCase("1")) {


                            Toast.makeText(context, obj.getString("message"), Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(context, obj.getString("message"), Toast.LENGTH_LONG).show();

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(context, "Something went's wrong", Toast.LENGTH_LONG).show();

            }
        });
    }


}