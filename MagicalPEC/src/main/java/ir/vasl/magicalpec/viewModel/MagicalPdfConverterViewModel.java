package ir.vasl.magicalpec.viewModel;

import android.app.Application;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import ir.vasl.magicalpec.utils.Core.MagicalPdfConverter;
import ir.vasl.magicalpec.utils.Exceptions.MagicalException;

public class MagicalPdfConverterViewModel extends AndroidViewModel {

    public enum PdfConverterStatusEnum {IDLE, PROCESSING, FAILED, SUCCESS}

    private MutableLiveData<PdfConverterStatusEnum> pdfConverterStatus;
    private MutableLiveData<String> savePdfDestinationLiveData;

    public MagicalPdfConverterViewModel(@NonNull Application application) {
        super(application);
        this.pdfConverterStatus = new MutableLiveData<>();
        this.savePdfDestinationLiveData = new MutableLiveData<>();

        this.pdfConverterStatus.postValue(PdfConverterStatusEnum.IDLE);
        this.savePdfDestinationLiveData.postValue(null);
    }

    public MutableLiveData<PdfConverterStatusEnum> getPdfConverterStatus() {
        return pdfConverterStatus;
    }

    public MutableLiveData<String> getSavePdfDestinationLiveData() {
        return savePdfDestinationLiveData;
    }

    public void convertImageToPdf(String savePdfDestination, Uri uriImage) {
        pdfConverterStatus.postValue(PdfConverterStatusEnum.PROCESSING);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // Code here will run in UI thread
                try {
                    MagicalPdfConverter.getInstance().convertImageIntoPDF(getApplication(), savePdfDestination, uriImage);
                    pdfConverterStatus.postValue(PdfConverterStatusEnum.SUCCESS);
                    savePdfDestinationLiveData.postValue(savePdfDestination);
                } catch (MagicalException e) {
                    pdfConverterStatus.postValue(PdfConverterStatusEnum.FAILED);
                    e.printStackTrace();
                }
            }
        });
    }

}
