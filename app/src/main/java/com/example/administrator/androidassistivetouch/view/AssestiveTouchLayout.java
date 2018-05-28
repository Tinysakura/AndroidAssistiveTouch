package com.example.administrator.androidassistivetouch.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import java.util.HashMap;
import java.util.Map;

import static com.example.administrator.androidassistivetouch.view.AssestiveTouchDemo.STATUS_CLOSED;
import static com.example.administrator.androidassistivetouch.view.AssestiveTouchDemo.STATUS_SPREAD;
import static com.example.administrator.androidassistivetouch.view.AssestiveTouchDemo.TOUCH_OUTSIDE_ORIGNAL;

/**
 * 盛放AssestiveTouch的frameLayout
 * Created by Mr.chen on 2018/5/25.
 */
public class AssestiveTouchLayout extends FrameLayout implements GestureDetector.OnGestureListener{
    private AssestiveTouchDemo mAssestiveTouch;
    private GestureDetector mGestrueDetector;

    /**
     * flag
     * 用于指示使用者当前的触控状态
     */
    private int eventType=NONE;
    private static final int NONE=0;
    private static final int DRAG=1;
    private static final int ZOOM=2;

    /**
     * data
     */
    private float oldDistance;

    public AssestiveTouchLayout(@NonNull Context context) {
        super(context);

        init();
    }

    public AssestiveTouchLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public void init(){
        mGestrueDetector=new GestureDetector(getContext(),this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        float newDistance;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            //第一手指按下时触发
            case MotionEvent.ACTION_DOWN:
                eventType = DRAG;
                break;
            //当屏幕上已经有触点处于按下的状态的时候，再有新的触点被按下时触发。
            case MotionEvent.ACTION_POINTER_DOWN:
                eventType = ZOOM;
                oldDistance = distance(event);
                break;
            //当触点在屏幕上移动时触发
            case MotionEvent.ACTION_MOVE:
                /*
                如果是双指触控且AssestiveTouch处于spread状态则对控制面板进行缩放
                 */
                if (mAssestiveTouch.STATUS== STATUS_SPREAD && eventType == ZOOM) {
                    newDistance = distance(event);
                    float scale = newDistance / oldDistance;

                    /*
                     *使用一个属性动画进行平缓的过渡
                     */
                    scaleAnimation(scale);
                }
                break;
            //当屏幕上有多个点被按住，松开其中一个点时触发（即非最后一个点被放开时）触发。
            case MotionEvent.ACTION_POINTER_UP:
                if(event.getPointerCount()>2){
                    oldDistance=distance(event);
                    eventType=ZOOM;
                }else{
                    eventType = NONE;
                }
                break;
            //当屏幕上有点被松开时触发
            case MotionEvent.ACTION_UP:
                break;
        }
        return mGestrueDetector.onTouchEvent(event);
    }

    /**
     * 计算Zoom状态下两个触控点之间的距离
     */
    float distance(MotionEvent e){
        return (float)Math.pow(Math.pow(e.getX(0)-e.getX(1),2)+Math.pow(e.getY(0)-e.getY(1),2),0.5);
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom){
        super.onLayout(changed, left,top,right,bottom);
        if(mAssestiveTouch==null){
            mAssestiveTouch=(AssestiveTouchDemo)getChildAt(0);
        }
        Log.v("mAssestiveTouch",mAssestiveTouch+"");

        switch(mAssestiveTouch.STATUS){
            case STATUS_CLOSED:
                Log.v("floatX",mAssestiveTouch.floatX+"");
                Log.v("floatY",mAssestiveTouch.floatY+"");
                mAssestiveTouch.layout((int)mAssestiveTouch.floatY,(int)mAssestiveTouch.floatX,(int)(mAssestiveTouch.floatY+2*mAssestiveTouch.originalRadius),(int)(mAssestiveTouch.floatX+2*mAssestiveTouch.originalRadius));
                break;
            case STATUS_SPREAD:
                /*
                 * 当view变为spread模式后将其定位在屏幕中央
                 */
                mAssestiveTouch.layout((int)(mAssestiveTouch.measureWidth/2-mAssestiveTouch.spreadRadius),(int)(mAssestiveTouch.measureHeigt/2-mAssestiveTouch.spreadRadius),
                        (int)(mAssestiveTouch.measureWidth/2+mAssestiveTouch.spreadRadius),(int)(mAssestiveTouch.measureHeigt/2+mAssestiveTouch.spreadRadius));
                break;
        }
    }

