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

import smart.sonitum.Adapters.AudioAdapter;
import smart.sonitum.Data.Audio;
import smart.sonitum.R;
import smart.sonitum.Utils.Utils;
import smart.sonitum.Utils.VerticalSpaceItemDecoration;

public class AudioFragment extends Fragment {
    private static final String ARG_TRACKS = "tracks";

    private ArrayList<Audio> tracks;

    private OnTrackStartListener listener;

    public AudioFragment() {}

    public static AudioFragment newInstance(ArrayList<Audio> tracks) {
        AudioFragment fragment = new AudioFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_TRACKS, tracks);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tracks = getArguments().getParcelableArrayList(ARG_TRACKS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio, container, false);

        RecyclerView rvMain = (RecyclerView) view.findViewById(R.id.rvMain);
        rvMain.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvMain.addItemDecoration(new VerticalSpaceItemDecoration(5));
        rvMain.setLayoutAnimation(Utils.listAlphaTranslateAnimation(300, 100, false, 0.3f));

        AudioAdapter audioAdapter = new AudioAdapter(tracks);
        rvMain.setAdapter(audioAdapter);
        audioAdapter.setOnItemClickListener(new AudioAdapter.OnItemClickListener() {
            @Override
            public void OnItemClicked(View view, int position) {
                startTrack(position);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTrackStartListener) {
            listener = (OnTrackStartListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTrackStartListener");
        }
    }

    public interface OnTrackStartListener {
        void onTrackStarted(ArrayList<Audio> queue, int position);
    }

    public void startTrack(int position) {
        listener.onTrackStarted(tracks, position);
    }
}
