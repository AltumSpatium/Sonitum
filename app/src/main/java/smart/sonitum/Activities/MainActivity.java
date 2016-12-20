package smart.sonitum.Activities;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import smart.sonitum.Data.Audio;
import smart.sonitum.Fragments.AlbumsFragment;
import smart.sonitum.Fragments.ArtistsFragment;
import smart.sonitum.Fragments.AllMusicFragment;
import smart.sonitum.Fragments.CurrentAlbumFragment;
import smart.sonitum.Helpers.AudioRepository;
import smart.sonitum.Helpers.DBHelper;
import smart.sonitum.R;

public class MainActivity extends AppCompatActivity implements AlbumsFragment.OnFragmentInteractionListener {
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private Toolbar toolbar;

    public static int navItemIndex = 0;

    private static final String TAG_MUSIC = "music";
    private static final String TAG_ALBUMS = "albums";
    private static final String TAG_ARTISTS = "artists";
    private static final String TAG_CURRENT_ALBUM = "current_album";
    public static String CURRENT_TAG = TAG_MUSIC;

    private String[] activityTitles;
    private String currentAlbum;

    LinearLayout llNowPlaying;

    ArrayList<Audio> tracks = new ArrayList<>();
    HashMap<String, ArrayList<Audio>> albums = new HashMap<>();
    HashMap<String, ArrayList<Audio>> artistsTracks = new HashMap<>();

    DBHelper dbHelper;

    private android.os.Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new android.os.Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        activityTitles = getResources().getStringArray(R.array.activity_titles);

        setUpNavigationMenu();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_MUSIC;
            loadMusicFragment(null);
        }

        fillTracks();
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
                item.setChecked(true);

                loadMusicFragment(null);

                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void loadMusicFragment(String toolbarTitle) {
        selectNavMenu();
        setToolbarTitle(toolbarTitle);

        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }

        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                Fragment fragment = getMusicFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                if (currentAlbum != null)
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
            if (title.length() > 15) title = title.substring(0, 13) + "...";
            getSupportActionBar().setTitle(title);
        }
    }

    private Fragment getMusicFragment() {
        if (CURRENT_TAG.equals(TAG_CURRENT_ALBUM) && currentAlbum != null) {
            ArrayList<Audio> albumTracks = albums.get(currentAlbum);
            return CurrentAlbumFragment.newInstance(albumTracks);
        }

        switch (navItemIndex) {
            case 0:
                return AllMusicFragment.newInstance(tracks);
            case 1:
                if (albums.isEmpty()) fillAlbums();
                ArrayList<String> albumTitles = getAlbumTitles();
                return AlbumsFragment.newInstance(albumTitles, albums);
            case 2:
                if (artistsTracks.isEmpty()) fillArtists();
                ArrayList<String> artists = getArtistsTracks();
                return ArtistsFragment.newInstance(artists, artistsTracks);
            default:
                return AllMusicFragment.newInstance(tracks);
        }
    }

    private ArrayList<Audio> loadFromCursor(Cursor c) {
        ArrayList<Audio> tracks = new ArrayList<>();

        if (c != null) {
            for (int i = 0; i < c.getCount(); i++) {
                c.moveToPosition(i);

                long id = c.getInt(c.getColumnIndex(MediaStore.Audio.Media._ID));
                int totalTime = c.getInt(c.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String album = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String artist = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String title = c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String genre = "Other";
                String data = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));

                Audio track = new Audio(id, totalTime, album, artist, title, genre, data);
                if (!track.getArtist().equals("<unknown>"))
                    tracks.add(track);
            }
            c.close();
        }

        return tracks;
    }

    public ArrayList<Audio> findTracks() {
        ArrayList<Audio> tracks;

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA
        };

        Uri uriExternal = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri uriInternal = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;

        ContentResolver contentResolver = getContentResolver();

        Cursor cursorExt = contentResolver.query(uriExternal, projection, null, null, null);
        Cursor cursorInt = contentResolver.query(uriInternal, projection, null, null, null);

        tracks = loadFromCursor(cursorExt);
        tracks.addAll(loadFromCursor(cursorInt));
        Collections.sort(tracks, new Comparator<Audio>() {
            @Override
            public int compare(Audio o1, Audio o2) {
                return o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase());
            }
        });

        return tracks;
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

    private ArrayList<String> getArtistsTracks() {
        ArrayList<String> artists = new ArrayList<>();

        for (String artist : artistsTracks.keySet())
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
        dbHelper = new DBHelper(this);

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
            if (albums.get(track.getAlbum()) == null)
                albums.put(track.getAlbum(), new ArrayList<Audio>());
            albums.get(track.getAlbum()).add(track);
        }
    }

    private void fillArtists() {
        for (Audio track : tracks) {
            if (artistsTracks.get(track.getArtist()) == null)
                artistsTracks.put(track.getArtist(), new ArrayList<Audio>());
            artistsTracks.get(track.getArtist()).add(track);
        }
    }

    @Override
    public void onFragmentInteraction(String album) {
        currentAlbum = album;
        CURRENT_TAG = TAG_CURRENT_ALBUM;

        loadMusicFragment(currentAlbum);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (currentAlbum != null) {
                currentAlbum = null;
                CURRENT_TAG = TAG_ALBUMS;
                setToolbarTitle(null);
            }

            ArtistsFragment fragmentArtist = (ArtistsFragment) getSupportFragmentManager().findFragmentByTag(TAG_ARTISTS);
            if (fragmentArtist != null && fragmentArtist.tracksShowed) {
                fragmentArtist.exitArtist();
                return;
            }

            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //set to inflate needed menu for each fragment
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
