package com.example.test.myreceipts.BLL;

import com.example.test.myreceipts.Entity.Receipt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James on 16-04-2018.
 */

public class ReceiptService {

    List<Receipt> receipts = new ArrayList<>();
    private static ReceiptService instance;

    private ReceiptService() {
    }

    public static ReceiptService getInstance() {
        if(instance == null){
            instance = new ReceiptService();
        }
        return instance;
    }


    public List<Receipt> getReceipts() {
        return receipts;
    }

    public void setReceipts(List<Receipt> receipts) {
        this.receipts = receipts;
    }

    public void addReceipt(Receipt receipt) {
        this.receipts.add(receipt);
    }
}
