package ir.vasl.magicalpdfeditor.BaseClasses;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import ir.vasl.magicalpdfeditor.Utils.PublicValue;

public class BaseActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    protected boolean checkPermission(String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkPermissionsGrant(permissions);
        }
        return true;
    }

    private boolean checkPermissionsGrant(String[] requestedPermissions) {

        List<String> notGrantedPermissions = new ArrayList<>();

        for (String requestedPermission : requestedPermissions) {
            if (ContextCompat.checkSelfPermission(this, requestedPermission) != PackageManager.PERMISSION_GRANTED) {
                notGrantedPermissions.add(requestedPermission);
            }
        }

        if (notGrantedPermissions.size() > 0) {
            requestNotGrantedPermissions(notGrantedPermissions);
            return false;
        }

        return true;
    }

    private void requestNotGrantedPermissions(List<String> notGrantedPermissions) {
        ActivityCompat.requestPermissions(BaseActivity.this,
                notGrantedPermissions.toArray(new String[0]),
                PublicValue.KEY_REQUEST_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void launchFilePicker() {
        String[] needPermissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!checkPermission(needPermissions))
            return;

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        try {
            startActivityForResult(intent, PublicValue.KEY_REQUEST_FILE_PICKER);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "user that file manager not working", Toast.LENGTH_SHORT).show();
        }
    }

}
