package ir.vasl.magicalpec.viewModel;

import android.app.Application;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import ir.vasl.magicalpec.utils.Core.MagicalPdfCore;
import ir.vasl.magicalpec.utils.Exceptions.MagicalException;

public class MagicalPECViewModel extends AndroidViewModel {

    public enum PECCoreStatusEnum {IDLE, PROCESSING, FAILED, SUCCESS}

    private MutableLiveData<PECCoreStatusEnum> pecCoreStatus;

    public MagicalPECViewModel(@NonNull Application application) {
        super(application);
        this.pecCoreStatus = new MutableLiveData<>();
        this.pecCoreStatus.postValue(PECCoreStatusEnum.IDLE);
    }

    public MutableLiveData<PECCoreStatusEnum> getPecCoreStatus() {
        return pecCoreStatus;
    }

    public void addOCG(PointF pointF, String filePath, int currPage, String referenceHash, byte[] OCGCover) {
        addOCG(pointF, filePath, currPage, referenceHash, OCGCover, 0, 0);
    }

    public void addOCG(PointF pointF, Uri uri, int currPage, String referenceHash, byte[] OCGCover) {
        addOCG(pointF, uri, currPage, referenceHash, OCGCover, 0, 0);
    }

    public void addOCG(PointF pointF, String filePath, int currPage, String referenceHash, byte[] OCGCover, float OCGWidth, float OCGHeight) {

        MagicalPECViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.PROCESSING);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // Code here will run in UI thread
                try {
                    MagicalPdfCore.getInstance().addOCG(pointF, filePath, currPage, referenceHash, OCGCover, OCGWidth, OCGHeight);
                    MagicalPECViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.SUCCESS);
                } catch (MagicalException e) {
                    MagicalPECViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.FAILED);
                    e.printStackTrace();
                }
            }
        });
    }

    public void addOCG(PointF pointF, Uri uri, int currPage, String referenceHash, byte[] OCGCover, float OCGWidth, float OCGHeight) {

        MagicalPECViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.PROCESSING);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // Code here will run in UI thread
                try {
                    MagicalPdfCore.getInstance().addOCG(getApplication(), pointF, uri, currPage, referenceHash, OCGCover, OCGWidth, OCGHeight);
                    MagicalPECViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.SUCCESS);
                } catch (MagicalException e) {
                    MagicalPECViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.FAILED);
                    e.printStackTrace();
                }
            }
        });
    }

    public void removeOCG(String filePath, String referenceHash) {

        MagicalPECViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.PROCESSING);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // Code here will run in UI thread
                try {
                    MagicalPdfCore.getInstance().removeOCG(filePath, referenceHash);
                    MagicalPECViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.SUCCESS);
                } catch (MagicalException e) {
                    MagicalPECViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.FAILED);
                    e.printStackTrace();
                }
            }
        });
    }

    public void removeOCG(Uri uri, String referenceHash) {

        MagicalPECViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.PROCESSING);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // Code here will run in UI thread
                try {
                    MagicalPdfCore.getInstance().removeOCG(getApplication(), uri, referenceHash);
                    MagicalPECViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.SUCCESS);
                } catch (MagicalException e) {
                    MagicalPECViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.FAILED);
                    e.printStackTrace();
                }
            }
        });
    }

    public void removeAllOCGs() {
    }

    public void updateOCG(PointF pointF, String filePath, int currPage, String referenceHash, byte[] newOCGCover) {
        MagicalPECViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.PROCESSING);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // Code here will run in UI thread
                try {
                    MagicalPdfCore.getInstance().updateOCG(pointF, filePath, currPage, referenceHash, newOCGCover);
                    MagicalPECViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.SUCCESS);
                } catch (MagicalException e) {
                    MagicalPECViewModel.this.pecCoreStatus.postValue(PECCoreStatusEnum.FAILED);
                    e.printStackTrace();
                }
            }
        });
    }

}
