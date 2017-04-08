package cn.haotowm.oneimg.oneimg;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 皓东 on 2017/4/9.
 */

public class GetData {
    // 定义一个获取网络图片数据的方法:
    public static byte[] getImage(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // 设置连接超时为5秒
        conn.setConnectTimeout(5000);
        // 设置请求类型为Get类型
        conn.setRequestMethod("GET");
        // 判断请求Url是否成功
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("请求url失败");
        }
        InputStream inStream = conn.getInputStream();
        byte[] bt = StreamTool.read(inStream);
        inStream.close();
        return bt;
    }

    // 获取网页的html源代码
    public static String getcode(String path) throws Exception {
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {
            InputStream in = conn.getInputStream();
            byte[] data = StreamTool.read(in);
            String html = new String(data, "UTF-8");
            return html;
        }
        return null;
    }
}