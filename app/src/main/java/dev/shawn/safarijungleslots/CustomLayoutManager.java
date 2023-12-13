package dev.shawn.safarijungleslots;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

public class CustomLayoutManager extends LinearLayoutManager {

    private boolean isScrollEnabled = true;

    public CustomLayoutManager(Context context) {
        super(context);
    }

    public void setScrollEnabled(boolean flag) {
        isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }
}
