package tr.edu.mu.ceng.mad.myimagedownloader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.AsynchronousByteChannel;

public class MainActivity extends AppCompatActivity {
    EditText txtURL;
    Button btnDownload;
    ImageView imgView;

    //Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtURL = findViewById(R.id.txtURL);
        btnDownload = findViewById(R.id.btnDownload);
        imgView = findViewById(R.id.imgView);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permission = ActivityCompat.checkSelfPermission
                        (MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if(permission != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,
                            PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);

                }else {
                    /*Thread backgroungThread = new Thread(new DownloadRunnable
                            (txtURL.getText().toString()));
                    backgroungThread.start();//This type more usable when we have multiple background threads. They are executing in the same time.

                    Thread backgroungThread2 = new Thread(new DownloadRunnable
                            (txtURL.getText().toString()));
                    backgroungThread2.start();
                    Thread backgroungThread3 = new Thread(new DownloadRunnable
                            (txtURL.getText().toString()));
                    backgroungThread3.start();*/

                    //3. Implement Updated Code:
                    //If we have multiple asynctask background threads can't be suitable.Because AsyncTask background threads works sequentially.
                    AsyncTask backgroundTask = new DownloadTask();
                    String[] urls = new String[1];
                    urls[0] = txtURL.getText().toString();
                    backgroundTask.execute(urls);

                    /*AsyncTask backgroundTask2 = new DownloadTask();
                    String[] urls2 = new String[1];
                    urls[0] = txtURL.getText().toString();
                    backgroundTask2.execute(urls);

                    AsyncTask backgroundTask3 = new DownloadTask();
                    String[] urls3 = new String[1];
                    urls[0] = txtURL.getText().toString();
                    backgroundTask3.execute(urls);*/
                }

                /*String imagePath= Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_DOWNLOADS).toString()+"/temp.jpg";
                downloadFile(txtURL.getText().toString(),imagePath);
                preview(imagePath);  We don't need anymore after 3. implement*/

            }
            
        });
    }
    //Checking request is correct or not
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_EXTERNAL_STORAGE){
            if (grantResults.length == 2 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1]== PackageManager.PERMISSION_GRANTED) {
                /*Thread backgroundThread = new Thread(new DownloadRunnable(txtURL.getText().toString()));
                backgroundThread.start();*/
                //3. Implement:
                AsyncTask backgroundTask = new DownloadTask();
                String[] urls = new String[1];
                urls[0] = txtURL.getText().toString();
                backgroundTask.execute(urls);


            }
        }
    }
    /* private void preview(String imagePath) {
        Bitmap image= BitmapFactory.decodeFile(imagePath);
        imgView.setImageBitmap(image);
    } We don't need anymore after 3. implement*/

    private void downloadFile(String urlStr, String imagePath) {
        try{
            URL url = new URL(urlStr);
            Log.e("downloadFile","connecting");
            URLConnection connection = url.openConnection();
            connection.connect();
            Log.e("downloadFile","reading and writing");

            int fileSize = connection.getContentLength();

            InputStream is = new BufferedInputStream(url.openStream(),8192);
            OutputStream os = new FileOutputStream(imagePath);

            byte data[]= new byte[1024];
            int total =0;
            int count;
            while((count= is.read(data)) != -1){
                os.write(data,0,count);
                total += count;

                int percentage= (total*100/fileSize);
            }
            os.flush();
            os.close();
            is.close();
        }catch(Exception ex){
            Log.e("downloadFile",ex.getMessage());
            ex.printStackTrace();
        }

    }
    //***********
    //AndroidStudio forces us to implement threads between communicate the datas
    //AsyncTask class is easy to reach/communicate data between UI threads and background threads:
    class DownloadTask extends AsyncTask<String, Integer, Bitmap> {
        ProgressDialog PD;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            PD= new ProgressDialog(MainActivity.this);
            PD.setMax(100);
            PD.setIndeterminate(false);
            PD.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            PD.setTitle("Downloading");
            PD.setMessage("Please wait...");
            PD.show();
        }


        //3. Implement:
        @Override
        protected Bitmap doInBackground(String... strs) {
            Log.e("Download Task",strs[0]);
            String imagePath= Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS).toString()+"/temp.jpg";
            downloadFile2(strs[0],imagePath);
            Log.e("Download Task","Download Completed");
            Bitmap image= BitmapFactory.decodeFile(imagePath);
            Log.e("Download Task","File to Image");

            //scaling the image
            float w = image.getWidth();
            float h = image.getHeight();
            int W = 400;
            int H = (int) ( (h*W)/w);
            Bitmap b = Bitmap.createScaledBitmap(image, W, H, false);
            return b;
        }
        //3. Implement:
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.e("onPostExecute","Setting Image");
            super.onPostExecute(bitmap);
            Log.e("Download Task","Before imageView");
            imgView.setImageBitmap(bitmap); //image returned to bitmap
            Log.e("Download Task","After imageView");
            PD.dismiss();
        }
        private void downloadFile2(String urlStr, String imagePath) {
            try{
                URL url = new URL(urlStr);
                Log.e("downloadFile","connecting");
                URLConnection connection = url.openConnection();
                connection.connect();
                Log.e("downloadFile","reading and writing");

                int fileSize = connection.getContentLength();

                InputStream is = new BufferedInputStream(url.openStream(),8192);
                OutputStream os = new FileOutputStream(imagePath);

                byte data[]= new byte[1024];
                int total =0;
                int count;
                while((count= is.read(data)) != -1){
                    os.write(data,0,count);
                    total += count;

                    //int percentage= ;
                    publishProgress(((int)((total*100)/fileSize)));
                }
                os.flush();
                os.close();
                is.close();
            }catch(Exception ex){
                Log.e("downloadFile",ex.getMessage());
                ex.printStackTrace();
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            PD.setProgress(values[0]);
            //Log.e("Percentage",values[0]+"");
        }
    }
    //***********

    class DownloadRunnable implements Runnable{
        String url;

        public DownloadRunnable(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            //4. Implement:
            Log.e("Download Task",url);
            String imagePath= Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS).toString()+"/temp.jpg";
            downloadFile(url,imagePath);
            Log.e("Download Task","Download Completed");
            Bitmap image= BitmapFactory.decodeFile(imagePath);
            Log.e("Download Task","File to Image");

            //scaling the image
            float w = image.getWidth();
            float h = image.getHeight();
            int W = 400;
            int H = (int) ( (h*W)/w);
            final Bitmap b = Bitmap.createScaledBitmap(image, W, H, false);//All of the above codes can be perform in background.
            //imgView.setImageBitmap(b); //We can not perform this in the background. It should be in UI.
            //there is a method:
            /*runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imgView.setImageBitmap(b);
                }
            });*/
            //OR!!!!:
            runOnUiThread(new UpdateBitmap(b));


        }
    }
    class UpdateBitmap implements Runnable{
        Bitmap bitmap;
        @Override
        public void run() {
            imgView.setImageBitmap(bitmap);
        }

        public UpdateBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }
    }



}