package com.xiaomaoqiu.old.ui.mainPages.pageMe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.bussiness.user.UserInstance;
import com.xiaomaoqiu.now.http.HttpCode;
import com.xiaomaoqiu.old.R;
import com.xiaomaoqiu.old.utils.HttpUtil;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends BaseActivity {

    ListView mLvMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.me_message);
        setContentView(R.layout.activity_message);
        mLvMessage = (ListView) findViewById(R.id.lv_message);
        mLvMessage.setAdapter(new MessageAdapter());
        queryMessage();
    }

    public void queryMessage()
    {
        HttpUtil.get2("msg.get_msg",new JsonHttpResponseHandler(){
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        HttpCode code = HttpCode.valueOf(response.optInt("status"));
                        if (code == HttpCode.EC_SUCCESS) {
                            MessageAdapter adapter = (MessageAdapter) MessageActivity.this.mLvMessage.getAdapter();
                            adapter.initFromJson(response);
                        }else
                        {
                            Toast.makeText(MessageActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(MessageActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                },
                UserInstance.getUserInstance().getUid(),UserInstance.getUserInstance().getToken());
    }

    class MessageAdapter extends BaseAdapter
    {
        class MessageData{
            public int msgType;
            public String msgContent;
        }

        List<MessageData> mData = new ArrayList<MessageData>();

        public void initFromJson(JSONObject jsonObject)
        {
            mData.clear();

        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null)
            {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.item_message, null);
            }

            TextView tvContent = (TextView) convertView.findViewById(R.id.tv_message);
            tvContent.setText(mData.get(position).msgContent);
            return convertView;
        }
    }
}
