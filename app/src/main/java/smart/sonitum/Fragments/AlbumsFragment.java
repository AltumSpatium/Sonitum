package smart.sonitum.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import smart.sonitum.Adapters.AlbumAdapter;
import smart.sonitum.Data.Audio;
import smart.sonitum.R;
import smart.sonitum.Utils.GridSpacingItemDecoration;

public class AlbumsFragment extends Fragment {
    private static final String ARG_ALBUM_TITLES = "albumTitles";
    private static final String ARG_ALBUMS = "albums";

    private ArrayList<String> albumTitles;
    private HashMap<String, ArrayList<Audio>> albums;

    private OnFragmentInteractionListener listener;

    RecyclerView rvMain;

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

        rvMain = (RecyclerView) view.findViewById(R.id.rvMain);
        rvMain.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        rvMain.addItemDecoration(new GridSpacingItemDecoration(3, 40, true));

        AlbumAdapter albumAdapter = new AlbumAdapter(albumTitles, albums);
        rvMain.setAdapter(albumAdapter);
        albumAdapter.setOnItemClickListener(new AlbumAdapter.OnItemClickListener() {
            @Override
            public void OnItemClicked(View view, int position) {
                openAlbum(position);
            }
        });

        return view;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String message, boolean isAlbum);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void openAlbum(int position) {
        String album = albumTitles.get(position);
        listener.onFragmentInteraction(album, true);
    }
}
