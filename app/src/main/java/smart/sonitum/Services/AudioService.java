package smart.sonitum.Services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;

import smart.sonitum.Data.Audio;
import smart.sonitum.Data.Playlist;

/**
 TODO: add PlayMode
 */

public class AudioService extends Service {
    private final String ACTION_UPDATE_TRACK = "smart.sonitum.update_track";
    private final String ACTION_UPDATE_PLAY_PAUSE = "smart.sonitum.update_play_pause";

    private AudioBinder audioBinder = new AudioBinder();

    private Playlist playlist;
    private Audio currentTrack;
    private int currentPosition;

    private MediaPlayer mediaPlayer;

    public AudioService() {}

    @Override
    public void onCreate() {
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public void onDestroy() {
        mediaPlayer.release();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        playlist = intent.getParcelableExtra("playlist");
        currentPosition = intent.getIntExtra("position", 0);
        Audio track = playlist.getTracks().get(currentPosition);
        if (currentTrack == null || currentTrack.getId() != track.getId()) {
            currentTrack = track;
            playCurrentTrack();
        } else {
            sendBroadcast(new Intent().setAction(ACTION_UPDATE_TRACK));
        }

        return START_STICKY;
    }

    public void play() {
        if (currentTrack == null) return;
        if (!mediaPlayer.isPlaying())
            mediaPlayer.start();
    }

    public void pauseOrResume() {
        if (currentTrack == null) return;
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else mediaPlayer.start();

        sendBroadcast(new Intent().setAction(ACTION_UPDATE_PLAY_PAUSE));
    }

    public void nextTrack() {
        if (currentTrack == null) return;

        if (currentPosition == playlist.getTracksCount() - 1)
            currentPosition = 0; // Mb stop playing
        else currentPosition++;

        currentTrack = playlist.getTracks().get(currentPosition);
        playCurrentTrack();
    }

    public void previousTrack() {
        if (currentTrack == null) return;

        if (currentPosition != 0 && getProgress() > 200) {
            currentPosition--;
            currentTrack = playlist.getTracks().get(currentPosition);
        }

        playCurrentTrack();
    }

    public void playCurrentTrack() {
        mediaPlayer.reset();
        try {
            Uri uri = getTrackUri(currentTrack);
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    nextTrack();
                }
            });
            mediaPlayer.prepare();
        } catch (IOException e) {
            currentTrack = null;
            e.printStackTrace();
        }
        play();

        sendBroadcast(new Intent().setAction(ACTION_UPDATE_TRACK));
    }

    public Audio getCurrentTrack() {
        return currentTrack;
    }

    public int getProgress() {
        return currentTrack == null ? 0 : mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return currentTrack == null ? 0 : mediaPlayer.getDuration();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void seekTo(int progress) {
        mediaPlayer.seekTo(progress);
    }

    private Uri getTrackUri(Audio track) {
        return Uri.parse("file://" + track.getData());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return audioBinder;
    }

    public class AudioBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    }
}
