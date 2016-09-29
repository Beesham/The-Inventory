package com.beesham.theinventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.beesham.theinventory.data.ProductContract;

/**
 * Created by Beesham on 9/27/2016.
 */
public class ProductCursorAdapter extends CursorAdapter{

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView title = (TextView) view.findViewById(R.id.title_textview);
        TextView currentQuantity = (TextView) view.findViewById(R.id.current_quantity_textview);
        TextView price = (TextView) view.findViewById(R.id.price_textview);

        title.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME)));
        currentQuantity.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_CURRENT_QUANTITY)));
        price.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE)));
        //TODO: implement the other details
    }
}
