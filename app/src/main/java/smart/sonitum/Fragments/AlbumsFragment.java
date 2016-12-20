package smart.sonitum.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import smart.sonitum.Activities.AudioActivity;
import smart.sonitum.Adapters.AlbumAdapter;
import smart.sonitum.Adapters.AudioAdapter;
import smart.sonitum.Data.Audio;
import smart.sonitum.R;

public class AlbumsFragment extends Fragment {
    private static final String ARG_ALBUM_TITLES = "albumTitles";
    private static final String ARG_ALBUMS = "albums";

    private ArrayList<String> albumTitles;
    private HashMap<String, ArrayList<Audio>> albums;
    private ArrayList<Audio> tracks;

    public boolean tracksShowed = false;

    ListView lvMain;

    public AlbumsFragment() {}

    public static AlbumsFragment newInstance(ArrayList<String> albumTitles, HashMap<String, ArrayList<Audio>> albums) {
        AlbumsFragment fragment = new AlbumsFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_ALBUM_TITLES, albumTitles);
        args.putSerializable(ARG_ALBUMS, albums);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            albumTitles = getArguments().getStringArrayList(ARG_ALBUM_TITLES);
            albums = (HashMap<String, ArrayList<Audio>>) getArguments().getSerializable(ARG_ALBUMS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums, container, false);

        lvMain = (ListView) view.findViewById(R.id.lvMain);

        ArrayAdapter<String> albumArrayAdapter = new AlbumAdapter(getActivity(), albumTitles, albums);

        if (lvMain != null)
            lvMain.setAdapter(albumArrayAdapter);

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!tracksShowed) {
                    tracks = albums.get(albumTitles.get(position));
                    ArrayAdapter<Audio> audioArrayAdapter = new AudioAdapter(getActivity(), tracks);
                    lvMain.setAdapter(audioArrayAdapter);
                    tracksShowed = true;
                } else {
                    Intent intent = new Intent(getActivity(), AudioActivity.class);
                    intent.putExtra("track", tracks.get(position));
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
                }
            }
        });

        return view;
    }

    public void exitAlbum() {
        tracksShowed = false;
        ArrayAdapter<String> albumArrayAdapter = new AlbumAdapter(getActivity(), albumTitles, albums);

        if (lvMain != null)
            lvMain.setAdapter(albumArrayAdapter);
    }
}
