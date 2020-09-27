package com.abishek534.createpdf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtFullName;
    private EditText edtContactNumber;
    private EditText edtAadharNumber;
    private EditText edtEmail;
    private EditText edtAddress;
    private EditText edtCollege;
    private EditText edtFatherName;
    private EditText edtOccupation;
    private EditText edtLinkedAddress;
    private Button btnContinue;
    private ProgressDialog progressDialog;
    private String fullName;
    private String email;
    private String address;
    private String contactNumber;
    private String aadharNumber;
    private String linkdInId;
    private String fatherName;
    private String occupation;
    private String collegeName;
    private boolean boolean_permission;
    private static final int FILE_SELECT_CODE = 100;

    LinearLayout mainLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUi();



    }

    private void initUi() {
        edtFullName = findViewById(R.id.full_name);
        edtEmail = findViewById(R.id.email);
        edtContactNumber = findViewById(R.id.contact_number);
        edtAadharNumber = findViewById(R.id.aadhar_number);
        edtLinkedAddress = findViewById(R.id.linked_id);
        edtAddress = findViewById(R.id.address);
        btnContinue = findViewById(R.id.btn_create);
        edtFatherName = findViewById(R.id.father_name);
        edtOccupation = findViewById(R.id.occupation);
        edtCollege = findViewById(R.id.college_name);
        mainLayout = findViewById(R.id.main_layout);

        btnContinue.setOnClickListener(this);


        fn_permission();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating account...");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_create:
                //  progressDialog.show();
                  getDataOfEditText();


                break;


        }


    }


    private boolean checkEmptyField() {
        if (TextUtils.isEmpty(fullName)||TextUtils.isEmpty(email)||TextUtils.isEmpty(address)
                ||TextUtils.isEmpty(aadharNumber)||TextUtils.isEmpty(linkdInId)||TextUtils.isEmpty(contactNumber)
                ||TextUtils.isEmpty(fatherName)||TextUtils.isEmpty(occupation)||TextUtils.isEmpty(collegeName))
            return true;
        else
            return false;
    }

    public void getDataOfEditText() {
        fullName = edtFullName.getText().toString();
        contactNumber = edtContactNumber.getText().toString();
        email = edtEmail.getText().toString();
        address = edtAddress.getText().toString();
        linkdInId = edtLinkedAddress.getText().toString();
        aadharNumber = edtAadharNumber.getText().toString();
        fatherName = edtFatherName.getText().toString();
        collegeName = edtCollege.getText().toString();
        occupation = edtOccupation.getText().toString();

        boolean check = checkEmptyField();
        if(check)
        {
            Toast.makeText(MainActivity.this,"no fields can be left empty",Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
        else{

            createPdf(fullName,contactNumber,email,linkdInId,aadharNumber,address,fatherName,collegeName,occupation);

        }

    }

    private void createPdf(String fullName,String contactNumber,String email,String linkdInId,
                        String aadharNumber,String address,String fatherName,String collegeName,String occupation)
    {
        if(boolean_permission)
             createPdf("fullName:  "+fullName+"\ncontactNumber:  "+contactNumber+"\naadharNumber:  "+aadharNumber+
                     "\nemail:  "+ email+"\nlinkedIn Id:  "+linkdInId+"\naddress:  "+address+
                     "\nFather's name:  "+fatherName+"\ncollegeName:  "+collegeName+"\noccupation:  "+occupation);
        else
            fn_permission();
    }






    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        200);

            }


        } else {
            boolean_permission = true;


        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                boolean_permission = true;


            } else {
                Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

            }
        }
    }

    private void createPdf(String data){

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300,600, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        float x=10,y=60;
        for(String line:data.split("\n"))
        {
            canvas.drawText(line,x,y,paint);
            y=y+paint.descent()+10;
        }

        document.finishPage(page);


        final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/app.Pdf";
        File file = new File(path);


        try {
            file.createNewFile();
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "pdf created ", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "location: "+path, Toast.LENGTH_LONG).show();
                }
            },1000);

           // share(path);

            sharePdf(path);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
    }



    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }

    public void share(String path){
       // Uri uri = Uri.parse(path);
        File file = new File(path);
        Uri fileUri=Uri.parse(path);

        try {
            fileUri = FileProvider.getUriForFile(
                    MainActivity.this,
                    "com.abishek534.createpdf",
                    file);
        } catch (IllegalArgumentException e) {
            Log.e("File Selector",
                    "The selected file can't be shared: " );
        }

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        sharingIntent.setType("application/pdf");
        startActivity(Intent.createChooser(sharingIntent, "Share pdf using"));
    }

    public void sharePdf(String path)
    {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/pdf");

        File sharingFile = new File(path);

        Uri outputPdfUri = FileProvider.getUriForFile(this, MainActivity.this.getPackageName() + ".provider", sharingFile);

        Uri uri = Uri.parse("file://" +path + ".pdf");
        shareIntent.putExtra(Intent.EXTRA_STREAM, outputPdfUri);

        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //Write Permission might not be necessary
        shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, "Share PDF using.."));
    }
}
