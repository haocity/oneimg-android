package cn.haotowm.oneimg.oneimg;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "IcsTestActivity";
    private final static String ALBUM_PATH= Environment.getExternalStorageDirectory() + "/oneimg/";
    private ImageView mImageView;
    private Button mBtnSave;
    private Button mBtnGet;
    private ProgressDialog mSaveDialog = null;
    private Bitmap mBitmap;
    private String mFileName;
    private String mSaveMessage;
    private int oneimgid;
    private WebView webView ;
    private String detail = "";
    private int Oneimgnumber=4300;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView)findViewById(R.id.img);
        mBtnGet = (Button)findViewById(R.id.btn);
        mBtnSave = (Button)findViewById(R.id.btn2);
        oneimgid=(int) (Math.random() * 4359);
        new Thread(connectNet).start();
        //获取图片数量
        new Thread() {
            public void run() {
                try {
                    detail = GetData.getcode("http://oneimg.haotown.cn/data/");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0x002);
            };
        }.start();
        // 下载图片
        mBtnSave.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                mSaveDialog = ProgressDialog.show(MainActivity.this, "一图壁纸", "获取高清壁纸中...", true);
                new Thread(saveFileRunnable).start();
            }
        });
        mBtnGet.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                oneimgid=(int) (Math.random() * Oneimgnumber);
                new Thread(connectNet).start();
            }
        });
    }
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Pattern pattern = Pattern.compile("[^\\d]");
            Matcher matcher = pattern.matcher(detail);
            Oneimgnumber=Integer.parseInt(matcher.replaceAll(""));
        }
    };
    /**
     * Get image from newwork
     * @param path The path of image
     * @return byte[]
     * @throws Exception
     */
    public byte[] getImage(String path) throws Exception{
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        InputStream inStream = conn.getInputStream();
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
            return readStream(inStream);
        }
        return null;
    }

    /**
     * Get image from newwork
     * @param path The path of image
     * @return InputStream
     * @throws Exception
     */
    public InputStream getImageStream(String path) throws Exception{
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
            return conn.getInputStream();
        }
        return null;
    }
    /**
     * Get data from stream
     * @param inStream
     * @return byte[]
     * @throws Exception
     */
    public static byte[] readStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len=inStream.read(buffer)) != -1){
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

    /**
     * 保存文件
     * @param bm
     * @param fileName
     * @throws IOException
     */
    public void saveFile(Bitmap bm, String fileName) throws IOException {
        File dirFile = new File(ALBUM_PATH);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }
        File myCaptureFile = new File(ALBUM_PATH + fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
    }

    private Runnable saveFileRunnable = new Runnable(){
        @Override
        public void run() {
            try {
                saveFile(mBitmap, mFileName);
                File d;
                d=new File (Environment.getExternalStorageDirectory() + "/oneimg/"+oneimgid+".jpg");
                Uri uri= getImageContentUri(MainActivity.this,d);
                mSaveMessage = "图片设置成功！";
                Log.d("uri:",""+uri);
                WallpaperManager wallpaperManager =WallpaperManager.getInstance(MainActivity.this);
                Intent intent = new Intent(wallpaperManager.getCropAndSetWallpaperIntent(uri));
                startActivity(intent);
            } catch (IOException e) {
                mSaveMessage = "图片保存失败！";
                Toast.makeText(MainActivity.this, mSaveMessage, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            messageHandler.sendMessage(messageHandler.obtainMessage());
        }

    };

    private Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mSaveDialog.dismiss();
            Log.d(TAG, mSaveMessage);
            //Toast.makeText(MainActivity.this, mSaveMessage, Toast.LENGTH_SHORT).show();
        }
    };

    /*
     * 连接网络
     * 由于在4.0中不允许在主线程中访问网络，所以需要在子线程中访问
     */
    private Runnable connectNet = new Runnable(){
        @Override
        public void run() {

            try {

                String filePath = "http://t4.haotown.cn/img/bj@"+oneimgid+".jpg";
                mFileName = oneimgid+".jpg";
               mBitmap = BitmapFactory.decodeStream(getImageStream(filePath));
                //********************************************************************/
                // 发送消息，通知handler在主线程中更新UI
                connectHanlder.sendEmptyMessage(0);
                Log.d(TAG, "set image ...");

            } catch (Exception e) {
               Toast.makeText(MainActivity.this,"无法链接网络！", Toast.LENGTH_SHORT).show();
                 e.printStackTrace();
            }

        }

    };

    private Handler connectHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "display image");

            // 更新UI，显示图片
            mImageView.setImageBitmap(mBitmap);// display image

        }
    };
    /**
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

