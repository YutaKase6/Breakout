package com.example.admin.breakout;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;


public class BreakoutView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
    private Context context;

    /**
     * list of blocks
     */
    private MyBlock[][] blocks;

    /**
     * list of balls
     */
    private ArrayList<MyBall> balls;

    /**
     * bar object
     */
    private MyBar bar;


    protected Handler hn;
    SurfaceHolder surfaceHolder;
    Thread thread;

    WindowManager wm;
    Display dp;

    private Bitmap background_bitmap;

    /**
     * flag of game over
     */
    private Boolean gameOverFlag = false;
    /**
     * flag of game clear
     */
    private Boolean gameClearFlag = false;
    /**
     * flag of sensor
     */
    private Boolean sensorFlag = false;

    /**
     * listener of finishing draw
     */
    private DrawFinishListener drawFinishListener;

    public BreakoutView(Context context, MyBlock[][] blocks, ArrayList<MyBall> balls, MyBar bar) {
        super(context);
        // TODO 自動生成されたコンストラクター・スタブ
        this.context = context;
        setBlocks(blocks);
        setBalls(balls);
        setBar(bar);
        setBackgroundColor(Color.WHITE);

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
//		hn = new Handler();
//		hn.postDelayed(this, 1);


        //ウィンドウに関するサービスを取得する
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //ディスプレイの情報を得る
        dp = wm.getDefaultDisplay();

        background_bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);

    }

    @Override
    public void onDraw(Canvas canvas) {

//        canvas.drawBitmap(background_bitmap, 0, 0, null);

        //加速度センサーON時
        if (sensorFlag) {
            canvas.drawColor(Color.GRAY);
        } else {
            canvas.drawColor(Color.BLACK);
            bar.paint(canvas);
        }

        //draw balls
        for (int i = 0; i < balls.size(); i++) {
            balls.get(i).paint(canvas);
        }

        //draw blocks
        for (int i = 0; i < blocks.length; i++) {
            for (int j = 0; j < blocks[i].length; j++) {
                blocks[i][j].paint(canvas);
            }
        }
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        //draw ded line
        canvas.drawLine(0, dp.getHeight() - 1, dp.getWidth(), dp.getHeight() - 1, p);

        if (gameOverFlag) {
            //ゲームオーバー描画
            p.setTextSize(250);
            p.setTextAlign(Paint.Align.CENTER);
            p.setColor(Color.WHITE);
            canvas.drawText("Game Over", dp.getWidth() / 2, dp.getHeight() / 2, p);
            return;
        } else if (gameClearFlag) {
            //ゲームクリア描画
            p.setTextSize(250);
            p.setTextAlign(Paint.Align.CENTER);
            p.setColor(Color.WHITE);
            canvas.drawText("Game Clear", dp.getWidth() / 2, dp.getHeight() / 2, p);
            return;
        }
    }

    //runメソッドの中を繰り返す
    @Override
    public void run() {
        while (thread != null) {
            //再描画
            postInvalidate();

            drawFinishListener.onDrawFinished();

            //10ミリ秒後に再度呼び出す
//			hn.postDelayed(this, 1);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO 自動生成された catch ブロック
                e.printStackTrace();
            }
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        // TODO 自動生成されたメソッド・スタブ
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO 自動生成されたメソッド・スタブ
        thread = null;
    }

    public void setBlocks(MyBlock[][] blocks) {
        this.blocks = blocks;
    }

    public void setBalls(ArrayList<MyBall> balls) {
        this.balls = balls;
    }

    public void setBar(MyBar bar) {
        this.bar = bar;
    }

    public void setSensorFlag(Boolean sensorFlag) {
        this.sensorFlag = sensorFlag;
    }

    public void setGameOverFlag(Boolean gameOverFlag) {
        this.gameOverFlag = gameOverFlag;
    }

    public void setGameClearFlag(Boolean gameClearFlag) {
        this.gameClearFlag = gameClearFlag;
    }

    public void setDrawFinishListener(DrawFinishListener drawFinishListener) {
        this.drawFinishListener = drawFinishListener;
    }
}
