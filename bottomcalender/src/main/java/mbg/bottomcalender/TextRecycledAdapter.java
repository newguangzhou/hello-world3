package mbg.bottomcalender;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/12/31.
 */

public class TextRecycledAdapter extends RecyclerView.Adapter<TextRecycledAdapter.TextViewHolder>{
    private LayoutInflater mInflater;
    private String[] mData;
    private int mGravity;

    public TextRecycledAdapter(Context context, int gravity){
        mInflater = LayoutInflater.from(context);
        mGravity = gravity;
    }

    public void setData(String[] data){
        mData = data;
        notifyDataSetChanged();
    }

    public String[] getData(){
        return mData;
    }

    @Override
    public TextViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        return new TextViewHolder(mInflater.inflate(R.layout.picker_item, viewGroup, false), mGravity);
    }

    @Override
    public void onBindViewHolder(TextViewHolder holder, int position) {
        holder.tv.setText(getItem(position));
    }

    public String getItem(int position){
        if(position < 2 || position > mData.length + 1){
            return "";
        }
        return mData[position - 2];
    }

    @Override
    public int getItemCount() {
        if(mData != null){
            return mData.length + 4;
        }
        return 0;
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder{
        TextView tv;

        public TextViewHolder(View itemView, int gravity) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.picker_item_tv);
            tv.setGravity(gravity);
        }
    }
}

