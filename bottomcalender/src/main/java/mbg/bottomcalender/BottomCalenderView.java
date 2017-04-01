package mbg.bottomcalender;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Administrator on 2016/12/31.
 */

public class BottomCalenderView extends Dialog implements WheelRecyclerView.OnWheelChangedListener {
    private static final int maxYear = Calendar.getInstance(Locale.CHINA).get(
            Calendar.YEAR);
    private static final int minYear = maxYear - 80;

    private String[] dayContent31;

    private String[] dayContent30;

    private String[] dayContent28;

    private String[] dayContent29;
    //UI
    private TextView infoTV;
    private WheelRecyclerView yearWheel, monthWheel, dayWheel;
    //DATA
    private String info;
    private String[] yearData;
    private String[] monthData;
    private String[] dayData;
    private int curYear;
    private int curMonth;
    private int curDay;
    private OnDatePickedListener onDatePickedListener;
    private View.OnClickListener cancelListener;

    public BottomCalenderView(Context context, int curYear,
                              int curMonth, int curDay,
                              OnDatePickedListener onDatePickedListener,
                              View.OnClickListener cancelListener) {
        super(context, R.style.dialog_fullscreen_menu);
        setContentView(R.layout.dialog_date_picker);
        this.onDatePickedListener = onDatePickedListener;
        this.cancelListener = cancelListener;
        initView();
        initData(curYear, curMonth, curDay);
        Window window = getWindow();
        WindowManager.LayoutParams paramsWindow = window.getAttributes();
        paramsWindow.width = window.getWindowManager().getDefaultDisplay().getWidth();
        paramsWindow.height = context.getResources().getDimensionPixelOffset(R.dimen.actionBar_hight) + context.getResources().getDimensionPixelSize(R.dimen.picker_wheel_height) + context.getResources().getDimensionPixelSize(R.dimen.bottom_actionBar_hight);
        paramsWindow.gravity = Gravity.BOTTOM;
        paramsWindow.windowAnimations = R.style.select_popup_bottom;
        window.setAttributes(paramsWindow);
        setCanceledOnTouchOutside(true);
    }

    private void initView() {
        infoTV = (TextView) findViewById(R.id.tv_dialog_date_info);
        yearWheel = (WheelRecyclerView) findViewById(R.id.picker_year);
        yearWheel.setChangeListener(this);
        monthWheel = (WheelRecyclerView) findViewById(R.id.picker_month);
        monthWheel.setChangeListener(this);
        dayWheel = (WheelRecyclerView) findViewById(R.id.picker_day);
        dayWheel.setChangeListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cancelListener != null) {
                    cancelListener.onClick(v);
                }
                dismiss();
            }
        });
        findViewById(R.id.tv_ensure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDatePickedListener != null) {
                    onDatePickedListener.onDatePicked(curYear, curMonth, curDay);
                }
                dismiss();
            }
        });
    }

    private void initData(int year, int month, int day) {
        if (year < minYear) {
            year = minYear;
        } else if (year > maxYear) {
            year = maxYear;
        }
        curYear = year;
        if (month < 1) {
            month = 1;
        } else if (month > 12) {
            month = 12;
        }
        curMonth = month;
        dayContent31 = new String[31];
        dayContent30 = new String[30];
        dayContent29 = new String[29];
        dayContent28 = new String[28];
        for (int i = 0; i < 31; i++) {
            String dayStr = String.valueOf(i + 1) + "日";
            if (i < 28) {
                dayContent31[i] = dayStr;
                dayContent30[i] = dayStr;
                dayContent29[i] = dayStr;
                dayContent28[i] = dayStr;
            } else if (i < 29) {
                dayContent31[i] = dayStr;
                dayContent30[i] = dayStr;
                dayContent29[i] = dayStr;
            } else if (i < 30) {
                dayContent31[i] = dayStr;
                dayContent30[i] = dayStr;
            } else {
                dayContent31[i] = dayStr;
            }
        }

        if (day > getDaysOfMonth(curYear, curMonth - 1).length) {
            day = getDaysOfMonth(curYear, curMonth - 1).length;
        }
        if (day < 1) {
            day = 1;
        }
        curDay = day;
        int yearSize = 81;
        //初始化年月日
        yearData = new String[yearSize];
        for (int i = 0; i < yearSize; i++) {
            yearData[i] = String.valueOf(i + minYear) + "年";
        }
        yearWheel.setData(yearData);
        yearWheel.setCurrentItem(curYear - minYear);
        monthData = new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
        monthWheel.setData(monthData);
        monthWheel.setCurrentItem(curMonth - 1);
        dayData = getDaysOfMonth(curYear, curMonth);
        dayWheel.setData(dayData);
        dayWheel.setCurrentItem(curDay - 1);
        infoTV.setText(curYear + "年" + curMonth + "月" + curDay + "日");
    }

    private String[] getDaysOfMonth(int year, int month) {
        String[] dayData;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                dayData = dayContent31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                dayData = dayContent30;
                break;
            case 2:
                if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
                    dayData = dayContent29;
                } else {
                    dayData = dayContent28;
                }
                break;
            default:
                dayData = dayContent31;
        }
        return dayData;
    }

    private void setDaysOfMonth(int year, int month){
        dayData = getDaysOfMonth(year, month);
        String[] dayDataOld = dayWheel.getAdapter().getData();
        int oldLength = dayDataOld == null ? 0 : dayDataOld.length;
        int length = dayData.length;
        if(oldLength != length){
            int dayIndex = dayWheel.getCurrentItem();
            String dayStr = dayDataOld[dayIndex];
            curDay = Integer.parseInt(dayStr.substring(0, dayStr.length() - 1));
            curDay = Math.min(curDay, dayData.length - 1);
            dayWheel.setData(dayData);
            dayWheel.setCurrentItem(curDay - 1);
        }
    }

    @Override
    public void onChanged(WheelRecyclerView wheel, int current) {
        if(wheel.getId() == R.id.picker_year){
            int yearIndex = yearWheel.getCurrentItem();
            if(yearIndex >= yearData.length){
                return;
            }
            String yearStr = yearData[yearIndex];
            curYear = Integer.parseInt(yearStr.substring(0, yearStr.length() - 1));
            setDaysOfMonth(curYear, curMonth);
        }else if(wheel.getId() == R.id.picker_month){
            int monthIndex = monthWheel.getCurrentItem();
            if(monthIndex > 11){
                return;
            }
            String monthStr = monthData[monthIndex];
            curMonth = Integer.parseInt(monthStr.substring(0, monthStr.length() - 1));
            setDaysOfMonth(curYear, curMonth);
        }
        else if(wheel.getId() == R.id.picker_day){
            int dayIndex = dayWheel.getCurrentItem();
            if(dayIndex > dayData.length){
                return;
            }
            String dayStr = dayData[dayIndex];
            curDay = Integer.parseInt(dayStr.substring(0, dayStr.length() - 1));
        }
        info = curYear + "年" + curMonth + "月" + curDay + "日";
        infoTV.setText(info);
    }

    public interface OnDatePickedListener {
        void onDatePicked(int year, int month, int day);
    }
}

