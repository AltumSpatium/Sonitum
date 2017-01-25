package smart.sonitum.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import smart.sonitum.Data.Playlist;
import smart.sonitum.R;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {
    static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        View self;
        TextView tvPlaylist;
        TextView tvPlaylistTracksCount;

        PlaylistViewHolder(View view) {
            super(view);

            self = view;
            tvPlaylist = (TextView) view.findViewById(R.id.tvPlaylist);
            tvPlaylistTracksCount = (TextView) view.findViewById(R.id.tvPlaylistTracksCount);
        }
    }

    private OnItemClickListener onItemClickListener;
    private ArrayList<Playlist> playlists;

    public PlaylistAdapter(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup viewGroup, int typeView) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.playlist_item, viewGroup, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder pvh, final int position) {
        Playlist playlist = playlists.get(position);

        pvh.tvPlaylist.setText(playlist.getName());
        String count = "" + playlist.getTracks().size();
        pvh.tvPlaylistTracksCount.setText(count);
        pvh.self.setOnClickListener(new View.OnClickListener() {
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
