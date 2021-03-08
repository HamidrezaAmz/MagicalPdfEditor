package ir.vasl.magicalpec.utils.Core;

import android.content.Context;
import android.net.Uri;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;

import ir.vasl.magicalpec.utils.Exceptions.MagicalException;
import ir.vasl.magicalpec.utils.PathUtil;

public class MagicalPdfConverter {

    private static MagicalPdfConverter magicalPdfConverter = null;

    public static MagicalPdfConverter getInstance() {
        if (magicalPdfConverter == null) {
            magicalPdfConverter = new MagicalPdfConverter();
        }
        return magicalPdfConverter;
    }

    public String convertImageIntoPDF(Context context, String savePdfDestination, Uri imageUri) throws MagicalException {

        if (savePdfDestination == null || savePdfDestination.isEmpty())
            throw new MagicalException("Save PDF file distance is not valid");

        if (imageUri == null)
            throw new MagicalException("Image URI is not valid");

        try {

            File file = new File(savePdfDestination);
            if (!file.exists()) {
                String parent = file.getParent();
                if (parent != null)
                    new File(parent).mkdirs();
            }

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(savePdfDestination));
            document.open();
            Image image = Image.getInstance(PathUtil.getPath(context, imageUri));

            float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                    - document.rightMargin() - 0) / image.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
            image.scalePercent(scaler);
            image.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);

            document.add(image);
            document.close();
            return savePdfDestination;
        } catch (Exception e) {
            throw new MagicalException(e.getMessage());
        }
    }
}
