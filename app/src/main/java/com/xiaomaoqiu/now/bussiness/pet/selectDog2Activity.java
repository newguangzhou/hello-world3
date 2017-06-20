package com.xiaomaoqiu.now.bussiness.pet;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.xiaomaoqiu.now.base.BaseActivity;
import com.xiaomaoqiu.now.util.ToastUtil;
import com.xiaomaoqiu.now.view.sortlistview.CharacterParser;
import com.xiaomaoqiu.now.view.sortlistview.ClearEditText;
import com.xiaomaoqiu.now.view.sortlistview.PinyinComparator;
import com.xiaomaoqiu.now.view.sortlistview.SideBar;
import com.xiaomaoqiu.now.view.sortlistview.SortAdapter;
import com.xiaomaoqiu.now.view.sortlistview.SortModel;
import com.xiaomaoqiu.pet.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class selectDog2Activity extends BaseActivity {

    public static String TAG_PARAM1 = "variety";

    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortAdapter adapter;
    private ClearEditText mClearEditText;
    private EditText edit_add_dog;
    private View bt_add_dog;

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<SortModel> sortedallDogNameModeList;


//	/**
//	 * 所有狗的名字
//	 */
//	private String[] allDogNameList;
//
//	/**
//	 * 所有狗的能量值
//	 */
//	public String[] allDogEnergyList;
    /**
     * 狗与算法匹配
     */
    public HashMap<String, String> allDogEnergyAndNameMap;


    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("选择品种");
        setContentView(R.layout.dlg_modify_variety2);
        initViews();
    }

    private void initViews() {
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();

        sideBar = (SideBar) findViewById(R.id.sidrbar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);
        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }

            }
        });

        sortListView = (ListView) findViewById(R.id.country_lvcountry);
        sortListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//				Intent intent = new Intent();
//				intent.putExtra(TAG_PARAM1,((SortModel)adapter.getItem(position)).getName());
                PetUtil.getInstance().setPetName(((SortModel) adapter.getItem(position)).getName());

//				setResult(Activity.RESULT_OK,intent);

                finish();
            }
        });
//		allDogNameList=getResources().getStringArray(R.array.alldog_name);
        sortedallDogNameModeList = filledData(PetUtil.getInstance().allDogNameList);
//
//		allDogEnergyList=getResources().getStringArray(R.array.all_energyFormulaCodeList);
//
//		allDogEnergyAndNameMap=new HashMap<>();
//		int length=allDogNameList.length;
//		for(int i=0;i<length;i++){
//			allDogEnergyAndNameMap.put(allDogNameList[i],allDogEnergyList[i]);
//		}

        // 根据a-z进行排序源数据
        Collections.sort(sortedallDogNameModeList, pinyinComparator);
        adapter = new SortAdapter(this, sortedallDogNameModeList);
        sortListView.setAdapter(adapter);
        sortListView.requestFocus();


        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        edit_add_dog = (EditText) findViewById(R.id.edit_add_dog);

        bt_add_dog = findViewById(R.id.bt_add_dog);
        bt_add_dog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!"".equals(edit_add_dog.getText().toString().trim())) {
                    PetUtil.getInstance().dogName = edit_add_dog.getText().toString().trim();
                    PetUtil.getInstance().energyType="2";
                    finish();
                } else {
                    ToastUtil.showAtCenter("名字不能为空");
                }
            }
        });
    }


    /**
     * 为ListView填充数据
     *
     * @param date
     * @return
     */
    private List<SortModel> filledData(String[] date) {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        for (int i = 0; i < date.length; i++) {
            SortModel sortModel = new SortModel();
            sortModel.setName(date[i]);
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<SortModel> filterDateList = new ArrayList<SortModel>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = sortedallDogNameModeList;
        } else {
            filterDateList.clear();
            for (SortModel sortModel : sortedallDogNameModeList) {
                String name = sortModel.getName();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }

}
