package com.xtremelabs.droidsugar.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.xtremelabs.droidsugar.ProxyDelegatingHandler;

@SuppressWarnings({"ALL"})
public class FakeView {
    protected View realView;

    private int id;
    private List<View> children = new ArrayList<View>();
    private FakeView parent;
    private Context context;
    public int visibility;
    public boolean selected;
    private View.OnClickListener onClickListener;
    private Object tag;
    private Map<Integer, Object> tags = new HashMap<Integer, Object>();
    protected View.OnKeyListener onKeyListener;
    public boolean hasFocus;
    private View.OnFocusChangeListener onFocusChangeListener;

    public FakeView(View view) {
        this.realView = view;
    }

    public void __constructor__(Context context) {
        this.context = context;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public View findViewById(int id) {
        if (id == this.id) {
            return realView;
        }

        for (View child : children) {
            View found = child.findViewById(id);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public View getRootView() {
        FakeView root = this;
        while(root.parent != null) {
            root = root.parent;
        }
        return root.realView;
    }

    public void addView(View child) {
        children.add(child);
        childProxy(child).parent = this;
    }

    private FakeView childProxy(View child) {
        return (FakeView) ProxyDelegatingHandler.getInstance().proxyFor(child);
    }

    public int getChildCount() {
        return children.size();
    }

    public View getChildAt(int index) {
        return children.get(index);
    }

    public final ViewParent getParent() {
        return (ViewParent) parent.realView;
    }

    public ViewGroup.LayoutParams getLayoutParams() {
        return new ViewGroup.LayoutParams(0, 0);
    }

    public void removeAllViews() {
        for (View child : children) {
            childProxy(child).parent = null;
        }
        children.clear();
    }

    public final Context getContext() {
        return context;
    }

    public Resources getResources() {
        return context.getResources();
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public boolean performClick() {
        if (onClickListener != null) {
            onClickListener.onClick(realView);
            return true;
        } else {
            return false;
        }
    }

    public void setOnKeyListener(View.OnKeyListener onKeyListener) {
        this.onKeyListener = onKeyListener;
    }

    public Object getTag() {
        return this.tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public Object getTag(int key) {
        return tags.get(key);
    }

    public void setTag(int key, Object value) {
        tags.put(key, value);
    }

    public void setViewFocus(boolean hasFocus) {
        this.hasFocus = hasFocus;
        if (onFocusChangeListener != null) {
            onFocusChangeListener.onFocusChange(realView, hasFocus);
        }
    }

    public boolean hasFocus() {
        return hasFocus;
    }

    public void setOnFocusChangeListener(View.OnFocusChangeListener listener) {
        onFocusChangeListener = listener;
    }
}

