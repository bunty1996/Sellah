package com.app.admin.sellah.view.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.admin.sellah.R;
import com.app.admin.sellah.controller.utils.Global;
import com.app.admin.sellah.model.extra.NotificationList.ArrFollow;
import com.app.admin.sellah.view.activities.ChatActivity;
import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationFollowListAdapter extends RecyclerView.Adapter<NotificationFollowListAdapter.NewMessViewHolder> /*implements Filterable*/ {
    List<ArrFollow>  newMessList;
    Context context;
    int state;
    private List<ArrFollow>  notificationListFilter;

    public NotificationFollowListAdapter(List<ArrFollow> newMessList, FragmentActivity activity, int state) {

        this.newMessList = newMessList;
        notificationListFilter=newMessList;
        context = activity;
        this.state=state;
    }
    public void filterList(List<ArrFollow>  newMessList) {
        this.newMessList = newMessList;
        notifyDataSetChanged();
    }

    @Override
    public NewMessViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_new_mess_adapter, parent, false);
        NewMessViewHolder nmvh = new NewMessViewHolder(view);
        return nmvh;
    }

    @Override
    public void onBindViewHolder(final NewMessViewHolder holder, final int position) {
        Glide.with(context).load(newMessList.get(position).getImage())
                .apply(Global.getGlideOptions()).into(holder.imageView);
        holder.newMessHeading.setText(newMessList.get(position).getUsername());
        holder.newMessSubHeading.setText(newMessList.get(position).getMessage());

        if(newMessList.get(position).getReadStatus().equalsIgnoreCase("1")){
            holder.imgNew.setVisibility(View.GONE);
        }else{
            holder.imgNew.setVisibility(View.VISIBLE);
        }
        if(state==0){
            if(position==0){
                holder.newOrder.setVisibility(View.VISIBLE);
            }
            else {
                holder.newOrder.setVisibility(View.GONE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in=new Intent(context,ChatActivity.class);
                    context.startActivity(in);
                }
            });
        }else{
            holder.newOrder.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return newMessList.size();
    }

    public class NewMessViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imageView;
        TextView newMessHeading,newMessSubHeading,newOrder;
        ImageView imgNew;

        public NewMessViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.profile_new_mess_image);
            newMessHeading = view.findViewById(R.id.new_mess_person_heading);
            newMessSubHeading = view.findViewById(R.id.new_mess_sub_heading);
            imgNew = view.findViewById(R.id.img_new_entry);
            newOrder = view.findViewById(R.id.newtextOrder);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }
}
