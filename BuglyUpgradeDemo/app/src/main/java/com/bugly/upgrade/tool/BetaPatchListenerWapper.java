package com.bugly.upgrade.tool;

import com.tencent.bugly.beta.interfaces.BetaPatchListener;

/**
 * Created by zill on 2017/3/27.
 */

public class BetaPatchListenerWapper implements BetaPatchListener {
    BetaPatchListener listener = null;

    public void regist(BetaPatchListener l) {
        listener = l;
    }
    public void unregist() {
        listener = null;
    }

    @Override
    public void onPatchReceived(String patchFileUrl) {
        if (listener != null)
            listener.onPatchReceived( patchFileUrl);
    }

    @Override
    public void onDownloadReceived(long savedLength, long totalLength) {
        if (listener != null)
            listener.onDownloadReceived( savedLength, totalLength);
    }

    @Override
    public void onDownloadSuccess(String patchFilePath) {
        if (listener != null)
            listener.onDownloadSuccess( patchFilePath);
    }

    @Override
    public void onDownloadFailure(String msg) {
        if (listener != null)
            listener.onDownloadFailure( msg);
    }

    @Override
    public void onApplySuccess(String msg) {
        if (listener != null)
            listener.onApplySuccess( msg);
    }

    @Override
    public void onApplyFailure(String msg) {
        if (listener != null)
            listener.onApplyFailure( msg);
    }
}
