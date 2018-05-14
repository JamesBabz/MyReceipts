package com.example.test.myreceipts;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.test.myreceipts.BLL.ReceiptService;
import com.example.test.myreceipts.Entity.Receipt;

import java.util.List;

/**
 * Created by thomas on 14-05-2018.
 */

public class ListAdapter extends ArrayAdapter<Receipt> {

    private List<Receipt> receipts;
    Context context;
    ReceiptService receiptService;

    // Array of colors to set in listView
    private final int[] colors = {
            Color.parseColor("#ffffff"),
            Color.parseColor("#ffe7b2")
    };

    public ListAdapter(Context context, int textViewResourceId,
                       List<Receipt> receipts) {
        super(context, textViewResourceId, receipts);
        this.receipts = receipts;
        this.context = context;
        receiptService = new ReceiptService();
    }


    @Override
    public View getView(int position, View v, ViewGroup parent) {

        if (v == null) {
            LayoutInflater li = LayoutInflater.from(context);

            v = li.inflate(R.layout.cell_extended, parent, false);
        }

        v.setBackgroundColor(colors[position % colors.length]);

        Receipt receipt = receipts.get(position);

        TextView name = v.findViewById(R.id.twReceiptName);
        TextView date = v.findViewById(R.id.twReceiptDate);
        ImageView receiptImg = v.findViewById(R.id.imageViewReceipt);

        name.setText(receipt.getName());
        date.setText(receipt.getDate());
        receiptImg.setImageBitmap(receipt.getBitmap());

        notifyDataSetChanged();
        return v;
    }

}
