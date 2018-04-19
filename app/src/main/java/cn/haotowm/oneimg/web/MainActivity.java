package cn.haotowm.oneimg.web;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    // 要申请的权限
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private AlertDialog dialog;

    private GoogleApiClient client;
    private ProgressDialog mSaveDialog = null;
    private int canback=0;

    private String  indexurl="file:///android_asset/index.html";
    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);




        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                showDialogTipUserRequestPermission();
            }
        }

        final HWebView mWebView=(HWebView) findViewById(R.id.myWebView);
        //String ua = "Mozilla/5.0 (ONEIMG) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36";
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        //settings.setUserAgentString(ua);
        settings.setAllowFileAccess(true);
        settings.setDatabaseEnabled(true);
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        settings.setDatabasePath(dir);
        settings.setDomStorageEnabled(true);
        settings.setGeolocationEnabled(true);
        mWebView.addJavascriptInterface(new JsInteration(), "control");
//        CookieManager cookieManager = CookieManager.getInstance();
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//        {
//            cookieManager.setAcceptThirdPartyCookies(myWebView,true);
//        } else {
//            cookieManager.setAcceptCookie(true);
//        }
        mWebView.setWebChromeClient(new WebChromeClient() {
        });
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

        });
        mWebView.loadUrl(indexurl);



        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        mWebView.setOnCustomScroolChangeListener( new HWebView.ScrollInterface() {
            @Override
            public void onSChanged(int l, int t, int oldl, int oldt) {
                // TODO Auto-generated method stub
//                 float  webviewHight = mWebView.getContentHeight()*mWebView.getScale();
//                //为解决4.4的系统无法获取正确的高度加一个“<10”的
//                if((int)webviewHight - (mWebView.getHeight() + mWebView.getScrollY()) == 0){
//                    String  call = "javascript:o.load(5)";
//                    mWebView.loadUrl(call);
//                }
//                //已经处于顶端
//               if (mWebView.getScrollY()<-250) {
//                   mWebView.loadUrl(indexurl);
//               }
            }
        });

    }



    //修改模式
    private void changermode(String i){
        SharedPreferences mySharedPreferences= getSharedPreferences("test",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("mode", i);
        editor.commit();
        Toast.makeText(this, "模式切换至模式" +i, Toast.LENGTH_LONG).show();
    }

    //获取模式
    public String getmode(){
        SharedPreferences mySharedPreferences= getSharedPreferences("test",
                Activity.MODE_PRIVATE);
        String str =mySharedPreferences.getString("mode", "1");
        Log.d("msg", "getmode: "+str);


        return str;
    }

    // 提示用户该请求权限的弹出框
    private void showDialogTipUserRequestPermission() {

        new AlertDialog.Builder(this)
                .setTitle("存储权限不可用")
                .setMessage("由于一图壁纸需要获取存储空间，为你存储壁纸信息；\n否则，您将无法正常使用一图壁纸")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startRequestPermission();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    // 开始提交请求权限
    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 321);
    }

    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    } else
                        finish();
                } else {
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // 提示用户去应用设置界面手动开启权限

    private void showDialogTipUserGoToAppSettting() {

        dialog = new AlertDialog.Builder(this)
                .setTitle("存储权限不可用")
                .setMessage("请在-应用设置-权限-中，允许一图使用存储权限来保存用户数据")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        startActivityForResult(intent, 123);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 检查该权限是否已经获取
                int i = ContextCompat.checkSelfPermission(this, permissions[0]);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (i != PackageManager.PERMISSION_GRANTED) {
                    // 提示用户应该去应用设置界面手动开启权限
                    showDialogTipUserGoToAppSettting();
                } else {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
        //截图回调

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Log.d("msg", "裁剪完成");
            final Uri resultUri = UCrop.getOutput(data);
            String f= new File(getCacheDir(), "wallpage.png").getPath();
            BitmapDrawable source = new BitmapDrawable(f);
            try {
                Log.d("msg", "开始设置壁纸");
                WallpaperManager wallpaperManager =WallpaperManager.getInstance(this);
                wallpaperManager.setBitmap(source.getBitmap());
                Log.d("msg", "壁纸设置完成");

            }catch (IOException k){
                k.printStackTrace();
            }

        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }else{
            Log.d("msg", "无用回调");
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        final HWebView mWebView = (HWebView) findViewById(R.id.myWebView);
        if (keyCode == KeyEvent.KEYCODE_BACK && canback==1) {
            String  call = "javascript:app.showright=false";
            mWebView.loadUrl(call);
            canback=0;
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_BACK && canback==2){
            mWebView.loadUrl(indexurl);
            canback=0;
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public class JsInteration {

        @JavascriptInterface
        public void toastMessage(String message) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }

        @JavascriptInterface
        public void startoneimg(String url,String id) {
            Oneimgset st1 = new Oneimgset(url,id);
            st1.start();
        }
        @JavascriptInterface
        public void setback(int i) {
            canback=i;
            Log.d("test","i"+i);
        }
        @JavascriptInterface
        public void jschangermode(String i) {
            changermode(i);
        }
        @JavascriptInterface
        public void openurl(String i) {
            Log.d("test","i"+i);
            Uri  uri = Uri.parse(i);
            Intent  intent = new  Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

    }



    protected File downLoadFile(String httpUrl,String fileName) {
        // TODO Auto-generated method stub
        //String ff=this.getExternalCacheDir().getAbsolutePath();
        //Log.i("xx", "downLoadFile: "+this.getExternalCacheDir().getAbsolutePath());
        String ff=Environment.getExternalStorageDirectory()+ "/oneimg";
        File tmpFile = new File(ff);
        if (!tmpFile.exists()) {
            tmpFile.mkdir();
        }
        final File file = new File(ff+"/" + fileName);

        try {
            URL url = new URL(httpUrl);
            try {
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buf = new byte[256];
                conn.connect();
                double count = 0;
                if (conn.getResponseCode() >= 400) {
                    Toast.makeText(MainActivity.this, "连接超时", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    while (count <= 100) {
                        if (is != null) {
                            int numRead = is.read(buf);
                            if (numRead <= 0) {
                                break;
                            } else {
                                fos.write(buf, 0, numRead);
                            }

                        } else {
                            break;
                        }

                    }
                }

                conn.disconnect();
                fos.close();
                is.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block

                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
        }
        final HWebView mWebView=(HWebView) findViewById(R.id.myWebView);
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                mWebView.loadUrl("javascript:app.full.show=false");
            }
        });
        setwallpaper(new File(ff+"/" + fileName));
        return file;
    }
    //多线程
    public class Oneimgset extends Thread {
        String a,b;
        public Oneimgset(String a, String b) {
            super();
            this.a = a;
            this.b = b;
        }
        @Override
        public void run() {
            if(fileIsExists(Environment.getExternalStorageDirectory()+ "/oneimg"+"/" +b+".jpg")){
                Log.d("msg","文件已经存在"+b);
                final HWebView mWebView=(HWebView) findViewById(R.id.myWebView);
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        mWebView.loadUrl("javascript:app.full.show=false");
                    }
                });
                setwallpaper(new File(Environment.getExternalStorageDirectory()+ "/oneimg"+"/" +b+".jpg"));
            }else{
                Log.d("msg","下载"+a+b);
                downLoadFile(a,b+".jpg");
            }

        }
    }

    public void setwallpaper(File file){
        Uri uri= getImageContentUri(MainActivity.this,file);
        //获取模式
        int mode= Integer.parseInt(getmode());

        Log.d("mode", "setwallpaper: "+mode);
         if(mode==2){
             //裁剪后保存到文件中
             Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "wallpage.png"));
             //初始化，第一个参数：需要裁剪的图片；第二个参数：裁剪后图片
             UCrop uCrop = UCrop.of(uri, destinationUri);
             //初始化UCrop配置
             UCrop.Options options = new UCrop.Options();
             //设置裁剪图片可操作的手势
             //options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
             //是否隐藏底部容器，默认显示
             //options.setHideBottomControls(true);
             //设置toolbar颜色
             options.setToolbarColor(ActivityCompat.getColor(this, R.color.colorPrimary));
             //设置状态栏颜色
             options.setStatusBarColor(ActivityCompat.getColor(this, R.color.colorPrimaryDark));
             //是否能调整裁剪框
             options.setFreeStyleCropEnabled(true);
             options.setCompressionFormat(Bitmap.CompressFormat.PNG);
             //UCrop配置
             uCrop.withOptions(options);
             //设置裁剪图片的宽高比，比如16：9
             //uCrop.withAspectRatio(aspectRatioX, aspectRatioY);
             //uCrop.withMaxResultSize(900,900);
             //uCrop.useSourceImageAspectRatio();
             //跳转裁剪页面
             uCrop.start(this);
        }else if (mode==3){
             WallpaperManager wallpaperManager =WallpaperManager.getInstance(MainActivity.this);
             String path = file.getPath();
             BitmapDrawable source = new BitmapDrawable(path);
             try {
                 wallpaperManager.setBitmap(source.getBitmap());
                 Toast.makeText(getApplicationContext(), "设置完成", Toast.LENGTH_SHORT).show();
            }catch (IOException k){
                 Looper.prepare();
                 Toast.makeText(getApplicationContext(), "出现错误了..请尝试更改模式", Toast.LENGTH_SHORT).show();
                 Looper.loop();
                k.printStackTrace();
            }
        }else{
             WallpaperManager wallpaperManager =WallpaperManager.getInstance(MainActivity.this);
              try{
                Intent intent = new Intent(wallpaperManager.getCropAndSetWallpaperIntent(uri));
                startActivity(intent);
             }catch (Exception e){
                  Looper.prepare();
                  changermode("2");
                  setwallpaper(file);
                  Looper.loop();
              }

         }


    }
    /**URI转换
     * Gets the content:// URI  from the given corresponding path to a file
     * @param context
     * @param imageFile
     * @return content Uri
     */
    public static Uri getImageContentUri(Context context, java.io.File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
    //判断文件是否存在
    public boolean fileIsExists(String strFile)
    {
        try
        {
            File f=new File(strFile);
            if(!f.exists())
            {
                return false;
            }

        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }

}
