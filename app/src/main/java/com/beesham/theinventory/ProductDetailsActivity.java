package com.beesham.theinventory;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.beesham.theinventory.data.ProductContract;
import com.beesham.theinventory.data.ProductDbHelper;

public class ProductDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER_ID = 1;

    EditText mNameEdittext;
    EditText mDescriptionEdittext;
    EditText mPriceEdittext;
    EditText mManufacturerNameEdittext;
    EditText mManufacturerPhoneEdittext;
    EditText mManufacturerEmailEdittext;
    EditText mCurrentQuantityEdittext;

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
            invalidateOptionsMenu();
        }

        mNameEdittext = (EditText) findViewById(R.id.name_edittext);
        mDescriptionEdittext = (EditText) findViewById(R.id.description_edittext);
        mPriceEdittext = (EditText) findViewById(R.id.price_edittext);
        mManufacturerNameEdittext = (EditText) findViewById(R.id.manufacturer_name_edittext);
        mManufacturerPhoneEdittext = (EditText) findViewById(R.id.manufacturer_phone_edittext);
        mManufacturerEmailEdittext = (EditText) findViewById(R.id.manufacturer_email_edittext);
        mCurrentQuantityEdittext = (EditText) findViewById(R.id.current_quantity_edittext);

        mNameEdittext.setOnTouchListener(mTouchListener);
        mDescriptionEdittext.setOnTouchListener(mTouchListener);
        mPriceEdittext.setOnTouchListener(mTouchListener);
        mManufacturerNameEdittext.setOnTouchListener(mTouchListener);
        mManufacturerPhoneEdittext.setOnTouchListener(mTouchListener);
        mManufacturerEmailEdittext.setOnTouchListener(mTouchListener);
        mCurrentQuantityEdittext.setOnTouchListener(mTouchListener);

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
                finish();
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

        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, name);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_DESCRIPTION, description);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(ProductContract.ProductEntry.COLUMN_MANUFACTURER, manufacturerName);
        values.put(ProductContract.ProductEntry.COLUMN_MANUFACTURER_PHONE, manufacturerPhone);
        values.put(ProductContract.ProductEntry.COLUMN_MANUFACTURER_EMAIL, manufacturerEmail);
        values.put(ProductContract.ProductEntry.COLUMN_CURRENT_QUANTITY, currentQuantity);

        Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_DESCRIPTION,
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
