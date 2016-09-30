package com.beesham.theinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.beesham.theinventory.data.ProductContract;

/**
 * Created by Beesham on 9/27/2016.
 */
public class ProductCursorAdapter extends CursorAdapter{

    private static final String LOG_TAG = ProductCursorAdapter.class.getSimpleName();

    private TextView title;
    private TextView currentQuantity;
    private TextView price;
    private Button  saleButton;

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }



    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        title = (TextView) view.findViewById(R.id.title_textview);
        currentQuantity = (TextView) view.findViewById(R.id.current_quantity_textview);
        price = (TextView) view.findViewById(R.id.price_textview);
        saleButton = (Button) view.findViewById(R.id.sale_button);

        title.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME)));
        currentQuantity.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_CURRENT_QUANTITY)));
        price.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE)));

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                int quantity = Integer.parseInt(currentQuantity.getText().toString())-1;
                if(quantity > -1){
                    values.put(ProductContract.ProductEntry.COLUMN_CURRENT_QUANTITY, Integer.toString(quantity));
                    context.getContentResolver().update(
                            ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI,
                                    Long.parseLong(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry._ID)))),
                            values,
                            null,
                            null);
                }else{
                    Toast.makeText(context, R.string.out_of_stock_message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
