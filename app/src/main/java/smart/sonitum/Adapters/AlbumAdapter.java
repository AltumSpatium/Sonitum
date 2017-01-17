package smart.sonitum.Adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import smart.sonitum.Data.Audio;
import smart.sonitum.R;
import smart.sonitum.Utils.ImageLoader;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
    static class AlbumViewHolder extends RecyclerView.ViewHolder {
        View self;
        ImageView ivAlbumPic;
        TextView tvAlbumTitle;
        TextView tvAlbumArtist;
        TextView tvAlbumTrackCount;

        AlbumViewHolder(View view) {
            super(view);

            self = view;
            ivAlbumPic = (ImageView) view.findViewById(R.id.ivAlbumPic);
            tvAlbumTitle = (TextView) view.findViewById(R.id.tvAlbumTitle);
            tvAlbumArtist = (TextView) view.findViewById(R.id.tvAlbumArtist);
            tvAlbumTrackCount = (TextView) view.findViewById(R.id.tvAlbumTrackCount);
        }
    }

    private OnItemClickListener onItemClickListener;
    private ArrayList<String> albumTitles;
    private HashMap<String, ArrayList<Audio>> albums;
    private HashMap<String, Bitmap> albumArts;

    public AlbumAdapter(ArrayList<String> albumTitles, HashMap<String, ArrayList<Audio>> albums, HashMap<String, Bitmap> albumArts) {
        this.albumTitles = albumTitles;
        for (ArrayList<Audio> tracks : albums.values()) {
            Collections.sort(tracks, new Comparator<Audio>() {
                @Override
                public int compare(Audio o1, Audio o2) {
                    if (o1.getTrackNumber().equals(""))
                        return o1.getTitle().toLowerCase().compareTo(o2.getTitle().toLowerCase());
                    return o1.getTrackNumber().toLowerCase().compareTo(o2.getTrackNumber().toLowerCase());
                }
            });
        }
        this.albums = albums;
        this.albumArts = albumArts;
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
        String albumArt = tracks.get(0).getAlbumArt();
        String tracksCount = "Tracks: " + tracks.size();

        if (album.length() > 15) album = album.substring(0, 13) + "...";
        if (artist.length() > 17) artist = artist.substring(0 , 15) + "...";

        if (albumArts.get(album) == null) {
            ImageLoader imageLoader = new ImageLoader(avh.ivAlbumPic, avh.self.getContext());
            imageLoader.execute(albumArt);
            try {
                Bitmap bmp = imageLoader.get(2, TimeUnit.SECONDS);
                albumArts.put(album, bmp);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            avh.ivAlbumPic.setImageBitmap(albumArts.get(album));
        }

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
