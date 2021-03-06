package smart.sonitum.Adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import smart.sonitum.Data.Audio;
import smart.sonitum.R;
import smart.sonitum.Utils.Utils;

public class NewPlaylistDialogAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {
    private class TrackSelectModel {
        private boolean selected = false;

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

    private ArrayList<Audio> tracks;
    private ArrayList<TrackSelectModel> trackSelectModels = new ArrayList<>();

    public NewPlaylistDialogAdapter(ArrayList<Audio> tracks, ArrayList<Integer> selectedTracks) {
        this.tracks = tracks;
        for (int i = 0; i < tracks.size(); i++) {
            TrackSelectModel trackSelectModel = new TrackSelectModel();
            if (selectedTracks != null) {
                if (selectedTracks.contains(i))
                    trackSelectModel.setSelected(true);
            }
            trackSelectModels.add(trackSelectModel);
        }
    }

    @Override
    public int getItemCount() {
        return trackSelectModels.size();
    }

    @Override
    public AudioAdapter.AudioViewHolder onCreateViewHolder(ViewGroup viewGroup, int typeView) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.audio_item, viewGroup, false);
        return new AudioAdapter.AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AudioAdapter.AudioViewHolder avh, final int position) {
        final TrackSelectModel model = trackSelectModels.get(position);
        Audio track = tracks.get(position);
        String time = Utils.getMinutesFromMillis(track.getTotalTime());

        avh.tvAudioTitle.setText(track.getTitle());
        avh.tvAudioArtist.setText(track.getArtist());
        avh.tvAudioDuration.setText(time);
        avh.self.setBackgroundColor(model.isSelected() ?
                Color.parseColor("#0b0a0a") : Color.parseColor("#2b2b2b"));
        avh.self.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setSelected(!model.isSelected());
                avh.self.setBackgroundColor(model.isSelected() ?
                        Color.parseColor("#0b0a0a") : Color.parseColor("#2b2b2b"));
            }
        });
    }

    public ArrayList<Integer> getSelected() {
        ArrayList<Integer> selectedTracks = new ArrayList<>();
        for (int i = 0; i < trackSelectModels.size(); i++) {
            if (trackSelectModels.get(i).isSelected())
                selectedTracks.add(i);
        }
        return selectedTracks;
    }
}
