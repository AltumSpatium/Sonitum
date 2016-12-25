package smart.sonitum.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import smart.sonitum.R;

public class AudioActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        //getSupportActionBar().hide();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
