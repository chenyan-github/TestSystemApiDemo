package com.cy.test;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    FrameLayout ll_parent;
    Button btn_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        ll_parent = findViewById(R.id.ll_parent);
        btn_test = findViewById(R.id.btn_test);

        ll_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "parent click", Toast.LENGTH_SHORT).show();
            }
        });
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "btn click", Toast.LENGTH_SHORT).show();
            }
        });

        //扩大按钮点击区域
        ll_parent.post(new Runnable() {
            @Override
            public void run() {
                Rect testRect = new Rect();
                btn_test.getHitRect(testRect);
                Log.e(MainActivity.class.getSimpleName(), testRect.toShortString());
                testRect.right += 200;
                testRect.bottom += 200;
                TouchDelegate touchDelegate = new TouchDelegateFix(testRect, btn_test);
                // within the touch delegate bounds are routed to the child.
                ll_parent.setTouchDelegate(touchDelegate);
            }
        });
    }
}
