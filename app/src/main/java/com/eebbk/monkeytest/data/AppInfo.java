package com.eebbk.monkeytest.data;

import java.io.Serializable;

/**
 * 作者：lj
 * 实现的主要功能：
 * 创建日期：17-4-6
 * 修改信息：
 */
public class AppInfo implements Serializable {

    private static final long serialVersionUID = -6919461967497580385L;

    private String appName;
    private String appPYName; //App名字的拼音
    private String packageName;
    private String versionName;
    private int versionCode;
//    private Drawable appIcon;
//    private byte []appIconBytes;
    private boolean isSystemApp;
    private String buildId;
    private String launchClass;

//    public byte[] getAppIconBytes() {
//        return appIconBytes;
//    }
//
//    public void setAppIconBytes(byte[] appIconBytes) {
//        this.appIconBytes = appIconBytes;
//    }

    public String getAppPYName() {
        return appPYName;
    }

    public void setAppPYName(String appPYName) {
        this.appPYName = appPYName;
    }

    public String getLaunchClass() {
        return launchClass;
    }

    public void setLaunchClass(String launchClass) {
        this.launchClass = launchClass;
    }

    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

    public boolean isSystemApp() {
        return isSystemApp;
    }

    public void setSystemApp(boolean systemApp) {
        isSystemApp = systemApp;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

//    public Drawable getAppIcon() {
////        return appIcon;
//        return new BitmapDrawable(Bytes2Bimap(appIconBytes));
//    }

//    public void setAppIcon(Drawable appIcon) {
////        this.appIcon = appIcon;
////        setAppIconBytes(getBytes(drawableToBitmap(appIcon)));
//    }

//    public static Bitmap drawableToBitmap(Drawable drawable) {
//
//        int w = drawable.getIntrinsicWidth();
//        int h = drawable.getIntrinsicHeight();
//        System.out.println("Drawable转Bitmap");
//        Bitmap.Config config =
//                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
//                        : Bitmap.Config.RGB_565;
//        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
//        //注意，下面三行代码要用到，否则在View或者SurfaceView里的canvas.drawBitmap会看不到图
//        Canvas canvas = new Canvas(bitmap);
//        drawable.setBounds(0, 0, w, h);
//        drawable.draw(canvas);
//
//        return bitmap;
//    }

//    public static byte[] getBytes(Bitmap bitmap){
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//
//        return baos.toByteArray();
//
//    }
//
//    public static Bitmap Bytes2Bimap(byte[] b) {
//
//        if (b.length != 0) {
//            return BitmapFactory.decodeByteArray(b, 0, b.length);
//        } else {
//            return null;
//        }
//
//    }

}
