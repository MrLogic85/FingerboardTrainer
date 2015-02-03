
package com.sleepyduck.fingerboardtrainer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class MainLayout extends ViewGroup {

    public MainLayout(Context context) {
        super(context);
    }

    public MainLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MainLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int unitSize = Math.min(width / 2, height / 5);
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
                    measuredHeight = MeasureSpec.makeMeasureSpec(unitSize * 3, MeasureSpec.EXACTLY);
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
                    measuredHeight = MeasureSpec.makeMeasureSpec((int)(unitSize * 1.5),
                            MeasureSpec.EXACTLY);
                    view.measure(measuredWidth, measuredHeight);
                    break;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        int unitSize = Math.min(width / 2, height / 5);
        int widthPadding = (width - unitSize * 2) / 2;
        int heightPadding = (height - unitSize * 5) / 2;
        int childCount = getChildCount();
        View view;
        for (int i = 0; i < childCount; ++i) {
            view = getChildAt(i);
            switch (view.getId()) {
                case R.id.hang_time:
                    view.layout(widthPadding, heightPadding, widthPadding + unitSize, heightPadding
                            + unitSize);
                    break;
                case R.id.pause_time:
                    view.layout(widthPadding, heightPadding + unitSize, widthPadding + unitSize,
                            heightPadding + unitSize * 2);
                    break;
                case R.id.rest_time:
                    view.layout(widthPadding, heightPadding + unitSize * 2,
                            widthPadding + unitSize, heightPadding + unitSize * 3);
                    break;
                case R.id.total_repetitions:
                    view.layout(widthPadding + unitSize, heightPadding + unitSize * 2, widthPadding
                            + unitSize * 2, heightPadding + unitSize * 3);
                    break;
                case R.id.repetitions:
                    view.layout(widthPadding + unitSize, heightPadding,
                            widthPadding + unitSize * 2, heightPadding + unitSize * 2);
                    break;
                case R.id.start_button:
                    view.layout(widthPadding, heightPadding + unitSize * 3, widthPadding + unitSize
                            * 2, (int)(heightPadding + unitSize * 3.5));
                    break;
                case R.id.textView:
                    view.layout(widthPadding, (int)(heightPadding + unitSize * 3.5), widthPadding
                            + unitSize * 2, heightPadding + unitSize * 5);
                    break;
            }
        }
    }

}