package cn.haotowm.oneimg.web;

/**
 * Created by 皓东 on 2017/4/10.
 */


import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by 皓东 on 2017/4/9.
 */

public class HWebView extends WebView {
    ScrollInterface mt;

    public HWebView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    public HWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public HWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //Log.e("hhah",""+l+" "+t+" "+oldl+" "+oldt);
        mt.onSChanged(l, t, oldl, oldt);
    }

    public void setOnCustomScroolChangeListener(ScrollInterface t){
        this.mt=t;
    }

    /**
     * 定义滑动接口
     * @param
     */
    public interface ScrollInterface {
        public void onSChanged(int l, int t, int oldl, int oldt) ;
    }

}