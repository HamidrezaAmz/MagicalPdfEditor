package ir.vasl.magicalpdfeditor.Utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;

import androidx.core.content.FileProvider;

import com.tapadoo.alerter.Alerter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import ir.vasl.magicalpdfeditor.BuildConfig;
import ir.vasl.magicalpdfeditor.R;
import ir.vasl.magicalpdfeditor.Utils.Interfaces.GlobalClickCallBack;

public class PublicFunction {

    public static void showAlerter(Activity activity, String message) {

        Alerter.create(activity)
                .setTitle(message)
                .setBackgroundColorRes(R.color.teal_200) // or setBackgroundColorInt(Color.CYAN)
                .show();
    }

    public static void showAlerterWithAction(Activity activity, String message, GlobalClickCallBack globalClickCallBack) {

        Alerter.create(activity)
                .setTitle(message)
                .enableInfiniteDuration(true)
                .setBackgroundColorRes(R.color.teal_200) // or setBackgroundColorInt(Color.CYAN)
                .addButton("Choose File", R.style.AlertButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Alerter.hide();
                        if (globalClickCallBack != null)
                            globalClickCallBack.onChooseFileClicked();
                    }
                }).show();
    }

    public static void showAlerterWithTwoAction(Activity activity, String message, GlobalClickCallBack globalClickCallBack) {

        Alerter.create(activity)
                .setTitle(message)
                .enableInfiniteDuration(true)
                .setBackgroundColorRes(R.color.teal_200) // or setBackgroundColorInt(Color.CYAN)
                .addButton("Delete Annot", R.style.AlertButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Alerter.hide();
                        if (globalClickCallBack != null)
                            globalClickCallBack.onDeleteAnnotClicked();
                    }
                }).addButton("Update Annot", R.style.AlertButton, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alerter.hide();
                if (globalClickCallBack != null)
                    globalClickCallBack.onUpdateAnnotClicked();
            }
        }).show();
    }

    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    public static byte[] getByteFromDrawable(Context context, int resDrawable) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resDrawable);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static String getFilePathForN(Context context, Uri uri) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getFilesDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }

    public static File getFile(Context context, String fileName) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return null;
        }

        File storageDir = context.getExternalFilesDir(null);
        return new File(storageDir, fileName);
    }

    public static Uri getFileUri(Context context, String fileName) {
        File file = getFile(context, fileName);
        return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
    }
}
