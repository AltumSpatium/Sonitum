package smart.sonitum.Fragments;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import smart.sonitum.Activities.MainActivity;
import smart.sonitum.Adapters.PlaylistAdapter;
import smart.sonitum.Data.Audio;
import smart.sonitum.Data.Playlist;
import smart.sonitum.Helpers.AudioRepository;
import smart.sonitum.Helpers.DBHelper;
import smart.sonitum.R;
import smart.sonitum.Utils.Utils;
import smart.sonitum.Utils.VerticalSpaceItemDecoration;

public class PlaylistsFragment extends Fragment implements NewPlaylistDialog.PlaylistCreateListener {
    private static final String ARG_PLAYLISTS = "playlists";

    private ArrayList<Playlist> playlists;

    PlaylistAdapter playlistAdapter;

    private OnPlaylistOpenListener listener;

    public PlaylistsFragment() {}

    public static PlaylistsFragment newInstance(ArrayList<Playlist> playlists) {
        PlaylistsFragment fragment = new PlaylistsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PLAYLISTS, playlists);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playlists = getArguments().getParcelableArrayList(ARG_PLAYLISTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlists, container, false);

        FloatingActionButton fabAddPlaylist = (FloatingActionButton) view.findViewById(R.id.fabAddPlaylist);
        ArrayList<Audio> tracks = ((MainActivity)getActivity()).tracks;
        final NewPlaylistDialog dialog = NewPlaylistDialog.newInstance(tracks);
        //dialog.setTargetFragment(this, 0);
        fabAddPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show(getFragmentManager(), "");
            }
        });

        RecyclerView rvMain = (RecyclerView) view.findViewById(R.id.rvMain);
        rvMain.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvMain.addItemDecoration(new VerticalSpaceItemDecoration(5));
        rvMain.setLayoutAnimation(Utils.listAlphaTranslateAnimation(300, 100, false, 0.3f));

        playlistAdapter = new PlaylistAdapter(playlists);
        rvMain.setAdapter(playlistAdapter);
        playlistAdapter.setOnItemClickListener(new PlaylistAdapter.OnItemClickListener() {
            @Override
            public void OnItemClicked(View view, int position) {
                openPlaylist(position);
            }
        });

        return view;
    }

    public interface OnPlaylistOpenListener {
        void onPlaylistOpened(Playlist playlist);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPlaylistOpenListener) {
            listener = (OnPlaylistOpenListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPlaylistOpenListener");
        }
    }

    public void openPlaylist(int position) {
        Playlist playlist = playlists.get(position);
        listener.onPlaylistOpened(playlist);
    }

    @Override
    public void playlistCreated(String name, ArrayList<Audio> selectedTracks, boolean cancelled) {
        if (!cancelled) {
            Playlist newPlaylist = new Playlist(name, selectedTracks);
            Context ctx2 = getFragmentManager().findFragmentByTag("playlists").getActivity();
            DBHelper dbHelper = new DBHelper(ctx2);
            AudioRepository audioRepository = new AudioRepository();
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            audioRepository.connect(db);
            audioRepository.addPlaylist(newPlaylist);
            playlists.add(newPlaylist);
            playlistAdapter.notifyDataSetChanged();
        }
    }
}
