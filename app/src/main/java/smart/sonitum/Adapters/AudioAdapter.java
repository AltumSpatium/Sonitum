package smart.sonitum.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import smart.sonitum.Data.Audio;
import smart.sonitum.R;
import smart.sonitum.Utils.Utils;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {
    static class AudioViewHolder extends RecyclerView.ViewHolder {
        View self;
        TextView tvAudioTitle;
        TextView tvAudioArtist;
        TextView tvAudioDuration;

        AudioViewHolder(View view) {
            super(view);

            self = view;
            tvAudioArtist = (TextView) view.findViewById(R.id.tvAudioArtist);
            tvAudioTitle = (TextView) view.findViewById(R.id.tvAudioTitle);
            tvAudioDuration = (TextView) view.findViewById(R.id.tvAudioDuration);
        }
    }

    private OnItemClickListener onItemClickListener;
    private ArrayList<Audio> tracks;

    public AudioAdapter(ArrayList<Audio> tracks) {
        this.tracks = tracks;
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    @Override
    public AudioViewHolder onCreateViewHolder(ViewGroup viewGroup, int typeView) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.audio_item, viewGroup, false);
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AudioViewHolder avh, final int position) {
        Audio track = tracks.get(position);
        String time = Utils.getMinutesFromMillis(track.getTotalTime());


        avh.tvAudioTitle.setText(track.getTitle());
        avh.tvAudioArtist.setText(track.getArtist());
        avh.tvAudioDuration.setText(time);
        avh.self.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.OnItemClicked(v, position);
            }
        });
    }

    public interface OnItemClickListener {
        void OnItemClicked(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
