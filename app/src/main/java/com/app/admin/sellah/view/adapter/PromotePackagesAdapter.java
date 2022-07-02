package com.app.admin.sellah.view.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.admin.sellah.R;
import com.app.admin.sellah.model.extra.ProductDetails.Promote;

import java.util.List;

public class PromotePackagesAdapter extends RecyclerView.Adapter<PromotePackagesAdapter.ViewHolder> {

    LayoutInflater mInflater;
    Context context;
    View view;
    List<Promote> packagesList;
    PromotePackagesAdapter.OfferCallBack callBack;
    String no_of_clicks;
    int totalCount;

    public PromotePackagesAdapter(Context context,String no_of_clicks,int totalCount, List<Promote> packagesList, OfferCallBack callBack) {
        mInflater=LayoutInflater.from(context);
        this.context=context;
        this.no_of_clicks=no_of_clicks;
        this.totalCount=totalCount;
        this.packagesList=packagesList;
        this.callBack=callBack;

    }


    @NonNull
    @Override
    public PromotePackagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = mInflater.inflate(R.layout.layout_packages_detail, parent, false);
        return new PromotePackagesAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(PromotePackagesAdapter.ViewHolder holder, int position) {

        holder.txtOffer.setText("S$ "+packagesList.get(position).getAmount());

        if (no_of_clicks==null || no_of_clicks.equalsIgnoreCase(""))
           holder.txtOfferPrice.setText("0/"+totalCount);
        else
           holder.txtOfferPrice.setText(no_of_clicks+"/"+totalCount);

        holder.txtDuration.setText(packagesList.get(position).getDays_left()+" days");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.onOfferSelect(packagesList.get(position).getId());
            }
        });

    }



    @Override
    public int getItemCount() {

        return packagesList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView txtOffer,txtOfferPrice,txtDuration;
        public ViewHolder(View v) {
            super(v);
            cardView=view.findViewById(R.id.cd_root);
            txtOffer=view.findViewById(R.id.txt_offer);
            txtOfferPrice=view.findViewById(R.id.txt_offer_price);
            txtDuration=view.findViewById(R.id.txt_offer_duration);
        }
    }

    public interface OfferCallBack{
        void onOfferSelect(String id);
    }

}
