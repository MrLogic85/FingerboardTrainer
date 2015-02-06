
package com.sleepyduck.fingerboardtrainer;

import android.animation.LayoutTransition;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class MainLayout extends ViewGroup {
    public static final String TAG = "fingerboardtrainer";

    private LayoutState mLayoutState = LayoutState.NORMAL;

    private LayoutTransition mLayoutTransition;

    enum LayoutState {
        NORMAL, TEXT_FOCUS
    }

    public MainLayout(Context context) {
        super(context);
        setup();
    }

    public MainLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public MainLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup() {
        mLayoutTransition = new LayoutTransition();
        mLayoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        setLayoutTransition(mLayoutTransition);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        switch (mLayoutState) {
            case NORMAL:
                onNormalMeasure(widthMeasureSpec, heightMeasureSpec);
                break;
            case TEXT_FOCUS:
                onTextFocusMeasure(widthMeasureSpec, heightMeasureSpec);
                break;
            default:
                break;
        }
    }

    private void onNormalMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int unitSize = Math.min(width / 2, height / 4);
        int childCount = getChildCount();
        View view;
        int measuredWidth, measuredHeight;
        for (int i = 0; i < childCount; ++i) {
            view = getChildAt(i);
            switch (view.getId()) {
                case R.id.hang_time:
                case R.id.pause_time:
                case R.id.rest_time:
                case R.id.total_repetitions:
                    measuredWidth = MeasureSpec.makeMeasureSpec(unitSize, MeasureSpec.EXACTLY);
                    measuredHeight = MeasureSpec.makeMeasureSpec(unitSize, MeasureSpec.EXACTLY);
                    view.measure(measuredWidth, measuredHeight);
                    break;
                case R.id.repetitions:
                    measuredWidth = MeasureSpec.makeMeasureSpec(unitSize, MeasureSpec.EXACTLY);
                    measuredHeight = MeasureSpec.makeMeasureSpec(unitSize * 2, MeasureSpec.EXACTLY);
                    view.measure(measuredWidth, measuredHeight);
                    break;
                case R.id.start_button:
                    measuredWidth = MeasureSpec.makeMeasureSpec(unitSize * 2, MeasureSpec.EXACTLY);
                    measuredHeight = MeasureSpec.makeMeasureSpec((int)(unitSize * 0.5),
                            MeasureSpec.EXACTLY);
                    view.measure(measuredWidth, measuredHeight);
                    break;
                case R.id.textView:
                    measuredWidth = MeasureSpec.makeMeasureSpec(unitSize * 2, MeasureSpec.EXACTLY);
                    measuredHeight = MeasureSpec.makeMeasureSpec((int)(height - unitSize * 3.5),
                            MeasureSpec.EXACTLY);
                    view.measure(measuredWidth, measuredHeight);
                    break;
            }
        }
    }

    private void onTextFocusMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int unitSize = Math.min(width / 3, height / 4);
        int childCount = getChildCount();
        View view;
        int measuredWidth, measuredHeight;
        for (int i = 0; i < childCount; ++i) {
            view = getChildAt(i);
            switch (view.getId()) {
                case R.id.hang_time:
                case R.id.pause_time:
                case R.id.rest_time:
                case R.id.total_repetitions:
                    measuredWidth = MeasureSpec.makeMeasureSpec(unitSize, MeasureSpec.EXACTLY);
                    measuredHeight = MeasureSpec.makeMeasureSpec(unitSize, MeasureSpec.EXACTLY);
                    view.measure(measuredWidth, measuredHeight);
                    break;
                case R.id.repetitions:
                    measuredWidth = MeasureSpec.makeMeasureSpec(unitSize, MeasureSpec.EXACTLY);
                    measuredHeight = MeasureSpec.makeMeasureSpec(unitSize * 2, MeasureSpec.EXACTLY);
                    view.measure(measuredWidth, measuredHeight);
                    break;
                case R.id.start_button:
                    measuredWidth = MeasureSpec.makeMeasureSpec(unitSize * 3, MeasureSpec.EXACTLY);
                    measuredHeight = MeasureSpec.makeMeasureSpec((int)(unitSize * 0.5),
                            MeasureSpec.EXACTLY);
                    view.measure(measuredWidth, measuredHeight);
                    break;
                case R.id.textView:
                    measuredWidth = MeasureSpec.makeMeasureSpec(unitSize * 3, MeasureSpec.EXACTLY);
                    measuredHeight = MeasureSpec.makeMeasureSpec((int)(height - unitSize * 2.5),
                            MeasureSpec.EXACTLY);
                    view.measure(measuredWidth, measuredHeight);
                    break;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        switch (mLayoutState) {
            case NORMAL:
                onNormalLayout(changed, left, top, right, bottom);
                break;
            case TEXT_FOCUS:
                onTextFocusLayout(changed, left, top, right, bottom);
                break;
            default:
                break;
        }
    }

    private void onNormalLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        int unitSize = Math.min(width / 2, height / 4);
        int widthPadding = (width - unitSize * 2) / 2;
        int childCount = getChildCount();
        View view;
        for (int i = 0; i < childCount; ++i) {
            view = getChildAt(i);
            switch (view.getId()) {
                case R.id.hang_time:
                    view.layout(widthPadding, 0, widthPadding + unitSize, unitSize);
                    break;
                case R.id.pause_time:
                    view.layout(widthPadding, unitSize, widthPadding + unitSize, unitSize * 2);
                    break;
                case R.id.rest_time:
                    view.layout(widthPadding, unitSize * 2, widthPadding + unitSize, unitSize * 3);
                    break;
                case R.id.total_repetitions:
                    view.layout(widthPadding + unitSize, unitSize * 2, widthPadding + unitSize * 2,
                            unitSize * 3);
                    break;
                case R.id.repetitions:
                    view.layout(widthPadding + unitSize, 0, widthPadding + unitSize * 2,
                            unitSize * 2);
                    break;
                case R.id.start_button:
                    view.layout(widthPadding, unitSize * 3, widthPadding + unitSize * 2,
                            (int)(unitSize * 3.5));
                    break;
                case R.id.textView:
                    view.layout(widthPadding, (int)(unitSize * 3.5), widthPadding + unitSize * 2,
                            height);
                    break;
            }
        }
    }

    private void onTextFocusLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        int unitSize = Math.min(width / 3, height / 4);
        int widthPadding = (width - unitSize * 3) / 2;
        int childCount = getChildCount();
        View view;
        for (int i = 0; i < childCount; ++i) {
            view = getChildAt(i);
            switch (view.getId()) {
                case R.id.hang_time:
                    view.layout(widthPadding, 0, widthPadding + unitSize, unitSize);
                    break;
                case R.id.pause_time:
                    view.layout(widthPadding + unitSize, 0, widthPadding + unitSize * 2, unitSize);
                    break;
                case R.id.rest_time:
                    view.layout(widthPadding, unitSize, widthPadding + unitSize, unitSize * 2);
                    break;
                case R.id.total_repetitions:
                    view.layout(widthPadding + unitSize, unitSize, widthPadding + unitSize * 2,
                            unitSize * 2);
                    break;
                case R.id.repetitions:
                    view.layout(widthPadding + unitSize * 2, 0, widthPadding + unitSize * 3,
                            unitSize * 2);
                    break;
                case R.id.start_button:
                    view.layout(widthPadding, unitSize * 2, widthPadding + unitSize * 3,
                            (int)(unitSize * 2.5));
                    break;
                case R.id.textView:
                    view.layout(widthPadding, (int)(unitSize * 2.5), widthPadding + unitSize * 3,
                            height);
                    break;
            }
        }
    }

    public void setLayoutState(LayoutState layoutState) {
        if (layoutState != mLayoutState) {
            mLayoutState = layoutState;
            invalidate();
        }
    }

}
