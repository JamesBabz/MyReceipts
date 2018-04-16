package com.example.test.myreceipts;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by James on 16-04-2018.
 */

public class CategoryAdapter extends BaseAdapter {

    private final Context mContext;

    // 1
    public CategoryAdapter(Context context) {
        this.mContext = context;
    }

    // 2
    @Override
    public int getCount() {
        return 0;
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    // 5
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView dummyTextView = new TextView(mContext);
        dummyTextView.setText(String.valueOf(position));
        return dummyTextView;
    }

}