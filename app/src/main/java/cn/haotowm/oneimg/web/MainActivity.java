package cn.haotowm.oneimg.web;


import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import android.view.KeyEvent;
import android.webkit.CookieManager;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    private GoogleApiClient client;
    private ProgressDialog mSaveDialog = null;
    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final HWebView myWebView = (HWebView) findViewById(R.id.myWebView);
        WebSettings settings = myWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        myWebView.addJavascriptInterface(new JsInteration(), "control");
//        CookieManager cookieManager = CookieManager.getInstance();
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//        {
//            cookieManager.setAcceptThirdPartyCookies(myWebView,true);
//        } else {
//            cookieManager.setAcceptCookie(true);
//        }
        myWebView.setWebChromeClient(new WebChromeClient() {
        });
        myWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

        });
        myWebView.loadUrl("file:///android_asset/apk.html");
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        final HWebView mWebView=(HWebView) findViewById(R.id.myWebView);
        mWebView.setOnCustomScroolChangeListener( new HWebView.ScrollInterface() {
            @Override
            public void onSChanged(int l, int t, int oldl, int oldt) {
                // TODO Auto-generated method stub
                 float  webviewHight = mWebView.getContentHeight()*mWebView.getScale();
                //为解决4.4的系统无法获取正确的高度加一个“<10”的
                if((int)webviewHight - (mWebView.getHeight() + mWebView.getScrollY()) == 0){
                    String  call = "javascript:load(5)";
                    mWebView.loadUrl(call);
                }
                //已经处于顶端
               if (mWebView.getScrollY()<-100) {
                   myWebView.loadUrl("file:///android_asset/apk.html");
               }
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        final HWebView myWebView = (HWebView) findViewById(R.id.myWebView);
        if (keyCode == KeyEvent.KEYCODE_BACK && myWebView.canGoBack()) {
            myWebView.goBack();// 返回前一个页面
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
        public void geturl(String url,String id) {
            Oneimgset st1 = new Oneimgset(url,id);
            st1.start();
        }
    }



    protected File downLoadFile(String httpUrl,String fileName) {
        // TODO Auto-generated method stub
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
                mWebView.loadUrl("javascript:fullhiden(5)");
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
            Log.d("ddd","ddd"+a+b);
            downLoadFile(a,b+".jpg");
        }
    }

    public void setwallpaper(File file){
        Uri uri= getImageContentUri(MainActivity.this,file);
        WallpaperManager wallpaperManager =WallpaperManager.getInstance(MainActivity.this);
        Intent intent = new Intent(wallpaperManager.getCropAndSetWallpaperIntent(uri));
        startActivity(intent);
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

}
