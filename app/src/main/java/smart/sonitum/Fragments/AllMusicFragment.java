package smart.sonitum.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import smart.sonitum.Activities.AudioActivity;
import smart.sonitum.Adapters.AudioAdapter;
import smart.sonitum.Data.Audio;
import smart.sonitum.R;

public class AllMusicFragment extends Fragment {
    private static final String ARG_TRACKS = "tracks";

    private ArrayList<Audio> tracks;

    public AllMusicFragment() {}

    public static AllMusicFragment newInstance(ArrayList<Audio> tracks) {
        AllMusicFragment fragment = new AllMusicFragment();
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
        View view = inflater.inflate(R.layout.fragment_allmusic, container, false);

        //ListView lvMain = (ListView) view.findViewById(R.id.lvMain);
        RecyclerView rvMain = (RecyclerView) view.findViewById(R.id.rvMain);
        rvMain.setLayoutManager(new LinearLayoutManager(getActivity()));

        ArrayAdapter<Audio> audioArrayAdapter = new AudioAdapter(this.getContext(), tracks);

        //if (rvMain != null)
        //    rvMain.setAdapter(audioArrayAdapter);

        //rvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        //    @Override
        //    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //        Intent intent = new Intent(getActivity(), AudioActivity.class);
        //        intent.putExtra("track", tracks.get(position));
        //        startActivity(intent);
        //        getActivity().overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
        //    }
        //});

        return view;
    }
}
