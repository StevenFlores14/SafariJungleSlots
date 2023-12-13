package dev.shawn.safarijungleslots;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PrivacyPolicy extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        // Initialize SharedPreferences
        sharedPreferences = getPreferences(MODE_PRIVATE);

        // Check if the user has already accepted the policy
        boolean hasAcceptedPolicy = sharedPreferences.getBoolean("hasAcceptedPolicy", false);

        if (!hasAcceptedPolicy) {
            // If the user has not accepted the policy, show it
            showPrivacyPolicy();
        } else {
            // If the user has already accepted the policy, proceed to the splash screen
            goToSplashScreen();
        }
    }

    private void showPrivacyPolicy() {
        // Find the WebView in your layout
        WebView webView = findViewById(R.id.privacyPolicyWebView);

        // Enable JavaScript (if needed)
        webView.getSettings().setJavaScriptEnabled(true);

        // Set a WebViewClient to open links within the WebView
        webView.setWebViewClient(new WebViewClient());

        // Load the privacy policy webpage URL
        webView.loadUrl("https://sites.google.com/view/safari-jungle-slots/home");

        // Find the accept and decline buttons
        Button acceptButton = findViewById(R.id.acceptButton);
        Button declineButton = findViewById(R.id.declineButton);

        // Set OnClickListener for the accept button
        acceptButton.setOnClickListener(v -> {
            // Save the preference that the user has accepted the policy
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("hasAcceptedPolicy", true);
            editor.apply();

            // Proceed to the splash screen activity
            goToSplashScreen();
        });

        // Set OnClickListener for the decline button
        declineButton.setOnClickListener(v ->
            // Close the app
            finishAffinity()
        );
    }

    private void goToSplashScreen() {
        Intent intent = new Intent(PrivacyPolicy.this, Splash.class);
        startActivity(intent);
        finish(); // Optional: finish the PrivacyPolicy activity to remove it from the back stack
    }
}