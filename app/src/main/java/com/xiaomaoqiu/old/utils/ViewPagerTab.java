package com.xiaomaoqiu.old.utils;

import android.support.v4.view.ViewPager;
import android.widget.RadioGroup;

/**
 * Created by Administrator on 2015/6/19.
 * 将一个ViewPager的换页事件和一个RadioGroup联系起来
 */
public class ViewPagerTab {
    private  boolean bFromVP = false;
    private  boolean bFromRG = false;

    public interface TabChanged{
        void onTabChanged(int index);
    }

    public void setTabChanged(TabChanged tabChanged) {
        this.tabChanged = tabChanged;
    }

    private TabChanged tabChanged;

    public  ViewPagerTab(final ViewPager vp,final RadioGroup rg, final int ids[])
    {
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(bFromRG) return;
                if(position < ids.length)
                {
                    bFromVP = true;
                    rg.check(ids[position]);
                    bFromVP = false;
                }

                if (null != tabChanged){
                    tabChanged.onTabChanged(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                if(bFromVP) return;
                for(int i =0;i < ids.length; i++)
                {
                    if(ids[i] == id)
                    {
                        bFromRG = true;
                        vp.setCurrentItem(i);
                        bFromRG = false;
                        break;
                    }
                }
            }
        });
    }
}
