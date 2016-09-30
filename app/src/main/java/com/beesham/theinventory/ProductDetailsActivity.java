package com.beesham.theinventory;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.beesham.theinventory.data.ProductContract;
import com.beesham.theinventory.data.ProductDbHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProductDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = ProductDetailsActivity.class.getSimpleName();

    private static final int PRODUCT_LOADER_ID = 1;

    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private Bitmap mCurrentPhotoBitmap;

    private EditText mNameEdittext;
    private EditText mDescriptionEdittext;
    private EditText mPriceEdittext;
    private EditText mManufacturerNameEdittext;
    private EditText mManufacturerPhoneEdittext;
    private EditText mManufacturerEmailEdittext;
    private EditText mCurrentQuantityEdittext;
    private ImageView mProductImage;
    private Button mIncreaseStockButton;
    private Button mDecreaseStockButton;

    private Uri mUri;
    private boolean mProductHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        mUri = getIntent().getData();
        if(mUri == null){
            setTitle(getString(R.string.title_new_product));
            invalidateOptionsMenu();
        }else{
            setTitle(getString(R.string.title_edit_product));
            getSupportLoaderManager().initLoader(PRODUCT_LOADER_ID, null, this);
        }

        mNameEdittext = (EditText) findViewById(R.id.name_edittext);
        mDescriptionEdittext = (EditText) findViewById(R.id.description_edittext);
        mPriceEdittext = (EditText) findViewById(R.id.price_edittext);
        mManufacturerNameEdittext = (EditText) findViewById(R.id.manufacturer_name_edittext);
        mManufacturerPhoneEdittext = (EditText) findViewById(R.id.manufacturer_phone_edittext);
        mManufacturerEmailEdittext = (EditText) findViewById(R.id.manufacturer_email_edittext);
        mCurrentQuantityEdittext = (EditText) findViewById(R.id.current_quantity_edittext);
        mProductImage = (ImageView) findViewById(R.id.imageview);
        mIncreaseStockButton = (Button) findViewById(R.id.increase_stock_button);
        mDecreaseStockButton = (Button) findViewById(R.id.decrease_stock_button);

        mNameEdittext.setOnTouchListener(mTouchListener);
        mDescriptionEdittext.setOnTouchListener(mTouchListener);
        mPriceEdittext.setOnTouchListener(mTouchListener);
        mManufacturerNameEdittext.setOnTouchListener(mTouchListener);
        mManufacturerPhoneEdittext.setOnTouchListener(mTouchListener);
        mManufacturerEmailEdittext.setOnTouchListener(mTouchListener);
        mCurrentQuantityEdittext.setOnTouchListener(mTouchListener);
        mCurrentQuantityEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mProductHasChanged = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

        mIncreaseStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentQuantityEdittext.setText(Integer.toString(Integer.parseInt(mCurrentQuantityEdittext.getText().toString())+1));
            }
        });

        mDecreaseStockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentQuantityEdittext.setText(Integer.toString(Integer.parseInt(mCurrentQuantityEdittext.getText().toString())-1));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.productdetails, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if(mUri == null){
            MenuItem menuItem = menu.findItem(R.id.delete);
            menuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.done:
                saveEntry();
                finish();
                return true;

            case R.id.delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                if(!mProductHasChanged){
                    NavUtils.navigateUpFromSameTask(ProductDetailsActivity.this);
                    return true;
                }
                unsavedChangesHelper();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        unsavedChangesHelper();
    }

    private void unsavedChangesHelper(){
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                NavUtils.navigateUpFromSameTask(ProductDetailsActivity.this);
            }
        };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_message);
        builder.setPositiveButton(R.string.unsaved_changes_discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.unsaved_changes_keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(dialogInterface != null){
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveEntry() {
        //TODO: data validation
        String name = mNameEdittext.getText().toString().trim();
        String description = mDescriptionEdittext.getText().toString().trim();
        String price = mPriceEdittext.getText().toString().trim();
        String manufacturerName = mManufacturerNameEdittext.getText().toString().trim();
        String manufacturerPhone = mManufacturerPhoneEdittext.getText().toString().trim();
        String manufacturerEmail = mManufacturerEmailEdittext.getText().toString().trim();
        String currentQuantity = mCurrentQuantityEdittext.getText().toString().trim();
        byte[] productImageBitmap = convertToByteArray();

        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, name);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_DESCRIPTION, description);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE, productImageBitmap);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(ProductContract.ProductEntry.COLUMN_MANUFACTURER, manufacturerName);
        values.put(ProductContract.ProductEntry.COLUMN_MANUFACTURER_PHONE, manufacturerPhone);
        values.put(ProductContract.ProductEntry.COLUMN_MANUFACTURER_EMAIL, manufacturerEmail);
        values.put(ProductContract.ProductEntry.COLUMN_CURRENT_QUANTITY, currentQuantity);

        if(mUri == null) {
            Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);
        }else{
            int rowId = getContentResolver().update(mUri, values, null, null);
        }
    }

    private byte[] convertToByteArray(){
        byte[] bytes = null;
        if(mProductImage != null){
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            mCurrentPhotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            bytes = out.toByteArray();
        }
        return bytes;
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_product_message));
        builder.setPositiveButton(getText(R.string.delete_product_option_delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(getText(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(dialogInterface != null){
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct(){
        int rowsDeleted = 0;
        if(mUri != null) {
            rowsDeleted = getContentResolver().delete(mUri, null, null);
        }

        if (rowsDeleted == 0) {
            Toast.makeText(this, getString(R.string.delete_product_failed_message),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.delete_product_successful_message),
                    Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            mCurrentPhotoBitmap = (Bitmap) extras.get("data");
            mProductImage.setImageBitmap(mCurrentPhotoBitmap);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_DESCRIPTION,
                ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE,
                ProductContract.ProductEntry.COLUMN_CURRENT_QUANTITY,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_MANUFACTURER,
                ProductContract.ProductEntry.COLUMN_MANUFACTURER_PHONE,
                ProductContract.ProductEntry.COLUMN_MANUFACTURER_EMAIL
        };

        switch(id){
            case PRODUCT_LOADER_ID:
                return new CursorLoader(this, mUri, projection, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor != null){
            if(cursor.moveToFirst()){
                mNameEdittext.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME)));
                mDescriptionEdittext.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_DESCRIPTION)));
                mPriceEdittext.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE)));
                mCurrentQuantityEdittext.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_CURRENT_QUANTITY)));
                mManufacturerNameEdittext.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_MANUFACTURER)));
                mManufacturerPhoneEdittext.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_MANUFACTURER_PHONE)));
                mManufacturerEmailEdittext.setText(cursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_MANUFACTURER_EMAIL)));

                byte[] image = cursor.getBlob(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE));
                mCurrentPhotoBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                mProductImage.setImageBitmap(mCurrentPhotoBitmap);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEdittext.setText(null);
        mDescriptionEdittext.setText(null);
        mPriceEdittext.setText(null);
        mCurrentQuantityEdittext.setText(null);
        mManufacturerNameEdittext.setText(null);
        mManufacturerPhoneEdittext.setText(null);
        mManufacturerEmailEdittext.setText(null);
    }
}
