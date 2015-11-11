package com.cardboardmx.uberbita.database;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import com.cardboardmx.uberbita.database.tables.InfoCelular;
import com.cardboardmx.uberbita.database.tables.Viajes;

/**
 * Created by juanelo on 20/10/15.
 */
public class DBHelper extends SQLiteOpenHelper {

                                                //"/storage/emulated/0/Data/UberBita/data.db";//Name to desencript
    private static final String DATABASE_NAME = "viajesuberbita.db";//Name for db
    private static final int DATABASE_VERSION = 1;

    private final String CREATE_USUARIOS =
            "create table " + Viajes.TAB_NAME +
                    "(" + Viajes._ID_VIAJE + " integer primary key,"+ Viajes.LATITUDE + " TEXT,"+
                    Viajes.LONGITUDE + " TEXT, "+Viajes.FECHA_HORA+" TEXT)";

    private final String CREATE_INFO_CELULAR =
            "create table " + InfoCelular.TAB_NAME +
                    "(" + InfoCelular._ID_INFO_CELULAR + " integer primary key,"+ InfoCelular.PHONE + " TEXT,"+
                    InfoCelular.UUID + " TEXT)";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

//        db.execSQL("DROP TABLE IF EXISTS " + Viajes.TAB_NAME);
//        db.execSQL("DROP TABLE IF EXISTS " + InfoCelular.TAB_NAME);
//        createTables(db);
    }

    private void createTables(SQLiteDatabase db){

        db.execSQL(CREATE_USUARIOS);
        db.execSQL(CREATE_INFO_CELULAR);
    }
}
