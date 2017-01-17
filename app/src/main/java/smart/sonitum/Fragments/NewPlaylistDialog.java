package smart.sonitum.Fragments;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import smart.sonitum.Adapters.NewPlaylistDialogAdapter;
import smart.sonitum.Data.Audio;
import smart.sonitum.R;
import smart.sonitum.Utils.Utils;
import smart.sonitum.Utils.VerticalSpaceItemDecoration;

public class NewPlaylistDialog extends DialogFragment {
    private static final String ARG_TRACKS = "tracks";

    private ArrayList<Audio> tracks;

    private PlaylistCreateListener listener;

    public static NewPlaylistDialog newInstance(ArrayList<Audio> tracks) {
        NewPlaylistDialog dialog = new NewPlaylistDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_TRACKS, tracks);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tracks = getArguments().getParcelableArrayList(ARG_TRACKS);
        }
        listener = (PlaylistCreateListener) getTargetFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.new_playlist_dialog, container);

        RecyclerView rvPlaylistTracks = (RecyclerView) v.findViewById(R.id.rvPlaylistTracks);
        rvPlaylistTracks.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvPlaylistTracks.addItemDecoration(new VerticalSpaceItemDecoration(5));
        rvPlaylistTracks.setLayoutAnimation(Utils.listAlphaTranslateAnimation(300, 100, false, 0.3f));

        final NewPlaylistDialogAdapter adapter = new NewPlaylistDialogAdapter(tracks);
        rvPlaylistTracks.setAdapter(adapter);

        final EditText etPlaylistName = (EditText) v.findViewById(R.id.etPlaylistName);

        Button btnCancelPlaylist = (Button) v.findViewById(R.id.btnCancelPlaylist);
        btnCancelPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.playlistCreated(null, null, true);
                dismiss();
            }
        });

        Button btnCreatePlaylist = (Button) v.findViewById(R.id.btnCreatePlaylist);
        btnCreatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etPlaylistName.getText().toString();
                if (name.length() == 0) {
                    Toast.makeText(getActivity(), "Please, enter playlist name", Toast.LENGTH_SHORT).show();
                    return;
                }

                ArrayList<Integer> selectedTracksPositions = adapter.getSelected();
                if (selectedTracksPositions.size() == 0) {
                    Toast.makeText(getActivity(), "Please, select playlist tracks", Toast.LENGTH_SHORT).show();
                    return;
                }

                ArrayList<Audio> selectedTracks = new ArrayList<>();
                for (Integer position : selectedTracksPositions) {
                    selectedTracks.add(tracks.get(position));
                }

                listener.playlistCreated(name, selectedTracks, false);
                dismiss();
            }
        });

        return v;
    }

    public interface PlaylistCreateListener {
        void playlistCreated(String name, ArrayList<Audio> tracks, boolean cancelled);
    }
}
