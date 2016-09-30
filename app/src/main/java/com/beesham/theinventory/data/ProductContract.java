package com.beesham.theinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Beesham on 9/27/2016.
 */
public class ProductContract {
    private ProductContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.beesham.theinventory.products";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "products";

    public static class ProductEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        //MIME type of {@link #CONTENT_URI} for a list of products.
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        //MIME type of {@link #CONTENT_URI} for a single products.
        public static final String CONTENT_ITEM_BASE_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;


        public static final String TABLE_NAME = "products";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_DESCRIPTION = "description";
        public static final String COLUMN_PRODUCT_IMAGE = "image";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_CURRENT_QUANTITY = "current_quantity";
        public static final String COLUMN_MANUFACTURER = "manufacturer";
        public static final String COLUMN_MANUFACTURER_PHONE = "manufacturer_phone";
        public static final String COLUMN_MANUFACTURER_EMAIL = "manufacturer_email";
    }

}
