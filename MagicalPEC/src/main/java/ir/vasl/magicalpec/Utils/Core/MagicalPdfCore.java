package ir.vasl.magicalpec.Utils.Core;

import android.graphics.PointF;

import com.lowagie.text.Annotation;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfImage;
import com.lowagie.text.pdf.PdfIndirectObject;
import com.lowagie.text.pdf.PdfLayer;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import ir.vasl.magicalpec.Utils.Exceptions.MagicalException;
import ir.vasl.magicalpec.Utils.OCGHelper.OCGRemover;
import ir.vasl.magicalpec.Utils.PublicValue;

public class MagicalPdfCore {

    private static MagicalPdfCore instance;

    public static MagicalPdfCore getInstance() {
        if (instance == null)
            instance = new MagicalPdfCore();
        return instance;
    }

    public boolean addOCG(PointF pointF, String filePath, int currPage, String referenceHash, byte[] OCGCover) throws MagicalException {
        return addOCG(pointF, filePath, currPage, referenceHash, OCGCover, 0, 0);
    }

    public boolean addOCG(PointF pointF, String filePath, int currPage, String referenceHash, byte[] OCGCover, float OCGWidth, float OCGHeight) throws MagicalException {

        // Hint: OCG -> optional content group
        // Hint: Page Starts From --> 1 In OpenPdf Core
        currPage++;

        // OCG width & height
        if (OCGWidth == 0 || OCGHeight == 0) {
            OCGWidth = PublicValue.DEFAULT_OCG_WIDTH;
            OCGHeight = PublicValue.DEFAULT_OCG_HEIGHT;
        }

        // get file and FileOutputStream
        if (filePath == null || filePath.isEmpty())
            throw new MagicalException("Input file is empty");

        File file = new File(filePath);

        if (!file.exists())
            throw new MagicalException("Input file does not exists");

        try {

            // inout stream from file
            InputStream inputStream = new FileInputStream(file);

            // we create a reader for a certain document
            PdfReader reader = new PdfReader(inputStream);

            // we create a stamper that will copy the document to a new file
            PdfStamper stamp = new PdfStamper(reader, new FileOutputStream(file));

            // get watermark icon
            Image img = Image.getInstance(OCGCover);
            img.setAnnotation(new Annotation(0, 0, 0, 0, referenceHash));
            img.scaleAbsolute(OCGWidth, OCGHeight);
            img.setAbsolutePosition(pointF.x, pointF.y);
            PdfImage stream = new PdfImage(img, referenceHash, null);
            stream.put(new PdfName(PublicValue.KEY_SPECIAL_ID), new PdfName(referenceHash));
            PdfIndirectObject ref = stamp.getWriter().addToBody(stream);
            img.setDirectReference(ref.getIndirectReference());

            // add as layer
            PdfLayer wmLayer = new PdfLayer(referenceHash, stamp.getWriter());

            // prepare transparency
            PdfGState transparent = new PdfGState();
            transparent.setAlphaIsShape(false);

            // get page file number count
            if (reader.getNumberOfPages() < currPage) {
                stamp.close();
                reader.close();
                throw new MagicalException("Page index is out of pdf file page numbers");
            }

            // add annotation into target page
            PdfContentByte over = stamp.getOverContent(currPage);
            if (over == null) {
                stamp.close();
                reader.close();
                throw new MagicalException("GetUnderContent() is null");
            }

            // add as layer
            over.beginLayer(wmLayer);
            over.setGState(transparent); // set block transparency properties
            over.addImage(img);
            over.endLayer();

            // closing PdfStamper will generate the new PDF file
            stamp.close();

            // close reader
            reader.close();

            // finish method
            return true;

        } catch (Exception e) {
            throw new MagicalException(e.getMessage());
        }
    }

    public boolean removeOCG(String filePath, String annotationHash) throws MagicalException {

        // get file and FileOutputStream
        if (filePath == null || filePath.isEmpty())
            throw new MagicalException("Input file is empty");

        File file = new File(filePath);

        if (!file.exists())
            throw new MagicalException("Input file does not exists");

        try {

            // inout stream from file
            InputStream inputStream = new FileInputStream(file);

            // we create a reader for a certain document
            PdfReader pdfReader = new PdfReader(inputStream);

            // we create a stamper that will copy the document to a new file
            PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(file));

            // remove target object
            OCGRemover ocgRemover = new OCGRemover();
            ocgRemover.removeLayers(pdfReader, annotationHash);

            // closing PdfStamper will generate the new PDF file
            pdfStamper.close();

            // close reader
            pdfReader.close();

            // finish method
            return true;

        } catch (Exception e) {
            throw new MagicalException(e.getMessage());
        }
    }

    public boolean removeAllOCGs() {
        return true;
    }

    public boolean updateOCG(PointF pointF, String filePath, int currPage, String referenceHash, byte[] newOCGCover) throws MagicalException {

        // remove old OCG
        if (!removeOCG(filePath, referenceHash))
            throw new MagicalException("Cannot remove OCG with target reference");

        // add new OCG
        if (!addOCG(pointF, filePath, currPage, referenceHash, newOCGCover))
            throw new MagicalException("Cannot add new OCG with target reference");

        // finish method
        return true;

    }

}
