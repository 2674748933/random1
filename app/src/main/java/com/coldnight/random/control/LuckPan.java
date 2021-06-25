package com.coldnight.random.control;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

/**
 * Create By 92474 on 2021/4/21.
 */
public class LuckPan extends View {
    private float mStartAngle = 0;   //存储开始角度
    private Paint mPaintArc;        //转盘画笔
    private float mRadius;          //转盘半径
    private RectF mRectArc;         //构建转盘矩形
    private float mItemAnge;
    private String[] mItems = {"鸡排饭", "鸡块面", "麻辣拌", "麻辣烫", "麻辣香锅", "铁锅焖面", "面面聚到", "炒米"};
    private ArrayList<Path> mArcPaths;

    private float mOffsetAngle;       //偏移角度
    private Paint mPaintText;       //文字画笔
    private RectF mRectText;        //构建文字矩形
    private float mTextSize = 20f;  //文字大小

    private int mRepeatCount = 5;//转几圈
    private int mLuckNum = 1;//最终停止的位置
    private ObjectAnimator objectAnimator;

    private LuckPanAnimEndCallBack luckPanAnimEndCallBack;

    public LuckPan(Context context) {
        super(context);
        init();
    }

    public LuckPan(Context context, String[] mItems) {
        super(context);
        this.mItems = mItems;
        init();
    }

    /*public LuckPan(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LuckPan(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }*/

    private void init() {
        mPaintArc = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintArc.setStyle(Paint.Style.FILL);
        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setColor(Color.parseColor("#000000"));//设置文字颜色
        mPaintText.setTypeface(Typeface.DEFAULT_BOLD);
        mPaintText.setStrokeWidth(3);
        mPaintText.setTextAlign(Paint.Align.CENTER);//设置文字水平居中对齐
        mArcPaths = new ArrayList<>();
        mItemAnge = (float) 360 / mItems.length;
        mOffsetAngle = mItemAnge / 2;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = (float) Math.min(w, h) / 2 * 0.9f;
        mRectArc = new RectF(-mRadius, -mRadius, mRadius, mRadius);
        mRectText = new RectF(-mRadius / 7 * 5, -mRadius / 7 * 5, mRadius / 7 * 5, mRadius / 7 * 5);//构建文字路径的矩形半径为圆盘的五分之七
        mTextSize = mRadius / 9;//根据圆盘的半径设置文字大小
        mPaintText.setTextSize(mTextSize);//设置文字大小
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate((float) getWidth() / 2, (float) getHeight() / 2);
        canvas.rotate(-90 - mOffsetAngle);
        drawItem(canvas);
        drawText(canvas);
    }

    private void drawItem(Canvas canvas) {
        float startAnge = 0; //扇形开始角度
        for (int i = 1; i <= mItems.length; i++) {
            if (i % 2 == 1) {
                mPaintArc.setColor(Color.GREEN);
            } else {
                mPaintArc.setColor(Color.parseColor("#F8864A"));
            }
            Path path = new Path();
            path.addArc(mRectText, startAnge, mItemAnge);
            mArcPaths.add(path);
            canvas.drawArc(mRectArc, startAnge, mItemAnge, true, mPaintArc);
            startAnge += mItemAnge;
        }
    }

    private void drawText(Canvas canvas) {
        for (int i = 0; i < mItems.length; i++) {
            Path path = mArcPaths.get(i);
            canvas.drawTextOnPath(mItems[i], path, 0, 0, mPaintText);
        }
    }

    /**开始转动*/
    public void startAnim() {
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        float fA = mRepeatCount * 360;
        float pA = mItemAnge * mLuckNum;
        float sA = pA % 360;
        objectAnimator = ObjectAnimator.ofFloat(this, "rotation", -sA, -(fA + pA));
        objectAnimator.setDuration(3000);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                if (luckPanAnimEndCallBack != null) {
                    luckPanAnimEndCallBack.onAnimEnd(mItems[mLuckNum]);
                }

            }
        });
        objectAnimator.start();
        mStartAngle = sA;
    }

    /**
     * 随机转动
     */
    public void startAnimRandom() {
        Random random = new Random();
        mLuckNum = random.nextInt(mItems.length);
        startAnim();
    }

    /**
     * 转动到指定位置
     */
    public void startAnimAppoint(int appoint) {
        mLuckNum = Math.abs(appoint) % mItems.length;
        startAnim();
    }

    public interface LuckPanAnimEndCallBack {
        void onAnimEnd(String str);
    }

    public void setLuckPanAnimEndCallBack(LuckPanAnimEndCallBack luckPanAnimEndCallBack) {
        this.luckPanAnimEndCallBack = luckPanAnimEndCallBack;
    }


}
