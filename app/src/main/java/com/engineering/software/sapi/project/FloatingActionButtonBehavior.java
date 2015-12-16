package com.engineering.software.sapi.project;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Tomi on 12/16/2015.
 */
public class FloatingActionButtonBehavior extends FloatingActionButton.Behavior {

    public FloatingActionButtonBehavior(Context context, AttributeSet attributeSet) {
        super();
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return dependency instanceof RecyclerView;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (dxConsumed > 0 && child.getVisibility() == View.VISIBLE) {
            Log.d("FAB ", "Hide");
            child.setTranslationY(-300);
//            child.hide();
        } else if (dxConsumed < 0 && child.getVisibility() == View.GONE) {
            Log.d("FAB", "Show");
            child.setTranslationY(300);
//            child.show();
        }
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_HORIZONTAL;
    }
}
