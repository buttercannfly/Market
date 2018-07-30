package com.example.zhangweikang.app_market;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShowActivity extends AppCompatActivity {
    private final String lock="lock";
    private static final String TAG ="SHOWACTIVITY" ;
    private String pg;
    private Document document;
    private ImageView img;
    private List<Descri> good_des;
    private TextView tx;
    private RadioGroup radioGroup;
    private RadioGroup radioGroup2;
    private Button bt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        Intent it2 = getIntent();
        Bundle bd = it2.getExtras();
        pg=bd.getString("a");
        Log.i(TAG,"new url:"+pg);
        good_des=new ArrayList<>();
        jsoupGet(pg);
        img=findViewById(R.id.img);
        Glide.with(this)
                .load(good_des.get(0).getImg())
                .into(img);
        tx=findViewById(R.id.tx1);
        tx.setText(good_des.get(0).getPrice());
        radioGroup=findViewById(R.id.radiogroup);
        for(int i=0;i<good_des.get(0).getKinds().size();i++){
            RadioButton radioButton=new RadioButton(this);
            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            //设置RadioButton边距 (int left, int top, int right, int bottom)
            lp.setMargins(15,0,0,0);
            radioButton.setPadding(60, 0, 0, 0);
            radioButton.setText((CharSequence) good_des.get(0).getKinds().get(i));
            final int finalI = i;
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            radioGroup.addView(radioButton);
        }
        radioGroup2=findViewById(R.id.radiogroup2);
        for(int j=0;j<good_des.get(0).getSize().size();j++){
            RadioButton radioButton=new RadioButton(this);
            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            //设置RadioButton边距 (int left, int top, int right, int bottom)
            lp.setMargins(15,0,0,0);
            radioButton.setPadding(40, 0, 0, 0);
            radioButton.setText((CharSequence) good_des.get(0).getSize().get(j));
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            radioGroup2.addView(radioButton);
        }
        bt=findViewById(R.id.bt1);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ShowActivity.this,Perchase.class);
                startActivity(intent);
            }
        });
    }
    public void jsoupGet(final String url){
        good_des.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    try {
                        document = Jsoup.connect(url).timeout(10000).get();
                        Elements noteList = document.select("div.danPinBox");//done
                        Descri good = new Descri();
                        good.setImg(noteList.select("img#midimg").attr("abs:src"));
                        Log.i(TAG, "img:" + good.getImg());
                        good.setPrice(noteList.select("span.tehuiMoney").text());
                        Log.i(TAG, "price:" + good.getPrice());
                        Elements ul = noteList.select("div.selColor");
                        Elements li = ul.select("li");
                        ArrayList ull = new ArrayList();
                        for (Element element : li) {
                            ull.add(element.select("p").text());
                        }
                        good.setKinds(ull);//
                        Elements nice = noteList.select("div.selSize");
                        Elements lr = nice.select("li");
                        ArrayList size = new ArrayList();
                        for (Element element : lr) {
                            size.add(element.select("p").text());//
                        }
                        good.setSize(size);
                        good_des.add(good);
                        lock.notify();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        synchronized (lock) {
            try {
                lock.wait();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
