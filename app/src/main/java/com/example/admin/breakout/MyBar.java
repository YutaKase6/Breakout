package com.example.admin.breakout;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class MyBar {
    private int x;
    private int y;
    private int dx;
    private int before_x;
    private int red = 255;
    private int green = 255;
    private int blue = 255;
    private final int WIDTH = 400;
    private final int HEIGHT = 30;

    //バー操作時のタッチ範囲の補正
    private final int BARMARGIN = 50;
    //バーの衝突範囲の補正
    private final int BARHITMARGIN = 20;
    //バーの移動速度
    private final int BARDX = 10;

    private Rect rect;

    public MyBar(int x, int y) {
        this.x = x - WIDTH / 2;
        this.y = y;
        this.dx = 0;
        this.before_x = 0;
        rect = new Rect(this.x, y, x + WIDTH, y + HEIGHT);
    }

    public void paint(Canvas canvas) {
        Paint p = new Paint();
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.rgb(red, green, blue));
        canvas.drawRect(x, y, x + WIDTH, y + HEIGHT, p);
        rect.set(x, y, x + WIDTH, y + HEIGHT);
    }

    public void calc_dx() {
        dx = x - before_x;
        before_x = x;
    }


    public void addX() {
        x += dx;
    }

    public void refreshX(int x) {
        this.x = x - WIDTH / 2;
        calc_dx();
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

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public int getBefore_x() {
        return before_x;
    }

    public void setBefore_x(int before_x) {
        this.before_x = before_x;
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

    public int getWIDTH() {
        return WIDTH;
    }

    public int getHEIGHT() {
        return HEIGHT;
    }

    public int getBARMARGIN() {
        return BARMARGIN;
    }

    public int getBARHITMARGIN() {
        return BARHITMARGIN;
    }

    public int getBARDX() {
        return BARDX;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

}
