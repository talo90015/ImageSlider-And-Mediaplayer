package com.talo.imageslider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.VolumeShaper;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnAudio;
    ImageSlider imageSlider;
    ImageView imgPlayAndPause;
    TextView txtCurrTime;
    TextView txtTotalDuration;
    SeekBar playSeekBar;
    MediaPlayer mediaPlayer;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get view ID``
        imageSlider = findViewById(R.id.slider);
        btnAudio = findViewById(R.id.btn_audio);
        imgPlayAndPause = findViewById(R.id.img_play_and_pause);
        txtCurrTime = findViewById(R.id.txt_CurrTime);
        txtTotalDuration = findViewById(R.id.txt_TotalDuration);
        playSeekBar = findViewById(R.id.seekBar);
        mediaPlayer = new MediaPlayer();

        playSeekBar.setMax(100);


        //slideModels.add(new SlideModel("You can input url or drawable resource", "title"))
        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.img1, "example_image_1"));
        slideModels.add(new SlideModel(R.drawable.img2, "example_image_2"));
        slideModels.add(new SlideModel(R.drawable.img3, "example_image_3"));
        imageSlider.setImageList(slideModels, true);    //add for list

        btnAudio.setOnClickListener(btnAudioChoose);
        imgPlayAndPause.setOnClickListener(imgPlayAndPauseClick);
    }
    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            long currentDuration = mediaPlayer.getCurrentPosition();
            txtCurrTime.setText(millSecondToTimer(currentDuration));
        }
    };

    private void updateSeekBar(){
        if (mediaPlayer.isPlaying()){
            playSeekBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100));
            handler.postDelayed(updater,1000);
        }
    }

    private String millSecondToTimer(long millSecond){
        String timerStr = "";
        String secondStr;

        int hour = (int)(millSecond / (1000* 60 * 60));
        int minutes = (int)(millSecond % (1000 * 60 * 60)) / (1000 * 60);
        int second = (int)((millSecond % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if (hour > 0){
            timerStr = hour + ":";
        }
        if (second < 10){
            secondStr = "0" + second;
        }else{
            secondStr = "" + second;
        }
        timerStr = timerStr + minutes + ":" + secondStr;
        return timerStr;
    }

    private View.OnClickListener imgPlayAndPauseClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mediaPlayer.isPlaying()){
                handler.removeCallbacks(updater);
                mediaPlayer.pause();
                imgPlayAndPause.setImageResource(R.drawable.ic_baseline_play_circle_filled_24);
            }else{
                mediaPlayer.start();
                imgPlayAndPause.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
                updateSeekBar();
            }
        }
    };

    private View.OnClickListener btnAudioChoose = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, FilePickerActivity.class);

            intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
            .setCheckPermission(true)
            .setShowAudios(true)
            .setShowImages(false)
            .setShowVideos(false)
            .setMaxSelection(1)
            .setSkipZeroSizeFiles(true)
            .build());

            startActivityForResult(intent, 103);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
            Toast.makeText(this, "...", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null){
            ArrayList<MediaFile> mediaFiles = data.getParcelableArrayListExtra(
                    FilePickerActivity.MEDIA_FILES);
            String path = mediaFiles.get(0).getPath();

            switch (requestCode){
                case 103:
                    displatToast("Audio Path : " + path);
                    try {
                        mediaPlayer.setDataSource(path);
                        mediaPlayer.prepare();
                        txtTotalDuration.setText(millSecondToTimer(mediaPlayer.getDuration()));
                    }catch (Exception e){
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }

    private void displatToast(String path) {
        Toast.makeText(getApplicationContext(), path, Toast.LENGTH_SHORT).show();
    }
}