package com.app.admin.sellah.view.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.app.admin.sellah.R;
import com.app.admin.sellah.controller.utils.Global;
import com.app.admin.sellah.model.extra.Notification.NotificationModel;
import com.app.admin.sellah.view.CustomViews.NonSwipeableViewPager;
import com.app.admin.sellah.view.activities.MainActivity;
import com.app.admin.sellah.view.adapter.NonSwipableViewPaggerAdapter;
import java.util.ArrayList;
import static com.app.admin.sellah.controller.utils.SAConstants.Keys.PUSH_NOTIFICATION;
import static com.app.admin.sellah.controller.utils.SAConstants.NotificationKeys.NT_ACCEPT_REJECT;
import static com.app.admin.sellah.controller.utils.SAConstants.NotificationKeys.NT_CHAT;
import static com.app.admin.sellah.controller.utils.SAConstants.NotificationKeys.NT_COMMENT_ADDED;
import static com.app.admin.sellah.controller.utils.SAConstants.NotificationKeys.NT_DATA;
import static com.app.admin.sellah.controller.utils.SAConstants.NotificationKeys.NT_FOLLOW;
import static com.app.admin.sellah.controller.utils.SAConstants.NotificationKeys.NT_OFFER_MAKE;
import static com.app.admin.sellah.controller.utils.SAConstants.NotificationKeys.NT_PAYMENT;
import static com.app.admin.sellah.controller.utils.SAConstants.NotificationKeys.NT_PRODUCT_ADDED;

public class NotificationFragment extends Fragment {
    View view;
    TextView notification, messages;
    public ImageView messageImage, notifivcationImage;
    NonSwipeableViewPager vpnotification;
    private ArrayList<String> searchSuggestions;
    private ArrayAdapter<String> searchSuggessionAdapter;


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter(PUSH_NOTIFICATION));
        view = inflater.inflate(R.layout.notification_fragment, container, false);

        hideView();
        notification = view.findViewById(R.id.btn_Noti);
        messages = view.findViewById(R.id.btn_mess);
        messageImage = view.findViewById(R.id.message_img);
        notifivcationImage = view.findViewById(R.id.notification_img);
        vpnotification = view.findViewById(R.id.notification_viewpagger);
        createViewPager(vpnotification);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messages.setBackgroundResource(R.drawable.noti_button_bg_normal_right);
                notification.setBackgroundResource(R.drawable.noti_button_bg_selected_left);
                vpnotification.setCurrentItem(0);
            }
        });

        vpnotification.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                Global.hideKeyboard(vpnotification, getActivity());

                if (position == 0) {

                    messages.setBackgroundResource(R.drawable.noti_button_bg_normal_right);
                    notification.setBackgroundResource(R.drawable.noti_button_bg_selected_left);

                } else {

                    notification.setBackgroundResource(R.drawable.noti_button_bg_normal_left);
                    messages.setBackgroundResource(R.drawable.noti_button_bg_selected_right);

                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        searchSuggestions = new ArrayList<>();
        setSearchView();
        return view;
    }

    public void setSearchView() {

        searchSuggessionAdapter = new ArrayAdapter<>(getActivity(), R.layout.layout_autocomplete_text_design, R.id.text1, searchSuggestions);
        ((MainActivity) getActivity()).searchEditText.setAdapter(searchSuggessionAdapter);
        ((MainActivity) getActivity()).searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Global.hideKeyboard(((MainActivity) getActivity()).searchEditText, getActivity());
                } else {
                    Global.hideKeyboard(((MainActivity) getActivity()).searchEditText, getActivity());
                }

                return false;
            }
        });
        ((MainActivity) getActivity()).searchEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String autoCompleteText = (String) adapterView.getItemAtPosition(i);
                ((MainActivity) getActivity()).searchEditText.setText(autoCompleteText);
            }
        });
        ((MainActivity) getActivity()).searchEditText.setText("");
        searchSuggestions = new ArrayList<>();
        searchSuggessionAdapter.notifyDataSetChanged();

    }

    private void createViewPager(ViewPager viewPager) {

        NonSwipableViewPaggerAdapter adapter = new NonSwipableViewPaggerAdapter(getChildFragmentManager());
        adapter.addFrag(new MessageFragment());
        viewPager.setAdapter(adapter);
        vpnotification.setCurrentItem(1);

    }


    public void hideView() {

        //Vishal changes
        ((MainActivity) getActivity()).rlBack.setVisibility(View.GONE);
        ((MainActivity) getActivity()).findViewById(R.id.rl_chat).setVisibility(View.GONE);
        ((MainActivity) getActivity()).rlFilter.setVisibility(View.GONE);
        ((MainActivity) getActivity()).rlMenu.setVisibility(View.GONE);
        ((MainActivity) getActivity()).rel_search.setVisibility(View.GONE);
        ((MainActivity) getActivity()).searchEditText.setHint("Search Conversations");
        ((MainActivity) getActivity()).text_sell.setVisibility(View.VISIBLE);
        ((MainActivity) getActivity()).text_sell.setText("Chat List");
        ((MainActivity) getActivity()).rloptions.setVisibility(View.GONE);
        ((MainActivity) getActivity()).changeOptionColor(3);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ((MainActivity) getActivity()).findViewById(R.id.rl_chat).setElevation(10);
        }

        ((MainActivity) getActivity()).findViewById(R.id.rl_insideback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).findViewById(R.id.rl_chat).setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity) getActivity()).findViewById(R.id.rl_chat).setVisibility(View.GONE);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent

            try {

                NotificationModel message = intent.getParcelableExtra(NT_DATA);

                if (message != null) {

                    Fragment frg = null;
                    frg = new NotificationFragment();
                    final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment f = getActivity().getSupportFragmentManager().findFragmentById(R.id.frameLayout);

                    switch (message.getNotiType()) {

                        case NT_ACCEPT_REJECT:
                            break;
                        case NT_FOLLOW:
                            if ((f instanceof ViewFollowListFragment)) {
                                createViewPager(vpnotification);
                                vpnotification.setCurrentItem(0);
                            }
                            break;
                        case NT_COMMENT_ADDED:
                            break;
                        case NT_PRODUCT_ADDED:
                            break;
                        case NT_CHAT:
                            if ((f instanceof ViewFollowListFragment)) {
                                createViewPager(vpnotification);
                            } else {
                                createViewPager(vpnotification);
                            }

                            break;
                        case NT_PAYMENT:
                            break;
                        case NT_OFFER_MAKE:
                            break;
                        default:

                            break;
                    }
                }

               // Log.e("receiver", "Got message: msg" + message.getMessage());

            } catch (Exception e) {

                //Log.e("receiver_failure", "onReceive: " + e.getMessage());

            }

        }
    };

}
