package ir.vasl.magicalpdfeditor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.io.File;
import java.util.Objects;

import ir.vasl.magicalpdfeditor.BaseClasses.BaseActivity;
import ir.vasl.magicalpdfeditor.Utils.PublicValue;
import ir.vasl.magicalpec.utils.PublicFunction;
import ir.vasl.magicalpec.viewModel.MagicalPdfConverterViewModel;

import static ir.vasl.magicalpdfeditor.Utils.PublicFunction.showAlerter;

public class PdfToImageActivity
        extends BaseActivity
        implements View.OnClickListener {

    private static final String TAG = "PdfToImageActivity";

    private ProgressBar progressBar = null;

    private Uri currUri = null;
    private MagicalPdfConverterViewModel magicalPdfConverterViewModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_to_image);

        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.button_choose_image).setOnClickListener(this);
        findViewById(R.id.button_convert_image_to_pdf).setOnClickListener(this);

        initViewModel();
    }

    private void initViewModel() {
        magicalPdfConverterViewModel = new ViewModelProvider(PdfToImageActivity.this).get(MagicalPdfConverterViewModel.class);
        magicalPdfConverterViewModel.getPdfConverterStatus().observe(PdfToImageActivity.this, new Observer<MagicalPdfConverterViewModel.PdfConverterStatusEnum>() {
            @Override
            public void onChanged(MagicalPdfConverterViewModel.PdfConverterStatusEnum pdfConverterStatusEnum) {
                switch (pdfConverterStatusEnum) {
                    case IDLE:
                        Log.i(TAG, "onChanged: " + pdfConverterStatusEnum);
                        progressBar.setVisibility(View.GONE);
                        break;
                    case PROCESSING:
                        Log.i(TAG, "onChanged: " + pdfConverterStatusEnum);
                        progressBar.setVisibility(View.VISIBLE);
                        break;
                    case SUCCESS:
                        Log.i(TAG, "onChanged: " + pdfConverterStatusEnum);
                        progressBar.setVisibility(View.GONE);
                        break;
                    case FAILED:
                        Log.i(TAG, "onChanged: " + pdfConverterStatusEnum);
                        progressBar.setVisibility(View.GONE);
                        showAlerter(PdfToImageActivity.this, "We have error on PDF process");
                        break;
                }
            }
        });

        magicalPdfConverterViewModel.getSavePdfDestination().observe(PdfToImageActivity.this, new Observer<String>() {
            @Override
            public void onChanged(String savePdfDestination) {
                if (savePdfDestination != null)
                    openSavedPdfFile(savePdfDestination);
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_choose_image) {
            launchImagePicker();
        } else if (v.getId() == R.id.button_convert_image_to_pdf) {
            convertImageToPDF();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PublicValue.KEY_REQUEST_FILE_PICKER) {
            if (data != null && data.getData() != null) {
                this.currUri = data.getData();
                ((TextView) findViewById(R.id.textView_choose_image)).setText(currUri.getPath());
            }
        }
    }

    public void convertImageToPDF() {

        if (currUri == null) {
            Toast.makeText(this, "Please select valid image file", Toast.LENGTH_SHORT).show();
            return;
        }

        if (getExternalFilesDir(null) == null) {
            Toast.makeText(this, "ExternalFilesDir is not valid", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = "pdfFile" + PublicFunction.getRandomNumber();
        String savePdfDistance = Objects.requireNonNull(getExternalFilesDir(null)).toString() + "/MagicalPdfEditor/" + fileName + ".PDF";

        magicalPdfConverterViewModel.convertImageToPdf(savePdfDistance, currUri);

    }

    private void openSavedPdfFile(String fileAddress) {

        ((TextView) findViewById(R.id.textView_saved_pdf_location)).setText(fileAddress);

        File file = new File(fileAddress);
        Intent target = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(PdfToImageActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);
        target.setDataAndType(uri, "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent intent = Intent.createChooser(target, "Open File");
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Instruct the user to install a PDF reader here, or something", Toast.LENGTH_SHORT).show();
        }
    }

}