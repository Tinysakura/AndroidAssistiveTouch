package com.example.administrator.androidassistivetouch.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.androidassistivetouch.R;

/**
 * 一个自定义的悬浮的assestiveTouch
 * Created by Mr.chen on 2018/5/25.
 */

public class AssestiveTouchDemo extends View implements GestureDetector.OnGestureListener{
    private GestureDetector mGestrueDetector;

    /**
     * data
     */
    public int measureWidth;
    public int measureHeigt;

    /**
     * configration
     */
    public float originalRadius;
    public float spreadRadius;
    public float floatX;
    public float floatY;
    public float controlRadius;
    public int iconSourth;
    public int iconNorth;
    public int iconWest;
    public int iconEast;
    public int color;
    public Paint paint;

    private AssestiveTouchListener mAssestiveTouchListener;

    @SuppressWarnings("unused")
    public void setAssestiveTouchListener(AssestiveTouchListener assestiveTouchListener){
        this.mAssestiveTouchListener=assestiveTouchListener;
    }

    /**
     * flag
     */
    public int STATUS=STATUS_CLOSED;
    public static final int STATUS_CLOSED=1;
    public static final int STATUS_SPREAD=2;

    public static final int TOUCH_INSIDE_ORIGNAL=3;
    public static final int TOUCH_OUTSIDE_ORIGNAL=4;
    public static final int TOUCH_OUTSIDE_SPREAD=5;
    public static final int TOUCH_SOURTH_SPREAD=6;
    public static final int TOUCH_NORTH_SPREAD=7;
    public static final int TOUCH_WEST_SPREAD=8;
    public static final int TOUCH_EAST_SPREAD=9;

    public AssestiveTouchDemo(Context context) {
        super(context);

        mGestrueDetector=new GestureDetector(getContext(),this);
        //setBackgroundColor(getResources().getColor(R.color.colorAccent));
    }

