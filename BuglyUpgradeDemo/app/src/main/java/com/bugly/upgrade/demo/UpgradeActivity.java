package com.bugly.upgrade.demo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

    private String TAG_PATCH = "TAG_PATCH";
    private String TAG_UPGRADE = "TAG_UPGRADE";


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

    private ProgressBar bar;

    protected void onCreate(Bundle savedInstanceState) {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_upgrade);

        progressTextView = getView(R.id.progressTextView);
        version = getView(R.id.version);
        content = getView(R.id.content);
        bar = getView(R.id.progressBar);
        bar.setMax(100);

        /* 设置更新状态回调接口 */
        MyApplication app = (MyApplication)getApplication();
        app.registUpgradeStateListener(new UpgradeStateListener() {
            @Override
            public void onUpgradeSuccess(boolean isManual) {
                Toast.makeText(getApplicationContext(),"UPGRADE_SUCCESS",Toast.LENGTH_SHORT).show();
                progressTextView.setText("检测到有新版本.");
                // TODO : 添加网络监测判断
                if ( true) {
                    new AlertDialog.Builder(getBaseContext()).setTitle("发现您使用的是移动网络， 要继续下载吗？")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Beta.startDownload();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    System.exit(0);
                                }
                            }).show();
                } else {
                    Beta.startDownload();
                }


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
                navigateToGame();
            }

            @Override
            public void onDownloadCompleted(boolean isManual)
            {
                Toast.makeText(getApplicationContext(),"DOWNLOAD_COMPLETED",Toast.LENGTH_SHORT).show();
                progressTextView.setText("下载完成.");

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
                            updateProgress(task);

                        }

                        @Override
                        public void onCompleted(DownloadTask task) {
                            progressTextView.setText("成功下载补丁.");
                        }

                        @Override
                        public void onFailed(DownloadTask task, int code, String extMsg) {
                            AlertDownloadFail();
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "没有更新", Toast.LENGTH_LONG).show();
                }
            }
        });



        /**
         * 补丁回调接口，可以监听补丁接收、下载、合成的回调
         */

        app.registBetaPatchListener(new BetaPatchListener() {
            @Override
            public void onPatchReceived(String patchFileUrl) {
                setUpgradeType(UpgradeType.PATCH);
                progressTextView.setText("检测到有更新补丁包.");
//                Toast.makeText(getApplicationContext(), patchFileUrl, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDownloadReceived(long savedLength, long totalLength) {
                if ( getUpgradeType() == UpgradeType.PATCH) {
                    updateProgress(savedLength, totalLength);
                }
            }

            @Override
            public void onDownloadSuccess(String patchFilePath) {
//                Toast.makeText(getApplicationContext(), patchFilePath, Toast.LENGTH_SHORT).show();
                progressTextView.setText("成功下载补丁.");
                Beta.applyDownloadedPatch();


            }

            @Override
            public void onDownloadFailure(String msg) {
                AlertDownloadFail();
            }

            @Override
            public void onApplySuccess(String msg) {
                progressTextView.setText("补丁应用成功.");
                try {
                    Thread.sleep(1 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                navigateToGame();
            }

            @Override
            public void onApplyFailure(String msg) {
                Log.e(TAG_PATCH, msg);
                new AlertDialog.Builder(UpgradeActivity.this).setTitle("补丁失败，请联系客服人员")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.exit(0);
                            }
                        }).show();
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


    private void navigateToGame()
    {
        finish();

        Intent i = new Intent();
        i.setClass(getApplicationContext(), OtherActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(i);
    }

    private void AlertDownloadFail()
    {
        new AlertDialog.Builder(UpgradeActivity.this).setTitle("补丁下载失败，请检查您的网络")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                }).show();
    }

    public <T extends View> T getView(int id) {
        return (T) findViewById(id);
    }
}
