package ir.vasl.magicalpec.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.Random;

public class PublicFunction {

    public static int getRandomNumber() {

        int min = 10000;
        int max = 50000;

        Random rand = new Random();

        return rand.nextInt((max - min) + 1) + min;
    }

    public static byte[] getByteFromDrawable(Context context, int resDrawable) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resDrawable);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

}
