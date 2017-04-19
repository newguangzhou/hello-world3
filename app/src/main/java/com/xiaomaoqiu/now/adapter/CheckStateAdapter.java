package com.xiaomaoqiu.now.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaomaoqiu.now.bean.nocommon.WifiBean;
import com.xiaomaoqiu.now.bean.nocommon.WifiListBean;
import com.xiaomaoqiu.pet.R;

import java.util.ArrayList;

@SuppressLint("WrongConstant")
public class CheckStateAdapter extends RecyclerView.Adapter<CheckStateAdapter.StateHolder> {

    private Context context;
    //    private String[] arrayState;
    WifiListBean mdatasWrapper;
    public ArrayList<WifiBean> mdatas;
    private int selectedPosition = -5; //默认一个参数

    public CheckStateAdapter(WifiListBean datasWrapper, Context context) {
        this.context = context;
//        this.arrayState = arrayState;
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
        WifiBean bean=mdatas.get(position);
        holder.itemView.setSelected(bean.Is_homewifi);
        if (bean.Is_homewifi) {
            holder.ivSelected.setVisibility(View.VISIBLE);
        } else {
            holder.ivSelected.setVisibility(View.INVISIBLE);
        }
        holder.tvState.setText(mdatas.get(position).wifi_ssid);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                   onItemClickListener.OnItemClick(view, holder, holder.getAdapterPosition());
                                                   selectedPosition = position; //选择的position赋值给参数，
//                                                   notifyItemChanged(selectedPosition);//刷新当前点击item
                                               }
                                           }

        );
    }

    @Override
    public int getItemCount() {
        return (mdatas==null)||(mdatas.size() == 0 )? 0 : mdatas.size();
    }

    public void updateData(ArrayList<WifiBean> datas) {
        if (null != datas && datas.size() > 0) {
            selectedPosition = -5;
            mdatas = datas;
            notifyDataSetChanged();
        }
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

        public StateHolder(View itemView) {
            super(itemView);
            tvState= (TextView) itemView.findViewById(R.id.tv_state);
            ivSelected= (ImageView) itemView.findViewById(R.id.iv_selected);
        }
    }
}