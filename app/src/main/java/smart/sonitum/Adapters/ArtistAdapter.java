package smart.sonitum.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import smart.sonitum.Data.Audio;
import smart.sonitum.R;

public class ArtistAdapter extends ArrayAdapter<String> {
    private HashMap<String, ArrayList<Audio>> artistsTracks;

    public ArtistAdapter(Context ctx, ArrayList<String> artists, HashMap<String, ArrayList<Audio>> artistsTracks) {
        super(ctx, R.layout.artist_item, artists);
        this.artistsTracks = artistsTracks;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {
        String artist = getItem(position);
        ArrayList<Audio> tracks = artistsTracks.get(artist);
        String tracksCount = "Tracks: " + tracks.size();

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.artist_item, null);
        }

        ((TextView) convertView.findViewById(R.id.tvArtist))
                .setText(artist);
        ((TextView) convertView.findViewById(R.id.tvArtistTrackCount))
                .setText(tracksCount);

        return convertView;
    }
}