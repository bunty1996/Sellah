package com.app.admin.sellah.view.adapter;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.app.admin.sellah.R;
import com.app.admin.sellah.controller.WebServices.WebService;
import com.app.admin.sellah.controller.utils.Global;
import com.app.admin.sellah.controller.utils.HelperPreferences;
import com.app.admin.sellah.model.extra.FeaturedPosts.FeaturedPosts;
import com.app.admin.sellah.model.extra.getProductsModel.GetProductList;
import com.app.admin.sellah.model.extra.getProductsModel.Result;
import com.app.admin.sellah.view.CustomDialogs.PromoteDialog;
import com.app.admin.sellah.view.activities.MainActivity;
import com.app.admin.sellah.view.fragments.ProductFrgament;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.util.ArrayList;
import retrofit2.Call;
import static com.app.admin.sellah.controller.utils.Global.getUser.isLogined;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.UID;

public class FeaturedPostsAdapter extends RecyclerView.Adapter<FeaturedPostsAdapter.ViewHolder> {

    FeaturedPosts mData, filtedData;
    Context context;
    //flag for footer ProgressBar (i.e. last item of list)
    private boolean isLoadingAdded = false;
    Call<GetProductList> getProductsCall;
    WebService service;
    private ArrayList<Result> resultList;

    public FeaturedPostsAdapter(FragmentActivity activity, FeaturedPosts mainList) {

        this.context = activity;
        this.mData = mainList;
        this.filtedData = mData;
        service = Global.WebServiceConstants.getRetrofitinstance();
        resultList = new ArrayList<>();

    }

    public void filterList(FeaturedPosts newMessList) {
        this.filtedData = newMessList;
        notifyDataSetChanged();

    }

    @Override
    @NonNull
    public FeaturedPostsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_product_adapter_view, parent, false);
        return new FeaturedPostsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FeaturedPostsAdapter.ViewHolder holder, int position) {

       // Log.e("adpterPosPrint", position + "//" + mData.getResult().get(position).getUserId() + "//" + HelperPreferences.get(context).getString(UID));

        if (isLogined(context) && mData.getResult().get(position).getUserId().equalsIgnoreCase(HelperPreferences.get(context).getString(UID))) {

            holder.txtPromoteButton.setVisibility(View.VISIBLE);

        } else {

            holder.txtPromoteButton.setVisibility(View.GONE);

        }

        if (!TextUtils.isEmpty(mData.getResult().get(position).getProductVideo())) {

            holder.imgVideoIndicator.setVisibility(View.VISIBLE);

        } else {

            holder.imgVideoIndicator.setVisibility(View.GONE);

        }

        holder.txtPromoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PromoteDialog.create(context, mData.getResult().get(position).getId(), new PromoteDialog.PromoteCallback() {
                    @Override
                    public void onPromoteSuccess() {

                        mData.getResult().get(position).setPromoteProduct("S");
                        notifyDataSetChanged();

                    }

                    @Override
                    public void onPromoteFailure() {

                    }

                }).show();

            }

        });

        String imageUrl = "";

        if (mData.getResult().get(position).getProductImages() != null) {

            imageUrl = !mData.getResult().get(position).getProductImages().isEmpty() ? mData.getResult().get(position).getProductImages().get(0).getImage() : "";
            imageUrl = imageUrl.replace("productimages", "thproductimages");

        }

        RequestOptions requestOptions = Global.getGlideOptions();
        Glide.with(context)
                .load(imageUrl)
               // .apply(requestOptions)
                .into(holder.imageView);

        if (!TextUtils.isEmpty(mData.getResult().get(position).getPromoteProduct()) && mData.getResult().get(position).getPromoteProduct().equalsIgnoreCase("S")) {

            holder.imgFeatured.setVisibility(View.VISIBLE);

        } else {

            holder.imgFeatured.setVisibility(View.GONE);

        }

        holder.txtProductCost.setText(mData.getResult().get(position).getPrice());
        holder.txtProductName.setText(mData.getResult().get(position).getName());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String product_id = mData.getResult().get(position).getId();
                String user_id = mData.getResult().get(position).getUserId();

                Bundle bundle = new Bundle();
                bundle.putString("FEATUREDPOSTS", product_id);
                ProductFrgament fragment = new ProductFrgament();
                fragment.setArguments(bundle);
                ((MainActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).addToBackStack(null).commit();

            }

        });

    }

    @Override
    public int getItemCount() {

        return filtedData.getResult() == null ? 0 : filtedData.getResult().size();


    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView imageView, imgVideoIndicator;
        TextView txtProductName, txtProductCost, txtPromoteButton;
        LinearLayout linearLayout;
        TextView imgFeatured;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.third_image);
            linearLayout = itemView.findViewById(R.id.main_grid_layout);
            txtProductName = itemView.findViewById(R.id.txt_product_name);
            txtProductCost = itemView.findViewById(R.id.txt_product_cost);
            imgFeatured = itemView.findViewById(R.id.img_featured);
            txtPromoteButton = itemView.findViewById(R.id.txt_promote_button);
            imgVideoIndicator = itemView.findViewById(R.id.img_video_indicator);

        }

    }

}
