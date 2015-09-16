package com.example.admin.breakout;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity implements DrawFinishListener{

    /**
     * ゲーム描画View
     */
    private BreakoutView view;

    /**
     * bar object
     */
    private MyBar bar;

    /**
     * position of bar
     */
    private int barY, barX;

    /**
     * list of balls
     */
    private ArrayList<MyBall> balls;

    /**
     * size of ball(radius)
     */
    private final int BALL_RADIUS = 15;
    /**
     * first speed of ball
     */
    private final int BALL_DX = 1;
    private final int BALL_DY = -4;

    private final int RESTART_BALL_SPEED = 3;
    /**
     * color of ball(RGB)
     */
    private final int BALL_R = 255;
    private final int BALL_G = 255;
    private final int BALL_B = 255;

    /**
     * list of blocks
     */
    private MyBlock[][] blocks;

    /**
     * number of blocks
     */
    private final int BLOCK_NUM_X = 6;
    private final int BLOCK_NUM_Y = 20;
    /**
     * size of blocks
     */
    private final double BLOCK_SIZE_SCALE = 1.5;
    private final int BLOCK_WIDTH = (int) (100 * BLOCK_SIZE_SCALE);
    private final int BLOCK_HEIGHT = (int) (40 * BLOCK_SIZE_SCALE);
    /**
     * margin
     */
    private int blockMarginX;
    private int blockMarginY;
    /**
     * colors of blocks
     */
    private int[][] blockColors;
    /**
     * number of item blocks
     */
    private int numOfItem = 10;

    /**
     * flag of game over
     */
    private Boolean gameOverFlag = false;
    /**
     * flag of game clear
     */
    private Boolean gameClearFlag = false;

    private SensorManager sensorManager;
    private MoveListener moveListener;
    private List<Sensor> sensorList;
    /**センサー時速時間(フレーム)*/
    private int sensorTime = 500;
    /**センサー持続計測カウンタ*/
    private int sensorCount = 0;
    /**flag of on sensor*/
    private Boolean sensorFlag;

    WindowManager wm;
    Display dp;

    /**ゲームの状態*/
    public enum Type {
        NORMAL,
        ADD_BALL,
        ON_ACCELERATION,
        GAME_OVER,
        GAME_CLEAR
    }
    /**現在のゲームの状態*/
    private Type state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //ウィンドウに関するサービスを取得する
        wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        //ディスプレイの情報を得る
        dp = wm.getDefaultDisplay();

        //初期化
        initBall();
        initBar();
        initBlock();
        state = Type.NORMAL;

        //センサーの設定
        sensorManager = (SensorManager) this.getSystemService(this.SENSOR_SERVICE);
        moveListener = new MoveListener(balls);
        sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        sensorFlag = false;

        //viewの生成、初期化
        view = new BreakoutView(this, blocks, balls, bar);
        view.setDrawFinishListener(this);
        view.setSensorFlag(sensorFlag);
        view.setGameOverFlag(gameOverFlag);
        view.setGameClearFlag(gameClearFlag);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_CANCEL) {
                    return true;
                }
                view.invalidate();
                // 画面をタッチした時
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
//                    bar.refreshX(e.getX());
                    if (gameOverFlag || gameClearFlag) {
                        startActivity(new Intent(MainActivity.this, StartActivity.class));
                        finish();
                    }
                    return true;
                }
                // タッチしている間
                if (e.getAction() == MotionEvent.ACTION_MOVE) {
                    if (e.getX() >= bar.getX() - bar.getBARMARGIN() && e.getX() <= bar.getX() + bar.getWIDTH() + bar.getBARMARGIN()) {
                        bar.refreshX((int) e.getX());
                    }
                    return true;
                }
                // 指をタッチした状態から離した時
                if (e.getAction() == MotionEvent.ACTION_UP) {
                }
                return false;
            }
        });
        setContentView(view);
    }

    /**
     * 衝突判定
     * 衝突したら速度を反転し、座標を修正
     *
     * @param ball
     * @param bar
     * @param view_width
     * @param view_height
     * @param bls
     * @param sensorFlag
     * @return
     */
    public void bounce(MyBall ball, MyBar bar, int view_width, int view_height, MyBlock[][] bls, boolean sensorFlag) {
        state = Type.NORMAL;
        //左端壁
        if (ball.getX() - ball.getR() <= 0) {
            ball.setX(ball.getR());
            ball.setDx(-ball.getDx() * ball.getE());
        }
        //右端壁
        else if (ball.getX() + ball.getR() >= view_width) {
            ball.setX(view_width - ball.getR());
            ball.setDx(-ball.getDx() * ball.getE());
        }
        //上端壁
        if (ball.getY() - ball.getR() <= 0) {
            ball.setY(ball.getR());
            ball.setDy(-ball.getDy() * ball.getE());
        }
        //下端壁
        else if (ball.getY() + ball.getR() >= view_height) {
            state = Type.GAME_OVER;
//            y = height - r;
//            dy = -dy;
//            res = -1;
        }
        //加速度センサーON時は、バー非表示のため、不干渉
        if (!sensorFlag) {
            //バー
            if (ball.getX() <= bar.getX() + bar.getWIDTH() && ball.getX() >= bar.getX() && ball.getY() + ball.getR() >= bar.getY() && ball.getY() + ball.getR() <= bar.getY() + bar.getHEIGHT()) {
                ball.setY(bar.getY() - ball.getR());
                ball.setDx(ball.getDx() + bar.getDx() / 10.0);
                ball.setDy(-1 * ball.getDy() * ball.getE());
            }
        }

        //block
        /**ブロックの座標*/
        int block_x;
        int block_y;
        /**ブロックの大きさ*/
        int block_w;
        int block_h;
        /**ブロックとボールの距離*/
        double dist_x;
        double dist_y;
        /**ブロックの縦横比*/
        double aspect_ratio;
        /**ブロックを破壊したかどうか*/
        boolean breakFlag = false;
        /**ゲームをクリアしたかどうか*/
        boolean clearFlag = true;
        for (int i = 0; i < bls.length; i++) {
            for (int j = 0; j < bls[i].length; j++) {
                //ブロックがあるなら
                if (bls[i][j].getLife() > 0) {
                    clearFlag = false;
                    block_x = bls[i][j].getX();
                    block_y = bls[i][j].getY();
                    block_w = bls[i][j].getWidth();
                    block_h = bls[i][j].getHeight();
                    //distance block from ball
                    dist_x = (block_x + (block_w / 2)) - ball.getX();
                    dist_y = (block_y + (block_h / 2)) - ball.getY();
                    //ブロックの縦横比
                    aspect_ratio = bls[i][j].getBlock_aspect_ratio();

                    //ボールとブロックの位置関係で4分割
                    //judge top or bottom
                    if (dist_y >= 0 && Math.abs(dist_x) * aspect_ratio <= dist_y) {
                        //top
                        if (isHitTop(ball.getX(), ball.getY(), block_x, block_y, block_w, block_h, ball.getR())) {
                            bls[i][j].take_life(1);
                            ball.setDy(-1 * ball.getDy() * ball.getE()*blocks[i][j].getE());
                            ball.setY(block_y - ball.getR() - 1);
                            breakFlag = true;
//                            this.red = 255;
//                            this.green = 255;
//                            this.blue = 0;
                        }
                    } else if (dist_y <= 0 && Math.abs(dist_x) * aspect_ratio <= Math.abs(dist_y)) {
                        //bottom
                        if (isHitBottom(ball.getX(), ball.getY(), block_x, block_y, block_w, block_h, ball.getR())) {
                            bls[i][j].take_life(1);
                            ball.setDy(-1 * ball.getDy() * ball.getE()*blocks[i][j].getE());
                            ball.setY(block_y + block_h + ball.getR() + 1);
                            breakFlag = true;
//                            this.red = 255;
//                            this.green = 0;
//                            this.blue = 0;
                        }
                    }
                    //judge left or right
                    if (dist_x >= 0 && dist_x * aspect_ratio >= Math.abs(dist_y)) {
                        //left
                        if (isHitLeft(ball.getX(), ball.getY(), block_x, block_y, block_w, block_h, ball.getR())) {
                            bls[i][j].take_life(1);
                            ball.setDx(-1 * ball.getDx() * ball.getE()*blocks[i][j].getE());
                            ball.setX(block_x - ball.getR() - 1);
                            breakFlag = true;
//                            this.red = 0;
//                            this.green = 255;
//                            this.blue = 0;
                        }
                    } else if (dist_x <= 0 && Math.abs(dist_x) * aspect_ratio >= Math.abs(dist_y)) {
                        //right
                        if (isHitRight(ball.getX(), ball.getY(), block_x, block_y, block_w, block_h, ball.getR())) {
                            bls[i][j].take_life(1);
                            ball.setDx(-1 * ball.getDx() * ball.getE()*blocks[i][j].getE());
                            ball.setX(block_x + block_w + ball.getR() + 1);
                            breakFlag = true;
//                            this.red = 0;
//                            this.green = 0;
//                            this.blue = 255;
                        }
                    }
                }
                if (breakFlag) {
                    //アイテムブロックだったなら
                    if (bls[i][j].getItem() != Type.NORMAL) {
                        state = bls[i][j].getItem();
                    }
                    break;
                }
            }
            if (breakFlag) {
                break;
            }
        }
        if (clearFlag) {
            state = Type.GAME_CLEAR;
        }
    }


    /**
     * ブロック下部との衝突判定
     *
     * @param ball_x
     * @param ball_y
     * @param block_x
     * @param block_y
     * @param block_w
     * @param block_h
     * @param ball_r
     * @return
     */
    boolean isHitBottom(int ball_x, int ball_y, int block_x, int block_y, int block_w, int block_h, int ball_r) {
        if (ball_x + ball_r <= block_x + block_w && ball_x - ball_r >= block_x && ball_y - ball_r >= block_y && ball_y - ball_r <= block_y + block_h) {
            return true;
        }
        return false;
    }

    /**
     * ブロック上部との衝突判定
     *
     * @param ball_x
     * @param ball_y
     * @param block_x
     * @param block_y
     * @param block_w
     * @param block_h
     * @param ball_r
     * @return
     */
    boolean isHitTop(int ball_x, int ball_y, int block_x, int block_y, int block_w, int block_h, int ball_r) {
        if (ball_x + ball_r <= block_x + block_w && ball_x - ball_r >= block_x && ball_y + ball_r >= block_y && ball_y + ball_r <= block_y + block_h) {
            return true;
        }
        return false;
    }

    /**
     * ブロック左部との衝突判定
     *
     * @param ball_x
     * @param ball_y
     * @param block_x
     * @param block_y
     * @param block_w
     * @param block_h
     * @param ball_r
     * @return
     */
    boolean isHitLeft(int ball_x, int ball_y, int block_x, int block_y, int block_w, int block_h, int ball_r) {
        if (ball_y - ball_r <= block_y + block_h && ball_y + ball_r >= block_y && ball_x + ball_r <= block_x + block_w && ball_x + ball_r >= block_x) {
            return true;
        }
        return false;
    }

    /**
     * ブロック右部との衝突判定
     *
     * @param ball_x
     * @param ball_y
     * @param block_x
     * @param block_y
     * @param block_w
     * @param block_h
     * @param ball_r
     * @return
     */
    boolean isHitRight(int ball_x, int ball_y, int block_x, int block_y, int block_w, int block_h, int ball_r) {
        if (ball_y - ball_r <= block_y + block_h && ball_y + ball_r >= block_y && ball_x - ball_r <= block_x + block_w && ball_x - ball_r >= block_x) {
            return true;
        }
        return false;
    }

    /**
     * start sensor
     */
    void runSensor() {
        for (Sensor sensor : sensorList) {
            sensorManager.registerListener(moveListener, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
        //反発係数を設定
        for (int i = 0; i < balls.size(); i++) {
            balls.get(i).setE(0.5);
        }
    }

    /**
     * stop sensor
     */
    void sleepSensor() {
        sensorManager.unregisterListener(moveListener);
        //反発係数を設定
        for (int i = 0; i < balls.size(); i++) {
            balls.get(i).setE(1);
        }
    }

    /**
     * initialize blocks
     */
    public void initBlock() {
        blocks = new MyBlock[BLOCK_NUM_Y][BLOCK_NUM_X];
        //set margin
        blockMarginX = (dp.getWidth() - BLOCK_WIDTH * BLOCK_NUM_X) / 2;
        blockMarginY = BLOCK_HEIGHT * 3;
        //set color
        blockColors = initBlockColors();
        /**life of block*/
        int life;
        /**position of block*/
        int blockX, blockY;
        /**indices of item blocks*/
        int[] itemIndices = new int[numOfItem];
        int indexCount = 0;
        /**反発係数*/
        double e = 1.0001;

        //アイテム効果を付与するブロック番号を抽選
        for (int i = 0; i < itemIndices.length; i++) {
            itemIndices[i] = (int) (Math.random() * (BLOCK_NUM_X * BLOCK_NUM_Y));
        }
        Arrays.sort(itemIndices);

        for (int i = 0; i < BLOCK_NUM_Y; i++) {
            for (int j = 0; j < BLOCK_NUM_X; j++) {
                Type item = Type.NORMAL;
                //calc position
                blockX = BLOCK_WIDTH * j + blockMarginX;
                blockY = BLOCK_HEIGHT * i + blockMarginY;
                //set life as random
                life = (int) (Math.random() * 3) + 1;
                //judge of item
                if (indexCount < numOfItem) {
                    if (i * BLOCK_NUM_X + j == itemIndices[indexCount]) {
                        //set item as random
                        if ((int) (Math.random() * 2) + 1 == 1) {
                            item = Type.ADD_BALL;
                        } else {
                            item = Type.ON_ACCELERATION;
                        }
                        indexCount++;
                    }
                }
                blocks[i][j] = new MyBlock(this, blockX, blockY, BLOCK_WIDTH, BLOCK_HEIGHT, blockColors, life, item,e);
            }
        }
    }

    /**
     * initialize bar
     */
    public void initBar() {
        //calc position
        barY = dp.getHeight() - dp.getHeight() / 11;
        barX = dp.getWidth() / 2;
        bar = new MyBar(barX, barY);
    }

    /**
     * initialize ball
     */
    public void initBall() {
        balls = new ArrayList<MyBall>();
        //calc position
        int x = (dp.getWidth() / 2) + 50;
        int y = dp.getHeight() - dp.getHeight() / 7;
        balls.add(new MyBall(x, y, BALL_RADIUS, BALL_DX, -BALL_DY, BALL_R, BALL_G, BALL_B));
    }

    /**
     * initialize colors of  blocks
     *
     * @return colors
     */
    public int[][] initBlockColors() {
        int[][] res = new int[4][3];
        //アイテムブロック
        res[3][0] = 255;
        res[3][1] = 255;
        res[3][2] = 255;
        //耐久3ブロック
        res[2][0] = 0;
        res[2][1] = 128;
        res[2][2] = 255;
        //耐久2ブロック
        res[1][0] = 255;
        res[1][1] = 255;
        res[1][2] = 0;
        //耐久1ブロック
        res[0][0] = 255;
        res[0][1] = 0;
        res[0][2] = 0;

        return res;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        view.hn = null;
    }

    @Override
    public void onDrawFinished() {
        //加速度センサーON時
        if (sensorFlag) {
            sensorCount++;
            //一定時間たったらセンサー停止
            if (sensorCount > sensorTime) {
                sleepSensor();
                sensorFlag = false;
                view.setSensorFlag(sensorFlag);
                //センサー停止後ボールの速度をランダムで付与
                for (int i = 0; i < balls.size(); i++) {
                    if ((int) (Math.random() * 2) == 0) {
                        balls.get(i).setDx(RESTART_BALL_SPEED);
                    } else {
                        balls.get(i).setDx(-RESTART_BALL_SPEED);
                    }
                    if ((int) (Math.random() * 2) == 0) {
                        balls.get(i).setDy(RESTART_BALL_SPEED);
                    } else {
                        balls.get(i).setDy(-RESTART_BALL_SPEED);
                    }
                    //加速度は0に
                    balls.get(i).changeAcceleration(0, 0);
                }
            }
        }
        //衝突判定
        for (int i = 0; i < balls.size(); i++) {
            bounce(balls.get(i), bar, dp.getWidth(), dp.getHeight(), blocks, sensorFlag);
            //アイテム：センサー
            if (state == Type.ON_ACCELERATION) {
                if (!sensorFlag) {
                    runSensor();
                    sensorFlag = true;
                    view.setSensorFlag(true);
                    sensorCount = 0;
                } else {
                    sensorCount = 0;
                }
                //アイテム：ボール増加
            } else if (state == Type.ADD_BALL) {
                balls.add(new MyBall(balls.get(i).getX(), balls.get(i).getY(), BALL_RADIUS, balls.get(i).getDx() + (Math.random() * 3) + 1, balls.get(i).getDy() + (Math.random() * 3) + 1, BALL_R, BALL_G, BALL_B));
                balls.add(new MyBall(balls.get(i).getX(), balls.get(i).getY(), BALL_RADIUS, balls.get(i).getDx() + (Math.random() * 3) + 1, balls.get(i).getDy() + (Math.random() * 3) + 1, BALL_R, BALL_G, BALL_B));
                //ゲームオーバー
            } else if (state == Type.GAME_OVER) {
                balls.remove(i);
                if (balls.size() == 0) {
                    gameOverFlag = true;
                    view.setGameOverFlag(gameOverFlag);
                    break;
                }
                //ゲームクリア
            } else if (state == Type.GAME_CLEAR) {
                gameClearFlag = true;
                view.setGameClearFlag(gameClearFlag);
                break;
            }
        }

        //座標の更新
        for (int i = 0; i < balls.size(); i++) {
            balls.get(i).addX();
            balls.get(i).addY();
        }
    }
}