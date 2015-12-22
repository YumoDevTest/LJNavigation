package com.macernow.djstava.ljnavigation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.macernow.djstava.ljnavigation.adapter.AdapterImageTextView;
import com.macernow.djstava.ljnavigation.music.MusicViewActivity;
import com.macernow.djstava.ljnavigation.utils.DJLog;

public class MultimediaWizardActivity extends AppCompatActivity {
    private GridView gridView;
    private Button button;
    private int[] images = {R.drawable.music,R.drawable.movie,R.drawable.radio};
    private int[] title = {R.string.music,R.string.movie,R.string.radio};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_media_wizard);

        initUIComponent();
    }

    private void initUIComponent() {
        button = (Button)findViewById(R.id.button_back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        gridView = (GridView)findViewById(R.id.gridView);
        AdapterImageTextView adapterImageTextView = new AdapterImageTextView(this,images,title);
        gridView.setAdapter(adapterImageTextView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(MultimediaWizardActivity.this, MusicViewActivity.class));
                        break;

                    case 1:
                        startActivity(new Intent(MultimediaWizardActivity.this,MovieViewActivity.class));
                        break;

                    case 2:
                        Toast.makeText(MultimediaWizardActivity.this,"正在建设中...",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
}
