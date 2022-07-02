package com.app.admin.sellah.view.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.admin.sellah.R;
import com.app.admin.sellah.controller.utils.SubCategoryController;
import com.app.admin.sellah.model.extra.Categories.Subcategory;

import java.util.List;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.SubCategoryHolder> {

    private List<Subcategory> subcategoryList;
    Context context;
    SubCategoryController controller;
    private int row_index = 0;
    private String subCatId = "";

    public SubCategoryAdapter(List<Subcategory> textList, Context context, SubCategoryController controller, String sub_cat_id) {
        this.subcategoryList = textList;
        this.context = context;
        this.controller = controller;
        this.subCatId = sub_cat_id;

        for (int i = 0; i < subcategoryList.size(); i++) {
            if (subCatId.equalsIgnoreCase(subcategoryList.get(i).getId())) {
                row_index = i;
            }
        }

    }

    @NonNull
    @Override
    public SubCategoryAdapter.SubCategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_category_sale_adapter, parent, false);
        SubCategoryAdapter.SubCategoryHolder scvh = new SubCategoryAdapter.SubCategoryHolder(view);
        return scvh;
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoryAdapter.SubCategoryHolder holder, int position) {

        holder.textName.setText(subcategoryList.get(position).getName());


        if (row_index == position) {

            holder.view.setVisibility(View.VISIBLE);

        } else {

            holder.view.setVisibility(View.INVISIBLE);

        }

    }

    @Override
    public int getItemCount() {
        return subcategoryList.size();
    }

    public class SubCategoryHolder extends RecyclerView.ViewHolder {

        TextView textName;
        RelativeLayout rlSubCatRoot;
        View view;

        public SubCategoryHolder(View itemView) {
            super(itemView);

            textName = (TextView) itemView.findViewById(R.id.text_tag1);
            rlSubCatRoot = itemView.findViewById(R.id.rl_subcat_root);
            view = itemView.findViewById(R.id.view_selected);

            rlSubCatRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int adtPos = getAdapterPosition();
                    Log.e("subcatclk", adtPos + "//ok");
                    row_index = adtPos;

                    if (adtPos == 0) {

                        controller.onSubCategotyClick("");

                    } else {

                        controller.onSubCategotyClick(subcategoryList.get(adtPos).getId());

                    }

                }

            });

        }

    }

}
