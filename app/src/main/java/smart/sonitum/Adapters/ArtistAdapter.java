package smart.sonitum.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import smart.sonitum.R;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {
    static class ArtistViewHolder extends RecyclerView.ViewHolder {
        View self;
        TextView tvArtist;
        TextView tvArtistAlbumsCount;

        ArtistViewHolder(View view) {
            super(view);

            self = view;
            tvArtist = (TextView) view.findViewById(R.id.tvArtist);
            tvArtistAlbumsCount = (TextView) view.findViewById(R.id.tvArtistAlbumsCount);
        }
    }

    private OnItemClickListener onItemClickListener;
    private ArrayList<String> artists;
    private HashMap<String, ArrayList<String>> artistsAlbums;

    public ArtistAdapter(ArrayList<String> artists, HashMap<String, ArrayList<String>> artistsAlbums) {
        this.artists = artists;
        this.artistsAlbums = artistsAlbums;
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    @Override
    public ArtistViewHolder onCreateViewHolder(ViewGroup viewGroup, int typeView) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.artist_item, viewGroup, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArtistViewHolder avh, final int position) {
        String artist = artists.get(position);
        ArrayList<String> albums = artistsAlbums.get(artist);
        String albumsCount = "Albums: " + albums.size();

        avh.tvArtist.setText(artist);
        avh.tvArtistAlbumsCount.setText(albumsCount);
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
