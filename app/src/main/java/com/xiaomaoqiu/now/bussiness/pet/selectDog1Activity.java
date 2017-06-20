package com.xiaomaoqiu.now.bussiness.pet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.bussiness.pet.info.PetVarietyAdapter;
import com.xiaomaoqiu.pet.R;

public class selectDog1Activity extends BaseActivity {

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
				Intent intent = new Intent(getApplicationContext(),selectDog2Activity.class);
				startActivity(intent);
				finish();
			}
		});

		mRecyclerView=(RecyclerView)findViewById(R.id.recycler_variety);
		mRecyclerView.setLayoutManager(new GridLayoutManager(this,3));
		mAdapter=new PetVarietyAdapter();
		mAdapter.setOnItemClickListener(new PetVarietyAdapter.onVerityItemClickListener() {
			@Override
			public void onItemClick(View view, String name) {
				PetUtil.getInstance().setPetName(name);
				finish();
			}
		});
		mRecyclerView.setAdapter(mAdapter);
		mAdapter.setPetList(this);

	}



}
