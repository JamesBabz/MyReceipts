package com.example.test.myreceipts;

import android.os.Bundle;

/**
 * Created by thomas on 14-05-2018.
 */

public class ReceiptActivity extends CustomMenu {
    public ReceiptActivity() {
        super(true, true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);


    }

}
