package com.example.sasha.convaygameoflife;

/**
 * Created by sasha on 2/25/2017.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.lang.Math;

public class GridGraphics extends View implements View.OnTouchListener {

    private int deviceScale;
    private CellActivity _cellActivity;

    public GridGraphics(Context _context, AttributeSet attrs) {
        super(_context, attrs);
        deviceScale = Math.round(_context.getResources().getDisplayMetrics().density);
        _cellActivity = new CellActivity(deviceScale);
        setOnTouchListener(this);
    }

    public void updateCellData() {
        _cellActivity.nextGeneration();
        GridGraphics.this.invalidate();
    }

    public void initializeCellData() {
        _cellActivity.initializeGrid();
        GridGraphics.this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint background = new Paint();
        background.setColor(getResources().getColor(R.color.background));

        Paint cellBorder = new Paint();
        cellBorder.setStrokeWidth(10); // set stroke width
        cellBorder.setColor(getResources().getColor(R.color.gameBackground));

        Paint cell = new Paint();
        cell.setColor(getResources().getColor(R.color.cell));

        // draw background
        canvas.drawRect(0, 0, getWidth(), getHeight(), background);

        // draw cells
        for (int h = 0; h < _cellActivity.HEIGHT; h++) {
            for (int w = 0; w < _cellActivity.WIDTH; w++) {
                RectF rect = new RectF((w * _cellActivity.CELL_SIZE) * _cellActivity.Scale, (h * _cellActivity.CELL_SIZE) * _cellActivity.Scale,
                        ((w * _cellActivity.CELL_SIZE) + (_cellActivity.CELL_SIZE - 1)) * _cellActivity.Scale,
                        ((h * _cellActivity.CELL_SIZE) + (_cellActivity.CELL_SIZE - 1)) * _cellActivity.Scale);
                if (_cellActivity.getGrid()[h][w] != 0) {
                    canvas.drawRect(rect, cellBorder);
                    canvas.drawOval(rect, cell);
                } else {
                    canvas.drawRect(rect, cellBorder);
                }

            }
        }
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
            _cellActivity.updateCellData(java.lang.Math.round(event.getX()), java.lang.Math.round(event.getY()));
            GridGraphics.this.invalidate();
        }
        return true;
    }
}
