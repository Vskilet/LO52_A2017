package fr.utbm.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Exige on 23/09/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // Table Volant
    public static final String VOLANT_ID = "id";
    public static final String VOLANT_MARQUE = "marque";
    public static final String VOLANT_REF = "ref";
    public static final String VOLANT_CLASSEMENT = "classement";
    public static final String VOLANT_LOT_ID = "lotId";

    public static final String VOLANTS_TABLE_NAME = "Volant";
    public static final String VOLANTS_TABLE_CREATE =
            "CREATE TABLE " + VOLANTS_TABLE_NAME + " (" +
                    VOLANT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    VOLANT_MARQUE + " TEXT, " +
                    VOLANT_REF + " TEXT, " +
                    VOLANT_CLASSEMENT + " INTEGER, " +
                    VOLANT_LOT_ID + " INTEGER);";

    public static final String VOLANTS_TABLE_DROP = "DROP TABLE IF EXISTS " + VOLANTS_TABLE_NAME + ";";

    // Table LotVolant
    public static final String LOTVOLANT_ID = "id";
    public static final String LOTVOLANT_TAILLE = "taille";
    public static final String LOTVOLANT_PRIX = "prix";

    public static final String LOTVOLANT_TABLE_NAME = "LotVolant";
    public static final String LOTVOLANT_TABLE_CREATE =
            "CREATE TABLE " + LOTVOLANT_TABLE_NAME + " (" +
                    LOTVOLANT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    LOTVOLANT_TAILLE + " INTEGER, " +
                    LOTVOLANT_PRIX + " REAL);";

    public static final String LOTVOLANT_TABLE_DROP = "DROP TABLE IF EXISTS " + LOTVOLANT_TABLE_NAME + ";";

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(VOLANTS_TABLE_CREATE);
        sqLiteDatabase.execSQL(LOTVOLANT_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d("eDBTEAM/DatabaseHandler", "Updating database (from v" + oldVersion + " to v" + newVersion + ")");
        sqLiteDatabase.execSQL(VOLANTS_TABLE_DROP);
        sqLiteDatabase.execSQL(LOTVOLANT_TABLE_DROP);
        onCreate(sqLiteDatabase);
    }



}