    /**
     * Assestive Touch的位移动画
     */
    public void translateAnimation(final Map<String,Float> destination,long startDelay){
        Log.v("translateAnimation","play");
        Log.v("mAssestiveFloatX",mAssestiveTouch.floatX+"");
        ValueAnimator floatX=ValueAnimator.ofFloat(mAssestiveTouch.floatX,destination.get("x"));
        ValueAnimator floatY=ValueAnimator.ofFloat(mAssestiveTouch.floatY,destination.get("y"));

        floatX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.v("animatedValueX",animation.getAnimatedValue()+"");
                mAssestiveTouch.floatX=(float)animation.getAnimatedValue();
                
                requestLayout();
                invalidate();
            }
        });

        floatY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAssestiveTouch.floatY=(float)animation.getAnimatedValue();

                requestLayout();
                invalidate();
            }
        });

        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.play(floatX).with(floatY);
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.setStartDelay(startDelay);
        animatorSet.start();

        /*
         * 给animatorSet添加监听事件
         * 在动画集播放完后播放自动吸附边缘的动画（如果需要吸附）
         */
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                final Map<String,Float> adsorptionDestination=autoAdsorptionEdge(destination);

                if(!adsorptionDestination.get("x").equals(destination.get("x")) ||
                        !adsorptionDestination.get("y").equals(destination.get("y"))){
                    /*
                     让AssistiveTouch悬浮500ms后再吸附边缘
                     */
                    translateAnimation(adsorptionDestination,500);
                }
            }
        });
    }

    /**
     * Assestive Touch的缩放动画
     */
    public void scaleAnimation(float scale){
        ValueAnimator scaleAnim=ValueAnimator.ofFloat(mAssestiveTouch.spreadRadius,mAssestiveTouch.spreadRadius*scale);

        scaleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAssestiveTouch.spreadRadius=(float)animation.getAnimatedValue();

                requestLayout();
                invalidate();
            }
        });

        scaleAnim.setDuration(300);
        scaleAnim.setInterpolator(new LinearInterpolator());
        scaleAnim.start();
    }

    /**
     * 自动吸边
     */
    Map<String,Float> autoAdsorptionEdge(Map<String,Float> destination){
        Log.v("autoAdsorptionEdge","do");
        Map<String,Float> result=new HashMap<>();

        float floatX=destination.get("x");
        float floatY=destination.get("y");

        /*
         如果AssistiveTouch距屏幕顶部不到20dp则吸附于屏幕顶部
         */
        if(floatX<20){
            floatX=0;
        }
        /*
         如果AssistiveTouch距屏幕底部不到20dp则吸附于屏幕顶部
         */
        if(floatX>mAssestiveTouch.measureHeigt-2*mAssestiveTouch.originalRadius-20){
            floatX=mAssestiveTouch.measureHeigt-2*mAssestiveTouch.originalRadius;
        }
        /*
         如果AssistiveTouch距屏幕左部不到20do则吸附于屏幕左部
         */
        if(floatY<20){
            floatY=0;
        }
        /*
         如果AssistiveTouch距屏幕右部不到20dp则吸附于屏幕右部
         */
        if(floatY> mAssestiveTouch.measureWidth-2*mAssestiveTouch.originalRadius-20){
            floatY=mAssestiveTouch.measureWidth-2*mAssestiveTouch.originalRadius;
        }

        result.put("x",floatX);
        result.put("y",floatY);
        return result;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onDraw(Canvas canvas){
        Log.v("layout_draw","draw");
        mAssestiveTouch.invalidate();
    }

    /**
     * 仅做模拟器的测试用
     */
    @Override
    public void onShowPress(MotionEvent e) {
        /*
         如果手势事件发生在mAssestiveTouch外,则回调onLayout()修改布局
         */
        if(mAssestiveTouch.STATUS==STATUS_CLOSED){
            if(mAssestiveTouch.judgeGestureDetector(e) == TOUCH_OUTSIDE_ORIGNAL){
                Log.v("eventType_layout","TOUCH_OUTSIDE_ORIGINAL");
                Map<String,Float> destination=new HashMap<>();
                destination.put("x",e.getRawY());
                destination.put("y",e.getRawX());
                translateAnimation(destination,0);
            }
        }
        if(mAssestiveTouch.judgeGestureDetector(e)==AssestiveTouchDemo.TOUCH_OUTSIDE_SPREAD){
            Log.v("eventType","TOUCH_OUTSIDE_SPREAD");
            mAssestiveTouch.STATUS= STATUS_CLOSED;
            requestLayout();
            invalidate();
        }
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        /*
         如果手势事件发生在mAssestiveTouch外,则回调onLayout()修改布局
         */
        if(mAssestiveTouch.STATUS==STATUS_CLOSED){
            if(mAssestiveTouch.judgeGestureDetector(e) == TOUCH_OUTSIDE_ORIGNAL){
                Log.v("eventType_layout","TOUCH_OUTSIDE_ORIGINAL");
                Map<String,Float> destination=new HashMap<>();
                destination.put("x",e.getRawY());
                destination.put("y",e.getRawX());
                translateAnimation(destination,0);
            }
        }
        if(mAssestiveTouch.judgeGestureDetector(e)==AssestiveTouchDemo.TOUCH_OUTSIDE_SPREAD){
            Log.v("eventType","TOUCH_OUTSIDE_SPREAD");
            mAssestiveTouch.STATUS= STATUS_CLOSED;
            requestLayout();
            invalidate();
        }

        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        /*
         * 判断拖动事件的发生点是否在AssestiveTouch上再决定是否播放位移动画
         */
        if(mAssestiveTouch.judgeGestureDetector(e1)==AssestiveTouchDemo.TOUCH_INSIDE_ORIGNAL){
            if(mAssestiveTouch.judgeGestureDetector(e2)== TOUCH_OUTSIDE_ORIGNAL){
                Map<String,Float> destination=new HashMap<>();
                destination.put("x",e2.getRawY());
                destination.put("y",e2.getRawX());
                translateAnimation(destination,0);
            }
        }

        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
         /*
         * 计算松开手指后view依照惯性滑动的距离
         * 假设a为-50像素/秒
         * s=v0*t+0.5*a*t2
         */
        int a=-50;
        double tX=velocityX/Math.abs(a);
        double ditanceX=velocityX*tX+0.5*a*Math.pow(tX,2);

        double tY=velocityY/Math.abs(a);
        double ditanceY=velocityY*tY+0.5*a*Math.pow(tX,2);

        Map<String,Float> destination=new HashMap<>();

        destination.put("x",(float)(e2.getRawX()+ditanceX));
        destination.put("y",(float)(e2.getRawY()+ditanceY));

        translateAnimation(destination,0);
        return true;
    }
}
