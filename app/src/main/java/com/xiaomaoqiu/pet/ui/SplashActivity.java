package com.xiaomaoqiu.pet.ui;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.xiaomaoqiu.pet.R;
import com.xiaomaoqiu.pet.utils.DensityUtil;
import com.xiaomaoqiu.pet.utils.ViewPagerTab;

public class SplashActivity extends FragmentActivity {

    private View getRootView()
    {
        return ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appstart);


        SharedPreferences pref = getSharedPreferences("splash", Context.MODE_PRIVATE);
        boolean showGuide = pref.getBoolean("first_user",true);
        if(showGuide) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("first_user", false);
            editor.apply();


            ViewPager vp = (ViewPager) findViewById(R.id.vp_splash);
            SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
            vp.setAdapter(adapter);

            RadioGroup rg = (RadioGroup) findViewById(R.id.rg_indicator);

            int ids[] = new int[adapter.getCount()];
            for (int i = 0; i < ids.length; i++) {
                ids[i] = R.id.indicator_1st + i;
                RadioButton btn = (RadioButton) LayoutInflater.from(this).inflate(R.layout.indicator_splash, null);
                btn.setId(ids[i]);
                rg.addView(btn);

                int sz = DensityUtil.dip2px(this, 10);
                int margin = DensityUtil.dip2px(this, 3);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sz, sz);
                params.setMargins(margin, margin, margin, margin);
                btn.setLayoutParams(params);
            }

            new ViewPagerTab(vp, rg, ids);
            ((RadioButton) rg.findViewById(R.id.indicator_1st)).setChecked(true);


        }else
        {
            getRootView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }
            }, 1000);
        }

    }

    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_DRAWABLE_ID = "drawable_id";
        private static final String ARG_LAST_PAGE = "last_page";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int drawableID,boolean lastPage) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_DRAWABLE_ID, drawableID);
            args.putBoolean(ARG_LAST_PAGE,lastPage);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            ImageView rootView = (ImageView)inflater.inflate(R.layout.vp_splash, container, false);
            int nDrawableID = getArguments().getInt(ARG_DRAWABLE_ID);
            Drawable img = getResources().getDrawable(nDrawableID);
            rootView.setImageDrawable(img);
            boolean bLastPage = getArguments().getBoolean(ARG_LAST_PAGE);
            if(bLastPage)
            {
                rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(),MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
            }
            return rootView;
        }

    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        int splashPageID[]={R.drawable.welcome,
                        R.drawable.welcome,
                        R.drawable.welcome};

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(splashPageID[position],position == splashPageID.length-1);
        }

        @Override
        public int getCount() {
            return splashPageID.length;
        }

    }

}