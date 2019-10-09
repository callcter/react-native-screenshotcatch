package com.dreamser.screenshotcatch;

import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.ReactMethod;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.view.View.NO_ID;

public class RNScreenshotcatchModule extends ReactContextBaseJavaModule {

    private static final String TAG = "RNScreenshotcatch";
    private static final String NAVIGATION= "navigationBarBackground";
    private static final String[] KEYWORDS = {
            "screenshot", "screen_shot", "screen-shot", "screen shot",
            "screencapture", "screen_capture", "screen-capture", "screen capture",
            "screencap", "screen_cap", "screen-cap", "screen cap", "截屏", "截图"
    };
    private static Activity ma;
    private ReactContext reactContext;

    /** 读取媒体数据库时需要读取的列 */
    private static final String[] MEDIA_PROJECTIONS =  {
        MediaStore.Images.ImageColumns.TITLE,
        MediaStore.Images.ImageColumns.DATE_TAKEN,
    };
    /** 内部存储器内容观察者 */
    private ContentObserver mInternalObserver;
    /** 外部存储器内容观察者 */
    private ContentObserver mExternalObserver;
    private HandlerThread mHandlerThread;
    private Handler mHandler;

    public RNScreenshotcatchModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNScreenshotcatch";
    }

    public static void initScreenShotShareSDK(Activity activity){
        ma = activity;
    }

    @ReactMethod
    public void startListener(){
        mHandlerThread = new HandlerThread("Screenshot_Observer");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

        // 初始化
        mInternalObserver = new MediaContentObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI, mHandler);
        mExternalObserver = new MediaContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mHandler);

        // 添加监听
        this.reactContext.getContentResolver().registerContentObserver(
                MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                false,
                mInternalObserver
        );
        this.reactContext.getContentResolver().registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                false,
                mExternalObserver
        );
    }

    @ReactMethod
    public void stopListener(){
        this.reactContext.getContentResolver().unregisterContentObserver(mInternalObserver);
        this.reactContext.getContentResolver().unregisterContentObserver(mExternalObserver);
    }

    @ReactMethod
    public void hasNavigationBar(Promise promise){
        boolean navigationBarExisted =  isNavigationBarExist(ma);
        promise.resolve(navigationBarExisted);
    }

    private void handleMediaContentChange(Uri contentUri) {
        Cursor cursor = null;
        try {
            // 数据改变时查询数据库中最后加入的一条数据
            cursor = this.reactContext.getContentResolver().query(
                    contentUri,
                    MEDIA_PROJECTIONS,
                    null,
                    null,
                    MediaStore.Images.ImageColumns.DATE_ADDED + " desc limit 1"
            );

            if (cursor == null) {
                return;
            }
            if (!cursor.moveToFirst()) {
                return;
            }

            // 获取各列的索引
            int dataIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.TITLE);
            int dateTakenIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);

            // 获取行数据
            String data = cursor.getString(dataIndex);
            long dateTaken = cursor.getLong(dateTakenIndex);

            // 处理获取到的第一行数据
            handleMediaRowData(data, dateTaken);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
            WritableMap map = Arguments.createMap();
            map.putInt("code", 500);
            sendEvent(this.reactContext, "Screenshotcatch", map);
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    /**
     * 处理监听到的资源
     */
    private void handleMediaRowData(String data, long dateTaken) {
        if (checkScreenShot(data, dateTaken)) {
            Log.d(TAG, data + " " + dateTaken);
            saveBitmap(normalShot(ma));
        } else {
            Log.d(TAG, "Not screenshot event");
            WritableMap map = Arguments.createMap();
            map.putInt("code", 500);
            sendEvent(this.reactContext, "Screenshotcatch", map);
        }
    }

    /**
     * 判断是否是截屏
     */
    private boolean checkScreenShot(String data, long dateTaken) {
        data = data.toLowerCase();
        // 判断图片路径是否含有指定的关键字之一, 如果有, 则认为当前截屏了
        for (String keyWork : KEYWORDS) {
            if (data.contains(keyWork)) {
                return true;
            }
        }
        return false;
    }

    private class MediaContentObserver extends ContentObserver {

        private Uri mContentUri;

        public MediaContentObserver(Uri contentUri, Handler handler) {
            super(handler);
            mContentUri = contentUri;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.d(TAG, mContentUri.toString());
            handleMediaContentChange(mContentUri);
        }

    }

    public void sendEvent(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    // 判断全面屏虚拟导航栏是否存在
    public static  boolean isNavigationBarExist(Activity activity){
        ViewGroup vp = (ViewGroup) activity.getWindow().getDecorView();
        if (vp != null) {
            for (int i = 0; i < vp.getChildCount(); i++) {
                vp.getChildAt(i).getContext().getPackageName();
                if (vp.getChildAt(i).getId()!= NO_ID && NAVIGATION.equals(activity.getResources().getResourceEntryName(vp.getChildAt(i).getId()))) {
                    return true;
                }
            }
        }
        return false;
    }

    // 当前APP内容截图
    private static Bitmap normalShot(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setDrawingCacheEnabled(true);
        decorView.buildDrawingCache();

        Rect outRect = new Rect();
        decorView.getWindowVisibleDisplayFrame(outRect);
        int statusBarHeight = outRect.top;//状态栏高度

        Bitmap bitmap = Bitmap.createBitmap(decorView.getDrawingCache(),
                0, statusBarHeight,
                decorView.getMeasuredWidth(), decorView.getMeasuredHeight() - statusBarHeight);

        decorView.setDrawingCacheEnabled(false);
        decorView.destroyDrawingCache();
        return bitmap;
    }

    // 获取当前APP图片存储路径
    private String getSystemFilePath() {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = reactContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        } else {
            cachePath = reactContext.getFilesDir().getAbsolutePath();
        }
        return cachePath;
    }

    // 保存截屏的bitmap为图片文件并返回路径
    private void saveBitmap(Bitmap bitmap){
        Long time = System.currentTimeMillis();
        String path = getSystemFilePath() + "/screen-shot-catch" + time + ".png";
        Log.d(TAG, path);
        File filePic;
        WritableMap map = Arguments.createMap();
        try{
            filePic = new File(path);
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            map.putInt("code", 200);
            map.putString("uri", filePic.getAbsolutePath());
            sendEvent(this.reactContext, "Screenshotcatch", map);
            // 强制关闭软键盘
            try{
                ((InputMethodManager) ma.getSystemService(reactContext.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(ma.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }catch(java.lang.NullPointerException e){
                Log.d(TAG, e.toString());
            }
        }catch(IOException e){
            Log.d(TAG, e.toString());
            e.printStackTrace();
            map.putInt("code", 500);
            sendEvent(this.reactContext, "Screenshotcatch", map);
        }
    }
}