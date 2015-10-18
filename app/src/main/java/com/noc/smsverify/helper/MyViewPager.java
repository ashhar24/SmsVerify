package com.noc.smsverify.helper;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by defoliate on 14-10-2015.
 */
public class MyViewPager extends ViewPager
{
    public MyViewPager (Context context)
    {
        super(context);
    }

    public MyViewPager (Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent (MotionEvent event)
    {
        // Never allow swiping to switch between pages
        return false;
    }

    @Override
    public boolean onTouchEvent (MotionEvent event)
    {
        // Never allow swiping to switch between pages
        return false;
    }
}
