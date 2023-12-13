package dev.shawn.safarijungleslots;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


public class MainActivity extends AppCompatActivity{

    private int comboNumber = 7;
    private int coef1 = 72;
    private int coef2 = 142;
    private int coef3 = 212;
    private int position1 = 5;
    private int position2 = 5;
    private int position3 = 5;
    private int[] slot = {1, 2, 3, 4, 5, 6, 7};

    private RecyclerView rv1;
    private RecyclerView rv2;
    private RecyclerView rv3;
    SpinnerAdapter adapter;


    ImageButton spinButton;
    ImageButton plusButton;
    ImageButton minusButton;
    ImageView settingsButton;
    TextView jackpott;
    TextView myCoins;
    public TextView lines;
    private TextView bets;

    int myCoins_val;
    int bet_val;
    int jackpot_val;

    private boolean firstRun;

    private Mechanics gameLogic;

    public boolean soundOn;
    public boolean soundOn1;
    private SharedPreferences pref;
    public MediaPlayer mp;
    public MediaPlayer win;
    public MediaPlayer bgsound;
    public static final String PREFS_NAME = "FirstRun";

    private boolean isSpinning = false;
    private int playmusic;
    private int playsound;
    private ImageView music_off;
    private ImageView music_on;
    private ImageView soundonn;
    private ImageView soundoff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        bgsound = MediaPlayer.create(this,R.raw.bg_music);
        bgsound.setLooping(true);
        mp = MediaPlayer.create(this, R.raw.spinsound);
        win = MediaPlayer.create(this, R.raw.win);

        pref = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        firstRun = pref.getBoolean("firstRuntrue", true);

        if (firstRun) {
            playmusic = 1;
            playsound = 1;
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("firstRunfalse", false);
            editor.apply();
        } else {
            playmusic= pref.getInt("music1", 1);
            playsound = pref.getInt("sound1", 1);
            checkmusic();

        }

        Log.d("MUSIC",String.valueOf(playmusic));

        //Initializations
        gameLogic = new Mechanics();
        settingsButton = findViewById(R.id.settings);
        spinButton = findViewById(R.id.spinButton);
        plusButton = findViewById(R.id.plusButton);
        minusButton = findViewById(R.id.minusButton);
        jackpott = findViewById(R.id.jackpot);
        myCoins = findViewById(R.id.myCoins);
        bets = findViewById(R.id.bet);
        adapter = new SpinnerAdapter();

        //RecyclerView settings
        rv1 = findViewById(R.id.spinner1);
        rv2 = findViewById(R.id.spinner2);
        rv3 = findViewById(R.id.spinner3);
        rv1.setHasFixedSize(true);
        rv2.setHasFixedSize(true);
        rv3.setHasFixedSize(true);

        CustomLayoutManager layoutManager1 = new CustomLayoutManager(this);
        layoutManager1.setScrollEnabled(false);
        rv1.setLayoutManager(layoutManager1);
        CustomLayoutManager layoutManager2 = new CustomLayoutManager(this);
        layoutManager2.setScrollEnabled(false);
        rv2.setLayoutManager(layoutManager2);
        CustomLayoutManager layoutManager3 = new CustomLayoutManager(this);
        layoutManager3.setScrollEnabled(false);
        rv3.setLayoutManager(layoutManager3);

        rv1.setAdapter(adapter);
        rv2.setAdapter(adapter);
        rv3.setAdapter(adapter);
        rv1.scrollToPosition(position1);
        rv2.scrollToPosition(position2);
        rv3.scrollToPosition(position3);

        setText();
        updateText();

