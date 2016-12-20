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

public class AlbumAdapter extends ArrayAdapter<String> {
    private HashMap<String, ArrayList<Audio>> albums;

    public AlbumAdapter(Context ctx, ArrayList<String> albumTitles, HashMap<String, ArrayList<Audio>> albums) {
        super(ctx, R.layout.album_item, albumTitles);
        this.albums = albums;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {
        String album = getItem(position);
        ArrayList<Audio> tracks = albums.get(album);
        String artist = tracks.get(0).getArtist();
        String tracksCount = "Tracks: " + tracks.size();

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.album_item, null);
        }

        ((TextView) convertView.findViewById(R.id.tvAlbumTitle))
                .setText(album);
        ((TextView) convertView.findViewById(R.id.tvAlbumArtist))
                .setText(artist);
        ((TextView) convertView.findViewById(R.id.tvAlbumTrackCount))
                .setText(tracksCount);

        return convertView;
    }
}