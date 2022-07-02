package com.app.admin.sellah.view.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.admin.sellah.R;
import com.app.admin.sellah.view.adapter.WelcomAdapter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;

import static com.app.admin.sellah.controller.utils.Global.makeTransperantStatusBar;

public class VideoWelcomeActivity extends AppCompatActivity {
    ViewPager mPager;
    @BindView(R.id.wlcm_skip)
    TextView wlcmSkip;
    @BindView(R.id.wlcm_next)
    TextView wlcmNext;
    private int dotscount;
    private ImageView[] dots;
    Timer timer;



    ArrayList<Integer> viewImagesArray = new ArrayList<Integer>();
    String[] stringArray = new String[]{"Welcome to Sellah! Live Video", "Make Sales Easily", "Transact Effectively", "Share across other channels", "SMILE"};
    String[] stringArray2 = new String[]{"In here, you will be able to engage your buyers in real-time!", "Simply press and hold on the comments to send an offer to your buyers!","Offers sent will be recorded and reflect on your engagement page!", "Gain more viewers and sales by sharing your video across other social or chat platforms", "Lastly but not least, don’t forget to smile more and make more sales - Sellah!"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeTransperantStatusBar(this, true);
        setContentView(R.layout.welcome_layout);
        ButterKnife.bind(this);

        viewImagesArray.add(R.drawable.videowelcum_1);
        viewImagesArray.add(R.drawable.videowelcum_2);
        viewImagesArray.add(R.drawable.videowelcum_3);
        viewImagesArray.add(R.drawable.videowelcum_4);
        viewImagesArray.add(R.drawable.videowelcum_5);
        init();


      wlcmSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Log.e( "run: ",""+ mPager.getCurrentItem());

                mPager.setCurrentItem(4);


            }
        });

        wlcmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent1 = new Intent(new Intent(VideoWelcomeActivity.this, MainActivityLiveStream.class));
                intent1.putExtra("value", "LiveStream");
                startActivity(intent1);
                finish();

            }
        });
    }


    public void init() {


        mPager = (ViewPager) findViewById(R.id.pager);


        WelcomAdapter welcomAdapter = new WelcomAdapter(this, viewImagesArray, stringArray, stringArray2);
        mPager.setAdapter(welcomAdapter);

        dotscount = welcomAdapter.getCount();
        dots = new ImageView[dotscount];

        //automatic slide
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                mPager.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e( "run: ",""+ mPager.getCurrentItem());
                        if (mPager.getCurrentItem() == 3) {

                            wlcmNext.setVisibility(View.VISIBLE);
                            wlcmSkip.setVisibility(View.GONE);

                        }

                        mPager.setCurrentItem(mPager.getCurrentItem()+1,true);



                    }
                });
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 4000, 4000);

        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);



        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            //    Log.e( "onPageScrolled: ",""+position );
                if (position==4)
                {
                    wlcmNext.setVisibility(View.VISIBLE);
                    wlcmSkip.setVisibility(View.GONE);
                }
                else
                {
                    wlcmNext.setVisibility(View.GONE);
                    wlcmSkip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }


}
