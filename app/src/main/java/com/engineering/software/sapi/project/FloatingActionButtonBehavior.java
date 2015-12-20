package com.engineering.software.sapi.project;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Tomi on 12/16/2015.
 */
public class FloatingActionButtonBehavior extends FloatingActionButton.Behavior {

    private FloatingActionButton fab;

    /*
     * Constructor
     */
    public FloatingActionButtonBehavior(Context context, AttributeSet attributeSet) {
        super();
    }

    /*
     * The behavior of the FAB depends on the scroll of the recycler view.
     */
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        return super.layoutDependsOn(parent, child, dependency) || (dependency instanceof RecyclerView);
    }

    /*
     * When Snackbar is shown move the FAB on top of the it.
     * translationY -
     */
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
        float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
        child.setTranslationY(translationY);
        return true;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

        fab = child;

        /*
         * If the recycler view, which contains the passengers of a route, is scrolled
         * the subscribe button (FAB) is moved up. After 2 seconds the FAB is moved back to its
         * original position.
         */
        if (dxConsumed > 0 || dxConsumed < 0) {
            child.animate().translationY(-225).start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fab.animate().translationY(10).start();
                }
            }, 2000);
        }
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_HORIZONTAL;
    }
}
