package android.arduinos.activity;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyPager extends ViewPager {

  // contrôle le swipe
  boolean isSwipeEnabled;

  public MyPager(Context context) {
    super(context);
  }

  public MyPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent event) {
    // swipe autorisé ?
    if (isSwipeEnabled) {
      return super.onInterceptTouchEvent(event);
    } else {
      return false;
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    // swipe autorisé ?
    if (isSwipeEnabled) {
      return super.onTouchEvent(event);
    } else {
      return false;
    }
  }

  // setter
  public void setSwipeEnabled(boolean isSwipeEnabled) {
    this.isSwipeEnabled = isSwipeEnabled;
  }

}