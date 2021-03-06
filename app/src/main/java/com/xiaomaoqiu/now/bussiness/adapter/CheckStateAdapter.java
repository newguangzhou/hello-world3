package com.xiaomaoqiu.now.bussiness.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaomaoqiu.now.bussiness.Device.MeWifiListActivity;
import com.xiaomaoqiu.now.bussiness.bean.WifiBean;
import com.xiaomaoqiu.now.bussiness.bean.WifiListBean;
import com.xiaomaoqiu.pet.R;

import java.util.ArrayList;

@SuppressLint("WrongConstant")
public class CheckStateAdapter extends RecyclerView.Adapter<CheckStateAdapter.StateHolder> {

    private Context context;
    WifiListBean mdatasWrapper;
    public ArrayList<WifiBean> mdatas;

    public CheckStateAdapter(WifiListBean datasWrapper, Context context) {
        this.context = context;
        this.mdatasWrapper = datasWrapper;
        mdatas = datasWrapper.data;
    }

    @Override
    public StateHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StateHolder(LayoutInflater.from(context).inflate(R.layout.item_wifi, parent, false));
    }

    @Override
    public void onBindViewHolder(final StateHolder holder, final int position) {
        if (null == mdatas || mdatas.size() == 0) return;
        WifiBean bean = mdatas.get(position);
        holder.itemView.setSelected(bean.is_homewifi == 1);
        if (bean.is_homewifi == 1) {
            holder.ivSelected.setVisibility(View.VISIBLE);
            MeWifiListActivity.wifi_bssid=bean.wifi_bssid;
            MeWifiListActivity.wifi_ssid=bean.wifi_ssid;
        } else {
            holder.ivSelected.setVisibility(View.INVISIBLE);
        }
        holder.tvState.setText(bean.wifi_ssid);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                   onItemClickListener.OnItemClick(view, holder, holder.getAdapterPosition());
                                               }
                                           }

        );
        //判断wifi强度   -65 -80
        if (bean.wifi_power > -65) {
            //3格
            holder.iv_wifi_power.setImageResource(R.drawable.wifi_power_third);
        } else if (bean.wifi_power > -80) {
            //2格
            holder.iv_wifi_power.setImageResource(R.drawable.wifi_power_second);
        } else {
            //1格
            holder.iv_wifi_power.setImageResource(R.drawable.wifi_power_first);
        }


    }

    @Override
    public int getItemCount() {
        return (mdatas == null) || (mdatas.size() == 0) ? 0 : mdatas.size();
    }


    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener { //定义接口，实现Recyclerview点击事件
        void OnItemClick(View view, StateHolder holder, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) { //实现点击
        this.onItemClickListener = onItemClickListener;
    }


    public class StateHolder extends RecyclerView.ViewHolder {
        TextView tvState;
        ImageView ivSelected;
        ImageView iv_wifi_power;

        public StateHolder(View itemView) {
            super(itemView);
            tvState = (TextView) itemView.findViewById(R.id.tv_state);
            ivSelected = (ImageView) itemView.findViewById(R.id.iv_selected);
            iv_wifi_power = (ImageView) itemView.findViewById(R.id.iv_wifi_power);
        }
    }
}