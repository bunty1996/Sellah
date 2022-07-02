package com.app.admin.sellah.view.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.app.admin.sellah.R;
import com.app.admin.sellah.model.extra.getProductsModel.GetProductList;
import com.app.admin.sellah.controller.utils.Global;

public class SubCategoryGridAdapter extends RecyclerView.Adapter<SubCategoryGridAdapter.ViewHolder> {

    GetProductList mData;
    LayoutInflater mInflater;
    Context context;
    SubcatClick subcatClick;

    public SubCategoryGridAdapter(Context context, GetProductList mainList) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = mainList;
        this.context = context;
    }

    @Override
    @NonNull
    public SubCategoryGridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.main_categories_adapter, parent, false);
        return new SubCategoryGridAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubCategoryGridAdapter.ViewHolder holder, int position) {


        String imageUrl = "";
        if (mData.getResult().get(position).getProductImages() != null) {
            imageUrl = !mData.getResult().get(position).getProductImages().isEmpty() ? mData.getResult().get(position).getProductImages().get(0).getImage() : "";

        }
        RequestOptions requestOptions = Global.getGlideOptions();
        try {
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.imageView);
        } catch (Exception e) {

        }

        if (!TextUtils.isEmpty(mData.getResult().get(position).getPromoteProduct()) && mData.getResult().get(position).getPromoteProduct().equalsIgnoreCase("S")) {
            holder.imgFeatured.setVisibility(View.VISIBLE);
        } else {

            holder.imgFeatured.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(mData.getResult().get(position).getProductVideo())) {
            holder.imgVideoIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.imgVideoIndicator.setVisibility(View.GONE);
        }
        holder.txtItemName.setText(mData.getResult().get(position).getName());
        holder.txtItemCost.setText(mData.getResult().get(position).getPrice());

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Bundle bundle = new Bundle();
//                bundle.putParcelable(SAConstants.Keys.PRODUCT_DETAIL, mData.getResult().get(position));
//                ProductFrgament fragment = new ProductFrgament();
//                fragment.setArguments(bundle);
//                ((MainActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).addToBackStack(null).commit();
//
//            }
//
//        });

    }

    @Override
    public int getItemCount() {
        return mData.getResult().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatImageView imageView, imgVideoIndicator;
        TextView txtItemName, txtItemCost;
        TextView imgFeatured;
        LinearLayout main_grid_layout;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.third_image);
            txtItemName = itemView.findViewById(R.id.txt_product_name);
            txtItemCost = itemView.findViewById(R.id.txt_product_cost);
            imgFeatured = itemView.findViewById(R.id.img_featured);
            imgVideoIndicator = itemView.findViewById(R.id.img_video_indicator);
            main_grid_layout = itemView.findViewById(R.id.main_grid_layout);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("adapterClick", "onClick: ");


                    subcatClick.onClick(getAdapterPosition(),mData);
                }
            });

           /* main_grid_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });*/

        }

    }

    public void onClicklistener(SubcatClick subcatClick) {
        this.subcatClick = subcatClick;
    }

    public interface SubcatClick {
        void onClick(int pos,GetProductList mData);
    }
}
