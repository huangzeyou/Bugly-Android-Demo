package com.bugly.upgrade.tool;

import com.tencent.bugly.beta.upgrade.UpgradeStateListener;
import android.util.Log;
/**
 * Created by zill on 2017/3/27.
 */
public class UpgradeStateListenerWrapper implements UpgradeStateListener {
    private static String TAG = "Bugly";
    private UpgradeStateListener listener = null;

    public void regist(UpgradeStateListener l) {
        listener = l;
    }

    public void unregist() {
        listener = null;
    }

    @Override
    public void onUpgradeFailed(boolean isManual) {
        Log.i(TAG, "UpgradeStateListenerWrapper::onUpgradeFailed");
        if (listener != null)
            listener.onUpgradeFailed(isManual);
    }

    @Override
    public void onUpgradeSuccess(boolean isManual) {
        Log.i(TAG, "UpgradeStateListenerWrapper::onUpgradeSuccess");
        if (listener != null)
            listener.onUpgradeSuccess(isManual);
    }


    @Override
    public void onUpgradeNoVersion(boolean isManual) {
        Log.i(TAG, "UpgradeStateListenerWrapper::onUpgradeNoVersion");
        if (listener != null)
            listener.onUpgradeNoVersion(isManual);
    }

    @Override
    public void onUpgrading(boolean isManual) {
        Log.i(TAG, "UpgradeStateListenerWrapper::onUpgrading");
        if (listener != null)
            listener.onUpgrading(isManual);
    }

    @Override
    public void onDownloadCompleted(boolean isManual) {
        Log.i(TAG, "UpgradeStateListenerWrapper::onDownloadCompleted");
        if (listener != null)
            listener.onDownloadCompleted(isManual);
    }

}