    public AssestiveTouchDemo(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mGestrueDetector=new GestureDetector(getContext(),this);

        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.andriod_assestive_touch);
        initConfigration(typedArray);
        //setBackgroundColor(getResources().getColor(R.color.colorAccent));
    }

    void initConfigration(TypedArray typedArray){
        this.originalRadius=typedArray.getInt(R.styleable.andriod_assestive_touch_original_radius,0);
        this.spreadRadius=typedArray.getInt(R.styleable.andriod_assestive_touch_spread_radius,0);
        this.color=typedArray.getColor(R.styleable.andriod_assestive_touch_color,0);
        this.iconSourth=typedArray.getResourceId(R.styleable.andriod_assestive_touch_icon_sourth,0);
        this.iconNorth=typedArray.getResourceId(R.styleable.andriod_assestive_touch_icon_north,0);
        this.iconWest=typedArray.getResourceId(R.styleable.andriod_assestive_touch_icon_west,0);
        this.iconEast=typedArray.getResourceId(R.styleable.andriod_assestive_touch_icon_east,0);
        this.floatX=typedArray.getFloat(R.styleable.andriod_assestive_touch_floatX,0);
        this.floatY=typedArray.getFloat(R.styleable.andriod_assestive_touch_floatX,0);
        this.controlRadius=2*spreadRadius/10;
        this.paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
    }

    /**
     * 重写onMeasure使用自定义的radius属性测量控件的大小
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ViewGroup parentView=(ViewGroup)getParent();
        measureWidth = parentView.getWidth();
        measureHeigt = parentView.getHeight();

        Log.v("measureWidth",measureWidth+"");
        Log.v("measureHeight",measureHeigt+"");

        switch (STATUS){
            case STATUS_CLOSED:
                setMeasuredDimension((int)originalRadius*2,(int)originalRadius*2);
                break;
            case STATUS_SPREAD:
                setMeasuredDimension((int)spreadRadius*2,(int)spreadRadius*2);
                break;
        }
    }

    @Override
    public void onDraw(Canvas canvas){
        Log.v("view_draw","draw");

        switch (STATUS){
            case STATUS_CLOSED:
                drawClosed(canvas,paint);
                break;
            case STATUS_SPREAD:
                drawSpread(canvas,paint);
                break;
        }
    }

    public void drawClosed(Canvas canvas,Paint paint){
        /*
        画一个矩形
         */
        RectF rectF=new RectF();
        rectF.left=0;
        rectF.top=0;
        rectF.right=2*originalRadius;
        rectF.bottom=2*originalRadius;
        canvas.drawRoundRect(rectF,10,10,paint);

        /*
         * 在矩形中画一个同心圆
         */
        paint.setColor(getResources().getColor(R.color.colorWhite));
        canvas.drawCircle(originalRadius,originalRadius,(float)(originalRadius*0.8),paint);
        paint.setColor(getResources().getColor(R.color.colorGray));
    }

    public void drawSpread(Canvas canvas,Paint paint){
        /*
         * 画一个矩形
         */
        RectF rectF=new RectF();
        rectF.left=0;
        rectF.top=0;
        rectF.right=2*spreadRadius;
        rectF.bottom=2*spreadRadius;
        canvas.drawRoundRect(rectF,10,10,paint);

        /*
         * 在矩形的东南西北四个方位画四个控件
         * 控件的宽高固定为spread状态下view的1/5
         */
        Bitmap bitmapSourth= BitmapFactory.decodeResource(getResources(),iconSourth);
        canvas.drawBitmap(Utils.setImgSize(bitmapSourth,(int)controlRadius*2,(int)controlRadius*2),controlRadius*4,20,paint);

        Bitmap bitmapNorth= BitmapFactory.decodeResource(getResources(),iconNorth);
        canvas.drawBitmap(Utils.setImgSize(bitmapNorth,(int)controlRadius*2,(int)controlRadius*2),controlRadius*4,spreadRadius*2-20-2*controlRadius,paint);

        Bitmap bitmapWest= BitmapFactory.decodeResource(getResources(),iconWest);
        canvas.drawBitmap(Utils.setImgSize(bitmapWest,(int)controlRadius*2,(int)controlRadius*2),20,controlRadius*4,paint);

        Bitmap bitmapEast= BitmapFactory.decodeResource(getResources(),iconEast);
        canvas.drawBitmap(Utils.setImgSize(bitmapEast,(int)controlRadius*2,(int)controlRadius*2),spreadRadius*2-20-2*controlRadius,controlRadius*4,paint);
    }

    /**
     * 判断手势的触发位置
     */
    int judgeGestureDetector(MotionEvent event){
        int position=0;

        switch (STATUS){
            case STATUS_CLOSED:
                /*
                 判断事件是否发生在view内
                 */
                RectF rect = Utils.calcViewScreenLocation(this);
                boolean isInViewRect = rect.contains(event.getRawX(),event.getRawY());
                Log.v("isInViewRect",isInViewRect+"");
                if(isInViewRect){
                    position=TOUCH_INSIDE_ORIGNAL;
                }else{
                    position=TOUCH_OUTSIDE_ORIGNAL;
                }

                break;

            case STATUS_SPREAD:
                /*
                 判断事件是否发生在view内
                 */
                RectF rect2 = Utils.calcViewScreenLocation(this);
                boolean isInViewRect2 = rect2.contains(event.getRawX(),event.getRawY());
                if(isInViewRect2){
                    //如果事件发生在view内则继续判断具体方位
                    if(event.getY()>20 && event.getY()<20+controlRadius*2 &&
                            event.getX()>4*controlRadius && event.getX()<6*controlRadius){
                        position=TOUCH_SOURTH_SPREAD;
                    }
                    if(event.getY()>spreadRadius*2-20-2*controlRadius && event.getY()<spreadRadius*2-20 &&
                            event.getX()>4*controlRadius && event.getX()<6*controlRadius){
                        position=TOUCH_NORTH_SPREAD;
                    }
                    if(event.getY()>4*controlRadius && event.getY()<6*controlRadius &&
                            event.getX()>20 && event.getX()<20+4*controlRadius){
                        position=TOUCH_WEST_SPREAD;
                    }
                    if(event.getY()>4*controlRadius && event.getY()<6*controlRadius
                            && event.getX()>spreadRadius*2-20-controlRadius*2 && event.getX()<spreadRadius*2-20){
                        position=TOUCH_EAST_SPREAD;
                    }
                }else{
                    position=TOUCH_OUTSIDE_SPREAD;
                }

                break;
        }

        return position;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        return mGestrueDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.v("GestureDetector","onDown");
        return false;
    }

    /**
     * onShoePress中的逻辑仅做模拟器测试用
     */
    @Override
    public void onShowPress(MotionEvent e) {
        Log.v("GestureDetector","onShowPress");

        switch(judgeGestureDetector(e)){
            case TOUCH_INSIDE_ORIGNAL:
                Log.v("eventType","TOUCH_INSIDE_ORIGINAL");
                STATUS=STATUS_SPREAD;
                requestLayout();
                invalidate();
                break;
            case TOUCH_OUTSIDE_SPREAD:
                Log.v("eventType","TOUCH_OUTSIDE_SPREAD");
                STATUS=STATUS_CLOSED;
                requestLayout();
                invalidate();
                break;
            case TOUCH_SOURTH_SPREAD:
                Log.v("eventType","TOUCH_SOURTH_SPREAD");
                mAssestiveTouchListener.onSourthWigetClick();
                break;
            case TOUCH_NORTH_SPREAD:
                Log.v("eventType","TOUCH_NORTH_SPREAD");
                mAssestiveTouchListener.onNorthWigetClick();
                break;
            case TOUCH_WEST_SPREAD:
                Log.v("eventType","TOUCH_WEST_SPREAD");
                mAssestiveTouchListener.onWestWigetClick();
                break;
            case TOUCH_EAST_SPREAD:
                Log.v("eventType","TOUCH_EAST_SPREAD");
                mAssestiveTouchListener.onEastWigetClick();
                break;
        }
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.v("GestureDetector","onSingleTapUp");
        switch(judgeGestureDetector(e)){
            case TOUCH_INSIDE_ORIGNAL:
                Log.v("eventType","TOUCH_INSIDE_ORIGINAL");
                STATUS=STATUS_SPREAD;
                requestLayout();
                invalidate();
                break;
            case TOUCH_OUTSIDE_SPREAD:
                Log.v("eventType","TOUCH_OUTSIDE_SPREAD");
                STATUS=STATUS_CLOSED;
                requestLayout();
                invalidate();
                break;
            case TOUCH_SOURTH_SPREAD:
                Log.v("eventType","TOUCH_SOURTH_SPREAD");
                mAssestiveTouchListener.onSourthWigetClick();
                break;
            case TOUCH_NORTH_SPREAD:
                Log.v("eventType","TOUCH_NORTH_SPREAD");
                mAssestiveTouchListener.onNorthWigetClick();
                break;
            case TOUCH_WEST_SPREAD:
                Log.v("eventType","TOUCH_WEST_SPREAD");
                mAssestiveTouchListener.onWestWigetClick();
                break;
            case TOUCH_EAST_SPREAD:
                Log.v("eventType","TOUCH_EAST_SPREAD");
                mAssestiveTouchListener.onEastWigetClick();
                break;
        }

        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.v("GestureDetector","onScroll");
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.v("GestureDetector","onLongPress");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.v("GestureDetector","onFling");
        return false;
    }
}
