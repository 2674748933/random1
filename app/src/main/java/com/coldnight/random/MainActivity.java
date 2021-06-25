package com.coldnight.random;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.coldnight.random.control.LuckPan;


public class MainActivity extends AppCompatActivity {
    private LuckPan luckPan;
    private LinearLayout linearLayout;
    private RelativeLayout relativeLayout;
    private ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout = findViewById(R.id.layout_container);

        relativeLayout = findViewById(R.id.rel);
        luckPan = new LuckPan(this);
        relativeLayout.addView(luckPan);

        newImageView();

        Button button1 = findViewById(R.id.btn_new_pan);
        button1.setOnClickListener(v -> newLuckPan());

        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        EditText et = findViewById(R.id.et_new_pan);
        et.setText(sp.getString("pan", ""));
    }

    private void newLuckPan() {
        EditText et = findViewById(R.id.et_new_pan);
        String str = et.getText().toString();
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("pan", str);
        editor.apply();
        newLuckPan(str.split(";"));
    }

    private void newLuckPan(String[] args) {
        relativeLayout.removeView(luckPan);
        relativeLayout.removeView(iv);
        luckPan = new LuckPan(this, args);
        relativeLayout.addView(luckPan);
        newImageView();
    }

    private void newImageView() {
        iv = new ImageView(this);
        relativeLayout.addView(iv);
        iv.setImageResource(R.mipmap.ic_eating);
        RelativeLayout.LayoutParams layoutParams  = (RelativeLayout.LayoutParams) iv.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        iv.setOnClickListener(v -> {
            luckPan.startAnimRandom();
            luckPan.setLuckPanAnimEndCallBack(str -> {
                if ("再来一次".equals(str))
                    Toast.makeText(MainActivity.this, "臭手！再来一次", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(MainActivity.this, "买定离手！今天就吃" + str, Toast.LENGTH_LONG).show();
            });
        });
    }
}