package com.example.test.myreceipts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James on 16-04-2018.
 */

public class ButtonAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> categoryNames = new ArrayList<String>();


    // Gets the context so it can be used later
    public ButtonAdapter(Context c, List<String> categoryNames) {
        mContext = c;
        this.categoryNames = categoryNames;
    }

    // Total number of things contained within the adapter
    public int getCount() {
        return categoryNames.size();
    }

    // Require for structure, not really used in my code.
    public Object getItem(int position) {
        return null;
    }

    // Require for structure, not really used in my code. Can
    // be used to get the id of an item in the adapter for
    // manual control.
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position,
                        View convertView, ViewGroup parent) {
        Button btn;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            btn = new Button(mContext);
//            btn.setLayoutParams(new GridView.LayoutParams(100, 55));
            btn.setPadding(8, 8, 8, 8);
        }
        else {
            btn = (Button) convertView;
        }

        btn.setText(categoryNames.get(position));
        // filenames is an array of strings
        if(position == 0){
            btn.setBackgroundColor(Color.parseColor("#ffb41e"));
        }else{
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