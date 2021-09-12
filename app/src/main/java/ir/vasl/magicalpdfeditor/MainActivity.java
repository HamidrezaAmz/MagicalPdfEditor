package ir.vasl.magicalpdfeditor;

import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.link.LinkHandler;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnLongPressListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.github.barteksc.pdfviewer.model.LinkTapEvent;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import org.benjinus.pdfium.Bookmark;
import org.benjinus.pdfium.Meta;

import java.util.List;
import java.util.UUID;

import ir.vasl.magicalpdfeditor.BaseClasses.BaseActivity;
import ir.vasl.magicalpdfeditor.Utils.FileUtils;
import ir.vasl.magicalpdfeditor.Utils.Interfaces.GlobalClickCallBack;
import ir.vasl.magicalpdfeditor.Utils.PublicFunction;
import ir.vasl.magicalpdfeditor.Utils.PublicValue;
import ir.vasl.magicalpec.view.MagicalPdfViewer;
import ir.vasl.magicalpec.viewModel.MagicalPECViewModel;

public class MainActivity
        extends BaseActivity
        implements OnPageErrorListener, OnLoadCompleteListener, OnPageChangeListener, OnLongPressListener, LinkHandler, OnTapListener {

    private static final String TAG = "MainActivity";

    private MagicalPdfViewer magicalPdfViewer;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private Uri currUri = null;
    private String currFilePath = null;
    private String currFileName = null;
    private PDFView.Configurator configurator = null;
    private MagicalPECViewModel magicalPECViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initViewModel();
    }

    private void initView() {

        this.toolbar = findViewById(R.id.toolbar);
        this.magicalPdfViewer = findViewById(R.id.magicalPdfViewer);
        this.progressBar = findViewById(R.id.progressBar);

        setSupportActionBar(toolbar);
        setTitle("PDF EDITOR");

        PublicFunction.showAlerterWithAction(MainActivity.this, "CHOOSE YOUR FILE", new GlobalClickCallBack() {
            @Override
            public void onChooseFileClicked() {
                launchFilePicker();
            }
        });
    }

    private void initViewModel() {
        magicalPECViewModel = new ViewModelProvider(MainActivity.this).get(MagicalPECViewModel.class);
        magicalPECViewModel.getPecCoreStatus().observe(MainActivity.this, new Observer<MagicalPECViewModel.PECCoreStatusEnum>() {
            @Override
            public void onChanged(MagicalPECViewModel.PECCoreStatusEnum pecCoreStatusEnum) {
                switch (pecCoreStatusEnum) {
                    case IDLE:
                        Log.i(TAG, "onChanged: " + pecCoreStatusEnum);
                        progressBar.setVisibility(View.GONE);
                        break;
                    case PROCESSING:
                        Log.i(TAG, "onChanged: " + pecCoreStatusEnum);
                        progressBar.setVisibility(View.VISIBLE);
                        break;
                    case SUCCESS:
                        Log.i(TAG, "onChanged: " + pecCoreStatusEnum);
                        progressBar.setVisibility(View.GONE);
                        configurator.refresh(magicalPdfViewer.getCurrentPage()); // refresh view
                        break;
                    case FAILED:
                        Log.i(TAG, "onChanged: " + pecCoreStatusEnum);
                        progressBar.setVisibility(View.GONE);
                        PublicFunction.showAlerter(MainActivity.this, "We have error on PDF process");
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.chooseFile:
                launchFilePicker();
                break;

            case R.id.deleteAllAnnots:
                Toast.makeText(this, "You clicked delete all annotations", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PublicValue.KEY_REQUEST_FILE_PICKER) {
            if (data != null && data.getData() != null) {
                this.currUri = data.getData();
                displayFileFromUri();
            }
        }
    }

    private void displayFileFromUri() {

        if (currUri == null)
            return;

        // TODO: 1/17/21  DON NOT FORGET TO USE YOUR FILE HANDLING SCENARIO FOR NEW ANDROID APIs

        // this is OK
        // /storage/emulated/0/Download/PDF_ENGLISH.pdf
        // this.currFilePath = UriUtils.getPathFromUri(MainActivity.this, currUri);

        // this is not OK
        // /data/user/0/ir.vasl.magicalpdfeditor/files/PDF_ENGLISH.pdf
        // this.currFilePath = PublicFunction.getFilePathForN(MainActivity.this, currUri);

        // this is working
        // /storage/emulated/0/Download/PDF_ENGLISH.pdf
        this.currFilePath = FileUtils.newInstance(MainActivity.this).getPath(currUri);
        this.currFileName = PublicFunction.getFileName(MainActivity.this, currUri);
        this.toolbar.setSubtitle("File Name: " + currFileName);

        this.configurator = magicalPdfViewer.fromUri(currUri)
                .defaultPage(PublicValue.DEFAULT_PAGE_NUMBER)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .enableSwipe(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(this)
                .onTap(this)
                .onLongPress(this)
                .linkHandler(this);

        this.configurator.load();
    }

    @Override
    public void onPageError(int page, Throwable t) {

    }

    @Override
    public void loadComplete(int nbPages) {
        Meta meta = magicalPdfViewer.getDocumentMeta();
        Log.e(TAG, "title = " + meta.getTitle());
        Log.e(TAG, "author = " + meta.getAuthor());
        Log.e(TAG, "subject = " + meta.getSubject());
        Log.e(TAG, "keywords = " + meta.getKeywords());
        Log.e(TAG, "creator = " + meta.getCreator());
        Log.e(TAG, "producer = " + meta.getProducer());
        Log.e(TAG, "creationDate = " + meta.getCreationDate());
        Log.e(TAG, "modDate = " + meta.getModDate());

        printBookmarksTree(magicalPdfViewer.getTableOfContents(), "-");

        PublicFunction.showAlerter(MainActivity.this, "PDF file is ready for test");
    }

    public void printBookmarksTree(List<Bookmark> tree, String sep) {
        for (Bookmark b : tree) {
            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));
            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

        String referenceHash = new StringBuilder()
                .append(PublicValue.KEY_REFERENCE_HASH)
                .append(UUID.randomUUID().toString())
                .toString(); // generate reference hash

        byte[] OCGCover = PublicFunction.getByteFromDrawable(MainActivity.this, R.drawable.ic_logo_v3);
        PointF pointF = magicalPdfViewer.convertScreenPintsToPdfCoordinates(motionEvent); // convert coordinates

        addAnnotation(pointF, referenceHash, OCGCover);
    }

    @Override
    public void handleLinkEvent(LinkTapEvent event) {

        String referenceHash = event.getLink().getUri();
        String message = new StringBuilder()
                .append("Please Choose Your Action")
                .append("\n")
                .append("Delete Or Update")
                .append("\n\n")
                .append("Annot Hash: ")
                .append(referenceHash)
                .append("\n\n")
                .toString();

        PublicFunction.showAlerterWithTwoAction(MainActivity.this, message, new GlobalClickCallBack() {
            @Override
            public void onDeleteAnnotClicked() {
                deleteAnnotation(referenceHash);
            }

            @Override
            public void onUpdateAnnotClicked() {
                updateAnnotation(referenceHash, event);
            }
        });
    }

    @Override
    public boolean onTap(MotionEvent e) {
        return false;
    }

    private void addAnnotation(PointF pointF, String referenceHash, byte[] OCGCover) {

        magicalPECViewModel.addOCG(pointF,
                currUri,
                magicalPdfViewer.getCurrentPage(),
                referenceHash,
                OCGCover);

    }

    private void deleteAnnotation(String referenceHash) {
        magicalPECViewModel.removeOCG(currFilePath, referenceHash);
    }

    private void updateAnnotation(String referenceHash, LinkTapEvent event) {

        PointF pointF = magicalPdfViewer.convertScreenPintsToPdfCoordinates(event.getDocumentX(), event.getDocumentY()); // convert to pdf coordinates
        byte[] newOCGCover = PublicFunction.getByteFromDrawable(MainActivity.this, R.drawable.ic_logo_v2);

        magicalPECViewModel.updateOCG(pointF,
                currFilePath,
                magicalPdfViewer.getCurrentPage(),
                referenceHash,
                newOCGCover);
    }

}