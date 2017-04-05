package com.bugly.upgrade.tool;

import com.tencent.bugly.beta.interfaces.BetaPatchListener;
import android.util.Log;
/**
 * Created by zill on 2017/3/27.
 */

public class BetaPatchListenerWrapper implements BetaPatchListener {
    BetaPatchListener listener = null;
    private static String TAG = "Bugly";

    public void regist(BetaPatchListener l) {
        listener = l;
    }
    public void unregist() {
        listener = null;
    }

    @Override
    public void onPatchReceived(String patchFileUrl) {
        Log.i(TAG, "BetaPatchListenerWapper::onPatchReceived");
        if (listener != null)
            listener.onPatchReceived( patchFileUrl);
    }

    @Override
    public void onDownloadReceived(long savedLength, long totalLength) {
        Log.i(TAG, "BetaPatchListenerWapper::onDownloadReceived");
        if (listener != null)
            listener.onDownloadReceived( savedLength, totalLength);
    }

    @Override
    public void onDownloadSuccess(String patchFilePath) {
        Log.i(TAG, "BetaPatchListenerWapper::onDownloadSuccess");
        if (listener != null)
            listener.onDownloadSuccess( patchFilePath);
    }

    @Override
    public void onDownloadFailure(String msg) {
        Log.i(TAG, "BetaPatchListenerWapper::onDownloadFailure");
        if (listener != null)
            listener.onDownloadFailure( msg);
    }

    @Override
    public void onApplySuccess(String msg) {
        Log.i(TAG, "BetaPatchListenerWapper::onApplySuccess");
        if (listener != null)
            listener.onApplySuccess( msg);
    }

    @Override
    public void onApplyFailure(String msg) {
        Log.i(TAG, "BetaPatchListenerWapper::onApplyFailure");
        if (listener != null)
            listener.onApplyFailure( msg);
    }
}
