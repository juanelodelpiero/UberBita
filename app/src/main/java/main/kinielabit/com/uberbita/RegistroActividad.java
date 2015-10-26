package main.kinielabit.com.uberbita;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import main.kinielabit.com.uberbita.database.DBHelper;
import main.kinielabit.com.uberbita.database.tables.InfoCelular;
import main.kinielabit.com.uberbita.database.tables.Viajes;

public class RegistroActividad extends AppCompatActivity implements RegisteredTrips.OnFragmentInteractionListener{



    public static DBHelper dbHelper = null;
    private SQLiteDatabase dataBase = null;
    private LocationManager locationManager;
    private String mLatitude;
    private String mLongitude;

    private SimpleDateFormat DF_SQLITE_= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private RegisteredTrips registeredTrips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registro_actividad);

        SQLiteDatabase.loadLibs(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        openDataBase();
        saveInfo();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        if (savedInstanceState == null){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            registeredTrips = RegisteredTrips.newInstance(null, null);
            transaction.replace(R.id.frame_layout, registeredTrips);
            transaction.commit();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ((mLatitude!= null && mLongitude != null)
                && (!mLatitude.isEmpty() || !mLongitude.isEmpty())){
                    saveTrip();
                    Snackbar.make(view, "Viaje guardado", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }else {
                    Snackbar.make(view, "Espera unos segundos e intentalo de nuevo", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();


                }


            }
        });
    }


    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

    private String getId(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
                editor.commit();

            }
        }
        return uniqueID;
    }

    private String getPhone(){
        TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        return mPhoneNumber;
    }

    private File getExportDir() {
        File storageDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = getAlbumStorageDir(getString(R.string.export_folder));
            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Toast.makeText(RegistroActividad.this, "Error al crear el directorio", Toast.LENGTH_LONG).show();
                        return null;
                    }
                }
            }
        } else {
            Toast.makeText(RegistroActividad.this, "SD no montada", Toast.LENGTH_LONG).show();
        }

        return storageDir;
    }

    private File getAlbumStorageDir(String export_foder){
        return new File(
                Environment.getExternalStorageDirectory()
                        + "/Data/"
                        + export_foder
        );
    }


        @Override
    protected void onStart() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null){
            dbHelper.close();
        }

        if (dataBase != null){
            dataBase.close();
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        locationManager.removeUpdates(locationListener);
        super.onStop();

    }

    private void saveInfo(){

        String key = "wasSave";
        SharedPreferences sharedPref = this
                .getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        boolean wasSave = sharedPref.getBoolean(key, false);

        if (!wasSave) {
            ContentValues cv = new ContentValues();
            cv.put(InfoCelular.PHONE, getPhone());
            cv.put(InfoCelular.UUID, getId(this));

            dataBase.insert(InfoCelular.TAB_NAME, null, cv);

            editor.putBoolean(key, true);
            editor.commit();
        }


    }

    private void saveTrip(){
        long idViaje = -1;
        if(true){//Si el tiempo es mayor a 5 minutos

            ContentValues cv = new ContentValues();
            cv.put(Viajes.FECHA_HORA, DF_SQLITE_.format(new Date()));
            cv.put(Viajes.LATITUDE, mLatitude);
            cv.put(Viajes.LONGITUDE, mLongitude);

            idViaje = dataBase.insert(Viajes.TAB_NAME, null,cv);

            if (idViaje > 0){
                registeredTrips.addElemnt("Viaje no. " + idViaje + " Fecha:" + DF_SQLITE_.format(new Date()));
            }
        }



        //Consulto para validar que se insertio
//        Cursor cursor = dataBase.query(Viajes.TAB_NAME, null, null, null, null, null, null);
//
//        while (cursor.moveToNext()){
//            Log.d("UberBita", cursor.getString(cursor.getColumnIndex(Viajes.FECHA_HORA)) + " Ubicacion: "
//            +cursor.getString(cursor.getColumnIndex(Viajes.LATITUDE)) + ","
//                    +cursor.getString(cursor.getColumnIndex(Viajes.LONGITUDE)));
//        }
//
//        cursor.close();

    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("UberBita", location.toString());
            mLatitude = String.valueOf(location.getLatitude());
            mLongitude = String.valueOf(location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    private void openDataBase(){
        if (dbHelper == null){
            dbHelper = new DBHelper(this);
            dataBase = dbHelper.getWritableDatabase("");//"SOCIOSGDLMX"

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registro_actividad, menu);
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
            exportData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void exportData(){
        try {
            File sd = getExportDir();
            File data  = Environment.getDataDirectory();


            if (sd.canWrite()){
                String currentDBPath = "/data/main.kinielabit.com.uberbita/databases/viajesuberbita.db";
                String backupDBPath = "data";
                Log.d("DEBUG", backupDBPath);
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getBaseContext(), "Archivo listo para enviar",
                        Toast.LENGTH_LONG).show();
                Log.d("DEBUG", backupDB.toString());
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(backupDB));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }



    @Override
    public void onFragmentInteraction(String uri) {

    }
}
