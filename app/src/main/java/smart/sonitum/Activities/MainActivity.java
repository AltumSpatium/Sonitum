package smart.sonitum.Activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import smart.sonitum.Data.Audio;
import smart.sonitum.Data.Playlist;
import smart.sonitum.Fragments.AlbumsFragment;
import smart.sonitum.Fragments.ArtistsFragment;
import smart.sonitum.Fragments.AudioFragment;
import smart.sonitum.Helpers.AudioRepository;
import smart.sonitum.Helpers.DBHelper;
import smart.sonitum.R;

public class MainActivity extends AppCompatActivity implements AlbumsFragment.OnAlbumOpenListener,
        ArtistsFragment.OnArtistOpenListener {
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    public static int navItemIndex = 0;

    private static final String TAG_MUSIC = "music";
    private static final String TAG_ALBUMS = "albums";
    private static final String TAG_ARTISTS = "artists";
    private static final String TAG_PLAYLISTS = "playlists";
    private static final String TAG_CURRENT_ALBUM = "current_album";
    private static final String TAG_CURRENT_ARTIST = "current_artist";
    public static String CURRENT_TAG = TAG_MUSIC;

    private String[] activityTitles;
    private String currentAlbum;
    private String currentArtist;

    LinearLayout llNowPlaying;

    ArrayList<Audio> tracks = new ArrayList<>();
    HashMap<String, ArrayList<Audio>> albums = new HashMap<>();
    HashMap<String, ArrayList<String>> artistsAlbums = new HashMap<>();
    HashMap<String, Bitmap> albumArts = new HashMap<>();
    ArrayList<Playlist> playlists = new ArrayList<>();

    DBHelper dbHelper;

    private android.os.Handler mHandler;
    private AudioFillTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DBHelper(this);

        mHandler = new android.os.Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        activityTitles = getResources().getStringArray(R.array.activity_titles);

        setUpNavigationMenu();

        task = new AudioFillTask(this);
        task.execute();

        navItemIndex = 0;
        CURRENT_TAG = TAG_MUSIC;
        loadMusicFragment(null);
    }

    private void setUpNavigationMenu() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_allMusic:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_MUSIC;
                        break;
                    case R.id.nav_albums:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_ALBUMS;
                        break;
                    case R.id.nav_artists:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_ARTISTS;
                        break;
                    case R.id.nav_playlists:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_PLAYLISTS;
                        break;
                    case R.id.nav_settings:
                        //starting settings activity
                        return true;
                    default:
                        navItemIndex = 0;
                }

                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }

                loadMusicFragment(null);

                return true;
            }
        });

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        drawer.addDrawerListener(actionBarDrawerToggle);
    }

    private void loadMusicFragment(String toolbarTitle) {
        selectNavMenu();
        setToolbarTitle(toolbarTitle);

        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            currentAlbum = currentArtist = null;
            int back = getSupportFragmentManager().getBackStackEntryCount();
            while (back-- > 0) {
                getSupportFragmentManager().popBackStack();
            }
            actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        }

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                Fragment fragment = getMusicFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                if (currentAlbum != null || currentArtist != null)
                    fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };
        mHandler.post(mPendingRunnable);

        drawer.closeDrawers();
        invalidateOptionsMenu();
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setToolbarTitle(String title) {
        if (title == null)
            getSupportActionBar().setTitle(activityTitles[navItemIndex]);
        else {
            if (title.length() > 25) title = title.substring(0, 22) + "...";
            getSupportActionBar().setTitle(title);
        }
    }

    private Fragment getMusicFragment() {
        if (CURRENT_TAG.equals(TAG_CURRENT_ALBUM) && currentAlbum != null) {
            actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
            actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.arrow_back);

            ArrayList<Audio> albumTracks = albums.get(currentAlbum);
            return AudioFragment.newInstance(albumTracks);
        } else if (CURRENT_TAG.equals(TAG_CURRENT_ARTIST) && currentArtist != null) {
            actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
            actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.arrow_back);

            ArrayList<String> albumTitles = artistsAlbums.get(currentArtist);
            return AlbumsFragment.newInstance(albumTitles, albums, albumArts);
        }

        switch (navItemIndex) {
            case 0:
                return AudioFragment.newInstance(tracks);
            case 1:
                ArrayList<String> albumTitles = getAlbumTitles();
                return AlbumsFragment.newInstance(albumTitles, albums, albumArts);
            case 2:
                ArrayList<String> artists = getArtistsAlbums();
                return ArtistsFragment.newInstance(artists, artistsAlbums);
            case 3:
                return null;
            default:
                return AudioFragment.newInstance(tracks);
        }
    }

    private ArrayList<String> getAlbumTitles() {
        ArrayList<String> albumTitles = new ArrayList<>();

        for (String album : albums.keySet())
            albumTitles.add(album);

        Collections.sort(albumTitles, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.toLowerCase().compareTo(o2.toLowerCase());
            }
        });

        return albumTitles;
    }

    private ArrayList<String> getArtistsAlbums() {
        ArrayList<String> artists = new ArrayList<>();

        for (String artist : artistsAlbums.keySet())
            if (artistsAlbums.get(artist).size() != 0)
                artists.add(artist);

        Collections.sort(artists, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.toLowerCase().compareTo(o2.toLowerCase());
            }
        });

        return artists;
    }

    private void fillTracks() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        AudioRepository audioRepository = new AudioRepository();
        audioRepository.connect(db);

        tracks = audioRepository.loadAll();

        if (tracks.isEmpty()) {
            tracks = findTracks();
            audioRepository.saveAll(tracks);
        }
    }

    private void fillAlbums() {
        for (Audio track : tracks) {
            if (albums.get(track.getAlbum()) == null) {
                albums.put(track.getAlbum(), new ArrayList<Audio>());
                albumArts.put(track.getAlbum(), null);
            }
            albums.get(track.getAlbum()).add(track);
        }
    }

    private void fillArtists() {
        HashMap<String, Boolean> checkedAlbums = new HashMap<>();

        for (Audio track : tracks) {
            if (artistsAlbums.get(track.getArtist()) == null)
                artistsAlbums.put(track.getArtist(), new ArrayList<String>());
            if (checkedAlbums.get(track.getAlbum()) == null) {
                checkedAlbums.put(track.getAlbum(), true);
                artistsAlbums.get(track.getArtist()).add(track.getAlbum());
            }
        }
    }

    private ArrayList<Audio> findTracks() {
        ArrayList<Audio> tracks;

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.TRACK,
                MediaStore.Audio.Media.YEAR,
                MediaStore.Audio.Media.DATA
        };

        Uri uriExternal = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri uriInternal = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;

        ContentResolver contentResolver = getContentResolver();

        Cursor cursorExt = contentResolver.query(uriExternal, projection, null, null, null);
        Cursor cursorInt = contentResolver.query(uriInternal, projection, null, null, null);

        tracks = loadFromCursor(cursorExt, "external");
        tracks.addAll(loadFromCursor(cursorInt, "internal"));
        Collections.sort(tracks, new Comparator<Audio>() {
            @Override
            public int compare(Audio o1, Audio o2) {
                return o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase());
            }
        });

        return tracks;
    }

    private ArrayList<Audio> loadFromCursor(Cursor c, String place) {
        ArrayList<Audio> tracks = new ArrayList<>();

        if (c != null) {
            for (int i = 0; i < c.getCount(); i++) {
                c.moveToPosition(i);

                long id = c.getInt(c.getColumnIndex(MediaStore.Audio.Media._ID));
                int totalTime = c.getInt(c.getColumnIndex(MediaStore.Audio.Media.DURATION));
                int albumId = c.getInt(c.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String album = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String title = c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String genre = getTrackGenre(albumId, place);
                String trackNumber = c.getString(c.getColumnIndex(MediaStore.Audio.Media.TRACK));
                String albumArt = getAlbumArt(albumId, place);
                String year = c.getString(c.getColumnIndex(MediaStore.Audio.Media.YEAR));
                String data = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));

                Audio track = new Audio(id, totalTime, album, artist, title, genre, trackNumber, albumArt, year, data);
                if (!track.getArtist().equals("<unknown>"))
                    tracks.add(track);
            }
            c.close();
        }

        return tracks;
    }

    private String getTrackGenre(int id, String place) {
        String genre = "";
        String[] genresProjection = {
                MediaStore.Audio.Genres._ID,
                MediaStore.Audio.Genres.NAME
        };

        try {
            Cursor genresCursor = getContentResolver().query(
                    place.equals("external") ? MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI : MediaStore.Audio.Genres.INTERNAL_CONTENT_URI,
                    genresProjection,
                    MediaStore.Audio.Genres._ID + "=?",
                    new String[]{String.valueOf(id)},
                    null);
            if (genresCursor != null) {
                int genreColIndex = genresCursor.getColumnIndex(MediaStore.Audio.Genres.NAME);

                if (genresCursor.moveToFirst()) {
                    do {
                        genre += genresCursor.getString(genreColIndex) + "/";
                    } while (genresCursor.moveToNext());
                }
                if (genre.length() > 0)
                    genre = genre.substring(0, genre.length() - 1);
                genresCursor.close();
            }
        }
        catch (RuntimeException e) {
            genre = "<unknown>";
        }

        return genre;
    }

    private String getAlbumArt(int albumId, String place) {
        String albumArt = "";
        Cursor cursor = getContentResolver().query(
                place.equals("external") ? MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI : MediaStore.Audio.Albums.INTERNAL_CONTENT_URI,
                new String[] {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + "=?",
                new String[] {String.valueOf(albumId)},
                null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                albumArt = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
            }
            cursor.close();
        }

        return albumArt;
    }

    @Override
    public void onAlbumOpened(String album) {
        currentAlbum = album;
        CURRENT_TAG = TAG_CURRENT_ALBUM;

        loadMusicFragment(album);
    }

    @Override
    public void onArtistOpened(String artist) {
        currentArtist = artist;
        CURRENT_TAG = TAG_CURRENT_ARTIST;

        loadMusicFragment(artist);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (currentArtist != null && currentAlbum != null) {
                currentAlbum = null;
                CURRENT_TAG = TAG_CURRENT_ARTIST;
                setToolbarTitle(currentArtist);
            } else if (currentArtist != null) {
                currentArtist = null;
                CURRENT_TAG = TAG_ARTISTS;
                setToolbarTitle(null);
                actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
            } else if (currentAlbum != null) {
                currentAlbum = null;
                CURRENT_TAG = TAG_ALBUMS;
                setToolbarTitle(null);
                actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
            }

            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        switch (item.getItemId()) {
            case R.id.action_refresh:
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                dbHelper.onUpgrade(db, 0, 1);
                loadMusicFragment(null);
                actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    private class AudioFillTask extends AsyncTask<Void, Void, Void> {
        private Context ctx;
        private ProgressDialog progressDialog;

        AudioFillTask(Context ctx) {
            this.ctx = ctx;
            progressDialog = new ProgressDialog(this.ctx);
            progressDialog.setMessage("Searching audio...");
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            fillTracks();
            fillAlbums();
            fillArtists();
            try {
                synchronized (this) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadMusicFragment(null);
                        }
                    });
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (progressDialog.isShowing()) progressDialog.dismiss();
        }
    }
}
