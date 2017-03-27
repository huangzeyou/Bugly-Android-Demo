package com.bugly.upgrade.tool;

import com.tencent.bugly.beta.upgrade.UpgradeStateListener;

/**
 * Created by zill on 2017/3/27.
 */
public class UpgradeStateListenerWrapper implements UpgradeStateListener {
    private UpgradeStateListener listener = null;

    public void regist(UpgradeStateListener l) {
        listener = l;
    }

    public void unregist() {
        listener = null;
    }

    @Override
    public void onUpgradeFailed(boolean isManual) {
        if (listener != null)
            listener.onUpgradeFailed(isManual);
    }

    @Override
    public void onUpgradeSuccess(boolean isManual) {
        if (listener != null)
            listener.onUpgradeSuccess(isManual);
    }

    @Override
    public void onUpgradeNoVersion(boolean isManual) {
        if (listener != null)
            listener.onUpgradeNoVersion(isManual);
    }

    @Override
    public void onUpgrading(boolean isManual) {
        if (listener != null)
            listener.onUpgrading(isManual);
    }

    @Override
    public void onDownloadCompleted(boolean isManual) {
        if (listener != null)
            listener.onDownloadCompleted(isManual);
    }
}
