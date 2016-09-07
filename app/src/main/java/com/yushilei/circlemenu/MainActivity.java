package com.yushilei.circlemenu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.yushilei.circlemenu.widget.CircleMenuLayout;

public class MainActivity extends AppCompatActivity implements CircleMenuLayout.MenuItemClickListener {

    private CircleMenuLayout circle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        circle = (CircleMenuLayout) findViewById(R.id.circle);
        circle.setListener(this);

        String[] strItems = new String[]{"我的账户", "转账汇款", "投资理财"
                , "特色服务", "e账户", "快贷"};
        int[] rids = new int[]{R.mipmap.home_mbank_5_doraemon_clicked,
                R.mipmap.home_mbank_4_doraemon_clicked,
                R.mipmap.home_mbank_3_doraemon_clicked,
                R.mipmap.home_mbank_2_doraemon_clicked,
                R.mipmap.home_mbank_7_doraemon_clicked,
                R.mipmap.home_mbank_2_doraemon_clicked,
        };
        circle.addItemAndText(strItems, rids);
    }



    public void turn(View view) {
        circle.animStart();
    }

    @Override
    public void centerClick(View view) {
        Toast.makeText(this, "中心Item呗点击", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void childClick(View view, int pos) {
        Toast.makeText(this, "第" + pos + "个Item呗点击", Toast.LENGTH_SHORT).show();
    }

    public void jump(View view) {
        startActivity(new Intent(this, DiapatchActivity.class));
    }
}
