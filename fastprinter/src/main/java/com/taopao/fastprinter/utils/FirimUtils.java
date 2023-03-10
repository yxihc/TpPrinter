package com.taopao.fastprinter.utils;

import android.util.Log;
import android.widget.Toast;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.cretin.www.cretinautoupdatelibrary.interfaces.AppDownloadListener;
import com.cretin.www.cretinautoupdatelibrary.interfaces.AppUpdateInfoListener;
import com.cretin.www.cretinautoupdatelibrary.interfaces.MD5CheckListener;
import com.cretin.www.cretinautoupdatelibrary.model.DownloadInfo;
import com.cretin.www.cretinautoupdatelibrary.model.TypeConfig;
import com.cretin.www.cretinautoupdatelibrary.utils.AppUpdateUtils;
import com.taopao.fastprinter.MainActivity;
import com.taopao.fastprinter.bean.AppInfo;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class FirimUtils {

    public static String app_token = "d71ab3355fb1fa32ac7eff39c6541eeb";
    public static String app_id = "62b3091223389f344e5a80fe";
    public static String baseURL = "http://api.bq04.com";
    public static String TAG = "FirimUtils";

    public static void getDownloadToken() {
        String url = baseURL + "/apps/" + app_id + "/download_token?api_token=" + app_token;
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String download_token = jsonObject.getString("download_token");
                            Log.e(TAG, response);
                            Log.e(TAG, download_token);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }

    public static void getAppinfo() {
        String url = "http://api.bq04.com/apps/latest/62b3091223389f344e5a80fe?api_token=d71ab3355fb1fa32ac7eff39c6541eeb";
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        AppInfo appInfo = GsonUtils.fromJson(response, AppInfo.class);
                        Log.e(TAG, response);
                        updateApp(appInfo);
                    }
                });
    }

    public static void updateApp(AppInfo appInfo) {

        int appVersionCode = AppUtils.getAppVersionCode();

        int netVersion;
        try {
            netVersion = Integer.parseInt(appInfo.getBuild());
        } catch (Exception e) {
            netVersion = -1;
        }
        if (appVersionCode > netVersion) {
            return;
        }

        DownloadInfo info = new DownloadInfo().setApkUrl(appInfo.getInstallUrl())
                .setFileSize(appInfo.getBinary().getFsize())
                .setProdVersionCode(netVersion)
                .setProdVersionName(appInfo.getVersionShort())
//                .setMd5Check("68919BF998C29DA3F5BD2C0346281AC0")
                .setForceUpdateFlag(0)
                .setUpdateLog(appInfo.getChangelog());
        AppUpdateUtils.getInstance().getUpdateConfig().setUiThemeType(TypeConfig.UI_THEME_L);
        //打开文件MD5校验
//        AppUpdateUtils.getInstance().getUpdateConfig().setNeedFileMD5Check(true);
        //开启或者关闭后台静默下载功能
        AppUpdateUtils.getInstance().getUpdateConfig().setAutoDownloadBackground(false);
        //开启静默下载的时候推荐关闭通知栏进度提示
        AppUpdateUtils.getInstance().getUpdateConfig().setShowNotification(true);
        //因为这里打开了MD5的校验 我在这里添加一个MD5检验监听监听
        AppUpdateUtils.getInstance()
                .addAppUpdateInfoListener(new AppUpdateInfoListener() {
                    @Override
                    public void isLatestVersion(boolean isLatest) {
                        Log.e("HHHHHHHHHHHHHHH", "isLatest:" + isLatest);
                    }
                })
                .addAppDownloadListener(new AppDownloadListener() {
                    @Override
                    public void downloading(int progress) {
                        Log.e("HHHHHHHHHHHHHHH", "progress:" + progress);
                    }

                    @Override
                    public void downloadFail(String msg) {
                        Log.e("HHHHHHHHHHHHHHH", "msg:" + msg);
                    }

                    @Override
                    public void downloadComplete(String path) {
                        Log.e("HHHHHHHHHHHHHHH", "path:" + path);
                    }

                    @Override
                    public void downloadStart() {
                        Log.e("HHHHHHHHHHHHHHH", "start");
                    }

                    @Override
                    public void reDownload() {
                    }

                    @Override
                    public void pause() {
                    }
                })
                .addMd5CheckListener(new MD5CheckListener() {
                    @Override
                    public void fileMd5CheckFail(String originMD5, String localMD5) {
                    }

                    @Override
                    public void fileMd5CheckSuccess() {
                    }
                })
                .checkUpdate(info);
    }


}
