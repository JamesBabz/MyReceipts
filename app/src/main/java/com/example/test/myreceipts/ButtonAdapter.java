package com.example.test.myreceipts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James on 16-04-2018.
 */

public class ButtonAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> categoryNames = new ArrayList<String>();


    public ButtonAdapter(Context c, List<String> categoryNames) {
        // Gets the context so it can be used later
        mContext = c;
        this.categoryNames = categoryNames;
    }

    /**
     * Total number of things contained within the adapter
     *
     * @return The amount of items in the list
     */
    public int getCount() {
        return categoryNames.size();
    }

    /**
     * Required for structure, not used in my code. Can be
     * used to get an object at a specific position
     *
     * @param position Position of desired object
     * @return Object at position (not used so null)
     */
    public Object getItem(int position) {
        return null;
    }

    /**
     * Required for structure, not really used in my code. Can
     * be used to get the id of an item in the adapter for
     * manual control.
     *
     * @param position Position of desired object
     * @return the id of the object
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * Gets the view at a specific position. To be used in loops.
     *
     * @param position    Position of the object
     * @param convertView The view to be converted to a button
     * @param parent      The parent object for the button
     * @return The view (button)
     */
    public View getView(final int position,
                        View convertView, ViewGroup parent) {
        Button btn;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            btn = new Button(mContext);
//            btn.setLayoutParams(new GridView.LayoutParams(100, 55));
            btn.setPadding(8, 8, 8, 8);
        } else {
            btn = (Button) convertView;
        }

        btn.setText(categoryNames.get(position));
        // filenames is an array of strings
        if (position == 0) {
            btn.setBackgroundColor(Color.parseColor("#ffb41e"));
        } else {
            btn.setBackgroundColor(Color.DKGRAY);
        }
        btn.setTextColor(Color.WHITE);
        btn.setId(position);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CategoryActivity.class);
                intent.putExtra("categoryName", categoryNames.get(position));
                mContext.startActivity(intent);
            }
        });

        return btn;
    }
}