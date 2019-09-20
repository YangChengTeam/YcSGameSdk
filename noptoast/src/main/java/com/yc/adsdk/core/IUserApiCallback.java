package com.yc.adsdk.core;

/**
 * Created by caokun on 2019/8/19 19:45.
 */

public interface IUserApiCallback {
    void onSuccess(String msg);

    void onFailure(String msg, int code);
}
