package smart.sonitum.Helpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import smart.sonitum.Data.Audio;

public class AudioRepository {
    private SQLiteDatabase db;

    public void connect(SQLiteDatabase db) {
        this.db = db;
    }

    public void create() {
        db.execSQL(
                "create table if not exists Audio ("
                + "id integer primary key autoincrement, "
                + "totalTime integer, "
                + "album text, "
                + "artist text, "
                + "title text, "
                + "genre text, "
                + "data text" + ");"
        );
    }

    public void add(Audio audio) {
        ContentValues cv = new ContentValues();

        cv.put("totalTime", audio.getTotalTime());
        cv.put("album", audio.getAlbum());
        cv.put("artist", audio.getArtist());
        cv.put("title", audio.getTitle());
        cv.put("genre", audio.getGenre());
        cv.put("data", audio.getData());

        db.insert("Audio", null, cv);
    }

    public void deleteAudio(String title) {
        db.delete("Audio", "title = '" + title + "'", null);
    }

    public void deleteAlbum(String album) {
        db.delete("Audio", "album = '" + album + "'", null);
    }

    public void deleteArtist(String artist) {
        db.delete("Audio", "artist = '" + artist + "'", null);
    }

    private ArrayList<Audio> loadDataFromCursor(Cursor c) {
        ArrayList<Audio> tracks = new ArrayList<>();

        if (c != null) {
            if (c.moveToFirst()) {
                int idColIndex = c.getColumnIndex("id");
                int totalTimeColIndex = c.getColumnIndex("totalTime");
                int albumColIndex = c.getColumnIndex("album");
                int artistColIndex = c.getColumnIndex("artist");
                int titleColIndex = c.getColumnIndex("title");
                int genreColIndex = c.getColumnIndex("genre");
                int dataColIndex = c.getColumnIndex("data");

                do {
                    long id = c.getInt(idColIndex);
                    int totalTime = c.getInt(totalTimeColIndex);
                    String album = c.getString(albumColIndex);
                    String artist = c.getString(artistColIndex);
                    String title = c.getString(titleColIndex);
                    String genre = c.getString(genreColIndex);
                    String data = c.getString(dataColIndex);

                    tracks.add(new Audio(id, totalTime, album, artist, title, genre, data));
                } while (c.moveToNext());
            }

            c.close();
        }

        return tracks;
    }

    public ArrayList<Audio> loadAll() {
        Cursor c = db.query("Audio", null, null, null, null, null, null);
        return loadDataFromCursor(c);
    }

    public ArrayList<Audio> loadAlbum(String album) {
        String selection = "album = ?";
        String[] selectionArgs = new String[] { album };
        Cursor c = db.query("Audio", null, selection, selectionArgs, null, null, null);
        return loadDataFromCursor(c);
    }

    public ArrayList<Audio> loadArtist(String artist) {
        String selection = "artist = ?";
        String[] selectionArgs = new String[] { artist };
        Cursor c = db.query("Audio", null, selection, selectionArgs, null, null, null);
        return loadDataFromCursor(c);
    }

    public void saveAll(ArrayList<Audio> tracks) {
        db.delete("Audio", null, null);

        for (Audio track : tracks)
            add(track);
    }

    public void saveAlbum(ArrayList<Audio> tracksAlbum, String album) {
        deleteAlbum(album);

        for (Audio track : tracksAlbum)
            add(track);
    }

    public void saveArtist(ArrayList<Audio> tracksArtist, String artist) {
        deleteArtist(artist);

        for (Audio track : tracksArtist)
            add(track);
    }
}
