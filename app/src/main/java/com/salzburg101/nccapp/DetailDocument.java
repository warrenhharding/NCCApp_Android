package com.salzburg101.nccapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class DetailDocument extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DetailDocument";

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private ValueEventListener mValueEventListener;
    private WebView mWebView;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference pathReference = storageRef.child("/files/");

    LinearLayout linearLayout;

    final Context context = this;

    private String recordId;
    private Uri downloadUrl = Uri.parse("www.a.com");
    private ImageView imageView;
    // private WebView webView;

    // private volatile WebChromeClient mWebChromeClient;
    // private Button backButton;

    PDFView pdfView;
    Button downloadFiles;
    static ProgressBar progressBar;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This is the code that is executed once the listener tells us that a new Event has been created.
        DownloadTask.setListenerSendAttachment(new ListenerSendAttachmentByEmail() {
            @Override
            public void onEvent() {
                System.out.println("Message receievd to send the attachment...");
                sendFileByEmail();
            }
        });


        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_detail_document);

        recordId = this.getIntent().getExtras().getString("recordId");
        System.out.println("The recordId transferred from the summary screen was : " + recordId);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        pdfView = (PDFView)findViewById(R.id.pdfView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        DetailDocument.progressBar.setVisibility(View.VISIBLE);

        findViewById(R.id.largeMenuButton).setOnClickListener(this);
        findViewById(R.id.downloadFiles).setOnClickListener(this);

        downloadFiles = (Button) findViewById(R.id.downloadFiles);

        getDocForDisplay();

    }


    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.largeMenuButton) {
            System.out.println("Transparent Back button pressed.");
            Intent docIntent = new Intent(this, RetrieveDocuments.class);
            startActivity(docIntent);
            finish();
        }

        if (i == R.id.leftarrowwhite) {
            System.out.println("Arrow Back button pressed.");
            Intent docIntent = new Intent(this, RetrieveDocuments.class);
            startActivity(docIntent);
            finish();
        }

        if (i == R.id.downloadFiles) {
            System.out.println("We've been asked to download a file.");
            if (!NetworkConnectionCheck.isNetworkAvailable(this)) {
                ConnectionDownAlert.connectionDown(this);
                return;
            }

            downloadOrSendFile();
        }

    }


    private void downloadOrSendFile() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Download or Send File");
        builder1.setMessage("Please select whether you would like to download the file to your device or send it to an email address.\n\nIf sending by email the file will be stored to the NCC_Downloads directory on your device.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Download",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        isWriteStoragePermissionGranted();
                    }
                });

        builder1.setNegativeButton(
                "Email",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        GlobalVariables.sendAttachment = true;
                        isWriteStoragePermissionGranted();
                    }
                });

        AlertDialog guide = builder1.create();
        guide.show();
    }




    class RetrievePFDStream extends AsyncTask<String,Void, InputStream> {

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e) {
                return null;
            }
            return inputStream;
        }


        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream(inputStream).load();
        }
    }



    public void getDocForDisplay() {
        System.out.println("The name of the file is " + recordId);
        storageRef.child("/files/"+recordId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                downloadUrl = uri;
                System.out.println("Looks like we were successful with the file.");
                System.out.println("dowloadUrl = " + downloadUrl.toString());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                System.out.println("Looks like we failed to get the document / image...");
            }
        });
        System.out.println("About to kick off waitForDocInfo()");
        waitForDocInfo();
    }


    public void waitForDocInfo() {

        if (downloadUrl.toString().contains(".pdf")) {
            displayInPDFViewer();

        } else if (downloadUrl.toString().contains(".jpeg") || downloadUrl.toString().contains(".png") || downloadUrl.toString().contains(".jpg")) {
            nowDisplayDoc();

        } else {
            if (downloadUrl.toString().equals("www.a.com")) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("still waiting for Doc Info... in the middle of the loop");
                        waitForDocInfo();
                    }
                }, 200);
            } else {
                cantOpenThisFileType();
            }
        }
    }


    public void nowDisplayDoc() {
        System.out.println("Now we're out of the loop and can display the document in a webView");

        if(pdfView.getParent() != null) {
            ((ViewGroup) pdfView.getParent()).removeView(pdfView);
            System.out.println("Just removed the pdfView to make way for the imageView");
        }

        ImageView imageView = new ImageView(getApplicationContext());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        imageView.setVisibility(View.VISIBLE);
        imageView.setLayoutParams(params);

        GlideApp.with(getApplicationContext())
                .load(downloadUrl)
                .into(imageView);

        params.setMargins(50,0,50,0);
        linearLayout.addView(imageView);
        progressBar.setVisibility(View.GONE);
    }



    public void displayInPDFViewer() {
        System.out.println("The downloadUrl = " + downloadUrl.toString());
        System.out.println("Now we're out of the loop and can display the pdf in the PDF Viewer");
        new RetrievePFDStream().execute(downloadUrl.toString());
    }


    public void sendFileByEmail() {
        Intent i = new Intent(Intent.ACTION_SEND);
        File fileWithinMyDir = new File(Environment.getExternalStorageDirectory() + "/NCC_Downloads/" + recordId);
        i.setType("message/rfc822");

        if(fileWithinMyDir.exists()) {
            i.putExtra(Intent.EXTRA_EMAIL, new String[] { mAuth.getCurrentUser().getEmail() });
            i.putExtra(Intent.EXTRA_SUBJECT, "Downloaded " + recordId + " from NCC.");
            i.putExtra(Intent.EXTRA_TEXT, "NCC Document " + recordId + " attached.");
            i.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+fileWithinMyDir));
            startActivity(i);
        } else {
            Toast.makeText(DetailDocument.this, "Lookslike the file isn't there...", Toast.LENGTH_SHORT).show();
        }


    }





    public static void openFile(Context context, File url) throws IOException {
        // Create URI
        File file=url;
        Uri uri = Uri.fromFile(file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Check what kind of file you are trying to open, by comparing the url with extensions.
        // When the if condition is matched, plugin sets the correct intent (mime) type,
        // so Android knew what application to use to open the file
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if(url.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if(url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if(url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if(url.toString().contains(".zip") || url.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if(url.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if(url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if(url.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if(url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if(url.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if(url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            //if you want you can also define the intent type for any other file

            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(uri, "*/*");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }










    public boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted2");
                new DownloadTask(DetailDocument.this, downloadFiles, downloadUrl.toString(), recordId);
                return true;
            } else {
                Log.v(TAG,"Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted2");
            new DownloadTask(DetailDocument.this, downloadFiles, downloadUrl.toString(), recordId);
            return true;
        }
    }


    public boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted1");
                return true;
            } else {
                Log.v(TAG,"Permission is revoked1");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted1");
            return true;
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2:
                Log.d(TAG, "External storage2");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission
                    new DownloadTask(DetailDocument.this, downloadFiles, downloadUrl.toString(), recordId);
                }else{
                    return;
                }
                break;

            case 3:
                Log.d(TAG, "External storage1");
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
                    //resume tasks needing this permission
                    new DownloadTask(DetailDocument.this, downloadFiles, downloadUrl.toString(), recordId);
                }else{
                    return;
                }
                break;
        }
    }



    private void cantOpenThisFileType() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Can't Show File");
        builder1.setMessage("This type of file can't be loaded into the browser. You can still download the file and open it in your preferred app.");
        // builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        progressBar.setVisibility(View.GONE);
                        dialog.cancel();
                    }
                });

        AlertDialog guide = builder1.create();
        guide.show();
    }


    private void noPermissionForDownload() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Permission Required");
        builder1.setMessage("Permission is required to download files to this device. You can check your settings under Settings / Application Manager.");
        // builder1.setCancelable(true);

        builder1.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog guide = builder1.create();
        guide.show();
    }








}







//
//    public void displayDoc() {
//        System.out.println("The name of the file is " + recordId);
//        storageRef.child("/files/" + recordId).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                System.out.println("Looks like we got the document / image...");
//                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                ImageView pictureView = new ImageView(getApplicationContext());
//                pictureView.setImageBitmap(bmp);
//
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.WRAP_CONTENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT);
//
//                pictureView.setVisibility(View.VISIBLE);
//                pictureView.setLayoutParams(params);
//                params.setMargins(0,0,0,0);
//                // linearLayout.addView(pictureView);
//
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                System.out.println("Looks like we failed to get the document / image...");
//            }
//        });
//    }
//
//
//
//    public void displayDocUrl() {
//        System.out.println("The name of the file is " + recordId);
//        storageRef.child("/files/"+recordId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                // Got the download URL for 'users/me/profile.png'
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                System.out.println("Looks like we failed to get the document / image...");
//            }
//        });
//    }
//
//
