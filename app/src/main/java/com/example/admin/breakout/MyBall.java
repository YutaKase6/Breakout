package com.example.admin.breakout;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.util.Log;

public class MyBall {
    private int x;
    private int y;
    private int r;
    private double dx;
    private double dy;
    private double accelerationX;
    private double accelerationY;
    private double accelerationRate;
    private int red;
    private int green;
    private int blue;
    private Rect rect;
    private double e;

    public MyBall(int x, int y, int r, double dx, double dy, int red, int green, int blue) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.dx = dx;
        this.dy = dy;
        this.red = red;
        this.green = green;
        this.blue = blue;
        rect = new Rect(x - r, y - r, x + r, y + r);
        e = 1;
        this.accelerationX = 0;
        this.accelerationY = 0;
        this.accelerationRate = 0.05;
    }



    public void paint(Canvas canvas) {
        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.rgb(red, green, blue));
        canvas.drawCircle(x, y, r, p);
        rect.set(x - r, y - r, x + r, x + y);
    }

    public void addX() {
        dx += accelerationX;
        x += dx;
    }

    public void addY() {
        dy += accelerationY;
        y += dy;
    }

    public void changeAcceleration(float accelerationX, float accelerationY) {
        this.accelerationX = -accelerationX*accelerationRate;
        this.accelerationY = accelerationY*accelerationRate;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public double getDx() {
        return dx;
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public double getDy() {
        return dy;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }

    public double getAccelerationX() {
        return accelerationX;
    }

    public void setAccelerationX(double accelerationX) {
        this.accelerationX = accelerationX;
    }

    public double getAccelerationY() {
        return accelerationY;
    }

    public void setAccelerationY(double accelerationY) {
        this.accelerationY = accelerationY;
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public double getE() {
        return e;
    }

    public void setE(double e) {
        this.e = e;
    }
    /*
    public boolean hit(Rect rect) {
        if (this.rect.intersect(rect)) {
            return true;
        }
        return false;
    }

    //ブロックとボールの衝突処理
    void collisionDetection(MyBall b, MyBlock[][] bls, MyBar bar) {
        if (collideWidth(bls, bar)) {
            b.dx = -b.dx;
            b.dy = -b.dy;
        }
    }

    //ブロックとボールの衝突判定
    public boolean collideWidth(MyBlock[][] bls, MyBar bar) {
        Rect rect_ball = new Rect(x, y, x + r, y + r);
        Rect rect_block;
        for (int i = 0; i < bls.length; i++) {
            for (int j = 0; j < bls.length; j++) {
                if (bls[i][j].getLife() > 0) {
                    rect_block = new Rect(bls[i][j].x, bls[i][j].y, bls[i][j].x + bls[i][j].width, bls[i][j].y + bls[i][j].height);
                    if (rect_ball.intersect(rect_block)) {
                        bls[i][j].take_life(1);
                        return true;
                    }
                }
            }
        }
        return false;
    }
*/
}