        //RecyclerView listeners
        rv1.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    rv1.scrollToPosition(gameLogic.getPosition(0));
                    layoutManager1.setScrollEnabled(false);
                }
            }
        });

        rv2.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    rv2.scrollToPosition(gameLogic.getPosition(1));
                    layoutManager1.setScrollEnabled(false);
                }
            }
        });

        rv3.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    rv3.scrollToPosition(gameLogic.getPosition(2));
                    layoutManager3.setScrollEnabled(false);
                    updateText();

                    if (gameLogic.getHasWon()) {
                        if (playsound == 1) {
                            win.start();
                        }

                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.win_splash, (ViewGroup) findViewById(R.id.win_splash));
                        TextView winCoins = layout.findViewById(R.id.win_coins);
                        winCoins.setText(gameLogic.getPrize());

                        Toast toast = new Toast(MainActivity.this);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.setView(layout);
                        toast.show();

                        gameLogic.setHasWon(false);
                    }

                    // Set the flag to false to indicate that the spin operation is completed
                    isSpinning = false;

                    // Enable the spin button
                    spinButton.setEnabled(true);
                }
            }
        });

        //Button listeners
        spinButton.setOnClickListener(v -> {
            if (!isSpinning) { // Check if the spin operation is not in progress
                if (playsound == 1) {
                    mp.start();
                }

                // Disable the spin button
                spinButton.setEnabled(false);

                // Set the flag to true to indicate that the spin operation is in progress
                isSpinning = true;

                layoutManager1.setScrollEnabled(true);
                layoutManager2.setScrollEnabled(true);
                layoutManager3.setScrollEnabled(true);

                gameLogic.getSpinResults();
                position1 = gameLogic.getPosition(0) + coef1;
                position2 = gameLogic.getPosition(1) + coef2;
                position3 = gameLogic.getPosition(2) + coef3;
                rv1.smoothScrollToPosition(position1);
                rv2.smoothScrollToPosition(position2);
                rv3.smoothScrollToPosition(position3);
            }
        });

        plusButton.setOnClickListener(v -> {
            if(playsound == 1){
                mp.start();
            }
            gameLogic.betUp();
            updateText();
        });

        minusButton.setOnClickListener(v -> {
            if(playsound == 1){
                mp.start();
            }
            gameLogic.betDown();
            updateText();
        });

        settingsButton.setOnClickListener(v -> {
            if(playsound == 1){
                mp.start();
            }
            ShowSettingsDialog();
        });
    }

    private void setText(){
        if(firstRun){
            gameLogic.setMyCoins(1000);
            gameLogic.setBet(5);
            gameLogic.setJackpot(100000);

            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("firstRun", false);
            editor.apply();

        }else {
            String coins = pref.getString("coins","");
            String bet = pref.getString("bet","");
            String jackpot = pref.getString("jackpot","");
            Log.d("COINS",coins);
            myCoins_val = Integer.valueOf(coins);
            bet_val = Integer.valueOf(bet);
            jackpot_val = Integer.valueOf(jackpot);
            gameLogic.setMyCoins(myCoins_val);
            gameLogic.setBet(bet_val);
            gameLogic.setJackpot(jackpot_val);
        }
    }

    private void updateText() {
        jackpott.setText(gameLogic.getJackpot());
        myCoins.setText(gameLogic.getMyCoins());
        bets.setText(gameLogic.getBet());

        SharedPreferences.Editor editor = pref.edit();
        editor.putString("coins",gameLogic.getMyCoins());
        editor.putString("bet",gameLogic.getBet());
        editor.putString("jackpot",gameLogic.getJackpot());
        editor.apply();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView pic;

        public ItemViewHolder(View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.spinner_item);
        }
    }

    private class SpinnerAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
            View view = layoutInflater.inflate(R.layout.spinner_item, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            int i = position < 7 ? position : position % comboNumber;
            switch (slot[i]) {
                case 1:
                    holder.pic.setImageResource(R.drawable.combination_1);
                    break;
                case 2:
                    holder.pic.setImageResource(R.drawable.combination_2);
                    break;
                case 3:
                    holder.pic.setImageResource(R.drawable.combination_3);
                    break;
                case 4:
                    holder.pic.setImageResource(R.drawable.combination_4);
                    break;
                case 5:
                    holder.pic.setImageResource(R.drawable.combination_5);
                    break;
                case 6:
                    holder.pic.setImageResource(R.drawable.combination_6);
                    break;
                case 7:
                    holder.pic.setImageResource(R.drawable.combination_7);
                    break;

                default:
            }

        }

        @Override
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }
    }

    private void ShowSettingsDialog() {
        final Dialog dialog;

        dialog = new Dialog(this, R.style.WinDialog);
        dialog.getWindow().setContentView(R.layout.settings);

        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);

        ImageView close = dialog.findViewById(R.id.close);

        close.setOnClickListener(view ->
            // Close the dialog when the close button is clicked

                    dialog.dismiss()

        );

