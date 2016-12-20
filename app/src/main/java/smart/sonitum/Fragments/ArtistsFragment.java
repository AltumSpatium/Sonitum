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
import smart.sonitum.Adapters.ArtistAdapter;
import smart.sonitum.Adapters.AudioAdapter;
import smart.sonitum.Data.Audio;
import smart.sonitum.R;

public class ArtistsFragment extends Fragment {
    private static final String ARG_ARTISTS = "artists";
    private static final String ARG_ARTISTS_TRACKS = "artistsTracks";

    private ArrayList<String> artists;
    private HashMap<String, ArrayList<Audio>> artistsTracks;
    private ArrayList<Audio> tracks;

    public boolean tracksShowed = false;

    ListView lvMain;

    public ArtistsFragment() {}

    public static ArtistsFragment newInstance(ArrayList<String> artists, HashMap<String, ArrayList<Audio>> artistsTracks) {
        ArtistsFragment fragment = new ArtistsFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_ARTISTS, artists);
        args.putSerializable(ARG_ARTISTS_TRACKS, artistsTracks);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artists = getArguments().getStringArrayList(ARG_ARTISTS);
            artistsTracks = (HashMap<String, ArrayList<Audio>>) getArguments().getSerializable(ARG_ARTISTS_TRACKS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artists, container, false);

        lvMain = (ListView) view.findViewById(R.id.lvMain);

        ArrayAdapter<String> artistArrayAdapter = new ArtistAdapter(getActivity(), artists, artistsTracks);

        if (lvMain != null)
            lvMain.setAdapter(artistArrayAdapter);

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!tracksShowed) {
                    tracks = artistsTracks.get(artists.get(position));
                    //ArrayAdapter<Audio> audioArrayAdapter = new AudioAdapter(getActivity(), tracks);
                    //lvMain.setAdapter(audioArrayAdapter);
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

    public void exitArtist() {
        tracksShowed = false;
        ArrayAdapter<String> artistArrayAdapter = new ArtistAdapter(getActivity(), artists, artistsTracks);

        if (lvMain != null)
            lvMain.setAdapter(artistArrayAdapter);
    }
}
