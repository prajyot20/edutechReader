package edutech.map.com.ereader;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.artifex.mupdfdemo.MuPDFActivity;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.mapedutech.reader.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class HomeActivityWithDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    List<String> arrPackage;
    private ListView listView;
    private SharedPreferences shared;
    private int TEXT_ID = 0;

    enum Purpose {
        PickPDF,
        PickKeyFile
    }

    static public final String PICK_KEY_FILE = "com.artifex.mupdfdemo.PICK_KEY_FILE";
    private static final String TAG = "FileChooserExampleActivity";
    final int REQUEST_WRITE_STORAGE = 112;
    private static final int REQUEST_CODE = 6384; // onActivityResult request
    private Purpose      mPurpose;
    public String fileSelectPath = null;
    TextView browsePath;
    // add values for your ArrayList any where...
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_with_drawer);

        arrPackage = new ArrayList<>();
        shared = getSharedPreferences("App_settings", MODE_PRIVATE);
        retriveSharedValue();
/*
        if(arrPackage.isEmpty() && !arrPackage.contains("Recently Viewed File"))
            arrPackage.add(0,"Recently Viewed File");*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }
        mPurpose = PICK_KEY_FILE.equals(getIntent().getAction()) ? Purpose.PickKeyFile : Purpose.PickPDF;

        String storageState = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(storageState)
                && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(storageState))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(com.artifex.mupdfdemo.R.string.no_media_warning);
            builder.setMessage(com.artifex.mupdfdemo.R.string.no_media_hint);
            AlertDialog alert = builder.create();
            alert.setButton(AlertDialog.BUTTON_POSITIVE,getString(com.artifex.mupdfdemo.R.string.dismiss),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            alert.show();
            return;
        }

         browsePath = (TextView)findViewById(R.id.filePath);
        browsePath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooser();
            }
        });


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final Button openButton = (Button)findViewById(R.id.open_file);

        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    openFileInReader(fileSelectPath);
                    storesharedPreferences();

            }
        });

/*
        TourGuide mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                .setPointer(new Pointer())
                .setToolTip(new ToolTip().setTitle("Welcome!").setDescription("Click on Select file to browse the file..."))
                .setOverlay(new Overlay())
                .playOn(browsePath);

        mTourGuideHandler
                .setPointer(new Pointer())
                .setToolTip(new ToolTip().setTitle("Open file!").setDescription("Click on open file to read the file..."))
                .setOverlay(new Overlay())
                .playOn(openButton);
        mTourGuideHandler.cleanUp();
*/
        listView = (ListView) findViewById(R.id.lv_listfile);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, arrPackage){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.RED);
                return textView;
            }
        };

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int itemPosition     = position;
                String  itemValue    = (String) listView.getItemAtPosition(position);

                openFileInReader(itemValue);
            }

        });

    }

    private void storesharedPreferences() {
        SharedPreferences.Editor editor = shared.edit();
        Set<String> set = new HashSet<String>();
        set.addAll(arrPackage);
        editor.putStringSet("DATE_LIST", set);
        editor.apply();
        Log.d("storesharedPreferences",""+set);
    }

    private void retriveSharedValue() {
        Set<String> set = shared.getStringSet("DATE_LIST", null);
        if(set !=null)
        for (Iterator<String> it = set.iterator(); it.hasNext(); ) {
           String s = it.next();
            arrPackage.add(s);
        }
        //arrPackage.addAll(set);
        Log.d("retrivesharedPreferences",""+set);
    }
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_activity_with_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            arrPackage.clear();
            storesharedPreferences();
                    refreshData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showChooser() {
      Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
      intent.setType("*/*");      //all files
    //  intent.setType("pdf");   //XML file only
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), REQUEST_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
        // Use the GET_CONTENT intent from the utility class
        Intent target = FileUtils.createGetContentIntent();

    }
    public Uri uri = null;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE:
                // If the file selection was successful
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        // Get the URI of the selected file
                        uri = data.getData();
                        Log.i(TAG, "Uri = " + uri.toString());
                        try {
                            // Get the file fileSelectPath from the URI
                            final String path = FileUtils.getPath(this, uri);
                            Toast.makeText(HomeActivityWithDrawer.this,
                                    "File Selected: " + path, Toast.LENGTH_LONG).show();
                            if (path!=null){
                                browsePath.setText(path);

                                if(!arrPackage.contains(path))
                                  arrPackage.add(0,path);

                                fileSelectPath=path;
                                refreshData();
                            }

                        } catch (Exception e) {
                            Log.e("FileSelectorTestActivity", "File select error", e);
                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    AlertDialog alertD;
    private void openFileInReader(String fileToOpen) {

        if (fileToOpen != null) {
            uri = Uri.parse(fileToOpen);
            Intent intent = new Intent(HomeActivityWithDrawer.this, MuPDFActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(uri);
            startActivity(intent);

        } else {
             AlertDialog.Builder alertDialogBuilder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alertDialogBuilder = new AlertDialog.Builder(HomeActivityWithDrawer.this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                alertDialogBuilder = new AlertDialog.Builder(HomeActivityWithDrawer.this);
            }

            alertDialogBuilder.setTitle("File Path is empty");
            alertDialogBuilder.setIcon(R.drawable.reader_logo);
            alertDialogBuilder.setMessage("Please browse again and select valid file to open");
            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    alertD.dismiss();
                }
            });
            alertDialogBuilder.setPositiveButton( "Browse", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showChooser();
                }
            });
            alertD = alertDialogBuilder.create();
            alertD.show();

        }
    }

    private void refreshData() {
        browsePath.refreshDrawableState();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, arrPackage){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.RED);
                return textView;
            }
        };

        listView.setAdapter(adapter);

        // Assign adapter to ListView
        listView.setAdapter(adapter);
        storesharedPreferences();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_privacy_policy) {
            startActivity(new Intent(HomeActivityWithDrawer.this,WebviewActivity.class).putExtra("url","file:///android_asset/privacypolicy.html"));
        } else if (id == R.id.nav_readme) {
            startActivity(new Intent(HomeActivityWithDrawer.this,WebviewActivity.class).putExtra("url","file:///android_asset/readme.html"));

        } else if (id == R.id.nav_references) {
            startActivity(new Intent(HomeActivityWithDrawer.this,WebviewActivity.class).putExtra("url","file:///android_asset/references.html"));

        } else if (id == R.id.nav_share) {
            Toast.makeText(getApplicationContext(),"Will be updated soon....",Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_reader_share) {
            String message = "I like this application very useful and it has awesome reading features.\n YOU MUST TRY IT......";
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(share, "EduTech-Reader an Awesome app."));
        }
        else if (id == R.id.nav_reader_emailus) {

            Toast.makeText(getApplicationContext(),"Will be updated soon....",Toast.LENGTH_SHORT).show();
          /*  Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"info@mapedutech.com "});
            i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
            i.putExtra(Intent.EXTRA_TEXT   , "body of email");
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(HomeActivityWithDrawer.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }*/
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Called to create a dialog to be shown.
     */
   /* @Override
    protected Dialog onCreateDialog(int id) {

        switch (id) {
            case DLG_EXAMPLE1:
                return createExampleDialog();
            default:
                return null;
        }
    }
*/
    /**
     * If a dialog has already been created,
     * this is called to reset the dialog
     * before showing it a 2nd time. Optional.
     */
 /*   @Override
    protected void onPrepareDialog(int id, Dialog dialog) {

        switch (id) {
            case DLG_EXAMPLE1:
                // Clear the input box.
                EditText text = (EditText) dialog.findViewById(TEXT_ID);
                text.setText("");
                break;
        }
    }*/
    private Dialog createExampleDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hello User please give your feedback");
        builder.setMessage("What is your name:");

        // Use an EditText view to get user input.
        final EditText input = new EditText(this);
        input.setId(TEXT_ID);
        builder.setView(input);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                Log.d(TAG, "User name: " + value);
                return;
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        return builder.create();
    }
}
