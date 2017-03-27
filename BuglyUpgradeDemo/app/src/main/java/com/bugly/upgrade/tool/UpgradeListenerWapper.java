package com.bugly.upgrade.tool;

import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.upgrade.UpgradeListener;

/**
 * Created by zill on 2017/3/27.
 */

public class UpgradeListenerWapper implements UpgradeListener
{
    private UpgradeListener listener = null;

    public void regist(UpgradeListener l) {
        listener = l;
    }
    public void unregist() {
        listener = null;
    }
    @Override
    public void onUpgrade(int ret, UpgradeInfo strategy, boolean isManual, boolean isSilence) {
        if( listener != null)
            listener.onUpgrade(ret, strategy, isManual, isSilence);
    }
}
