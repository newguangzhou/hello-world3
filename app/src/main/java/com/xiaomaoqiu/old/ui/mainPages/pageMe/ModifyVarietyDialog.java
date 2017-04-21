package com.xiaomaoqiu.old.ui.mainPages.pageMe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.old.ui.mainPages.pageMe.adapter.PetVarietyAdapter;
import com.xiaomaoqiu.pet.R;

public class ModifyVarietyDialog extends BaseActivity {

	public static String TAG_PARAM1 = "variety";

	private RecyclerView  mRecyclerView;
	private PetVarietyAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("选择品种");
		setContentView(R.layout.dlg_modify_variety);
		initViews();
	}

	private void initViews() {
		findViewById(R.id.btn_all_variety).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),ModifyVarietyDialog2.class);
				startActivityForResult(intent, 1);
			}
		});

		mRecyclerView=(RecyclerView)findViewById(R.id.recycler_variety);
		mRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
		mAdapter=new PetVarietyAdapter();
		mAdapter.setOnItemClickListener(new PetVarietyAdapter.onVerityItemClickListener() {
			@Override
			public void onItemClick(View view, String name) {
				Intent data = new Intent();
				data.putExtra(ModifyVarietyDialog.TAG_PARAM1,name);
				setResult(Activity.RESULT_OK, data);
				ModifyVarietyDialog.this.finish();
			}
		});
		mRecyclerView.setAdapter(mAdapter);
		mAdapter.setPetList(this);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode== 1 && resultCode == RESULT_OK)
		{
			setResult(resultCode,data);
			finish();
		}
	}

}
