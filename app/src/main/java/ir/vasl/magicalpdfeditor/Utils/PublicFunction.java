package ir.vasl.magicalpdfeditor.Utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.view.View;

import com.tapadoo.alerter.Alerter;

import java.io.ByteArrayOutputStream;

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
}
