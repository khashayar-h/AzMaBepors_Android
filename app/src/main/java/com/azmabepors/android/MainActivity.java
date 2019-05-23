package com.azmabepors.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.ajts.androidmads.fontutils.FontUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String item;
    String item2;

    public static String Email = null;
    public static String Question = null;
    public static String Lesson = null;
    public static String Major = null;

    String realPath;
    Bitmap bm;
    HttpClient httpClient;
    HttpPost postRequest;

    HttpClient httpClientf;
    HttpPost postRequestf;

    Button btn_choose;
    Button btn_upload;
    EditText txt_question;
    EditText txt_mail;
    String filename;
    DrawerLayout drawer;
    boolean path;

    TextView txtfile;
    String mCurrentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtfile = (TextView)findViewById(R.id.txtfile);
        txtfile.setText("تصویری انتخاب نشده (ارسال تصویر اختیاری است.)");

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{READ_EXTERNAL_STORAGE}, 1);
        // Applying Custom Font
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/yekan.ttf");
        // Init Library
        FontUtils fontUtils = new FontUtils(MainActivity.this);


        txt_mail = (EditText) findViewById(R.id.edt1);
        txt_question = (EditText) findViewById(R.id.edt2);
        txt_mail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        btn_choose = (Button) findViewById(R.id.btn_choose);
        btn_upload = (Button) findViewById(R.id.sendbtn);


        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                txtfile = (TextView)findViewById(R.id.txtfile);

               final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("انتخاب تصویر سوال از :")
                        .setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface arg0, int keyCode,
                                                 KeyEvent event) {
                                // TODO Auto-generated method stub
                                if (keyCode == KeyEvent.KEYCODE_BACK) {
                                    //finish();
                                    arg0.cancel();
                                }
                                return true;
                            }
                        })
                        .setCancelable(false)
                        .setPositiveButton("گالری", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(intent, 0);
                                path = true;
                            }
                        })
                        .setNegativeButton("دوربین", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                // Ensure that there's a camera activity to handle the intent
                                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                    // Create the File where the photo should go
                                    File photoFile = null;
                                    try {
                                        photoFile = createImageFile();
                                    } catch (IOException ex) {
                                        // Error occurred while creating the File

                                    }
                                    // Continue only if the File was successfully created
                                    if (photoFile != null) {

                                        Uri imageUri;
                                        Uri photoURI;
// N is for Nougat Api 24 Android 7
                                        if (Build.VERSION_CODES.N <= android.os.Build.VERSION.SDK_INT) {
                                            // FileProvider required for Android 7.  Sending a file URI throws exception.
                                            photoURI = FileProvider.getUriForFile(MainActivity.this,
                                                    "com.example.android.fileprovider",
                                                    photoFile);
                                        } else {
                                            // For older devices:
                                            // Samsung Galaxy Tab 7" 2 (Samsung GT-P3113 Android 4.2.2, API 17)
                                            // Samsung S3
                                            photoURI = Uri.fromFile(photoFile);
                                        }

                                       /* Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                                                "com.example.android.fileprovider",
                                                photoFile);*/
                                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                        startActivityForResult(takePictureIntent, 1);
                                        path = true;
                                    }}

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });


        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                boolean connected = false;
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                    new waiting().execute();
                } else if (connected == false) {

                    Toast.makeText(getBaseContext(), "لطفا اتصال خود را به اینترنت بررسی کنید!", Toast.LENGTH_SHORT).show();

                }
                //new waiting().execute();


            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);


        String[] spinner1 = {"انتخاب درس...","ریاضی", "فیزیک", "شیمی", "زیست شناسی"};

        String[] spinner2 = {"انتخاب مقطع...","نهم و قبل از آن", "دهم", "یازدهم", "دوازدهم"};


        ArrayAdapter<String> aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinner1)
        {

            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/yekan.ttf");
                // Init Library
                FontUtils fontUtils = new FontUtils(MainActivity.this);

                fontUtils.applyFontToView(((TextView) v),typeface);


                return v;
            }


            public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                View v =super.getDropDownView(position, convertView, parent);

                Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/yekan.ttf");
                // Init Library
                FontUtils fontUtils = new FontUtils(MainActivity.this);

                fontUtils.applyFontToView(((TextView) v),typeface);



                return v;
            }
        };




        Spinner spin = (Spinner) findViewById(R.id.spn_lesson);
        spin.setOnItemSelectedListener(null);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        item= null;
                        break;
                    case 1:
                        item = "math";
                        break;
                    case 2:
                        item = "physics";
                        break;
                    case 3:
                        item = "chemistry";
                        break;
                    case 4:
                        item = "biology";

                }

                Lesson = item;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        ArrayAdapter bb = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinner2)
        {

            public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/yekan.ttf");
            // Init Library
            FontUtils fontUtils = new FontUtils(MainActivity.this);

            fontUtils.applyFontToView(((TextView) v),typeface);


            return v;
        }


        public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
            View v =super.getDropDownView(position, convertView, parent);

            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/yekan.ttf");
            // Init Library
            FontUtils fontUtils = new FontUtils(MainActivity.this);

            fontUtils.applyFontToView(((TextView) v),typeface);



            return v;
        }
        };
        Spinner spin2 = (Spinner) findViewById(R.id.spn_major);
        spin2.setOnItemSelectedListener(null);
        bb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin2.setAdapter(bb);
        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        item2 = null;
                        break;
                    case 1:
                        item2 = "bnineth";
                        break;
                    case 2:
                        item2 = "tenth";
                        break;
                    case 3:
                        item2 = "eth";
                        break;
                    case 4:
                        item2 = "twth";

                }

                Major = item2;

            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        TextView txtfile = (TextView) findViewById(R.id.txtfile);
        fontUtils.applyFontToView(txtfile, typeface);

        fontUtils.applyFontToView(txt_mail, typeface);
        fontUtils.applyFontToView(spin, typeface);
        fontUtils.applyFontToView(txt_question, typeface);
        TextView caution = (TextView) findViewById(R.id.caution);
        fontUtils.applyFontToView(caution, typeface);

        TextView navbartxt1 = (TextView) findViewById(R.id.navbartxt);
        fontUtils.applyFontToView(navbartxt1, typeface);

        fontUtils.applyFontToView(spin, typeface);
        fontUtils.applyFontToView(spin2, typeface);
        fontUtils.applyFontToView(btn_choose, typeface);
        fontUtils.applyFontToView(btn_upload, typeface);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("از ما بپرس!");
        fontUtils.applyFontToToolbar(toolbar, typeface);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        fontUtils.applyFontToNavigationView(navigationView, typeface);
        navigationView.setNavigationItemSelectedListener(this);

    }

    public void executeWithoutImage() throws Exception {

       try {


            httpClientf = new DefaultHttpClient();
            postRequestf = new HttpPost("http://azmabepors.com/sendkh.php");

            MultipartEntity reqEntityf = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

           EditText txt_mailf = (EditText) findViewById(R.id.edt1);
            txt_mailf.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
           String Emailf = txt_mailf.getText().toString();
            reqEntityf.addPart("mail", new StringBody(Emailf));

            String Lessonf = item;
            reqEntityf.addPart("lesson", new StringBody(Lessonf));

            String Majorf = item2;
            reqEntityf.addPart("grade", new StringBody(Majorf));

           EditText txt_questionf = (EditText) findViewById(R.id.edt2);
            String Questionf = txt_questionf.getText().toString();

            reqEntityf.addPart("text", new StringBody(Questionf, Charset.forName("UTF-8")));
           // Log.w("myApp", "no network");

            postRequestf.setEntity(reqEntityf);
            HttpResponse responsef = httpClientf.execute(postRequestf);
            BufferedReader readerf = new BufferedReader(new InputStreamReader(responsef.getEntity().getContent(), "UTF-8"));
            String sResponsef;
            StringBuilder sf = new StringBuilder();


            while ((sResponsef = readerf.readLine()) != null) {
                sf = sf.append(sResponsef);

            }

            String error1f = null;
            if (sf.indexOf("سوال شما دریافت شد و اسرع وقت در ایمیل خود پاسخ سوال خود را دریافت خواهید کرد") != -1) {
                error1f = "سوال شما دریافت شد و اسرع وقت در ایمیل خود پاسخ سوال خود را دریافت خواهید کرد";
            } else if (sf.indexOf("Sorry, your file was not uploaded.") != -1) {
                error1f = "فایل شما آپلود نشد!";
            } else if (sf.indexOf("Sorry, there was an error uploading your file.") != -1) {
                error1f = "مشکلی در آپلود فایل شما پیش آمد!";
            } else if (sf.indexOf("لطفا همه ی فیلد ها را پر کنید.") != -1) {
                error1f = "لطفا همه ی فیلد ها را پر کنید.";
            } else if (sf.indexOf("سوال شما ارسال نشد") != -1) {
                error1f = "سوال شما ارسال نشد.";
            } else if (sf.indexOf("Sorry, file already exists.") != -1) {
                error1f = "فایل شما قبلا آپلود شده است!";
            }
            AlertDialog.Builder builder1f = new AlertDialog.Builder(MainActivity.this);
            builder1f.setMessage(error1f);
            builder1f.setCancelable(true);

            builder1f.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11f = builder1f.create();
            alert11f.show();
           txt_question.setText("");
           //txt_mail.setText("");
           realPath = "";
           TextView txtfile = (TextView)findViewById(R.id.txtfile);
           txtfile.setText("تصویری انتخاب نشده (ارسال تصویر اختیاری است.)");
           path = false;


        } catch (Exception e) {
            Log.e(e.getClass().getName(), "" + e.getMessage());
        }


    }

    public class WithoutIMG {
        {
            try {
                if (item == null || item2 == null)
                {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setMessage("لطفا درس و مقطع خود را انتخاب نمایید...");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                } else {
                    executeWithoutImage();
                }

            } catch (Exception e) {

            }
        }
    }

    public class WithIMG {

        {
            try {
                if (item == null || item2 == null)
                {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setMessage("لطفا درس و مقطع خود را انتخاب نمایید...");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                } else {
                    executeMultipartPost();
                }
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent i = new Intent(this, WebViewActivity.class);
            i.putExtra("key", 0);
            startActivity(i);

        } else if (id == R.id.nav_gallery) {
            Intent i = new Intent(this, WebViewActivity.class);
            i.putExtra("key", 1);
            startActivity(i);

        } else if (id == R.id.nav_slideshow) {
            Intent i = new Intent(this, WebViewActivity.class);
            i.putExtra("key", 2);
            startActivity(i);

        } else if (id == R.id.nav_manage) {
            Intent i = new Intent(this, WebViewActivity.class);
            i.putExtra("key", 3);
            startActivity(i);

        }else if (id == R.id.nav_insta){
            Intent i = new Intent(this, WebViewActivity.class);
            i.putExtra("key", 4);
            startActivity(i);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void executeMultipartPost() throws Exception {

        ;
        try {


            httpClient = new DefaultHttpClient();
            postRequest = new HttpPost("http://azmabepors.com/sendkh.php");

            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            txt_mail = (EditText) findViewById(R.id.edt1);
            txt_mail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            Email = txt_mail.getText().toString();
            reqEntity.addPart("mail", new StringBody(Email));

            Lesson = item;
            reqEntity.addPart("lesson", new StringBody(Lesson));

            Major = item2;
            reqEntity.addPart("grade", new StringBody(Major));

            txt_question = (EditText) findViewById(R.id.edt2);
            Question = txt_question.getText().toString();

            reqEntity.addPart("text", new StringBody(Question, Charset.forName("UTF-8")));
            Log.w("myApp", "no network");
            bm = BitmapFactory.decodeFile(realPath);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 75, bos);
            byte[] data = bos.toByteArray();
            filename = realPath.substring(realPath.lastIndexOf("/") + 1);
            ByteArrayBody bab = new ByteArrayBody(data, filename);

                reqEntity.addPart("fileToUpload", bab);


            postRequest.setEntity(reqEntity);
            HttpResponse response = httpClient.execute(postRequest);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            String sResponse;
            StringBuilder s = new StringBuilder();


            while ((sResponse = reader.readLine()) != null) {
                s = s.append(sResponse);

            }
            //
            String error1 = null;
            if (s.indexOf("سوال شما دریافت شد و اسرع وقت در ایمیل خود پاسخ سوال خود را دریافت خواهید کرد") != -1) {
                error1 = "سوال شما دریافت شد و اسرع وقت در ایمیل خود پاسخ سوال خود را دریافت خواهید کرد";
            } else if (s.indexOf("Sorry, your file was not uploaded.") != -1) {
                error1 = "فایل شما آپلود نشد!";
            } else if (s.indexOf("Sorry, there was an error uploading your file.") != -1) {
                error1 = "مشکلی در آپلود فایل شما پیش آمد!";
            } else if (s.indexOf("لطفا همه ی فیلد ها را پر کنید.") != -1) {
                error1 = "لطفا همه ی فیلد ها را پر کنید.";
            } else if (s.indexOf("سوال شما ارسال نشد") != -1) {
                error1 = "سوال شما ارسال نشد.";
            } else if (s.indexOf("Sorry, file already exists.") != -1) {
                error1 = "فایل شما قبلا آپلود شده است!";
            }
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setMessage(error1);
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();
            txt_question.setText("");
            //txt_mail.setText("");
            realPath = "";
            TextView txtfile = (TextView)findViewById(R.id.txtfile);
            txtfile.setText("تصویری انتخاب نشده (ارسال تصویر اختیاری است.)");
            path=false;


        } catch (Exception e) {
            Log.e(e.getClass().getName(), "" + e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        if (reqCode == 0  && data!=null) {

            txtfile = (TextView)findViewById(R.id.txtfile);

            // Check the SDK Version
            if (Build.VERSION.SDK_INT < 11)
                realPath = PathOfImage.PathAPI11(this, data.getData());
            else if (Build.VERSION.SDK_INT < 19)
                realPath = PathOfImage.Path_API18(this, data.getData());
            else
                realPath = PathOfImage.Path_API19(this, data.getData());


            filename = realPath.substring(realPath.lastIndexOf("/") + 1);
            txtfile.setText( "تصویر انتخاب شده:"  + filename);
            Toast.makeText(MainActivity.this, " تصویر انتخاب شد" , Toast.LENGTH_SHORT).show();
        }
        if (reqCode == 1) {
            txtfile = (TextView)findViewById(R.id.txtfile);
            Bitmap photo = (Bitmap) data.getExtras().get("data");



            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            Uri tempUri = getImageUri(getApplicationContext(), photo);

            // CALL THIS METHOD TO GET THE ACTUAL PATH
            File finalFile = new File(getRealPathFromURI(tempUri));
            realPath = finalFile.toString();
            filename = realPath.substring(realPath.lastIndexOf("/") + 1);
            txtfile.setText( "تصویر انتخاب شده:"  + filename);
            Toast.makeText(MainActivity.this, " تصویر انتخاب شد", Toast.LENGTH_SHORT).show();
    }}

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }


    public static String receiveResponse(HttpURLConnection conn) throws IOException {
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        // retrieve the response from server
        InputStream is = null;
        try {
            is = conn.getInputStream();
            int ch;
            StringBuffer sb = new StringBuffer();
            while ((ch = is.read()) != -1) {
                sb.append((char) ch);
            }
            return sb.toString();
        } catch (IOException e) {
            throw e;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    class waiting extends AsyncTask<String, Void, Boolean> {


        ProgressDialog progressdialog;

        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (path == true) {

                new WithIMG();
            } else {

                new WithoutIMG();
            }
            progressdialog.dismiss();

        }

        protected void onPreExecute() {
            super.onPreExecute();
            // Shows Progress Bar Dialog and then call doInBackground method
            progressdialog = new ProgressDialog(MainActivity.this);
            progressdialog.setTitle("در حال ارسال سوال...");
            progressdialog.setMessage("لطفا کمی صبر کنید...");
            progressdialog.setCancelable(false);
            progressdialog.show();

        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {

                Thread.sleep(3000);

            } catch (Exception e) {

            }
            return null;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


}

















