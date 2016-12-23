package smart.sonitum.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import smart.sonitum.Adapters.ArtistAdapter;
import smart.sonitum.R;
import smart.sonitum.Utils.Utils;
import smart.sonitum.Utils.VerticalSpaceItemDecoration;

public class ArtistsFragment extends Fragment {
    private static final String ARG_ARTISTS = "artists";
    private static final String ARG_ARTISTS_ALBUMS = "artistsAlbums";

    private ArrayList<String> artists;
    private HashMap<String, ArrayList<String>> artistsAlbums;

    private OnArtistOpenListener listener;

    RecyclerView rvMain;

    public ArtistsFragment() {}

    public static ArtistsFragment newInstance(ArrayList<String> artists, HashMap<String, ArrayList<String>> artistsAlbums) {
        ArtistsFragment fragment = new ArtistsFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_ARTISTS, artists);
        args.putSerializable(ARG_ARTISTS_ALBUMS, artistsAlbums);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artists = getArguments().getStringArrayList(ARG_ARTISTS);
            artistsAlbums = (HashMap<String, ArrayList<String>>) getArguments().getSerializable(ARG_ARTISTS_ALBUMS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artists, container, false);

        rvMain = (RecyclerView) view.findViewById(R.id.rvMain);
        rvMain.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvMain.addItemDecoration(new VerticalSpaceItemDecoration(5));
        rvMain.setLayoutAnimation(Utils.listAlphaTranslateAnimation(300, 100, false, 0.3f));

        ArtistAdapter artistAdapter = new ArtistAdapter(artists, artistsAlbums);
        rvMain.setAdapter(artistAdapter);
        artistAdapter.setOnItemClickListener(new ArtistAdapter.OnItemClickListener() {
            @Override
            public void OnItemClicked(View view, int position) {
                openArtist(position);
            }
        });

        return view;
    }

    public interface OnArtistOpenListener {
        void onArtistOpened(String artist);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnArtistOpenListener) {
            listener = (OnArtistOpenListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAlbumOpenListener");
        }
    }

    public void openArtist(int position) {
        String artist = artists.get(position);
        listener.onArtistOpened(artist);
    }

}
