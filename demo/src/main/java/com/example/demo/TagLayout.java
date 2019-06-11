package com.example.demo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class TagLayout extends FrameLayout {
    //支持添加,编辑,删除,移动tag
    private boolean enableAdd;
    private TagView mCurrTagView;//点击同一个tag赋值,新增为null
    //手势监听器
//    private GestureDetector detector;
//    public interface OnTagOperationCallback {
//        void onAdd(float x, float y);
//    }
//    private OnTagOperationCallback mOnTagOperationCallback;

    //    public void setOnTagOperationCallback(OnTagOperationCallback callback) {
//        this.mOnTagOperationCallback = callback;
//    }
    public void setEnableAdd(boolean enableAdd) {
        this.enableAdd = enableAdd;
    }

    public TagLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
//        detector = new GestureDetector(getContext(), this);
        //获取自定义属性
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TagLayout);
        enableAdd = array.getBoolean(R.styleable.TagLayout_enableAdd, false);
        array.recycle();
    }

//    public TagView getmCurrTagView() {
//        return mCurrTagView;
//    }
//
//    public void setmCurrTagView(TagView mCurrTagView) {
//        this.mCurrTagView = mCurrTagView;
//    }

    //    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        return detector.onTouchEvent(event);
//    }
//    /**
//     * 单击添加或编辑当前的mCurrTagView
//     * @param e
//     * @return
//     */
//    @Override
//    public boolean onSingleTapUp(MotionEvent e) {
//    public boolean change(){
//        if (null != mOnTagOperationCallback && enableAdd) {
//            mOnTagOperationCallback.onAdd(0.7f, 0.5f);
//        }
//        return true;
//    }
//        return true;
//    }
    public static final class Tag {
        private double x, y;
        private TagLayout mParent;
        private Drawable mIcon;

        private Tag() {
            // Private constructor
        }

        public double getX() {
            return x;
        }

        public Tag setX(double x) {
            this.x = x;
            return this;
        }

        public double getY() {
            return y;
        }

        public Tag setY(double y) {
            this.y = y;
            return this;
        }

        public Tag setParent(TagLayout parent) {
            this.mParent = parent;
            return this;
        }

        public TagLayout getParent() {
            return mParent;
        }

        public Drawable getmIcon() {
            return mIcon;
        }

        public Tag setmIcon(Drawable Icon) {
            this.mIcon = Icon;
            return this;
        }
    }

    public List<TagView> mTagViews = new ArrayList<TagView>();

    /**
     * 添加新的tag
     *
     * @param tag
     */
    public void addTag(Tag tag) {
        TagView view = new TagView(getContext());
//        if (null != tag.getmIcon()) {
//            view.setmIcon(tag.getmIcon());//背景图
//        }
        view.setParent(tag.getParent());//绑定父view
        addView(view);//显示
        //定位
        view.measureSelf();
//        System.out.println( getHeight()+"getWidth"+ getWidth() * 0.5 +"原始宽"+ view.getMeasuredWidth());
        if (tag.getX() > getWidth() * 0.5) {
//                System.out.println( getWidth() * 0.5 + view.getMeasuredWidth()+"getWidth"+ getWidth() * 0.5 +"原始宽"+ view.getMeasuredWidth());
            if (tag.getX() > getWidth() * 0.5 + view.getMeasuredWidth()) {
                view.setPosition((int) (tag.getX() - view.getMeasuredWidth() + 15), (int) (tag.getY() - view.getMeasuredHeight() / 2));
//                    System.out.println( getWidth() * 0.5 + view.getMeasuredWidth()+"getWidth"+ getWidth() * 0.5 +"原始宽"+ view.getMeasuredWidth());
            } else {
                view.setPosition((int) (tag.getX()), (int) (tag.getY() - view.getMeasuredHeight() / 2));
//                    System.out.println( "大于一半"+"X "+ tag.getX()+"Y"+ (tag.getY() - view.getMeasuredHeight() / 2));
            }
        } else {
            view.setPosition((int) (tag.getX()), (int) (tag.getY() - view.getMeasuredHeight() / 2));
//                System.out.println( "小于一半"+"X "+ tag.getX()+"Y"+ (tag.getY() - view.getMeasuredHeight() / 2));
        }
        mTagViews.add(view);
//            System.out.println(mTagViews.size()+"类内");
    }

    public Tag newTag() {
        Tag tag = new Tag();
        tag.setParent(this);
        return tag;
    }

    public void cleanAllTag() {
        for (TagView view : mTagViews) {
            removeView(view);
        }
        mTagViews.clear();
    }

    class TagView extends FrameLayout {
        private RelativeLayout layout;
        private TagLayout mParent;
        private ImageView mIcon;

        public TagView(Context context) {
            super(context);
            LayoutInflater.from(getContext()).inflate(R.layout.layout_tag_view, this, true);
            layout = findViewById(R.id.layout);
//            mIcon = getResources().getDrawable(R.drawable.icon_position);
            mIcon = findViewById(R.id.tv_micon);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            View parent = (View) getParent();
            float halfParentW = parent.getWidth() * 0.5f;
        }

        private void setPosition(double x, double y) {
            measureSelf();
            if (x < 0) {
                x = 0;
            }
            if (x > mParent.getWidth() - getMeasuredWidth()) {
                x = mParent.getWidth() - getMeasuredWidth();
            }
            if (y < 0) {
                y = 0;
            }
            if (y > mParent.getHeight() - getMeasuredHeight()) {
                y = mParent.getHeight() - getMeasuredHeight();
            }
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.leftMargin = (int) x;
            params.topMargin = (int) y;
            setLayoutParams(params);
        }

        private void setParent(TagLayout parent) {
            mParent = parent;
        }

        private void measureSelf() {
            if (getMeasuredWidth() == 0 || getMeasuredHeight() == 0) {
                measure(0, 0);
            }
        }

        public void removeSelf() {
            mCurrTagView = null;
            mParent.removeView(this);
            mTagViews.remove(this);
        }
//        public Drawable getmIcon() {
//            return mIcon;
//        }
//
//        public void setmIcon(Drawable tIcon) {
//            mIcon = tIcon;
//        }
    }

}
