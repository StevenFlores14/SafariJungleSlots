package dev.shawn.safarijungleslots;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    VideoView videoView;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view layout
        setContentView(R.layout.activity_splash);

        // Initialize VideoView
        videoView = findViewById(R.id.videoView);

        // Set video path (replace "your_video_path" with the actual path or URL)
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.safarisplash);
        videoView.setVideoURI(videoUri);

        // Display splash screen for a second
        handler.postDelayed(() -> {
            // Start video playback
            videoView.start();

            // Add an OnCompletionListener to start the MainActivity after video completion
            videoView.setOnCompletionListener(mediaPlayer -> {
                Intent intent = new Intent(Splash.this, MainActivity.class);
                // Add the transition animation here
                startActivity(intent);
                finish();
            });
        }, 3000);
    }
}
