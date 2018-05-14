package com.example.test.myreceipts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.test.myreceipts.BLL.Callback;
import com.example.test.myreceipts.BLL.ReceiptService;
import com.example.test.myreceipts.BLL.UserService;
import com.example.test.myreceipts.Entity.Receipt;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.Console;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by James on 08-05-2018.
 */

public class CategoryActivity extends CustomMenu {
    ReceiptService receiptService;
    UserService userService;
    Spinner spinner;
    ListView listViewCategories;
    ListAdapter listAdapter;
    List<Receipt> allReceipts;
     List<Receipt> returnList = new ArrayList<>();
    private FirebaseFirestore mStore;
    private StorageReference mStorage;
    Bitmap bitmap;

    public CategoryActivity() {
        super(true, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_activity);
        listViewCategories = findViewById(R.id.listViewCategories);

        receiptService = new ReceiptService();
        userService = new UserService();
        mStore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        listViewCategories = findViewById(R.id.listViewCategories);
        String userUid = userService.getCurrentUser().getUid();
       // allReceipts = receiptService.getReceipts(userUid, getIntent().getExtras().getString("categoryName"));

        getAllReceiptsForCategory(userUid, getIntent().getExtras().getString("categoryName"));

    }

    @Override
    protected void onResume() {
        super.onResume();
        createSpinner();

        addListenerOnList();

    }

    private void createSpinner() {
        spinner = findViewById(R.id.spinnerSortBy);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.group_sort_by_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    public void getAllReceiptsForCategory(final String userUid, final String category) {

        mStore.document("users/" + userUid).collection("categories").document(category).collection("fileuids").get() .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> test = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        test.add(document.getId());
                    }

                    getFilesFromStorage(userUid, test);
                } else {
                    Log.w("shiat", "Error getting documents.", task.getException());
                }
            }
        });
    }

    private void getFilesFromStorage(String userUid, List<String> fileuids){

        for (final String fileuid:fileuids) {

            final StorageReference storageReference = mStorage.child("receipts/").child(userUid + "/" + fileuid );

            storageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                @Override
                public void onSuccess(StorageMetadata storageMetadata) {
                    final String fileName = storageMetadata.getCustomMetadata("name");
                    Log.d("fileMetadata", fileName);
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Receipt rec = new Receipt();
                            rec.setName(fileName);
                            rec.setURL(uri.toString());
                            rec.setId(fileuid);

                           // rec.setBitmap(returnbit(uri));



                            returnList.add(rec);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });

                }
            });
        }
    }

    public void shit(View v){
        setList();
    }

    private void setList(){
        listAdapter = new ListAdapter(this, R.layout.cell_extended, returnList);
        listViewCategories.setAdapter(listAdapter);
    }

    //Listens on witch item is clicked
    private void addListenerOnList() {
        listViewCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Receipt entry = (Receipt) parent.getItemAtPosition(position);
                openFriendView(entry);
            }
        });
    }

    //Opens ReceiptActivity with all information about the selected receipt
    private void openFriendView(Receipt entry){

        Intent intent = new Intent(this, ReceiptActivity.class);
        intent.putExtra("RECEIPT", entry);
        Log.d("HELLO", entry.getName());
        startActivity(intent);
    }

}


class ListAdapter extends ArrayAdapter<Receipt> {

    private List<Receipt> receipts;
    Context context;
    ReceiptService receiptService;

    // Array of colors to set in listView
    private final int[] colors = {
            Color.parseColor("#ffffff"),
            Color.parseColor("#b3cbf2")
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

            v = li.inflate(R.layout.cell_extended, parent,false);
        }

        v.setBackgroundColor(colors[position % colors.length]);




        final Receipt receipt = receipts.get(position);

        TextView name = v.findViewById(R.id.twReceiptName);
        TextView date = v.findViewById(R.id.twReceiptDate);
        final ImageView receiptImg = v.findViewById(R.id.imageViewReceipt);

        name.setText(receipt.getName());
        date.setText(receipt.getDate());
       // receiptImg.setImageBitmap(receipt.getBitmap());


        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    loadImageFromURL(receipt, receiptImg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        return v;
    }

    private void loadImageFromURL(Receipt receipt, ImageView receiptImg) {
        loadImageFromURL(receipt.getURL().toString(), receiptImg);
    }

    public boolean loadImageFromURL(String fileUrl,
                                    ImageView iv){
        try {

            URL myFileUrl = new URL (fileUrl);
            HttpURLConnection conn =
                    (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();

            InputStream is = conn.getInputStream();
            iv.setImageBitmap(BitmapFactory.decodeStream(is));

            return true;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
