package ir.vasl.magicalpdfeditor.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
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

    public static byte[] getByteFromDrawable(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static byte[] getByteFromDrawableWithColor(Context context, int color, int resDrawable) {
        if (color == 0)
            color = 0xffffffff;

        final Resources res = context.getResources();
        @SuppressLint("UseCompatLoadingForDrawables") Drawable maskDrawable = res.getDrawable(resDrawable);
        if (!(maskDrawable instanceof BitmapDrawable)) {
            return null;
        }

        Bitmap maskBitmap = ((BitmapDrawable) maskDrawable).getBitmap();
        final int width = 324; /*maskBitmap.getWidth();*/
        final int height = 324; /*maskBitmap.getHeight();*/

        Bitmap outBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outBitmap);
        canvas.drawBitmap(maskBitmap, 0, 0, null);

        Paint maskedPaint = new Paint();
        maskedPaint.setColor(color);
        maskedPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));

        canvas.drawRect(0, 0, width, height, maskedPaint);

        BitmapDrawable outDrawable = new BitmapDrawable(res, outBitmap);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        outDrawable.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static Drawable getTintedDrawable(Resources res, @DrawableRes int drawableResId, @ColorRes int colorResId) {
        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = res.getDrawable(drawableResId);
        int color = res.getColor(colorResId);
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    public static byte[] getByteFromColoredBitmap(Context context, Drawable drawable, int color) {

        Bitmap sourceBitmap = ((BitmapDrawable) drawable).getBitmap();
        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
                sourceBitmap.getWidth() - 1, sourceBitmap.getHeight() - 1);
        Paint p = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 1);
        p.setColorFilter(filter);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);

        ImageView image = new ImageView(context);
        image.setImageBitmap(resultBitmap);

        return getByteFromDrawable(image.getDrawable());
    }

    public static Drawable changeDrawableColor(int drawableRes, int colorRes, Context context) {
        //Convert drawable res to bitmap
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableRes);
        final Bitmap resultBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth() - 1, bitmap.getHeight() - 1);
        final Paint p = new Paint();
        final Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);

        //Create new drawable based on bitmap
        final Drawable drawable = new BitmapDrawable(context.getResources(), resultBitmap);
        drawable.setColorFilter(new
                PorterDuffColorFilter(context.getResources().getColor(colorRes), PorterDuff.Mode.MULTIPLY));
        return drawable;
    }

    public static BitmapDrawable getColoredBitmap(int color, Context context, int drawableId) {

        Bitmap source = BitmapFactory.decodeResource(context.getResources(), drawableId);
        final Bitmap bitmap = Bitmap.createBitmap(source.getWidth(),
                source.getHeight(), Bitmap.Config.ARGB_8888);
        for (int i = 0; i < source.getWidth(); i++) {
            for (int j = 0; j < source.getHeight(); j++) {
                int pixel = source.getPixel(i, j);

                if (pixel == Color.WHITE) {
                    pixel = Color.argb(Color.alpha(pixel),
                            Color.red(Color.WHITE), Color.green(Color.WHITE),
                            Color.blue(Color.WHITE));
                } else {
                    pixel = Color.argb(Color.alpha(pixel), Color.red(color),
                            Color.green(color), Color.blue(color));
                }
                bitmap.setPixel(i, j, pixel);
            }
        }
        return new BitmapDrawable(context.getResources(), bitmap);
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
