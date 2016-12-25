package smart.sonitum.Helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "SonitumDB";
    private static final String TABLE_AUDIO_NAME = "Audio";

    public DBHelper(Context ctx) {
        super(ctx, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        AudioRepository audioRepository = new AudioRepository();
        audioRepository.connect(db);
        audioRepository.create();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_AUDIO_NAME);
        onCreate(db);
    }
}
