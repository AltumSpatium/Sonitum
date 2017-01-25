package smart.sonitum.Helpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import smart.sonitum.Data.Audio;
import smart.sonitum.Data.Playlist;

public class AudioRepository {
    private static final String TABLE_AUDIO_NAME = "Audio";
    private static final String TABLE_PLAYLISTS_NAME = "Playlists";
    private static final String TABLE_PLAYLIST_TRACKS = "PlaylistTracks";

    private SQLiteDatabase db;

    public void connect(SQLiteDatabase db) {
        this.db = db;
    }

    public void create() {
        db.execSQL(
                "create table if not exists " + TABLE_AUDIO_NAME + " ("
                + "id integer primary key autoincrement, "
                + "totalTime integer, "
                + "album text, "
                + "artist text, "
                + "title text, "
                + "genre text, "
                + "trackNumber text, "
                + "albumArt text, "
                + "year text, "
                + "data text" + ");"
        );
    }

    public void createPlaylists() {
        db.execSQL(
                "create table if not exists " + TABLE_PLAYLISTS_NAME + " ("
                + "id integer primary key autoincrement, "
                + "name text" + ");"
        );

        db.execSQL(
                "create table if not exists " + TABLE_PLAYLIST_TRACKS + " ("
                + "playlist_id integer not null, "
                + "track_id integer not null" + ");"
        );
    }

    public void add(Audio audio) {
        ContentValues cv = new ContentValues();

        cv.put("totalTime", audio.getTotalTime());
        cv.put("album", audio.getAlbum());
        cv.put("artist", audio.getArtist());
        cv.put("title", audio.getTitle());
        cv.put("genre", audio.getGenre());
        cv.put("trackNumber", audio.getTrackNumber());
        cv.put("albumArt", audio.getAlbumArt());
        cv.put("year", audio.getYear());
        cv.put("data", audio.getData());

        db.insert(TABLE_AUDIO_NAME, null, cv);
    }

    public void addPlaylist(Playlist playlist) {
        ContentValues cv = new ContentValues();

        cv.put("name", playlist.getName());

        long id = db.insert(TABLE_PLAYLISTS_NAME, null, cv);

        for (Audio track : playlist.getTracks()) {
            cv = new ContentValues();
            cv.put("playlist_id", id);
            cv.put("track_id", track.getId());
            db.insert(TABLE_PLAYLIST_TRACKS, null, cv);
        }
    }

    public void deleteAudio(Audio track) {
        db.delete(TABLE_AUDIO_NAME, "id = ? and title = ?",
                new String[] { Long.toString(track.getId()), track.getTitle() });
    }

    public void deleteAlbum(String album) {
        db.delete(TABLE_AUDIO_NAME, "album = ?", new String[] { album });
    }

    public void deleteArtist(String artist) {
        db.delete("Audio", "artist = ?", new String[] { artist });
    }

    public void deletePlaylist(Playlist playlist) {
        db.delete(TABLE_PLAYLISTS_NAME, "id = ? and name = ?",
                new String[] { Long.toString(playlist.getId()), playlist.getName() });
    }

    private ArrayList<Audio> loadTracksFromCursor(Cursor c) {
        ArrayList<Audio> tracks = new ArrayList<>();

        if (c != null) {
            if (c.moveToFirst()) {
                int idColIndex = c.getColumnIndex("id");
                int totalTimeColIndex = c.getColumnIndex("totalTime");
                int albumColIndex = c.getColumnIndex("album");
                int artistColIndex = c.getColumnIndex("artist");
                int titleColIndex = c.getColumnIndex("title");
                int genreColIndex = c.getColumnIndex("genre");
                int trackNumberColIndex = c.getColumnIndex("trackNumber");
                int albumArtColIndex  = c.getColumnIndex("albumArt");
                int yearColIndex = c.getColumnIndex("year");
                int dataColIndex = c.getColumnIndex("data");

                do {
                    long id = c.getLong(idColIndex);
                    int totalTime = c.getInt(totalTimeColIndex);
                    String album = c.getString(albumColIndex);
                    String artist = c.getString(artistColIndex);
                    String title = c.getString(titleColIndex);
                    String genre = c.getString(genreColIndex);
                    String trackNumber = c.getString(trackNumberColIndex);
                    String albumArt = c.getString(albumArtColIndex);
                    String year = c.getString(yearColIndex);
                    String data = c.getString(dataColIndex);

                    tracks.add(new Audio(id, totalTime, album, artist, title, genre, trackNumber, albumArt, year, data));
                } while (c.moveToNext());
            }

            c.close();
        }

        return tracks;
    }

    private Playlist loadPlaylistFromCursor(Cursor c, int position) {
        Playlist playlist = new Playlist("", new ArrayList<Audio>());

        if (c != null) {
            if (c.moveToPosition(position)) {
                int idColIndex = c.getColumnIndex("id");
                int nameColIndex = c.getColumnIndex("name");

                long id = c.getLong(idColIndex);
                String name = c.getString(nameColIndex);

                playlist.setId(id);
                playlist.setName(name);
                Cursor cursor = db.rawQuery("select * from Audio, PlaylistTracks where PlaylistTracks.playlist_id = ?"
                        + " and PlaylistTracks.track_id = Audio.id;", new String[] { Long.toString(id) });
                playlist.setTracks(loadTracksFromCursor(cursor));
            }
        }

        return playlist;
    }

    public ArrayList<Audio> loadAll() {
        Cursor c = db.query(TABLE_AUDIO_NAME, null, null, null, null, null, null);
        return loadTracksFromCursor(c);
    }

    public ArrayList<Playlist> loadPlaylists() {
        ArrayList<Playlist> playlists = new ArrayList<>();
        Cursor c = db.query(TABLE_PLAYLISTS_NAME, null, null, null, null, null, null);
        for (int i = 0; i < c.getCount(); i++) {
            playlists.add(loadPlaylistFromCursor(c, i));
        }
        c.close();

        return playlists;
    }

    public ArrayList<Audio> loadAlbum(String album) {
        String selection = "album = ?";
        String[] selectionArgs = new String[] { album };
        Cursor c = db.query(TABLE_AUDIO_NAME, null, selection, selectionArgs, null, null, null);
        return loadTracksFromCursor(c);
    }

    public ArrayList<Audio> loadArtist(String artist) {
        String selection = "artist = ?";
        String[] selectionArgs = new String[] { artist };
        Cursor c = db.query(TABLE_AUDIO_NAME, null, selection, selectionArgs, null, null, null);
        return loadTracksFromCursor(c);
    }

    public Playlist loadPlaylist(String name) {
        String selection = "name = ?";
        String[] selectionArgs = new String[] { name };
        Cursor c = db.query(TABLE_PLAYLISTS_NAME, null, selection, selectionArgs, null, null, null);
        Playlist playlist = loadPlaylistFromCursor(c, 0);
        c.close();
        return playlist;
    }

    public void saveAll(ArrayList<Audio> tracks) {
        db.delete(TABLE_AUDIO_NAME, null, null);

        for (Audio track : tracks)
            add(track);
    }

    public void savePlaylists(ArrayList<Playlist> playlists) {
        db.delete(TABLE_PLAYLISTS_NAME, null, null);

        for (Playlist playlist : playlists)
            addPlaylist(playlist);
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
