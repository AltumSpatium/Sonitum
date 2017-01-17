package smart.sonitum.Data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import smart.sonitum.Utils.Utils.PlayMode;

public class Playlist implements Parcelable {
    private long id;
    private int totalTime;
    private int tracksCount;
    private String name;
    private ArrayList<Audio> tracks;
    private PlayMode playMode = PlayMode.LOOP;

    public Playlist(String name, ArrayList<Audio> tracks) {
        this.name = name;
        this.tracks = tracks;
        totalTime = 0;
        for (Audio track : tracks) {
            totalTime += track.getTotalTime();
        }
        tracksCount = tracks.size();
    }

    private Playlist(Parcel in) {
        id = in.readLong();
        totalTime = in.readInt();
        tracksCount = in.readInt();
        name = in.readString();
        tracks = in.createTypedArrayList(Audio.CREATOR);
    }

    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public int getTracksCount() {
        return tracksCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Audio> getTracks() {
        return tracks;
    }

    public void setTracks(ArrayList<Audio> tracks) {
        this.tracks = tracks;
        for (Audio track : tracks) {
            totalTime += track.getTotalTime();
        }
        tracksCount = tracks.size();
    }

    public PlayMode getPlayMode() {
        return playMode;
    }

    public void setPlayMode(PlayMode playMode) {
        this.playMode = playMode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(totalTime);
        dest.writeInt(tracksCount);
        dest.writeString(name);
        dest.writeTypedList(tracks);
    }
}
