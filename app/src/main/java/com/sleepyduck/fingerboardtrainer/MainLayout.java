
package com.sleepyduck.fingerboardtrainer;

import android.animation.LayoutTransition;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import timber.log.Timber;

public class MainLayout extends ViewGroup {
    public static final String TAG = "fingerboardtrainer";

    private LayoutState mLayoutState = LayoutState.NORMAL;
    private NavMenuState mNavMenuState = NavMenuState.CLOSED;

    enum LayoutState {
        NORMAL, RUNNING
    }

    enum NavMenuState {
        OPEN, CLOSED
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
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        setLayoutTransition(layoutTransition);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        switch (mLayoutState) {
            case NORMAL:
                onNormalMeasure(widthMeasureSpec, heightMeasureSpec);
                break;
            case RUNNING:
                onRunningMeasure(widthMeasureSpec, heightMeasureSpec);
                break;
        }
        onNavMeasure(widthMeasureSpec, heightMeasureSpec);
        onAdMeasure(widthMeasureSpec, heightMeasureSpec);
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
                    measuredHeight = MeasureSpec.makeMeasureSpec((int) (unitSize * 0.5),
                            MeasureSpec.EXACTLY);
                    view.measure(measuredWidth, measuredHeight);
                    break;
                case R.id.pause_button:
                    measuredWidth = MeasureSpec.makeMeasureSpec(0, MeasureSpec.EXACTLY);
                    measuredHeight = MeasureSpec.makeMeasureSpec((int) (unitSize * 0.5),
                            MeasureSpec.EXACTLY);
                    view.measure(measuredWidth, measuredHeight);
                    break;
                case R.id.textView:
                    measuredWidth = MeasureSpec.makeMeasureSpec(unitSize * 2, MeasureSpec.EXACTLY);
                    measuredHeight = MeasureSpec.makeMeasureSpec((int) (height - unitSize * 3.5),
                            MeasureSpec.EXACTLY);
                    view.measure(measuredWidth, measuredHeight);
                    break;
            }
        }
    }

    private void onRunningMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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
                    measuredWidth = MeasureSpec.makeMeasureSpec(unitSize * 2, MeasureSpec.EXACTLY);
                    measuredHeight = MeasureSpec.makeMeasureSpec((int) (unitSize * 0.5),
                            MeasureSpec.EXACTLY);
                    view.measure(measuredWidth, measuredHeight);
                    break;
                case R.id.pause_button:
                    measuredWidth = MeasureSpec.makeMeasureSpec(unitSize, MeasureSpec.EXACTLY);
                    measuredHeight = MeasureSpec.makeMeasureSpec((int) (unitSize * 0.5),
                            MeasureSpec.EXACTLY);
                    view.measure(measuredWidth, measuredHeight);
                    break;
                case R.id.textView:
                    measuredWidth = MeasureSpec.makeMeasureSpec(unitSize * 3, MeasureSpec.EXACTLY);
                    measuredHeight = MeasureSpec.makeMeasureSpec((int) (height - unitSize * 2.5),
                            MeasureSpec.EXACTLY);
                    view.measure(measuredWidth, measuredHeight);
                    break;
            }
        }
    }

    private void onNavMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int end = (int) getResources().getDimension(R.dimen.menu_width);
        findViewById(R.id.nav_menu_container).measure(MeasureSpec.makeMeasureSpec(end, MeasureSpec.EXACTLY), heightMeasureSpec);
        findViewById(R.id.navManuCloseButton).measure(MeasureSpec.makeMeasureSpec(width - end, MeasureSpec.EXACTLY), heightMeasureSpec);
    }

    private void onAdMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        Timber.d("adSize %d, %d", width, height);
        findViewById(R.id.ad_container).measure(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        switch (mLayoutState) {
            case NORMAL:
                onNormalLayout(changed, left, top, right, bottom);
                break;
            case RUNNING:
                onRunningLayout(changed, left, top, right, bottom);
                break;
            default:
                break;
        }
        switch (mNavMenuState) {
            case OPEN:
                onNavMenuOpenLayout(changed, left, top, right, bottom);
                break;
            case CLOSED:
                onNavMenuClosedLayout(changed, left, top, right, bottom);
                break;
        }
        onAdLayout(changed, left, top, right, bottom);
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
                            (int) (unitSize * 3.5));
                    break;
                case R.id.pause_button:
                    view.layout(widthPadding + unitSize * 2, unitSize * 3, widthPadding + unitSize * 2,
                            (int) (unitSize * 3.5));
                    break;
                case R.id.textView:
                    view.layout(widthPadding, (int) (unitSize * 3.5), widthPadding + unitSize * 2,
                            height);
                    break;
            }
        }
    }

    private void onRunningLayout(boolean changed, int left, int top, int right, int bottom) {
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
                    view.layout(widthPadding, unitSize * 2, widthPadding + unitSize * 2,
                            (int) (unitSize * 2.5));
                    break;
                case R.id.pause_button:
                    view.layout(widthPadding + unitSize * 2, unitSize * 2, widthPadding + unitSize * 3,
                            (int) (unitSize * 2.5));
                    break;
                case R.id.textView:
                    view.layout(widthPadding, (int) (unitSize * 2.5), widthPadding + unitSize * 3,
                            height);
                    break;
            }
        }
    }

    private void onNavMenuOpenLayout(boolean changed, int left, int top, int right, int bottom) {
        int end = (int) (getResources().getDimension(R.dimen.menu_width));
        findViewById(R.id.nav_menu_container).layout(0, top, end, bottom);
        findViewById(R.id.navManuCloseButton).layout(end, top, right, bottom);
    }

    private void onNavMenuClosedLayout(boolean changed, int left, int top, int right, int bottom) {
        int end = (int) (getResources().getDimension(R.dimen.menu_width));
        findViewById(R.id.nav_menu_container).layout(-end, top, 0, bottom);
        findViewById(R.id.navManuCloseButton).layout(end - right, top, 0, bottom);
    }

    private void onAdLayout(boolean changed, int left, int top, int right, int bottom) {
        View adContainer = findViewById(R.id.ad_container);
        int adTop = bottom - top - adContainer.getMeasuredHeight();
        adContainer.layout(0, adTop, right - left, bottom - top);
        Timber.d("adLayout top: %d, bottom: %d, width: %d, height: %d ", 0, adTop, (right - left), (bottom - top));
        Timber.d("adMeasuredSize %d, %d", adContainer.getMeasuredWidth(), adContainer.getMeasuredHeight());
    }

    public void setLayoutState(LayoutState layoutState) {
        if (layoutState != mLayoutState) {
            mLayoutState = layoutState;
            requestLayout();
        }
    }

    public void setNavMenu(NavMenuState navMenuState) {
        if (navMenuState != mNavMenuState) {
            mNavMenuState = navMenuState;
            requestLayout();
        }
    }

    public NavMenuState getNavMenuState() {
        return mNavMenuState;
    }

}
