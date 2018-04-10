package com.example.administrator.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 作者：xiaoHou
 * 时间：2018/4/8 0008
 * 描述：SwitchView 自定义开关
 **/
public class SwitchView extends View{

    private Bitmap imgBg;//背景
    private Bitmap imgSrc;//图片
    private Paint paint;
    private boolean isOpen;
    private float currentX;
    boolean switchState = false;//开关状态
    private OnSwitchUpdateListener onSwitchUpdateListener = null;

    public void setOnSwitchUpdateListener(OnSwitchUpdateListener onSwitchUpdateListener) {
        this.onSwitchUpdateListener = onSwitchUpdateListener;
    }

    public SwitchView(Context context) {
        super(context);
        init();
    }

    private void init() {
        //画笔
        paint = new Paint();
    }

    public SwitchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        //声明命名空间
        String name = "http://schemas.android.com/apk/res/com.example.administrator.customview";
        isOpen = attrs.getAttributeBooleanValue(name,"switch_state",false);
        setSwitchOpen(isOpen);
        int background = attrs.getAttributeResourceValue(name,"switch_background",-1);
        setSwitchBackground(background);
        int src = attrs.getAttributeResourceValue(name,"switch_src",-1);
        setSwitchSrc(src);

    }

    public SwitchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //设置宽度和高度
        setMeasuredDimension(imgBg.getWidth(),imgBg.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制背景
        canvas.drawBitmap(imgBg,0,0,paint);
        //通过开关状态绘制滑块位置
        if (switchState){
            //使用当前位置作为滑块滑动位置控制滑块不划出屏幕
            float newLeft = currentX - imgSrc.getWidth() /2.0f;
            //控制滑块位置
            int maxLeft = imgBg.getWidth() - imgSrc.getWidth();
            if (newLeft < 0){
                newLeft = 0;
            }else if (newLeft > maxLeft){
                newLeft = maxLeft;
            }
            canvas.drawBitmap(imgSrc, newLeft, 0, paint);
        }else {
            if (isOpen) {
                int left = imgBg.getWidth() - imgSrc.getWidth();
                canvas.drawBitmap(imgSrc, left, 0, paint);
            } else {
                canvas.drawBitmap(imgSrc, 0, 0, paint);
            }
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //记录当前位置
                switchState = true;
                currentX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                currentX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                switchState = false;
                currentX = event.getX();
                float center = imgBg.getWidth() / 2.0f;
                //记录变化
                boolean state = currentX > center;
                //设置监听
                if (isOpen != state && onSwitchUpdateListener !=null) {
                    onSwitchUpdateListener.onSwitchUpdateListener(state);
                }
                isOpen = state;

                break;
            default:
                break;
        }
        //重新绘制界面
        invalidate();
        return true;
    }

    /**
     * 设置滑块
     * @param switch_background
     */
    public void setSwitchSrc(int switch_background) {
        imgSrc = BitmapFactory.decodeResource(getResources(), switch_background);

    }

    /**
     * 设置背景
     * @param switch_background
     */
    public void setSwitchBackground(int switch_background) {
        imgBg = BitmapFactory.decodeResource(getResources(),switch_background);
    }

    public void setSwitchOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    /**
     * 更新状态
     */
    public interface OnSwitchUpdateListener{
        void onSwitchUpdateListener(boolean state);
    }
}