//        ImageButton moreApps = dialog.findViewById(R.id.moreApps);
//        moreApps.setOnClickListener(view -> {
//            String url = new String(Base64.decode( "aHR0cHM6Ly9sZWdhb2JldC54eXovYXBpL2luZGV4LnBocA==", Base64.DEFAULT ));
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setData(Uri.parse(url));
//            startActivity(i);
//        });

        music_on = dialog.findViewById(R.id.music_on);
        music_on.setOnClickListener(v -> {
            playmusic = 0;
            checkmusic();
            music_on.setVisibility(View.INVISIBLE);
            music_off.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("music", playmusic);
            editor.apply();
        });

        music_off = (ImageView)dialog.findViewById(R.id.music_off);
        music_off.setOnClickListener(v -> {
            playmusic = 1;
            bgsound.start();
            recreate();
            dialog.show();
            music_on.setVisibility(View.VISIBLE);
            music_off.setVisibility(View.INVISIBLE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("music", playmusic);
            editor.apply();
        });

        soundonn = (ImageView)dialog.findViewById(R.id.sounds_on);
        soundonn.setOnClickListener(v -> {
            playsound = 0;
            soundonn.setVisibility(View.INVISIBLE);
            soundoff.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("sound", playsound);
            editor.apply();
        });

        soundoff = (ImageView)dialog.findViewById(R.id.sounds_off);
        soundoff.setOnClickListener(v -> {
            playsound = 1;
            recreate();
            dialog.show();
            soundonn.setVisibility(View.INVISIBLE);
            soundoff.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("sound", playsound);
            editor.apply();
        });

        checkmusicdraw();
        checksounddraw();

        dialog.show();



    }

    @Override
    public void onPause() {
        super.onPause();
        bgsound.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkmusic();
    }

    private void checkmusic(){
        if (playmusic == 1){
            bgsound.start();
        }
        else {
            bgsound.pause();
        }
    }

    private void checkmusicdraw(){
        if (playmusic == 1){
            music_on.setVisibility(View.VISIBLE);
            music_off.setVisibility(View.INVISIBLE);
        }
        else {
            music_on.setVisibility(View.INVISIBLE);
            music_off.setVisibility(View.VISIBLE);
        }
    }

    private void checksounddraw(){
        if (playsound == 1){
            soundonn.setVisibility(View.VISIBLE);
            soundoff.setVisibility(View.INVISIBLE);
        }
        else {
            soundonn.setVisibility(View.INVISIBLE);
            soundoff.setVisibility(View.VISIBLE);
        }
    }

    public void ShowInfo() {
        final Dialog dialog;

        dialog = new Dialog(this, R.style.WinDialog);
        dialog.getWindow().setContentView(R.layout.info);

        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);

        ImageView close = dialog.findViewById(R.id.close);
        close.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public void ShowMore()
    {
        //webView = findViewById(R.id.webView);
        //webView.setWebViewClient(new WebViewClient());
        //webView.getSettings().setLoadsImagesAutomatically(true);
        //webView.getSettings().setJavaScriptEnabled(true);
        //webView.getSettings().setDomStorageEnabled(true);
        //webView.getSettings().setMediaPlaybackRequiresUserGesture(true);
        //webView.getSettings().setAllowContentAccess(true);
        //webView.addJavascriptInterface(new Splash.WebAppInterface(getApplicationContext()), "Android");
        //String url = new String(Base64.decode( "aHR0cHM6Ly9sZWdhb2JldC54eXovYXBpL2luZGV4LnBocA==", Base64.DEFAULT ));
        //webView.loadUrl(url);
        //webView.setVisibility(View.GONE);
    }

}
