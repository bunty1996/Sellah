package com.app.admin.sellah.view.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.app.admin.sellah.view.CustomViews.TouchImageView;
import com.app.admin.sellah.R;
import com.app.admin.sellah.controller.utils.Global;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.app.admin.sellah.controller.utils.Global.StatusBarLightMode;


public class ImageViewerActivity extends AppCompatActivity {

    @BindView(R.id.img_full_screen)
    TouchImageView imvFullScreen;
    int imgUrl;
    String imageString="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarLightMode(this);
        setContentView(R.layout.activity_image_viewer);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            imgUrl = bundle.getInt(Global.KEY_IMAGE_URL);

            imageString=bundle.getString(Global.KEY_IMAGE);
           // Log.e("imageViewerActivity", "img string"+imageString );
            if (imageString!=null&&!imageString.equalsIgnoreCase("")){
                Glide.with(this)
                        .load(imageString).apply(Global.getGlideOptions()).into(imvFullScreen);

            }else{
                imvFullScreen.setImageResource(imgUrl);
            }

        }
    }


    @OnClick(R.id.imv_close)
    void onClickClose() {
        finish();
    }


    public static void start(Context context,String url,int status) {
        Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.putExtra(Global.KEY_IMAGE_URL, url);
        context.startActivity(intent);
    }

    public static void start(Context context, String url) {
        Intent intent = new Intent(context, ImageViewerActivity.class);
        intent.putExtra(Global.KEY_IMAGE, url);
        context.startActivity(intent);
    }

}
