package smart.sonitum.Data;

import android.os.Parcel;
import android.os.Parcelable;

public class Audio implements Parcelable {
    private long id;
    private int totalTime;
    private String album;
    private String artist;
    private String title;
    private String genre;
    private String trackNumber;
    private String albumArt;
    private String year;
    private String data;

    public Audio(long id, int totalTime, String album, String artist, String title, String genre,
                 String trackNumber, String albumArt, String year, String data) {
        this.id = id;
        this.totalTime = totalTime;
        this.album = album;
        this.artist = artist;
        this.title = title;
        this.genre = genre;
        this.trackNumber = trackNumber;
        this.albumArt = albumArt;
        this.year = year;
        this.data = data;
    }

    private Audio(Parcel in) {
        id = in.readLong();
        totalTime = in.readInt();
        album = in.readString();
        artist = in.readString();
        title = in.readString();
        genre = in.readString();
        trackNumber = in.readString();
        albumArt = in.readString();
        year = in.readString();
        data = in.readString();
    }

    public static final Creator<Audio> CREATOR = new Creator<Audio>() {
        @Override
        public Audio createFromParcel(Parcel in) {
            return new Audio(in);
        }

        @Override
        public Audio[] newArray(int size) {
            return new Audio[size];
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

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(String trackNumber) {
        this.trackNumber = trackNumber;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(totalTime);
        dest.writeString(album);
        dest.writeString(artist);
        dest.writeString(title);
        dest.writeString(genre);
        dest.writeString(trackNumber);
        dest.writeString(albumArt);
        dest.writeString(year);
        dest.writeString(data);
    }
}
