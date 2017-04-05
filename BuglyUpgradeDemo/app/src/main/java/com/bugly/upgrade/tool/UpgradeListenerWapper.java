package com.bugly.upgrade.tool;

import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.upgrade.UpgradeListener;

import android.util.Log;

/**
 * Created by zill on 2017/3/27.
 */

public class UpgradeListenerWapper implements UpgradeListener
{
    private UpgradeListener listener = null;
    private static String TAG = "Bugly";

    public void regist(UpgradeListener l) {
        listener = l;
    }
    public void unregist() {
        listener = null;
    }
    @Override
    public void onUpgrade(int ret, UpgradeInfo strategy, boolean isManual, boolean isSilence) {
        Log.i(TAG, "UpgradeListenerWapper::onUpgrade");
        if( listener != null)
            listener.onUpgrade(ret, strategy, isManual, isSilence);
    }
}

