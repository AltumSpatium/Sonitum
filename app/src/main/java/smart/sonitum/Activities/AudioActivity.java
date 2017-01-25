package smart.sonitum.Activities;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import smart.sonitum.Data.Audio;
import smart.sonitum.Data.Playlist;
import smart.sonitum.R;
import smart.sonitum.Services.AudioService;
import smart.sonitum.Utils.Utils;

public class AudioActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private final String ACTION_UPDATE_TRACK = "smart.sonitum.update_track";
    private final String ACTION_UPDATE_PLAY_PAUSE = "smart.sonitum.update_play_pause";

    public AudioService audioService;

    private BroadcastReceiver broadcastReceiver;

    private ServiceConnection serviceConnection;
    private Intent serviceIntent;

    private ImageView ivAlbumPic;
    private TextView tvAudioArtist;
    private TextView tvAudioTitle;
    private TextView tvAudioAlbum;
    private TextView tvTrackCurrentTime;
    private TextView tvAudioDuration;
    private SeekBar sbTrack;
    private ImageButton ibPlayPause;

    private boolean isUpdatingThreadRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        Intent intent = getIntent();
        Playlist playlist = intent.getParcelableExtra("playlist");
        int currentPosition = intent.getIntExtra("position", 0);

        ivAlbumPic = (ImageView) findViewById(R.id.ivAlbumPic);
        tvAudioArtist = (TextView) findViewById(R.id.tvAudioArtist);
        tvAudioTitle = (TextView) findViewById(R.id.tvAudioTitle);
        tvAudioAlbum = (TextView) findViewById(R.id.tvAudioAlbum);
        tvTrackCurrentTime = (TextView) findViewById(R.id.tvTrackCurrentTime);
        tvAudioDuration = (TextView) findViewById(R.id.tvAudioDuration);

        sbTrack = (SeekBar) findViewById(R.id.sbTrack);
        sbTrack.setOnSeekBarChangeListener(this);

        ImageButton ibPrevTrack = (ImageButton) findViewById(R.id.ibPrevTrack);
        ibPrevTrack.setOnClickListener(this);

        ImageButton ibNextTrack = (ImageButton) findViewById(R.id.ibNextTrack);
        ibNextTrack.setOnClickListener(this);

        ibPlayPause = (ImageButton) findViewById(R.id.ibPlayPause);
        ibPlayPause.setOnClickListener(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_UPDATE_TRACK);
        intentFilter.addAction(ACTION_UPDATE_PLAY_PAUSE);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case ACTION_UPDATE_TRACK:
                        updateCurrentTrack();
                        break;
                    case ACTION_UPDATE_PLAY_PAUSE:
                        updatePlayPauseButton();
                        break;
                }
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);

        serviceIntent = new Intent(this, AudioService.class);
        serviceIntent.putExtra("playlist", playlist);
        serviceIntent.putExtra("position", currentPosition);

        //getSupportActionBar().hide();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibPrevTrack:
                audioService.previousTrack();
                break;
            case R.id.ibPlayPause:
                audioService.pauseOrResume();
                break;
            case R.id.ibNextTrack:
                audioService.nextTrack();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(serviceIntent);
        createServiceConnection();
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startUpdatingThread();
    }

    @Override
    protected void onStop() {
        super.onStop();
        audioService = null;
        unbindService(serviceConnection);
        stopUpdatingThread();
        //stopService(serviceIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void createServiceConnection() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                audioService = ((AudioService.AudioBinder)service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                audioService = null;
            }
        };
    }

    private void updateCurrentTrack() {
        Audio track = null;

        if (audioService != null)
            track = audioService.getCurrentTrack();

        if (track != null) {
            sbTrack.setMax(audioService.getDuration());
            sbTrack.setProgress(audioService.getProgress());
            sbTrack.setClickable(true);

            Bitmap bmp = BitmapFactory.decodeFile(track.getAlbumArt());
            ivAlbumPic.setImageBitmap(bmp != null ? bmp :
                    BitmapFactory.decodeResource(getResources(), R.mipmap.ic_album));

            tvAudioArtist.setText(track.getArtist());
            tvAudioTitle.setText(track.getTitle());
            tvAudioAlbum.setText(track.getAlbum());
            tvAudioDuration.setText(Utils.getMinutesFromMillis(track.getTotalTime()));
            tvTrackCurrentTime.setText(Utils.getMinutesFromMillis(0));
        }
    }

    private void updatePlayPauseButton() {
        if (audioService != null)
            ibPlayPause.setImageResource(audioService.isPlaying() ? R.mipmap.ic_pause : R.mipmap.ic_resume);
    }

    private void updateSeekbar() {
        if (audioService != null) {
            int progress = audioService.getProgress();
            sbTrack.setProgress(progress);

            tvTrackCurrentTime.setText(Utils.getMinutesFromMillis(progress));
        }
    }

    private void startUpdatingThread() {
        isUpdatingThreadRunning = true;
        new Thread() {
            @Override
            public void run() {
                while (isUpdatingThreadRunning) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateSeekbar();
                        }
                    });
                    try {
                        Thread.sleep(450);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            audioService.seekTo(progress);
            updateSeekbar();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    private void stopUpdatingThread() {
        isUpdatingThreadRunning = false;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }
}
