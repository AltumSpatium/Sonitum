package smart.sonitum.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import smart.sonitum.Data.Audio;
import smart.sonitum.R;

public class AudioAdapter extends ArrayAdapter<Audio> {
    public AudioAdapter(Context ctx, ArrayList<Audio> tracks) {
        super(ctx, R.layout.audio_item, tracks);
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {
        Audio track = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.audio_item, null);
        }

        ((TextView) convertView.findViewById(R.id.tvAudioTitle))
                .setText(track.getTitle());
        ((TextView) convertView.findViewById(R.id.tvAudioArtist))
                .setText(track.getArtist());

        int millis = track.getTotalTime();
        String time = String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

        ((TextView) convertView.findViewById(R.id.tvAudioDuration))
                .setText(time);

        return convertView;
    }
}
