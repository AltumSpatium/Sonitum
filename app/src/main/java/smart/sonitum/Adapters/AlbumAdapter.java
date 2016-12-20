package smart.sonitum.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import smart.sonitum.Data.Audio;
import smart.sonitum.R;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
    static class AlbumViewHolder extends RecyclerView.ViewHolder {
        View self;
        TextView tvAlbumTitle;
        TextView tvAlbumArtist;
        TextView tvAlbumTrackCount;

        AlbumViewHolder(View view) {
            super(view);

            self = view;
            tvAlbumTitle = (TextView) view.findViewById(R.id.tvAlbumTitle);
            tvAlbumArtist = (TextView) view.findViewById(R.id.tvAlbumArtist);
            tvAlbumTrackCount = (TextView) view.findViewById(R.id.tvAlbumTrackCount);
        }
    }

    private OnItemClickListener onItemClickListener;
    private ArrayList<String> albumTitles;
    private HashMap<String, ArrayList<Audio>> albums;

    public AlbumAdapter(ArrayList<String> albumTitles, HashMap<String, ArrayList<Audio>> albums) {
        this.albumTitles = albumTitles;
        this.albums = albums;
    }

    @Override
    public int getItemCount() {
        return albumTitles.size();
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup viewGroup, int typeView) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.album_item, viewGroup, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder avh, final int position) {
        String album = albumTitles.get(position);
        ArrayList<Audio> tracks = albums.get(album);
        String artist = tracks.get(0).getArtist();
        String tracksCount = "Tracks: " + tracks.size();

        if (album.length() > 17) album = album.substring(0, 15) + "...";
        if (artist.length() > 22) artist = artist.substring(0 , 20) + "...";

        avh.tvAlbumTitle.setText(album);
        avh.tvAlbumArtist.setText(artist);
        avh.tvAlbumTrackCount.setText(tracksCount);
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