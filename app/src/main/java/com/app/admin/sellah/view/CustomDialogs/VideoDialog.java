package com.app.admin.sellah.view.CustomDialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.app.admin.sellah.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VideoDialog extends Dialog {

    MakeOfferDialog.OfferController controller;
    Context context;

    @BindView(R.id.exitedtbtn)
    Button exitedBtn;



    public VideoDialog(Context context) {
        super(context);
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.BOTTOM);
        setContentView(R.layout.video_dialog);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        ButterKnife.bind(this);



    }



    @OnClick({R.id.exitedtbtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.exitedtbtn:
                dismiss();
                break;
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
    }
    }
