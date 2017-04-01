package com.xiaomaoqiu.pet.ui.mainPages.pageLocate.presenter;

/**
 * Created by Administrator on 2017/1/14.
 */

public interface ILocateView {
    void onShowPhoneLoc();
    void onChangeLightStatus(boolean isOpen,boolean isFromView);
    void onFail(String msg);
}
