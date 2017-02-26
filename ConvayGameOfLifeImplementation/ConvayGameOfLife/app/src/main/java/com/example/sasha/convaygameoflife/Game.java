package com.example.sasha.convaygameoflife;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

public class Game extends Activity implements View.OnClickListener {

    private GridGraphics _gridGraphics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        _gridGraphics = (GridGraphics) findViewById(R.id.grid);

        View btnNextClick = (View) findViewById(R.id.btnNext);
        btnNextClick.setOnClickListener(this);

        View btnResetClick = (View) findViewById(R.id.btnReset);
        btnResetClick.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNext:
                _gridGraphics.updateCellData();
                break;
            case R.id.btnReset:
                new AlertDialog.Builder(this).setTitle("Reset Activity")
                        .setMessage("Are you sure about resetting the view?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                _gridGraphics.initializeCellData();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
        }
    }
}
