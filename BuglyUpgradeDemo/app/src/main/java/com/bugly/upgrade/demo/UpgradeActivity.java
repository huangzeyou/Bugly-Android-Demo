package com.bugly.upgrade.demo;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.download.DownloadTask;
import com.tencent.bugly.beta.download.DownloadListener;
import com.tencent.bugly.beta.interfaces.BetaPatchListener;
import com.tencent.bugly.beta.upgrade.UpgradeListener;
import com.tencent.bugly.beta.upgrade.UpgradeStateListener;


public class UpgradeActivity extends AppCompatActivity {
    public enum UpgradeType {
        NONE, UPGRADE, PATCH
    }

    public UpgradeType getUpgradeType() {
        return upgradeType;
    }

    public void setUpgradeType(UpgradeType upgradeType) {
        this.upgradeType = upgradeType;
    }

    private UpgradeType upgradeType = UpgradeType.NONE;

    private TextView progressTextView;
    private TextView version;
    private TextView content;

    private Button start;
    private ProgressBar bar;

    protected void onCreate(Bundle savedInstanceState) {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_upgrade);

        progressTextView = getView(R.id.progressTextView);
        version = getView(R.id.version);
        content = getView(R.id.content);
        start = getView(R.id.start);
        bar = getView(R.id.progressBar);
        bar.setMax(100);

        /* 设置更新状态回调接口 */
        MyApplication app = (MyApplication)getApplication();
        app.registUpgradeStateListener(new UpgradeStateListener() {
            @Override
            public void onUpgradeSuccess(boolean isManual) {
                Toast.makeText(getApplicationContext(),"UPGRADE_SUCCESS",Toast.LENGTH_SHORT).show();
                progressTextView.setText("检测到有新版本.");
                updateBtn(Beta.getStrategyTask());


            }

            @Override
            public void onUpgradeFailed(boolean isManual) {
                // 检测更新失败
                Toast.makeText(getApplicationContext(),"UPGRADE_FAILED",Toast.LENGTH_SHORT).show();
                progressTextView.setText("更新失败.");
            }

            @Override
            public void onUpgrading(boolean isManual) {
                // 正在检测更新
                Toast.makeText(getApplicationContext(),"UPGRADE_CHECKING",Toast.LENGTH_SHORT).show();
                progressTextView.setText("正在检测更新.");
            }

            @Override
            public void onUpgradeNoVersion(boolean isManual) {
                Toast.makeText(getApplicationContext(),"UPGRADE_NO_VERSION",Toast.LENGTH_SHORT).show();
                progressTextView.setText("已经是最新版本.");
            }

            @Override
            public void onDownloadCompleted(boolean isManual)
            {
                Toast.makeText(getApplicationContext(),"DOWNLOAD_COMPLETED",Toast.LENGTH_SHORT).show();
                progressTextView.setText("下载完成.");
            }
        });



        /**
         * 补丁回调接口，可以监听补丁接收、下载、合成的回调
         */
        app.registBetaPatchListener(new BetaPatchListener() {
            @Override
            public void onPatchReceived(String patchFileUrl) {
                setUpgradeType(UpgradeType.PATCH);
                Toast.makeText(getApplicationContext(), patchFileUrl, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDownloadReceived(long savedLength, long totalLength) {
                if ( getUpgradeType() == UpgradeType.PATCH) {
                    updateProgress(savedLength, totalLength);
                }
            }

            @Override
            public void onDownloadSuccess(String patchFilePath) {
                Toast.makeText(getApplicationContext(), patchFilePath, Toast.LENGTH_SHORT).show();
                Beta.applyDownloadedPatch();
            }

            @Override
            public void onDownloadFailure(String msg) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onApplySuccess(String msg) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onApplyFailure(String msg) {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });


        app.registUpgradeListener(new UpgradeListener() {
            @Override
            public void onUpgrade(int ret,UpgradeInfo strategy, boolean isManual, boolean isSilence) {
                if (strategy != null) {

                    /*注册下载监听，监听下载事件*/
                    // 必须在 接收到策略之后再注册 Downloadlistener.
                    Beta.registerDownloadListener(new DownloadListener() {
                        @Override
                        public void onReceive(DownloadTask task) {
                            updateBtn(task);
                            updateProgress(task);

                        }

                        @Override
                        public void onCompleted(DownloadTask task) {
                            updateBtn(task);
                            start.setText("安装");

                            start.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    DownloadTask task = Beta.getStrategyTask();

                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    i.setDataAndType(Uri.parse("file://" + task.getSaveFile().getAbsolutePath()),
                                            "application/vnd.android.package-archive");
                                    getApplicationContext().startActivity(i);

                                    android.os.Process.killProcess(android.os.Process.myPid());

                                }
                            });

                        }

                        @Override
                        public void onFailed(DownloadTask task, int code, String extMsg) {
                            updateBtn(task);
                            progressTextView.setText("failed");

                        }
                    });
                    Toast.makeText(getApplicationContext(), "有更新 ", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "没有更新", Toast.LENGTH_LONG).show();
                }
            }
        });

            /*为下载按钮设置监听*/
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Beta.startDownload();
//                updateBtn(task);
//                if (task.getStatus() == DownloadTask.DOWNLOADING) {
//                    finish();
//                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

            /*注销下载监听*/
        Beta.unregisterDownloadListener();

        MyApplication app = (MyApplication)getApplication();
        app.unregistBetaPatchListener();
        app.unregistUpgradeListener();
        app.unregistUpgradeStateListener();

    }


    public void updateBtn(DownloadTask task) {
        int status = task.getStatus();
            /*根据下载任务状态设置按钮*/
        switch (status) {
            case DownloadTask.INIT:
            case DownloadTask.DELETED:
            case DownloadTask.FAILED: {
                start.setText("开始下载");
            }
            break;
            case DownloadTask.COMPLETE: {
                start.setText("安装");
            }
            break;
            case DownloadTask.DOWNLOADING: {
                start.setText("暂停");
            }
            break;
            case DownloadTask.PAUSED: {
                start.setText("继续下载");
            }
            break;
        }
    }

    public void updateProgress(DownloadTask task)
    {
        progressTextView.setVisibility(View.VISIBLE);
        bar.setVisibility(View.VISIBLE);

        long saved = task.getSavedLength();
        long total = task.getTotalLength();

        updateProgress(saved, total);
    }


    private void updateProgress(long saved, long total)
    {
        int progress = (int) ((double) saved * 100 / total);
        StringBuilder info = new StringBuilder();
        info.append( Integer.toString(progress)).append( "%" );
        info.append("(").append(Long.toString(saved)).append("/").append(Long.toString(total)).append(")");

        progressTextView.setText(info);
        bar.setProgress( progress );
    }


    public <T extends View> T getView(int id) {
        return (T) findViewById(id);
    }
}
