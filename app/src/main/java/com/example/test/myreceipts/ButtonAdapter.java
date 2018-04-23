package com.example.test.myreceipts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by James on 16-04-2018.
 */

public class ButtonAdapter extends BaseAdapter {
    private Context mContext;
    private String[] groupNames;



    // Gets the context so it can be used later
    public ButtonAdapter(Context c, String[] groupNames) {
        mContext = c;
        this.groupNames = groupNames;
    }

    // Total number of things contained within the adapter
    public int getCount() {
        return groupNames.length;
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

        btn.setText(groupNames[position]);
        // filenames is an array of strings
        btn.setTextColor(Color.WHITE);
        btn.setBackgroundColor(Color.DKGRAY);
        btn.setId(position);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,"HEJEHEJEHEJHEJE", Toast.LENGTH_LONG);
//                Log.i("test", "text");
                Intent intent = new Intent(mContext, ImageGroup.class);
                intent.putExtra("groupName", groupNames[position]);
                mContext.startActivity(intent);
            }
        });

        return btn;
    }
}