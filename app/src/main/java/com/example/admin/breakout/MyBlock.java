package com.example.admin.breakout;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class MyBlock {
    private int x;
    private int y;
    private int width;
    private int height;
    private int[][] colors;
    private int red;
    private int green;
    private int blue;
    //ブロックの強度
    private int life;
    //反発係数
    private double e;
    private double block_aspect_ratio;
    private MainActivity.Type item;

    private Rect src;
    private Rect dst;

    private Rect rect;

    public MyBlock(Context context, int x, int y, int w, int h, int[][] colors, int life, MainActivity.Type item, double e) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.block_aspect_ratio = (double) h / (double) w;

        Resources res = context.getResources();

        dst = new Rect(x, y, x + w, y + h);
        rect = new Rect(x, y, x + w, y + h);
        this.colors = colors;
        this.item = item;
        if (this.item != MainActivity.Type.NORMAL) {
            this.life = 1;
            setColors(4);
        } else {
            this.life = life;
            setColors(this.life);
        }
        this.e = e;
    }

    public void setColors(int life) {
        this.red = colors[life - 1][0];
        this.green = colors[life - 1][1];
        this.blue = colors[life - 1][2];
    }

    public void paint(Canvas canvas) {
        if (life > 0) {
            Paint p = new Paint();
            p.setStyle(Paint.Style.FILL);
            p.setColor(Color.rgb(red / 2, green / 2, blue / 2));
            canvas.drawRect(x, y, x + width, y + height, p);

            p.setStyle(Paint.Style.STROKE);
            p.setColor(Color.rgb(255, 255, 255));
            canvas.drawRect(x, y, x + width, y + height, p);
            canvas.drawLine(x, y, x + width, y + height, p);
            canvas.drawLine(x, y + height, x + width, y, p);

            p.setStyle(Paint.Style.FILL);
            p.setColor(Color.rgb(red, green, blue));
            canvas.drawRect(x + (width / 6), y + (height / 6), x + width - (width / 6), y + height - (height / 6), p);

            p.setStyle(Paint.Style.STROKE);
            p.setColor(Color.rgb(255, 255, 255));
            canvas.drawRect(x + (width / 6), y + (height / 6), x + width - (width / 6), y + height - (height / 6), p);
            if (item != MainActivity.Type.NORMAL) {
                p.setStyle(Paint.Style.FILL);
                p.setColor(Color.RED);
                p.setTextSize(50);
                p.setAntiAlias(true);
                p.setTextAlign(Paint.Align.CENTER);
                p.setStrokeWidth(2);
                canvas.drawText("?", x + width / 2, y + height - (height / 6), p);
            }

            //image
//			canvas.drawBitmap(bitmap, src, dst, p);

            //debug
//			p.setStyle(Paint.Style.STROKE);
//			canvas.drawRect(x, y, x + width, y + height, p);
//			canvas.drawLine(x - 500, y - 200, x + width + 500, y + height + 200, p);
//			canvas.drawLine(x-500, y+height+200, x+width+500, y-200, p);
        }
    }

    public void take_life(int n) {
        this.life -= n;
        if (life != 0) {
            setColors(life);
        }
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int[][] getColors() {
        return colors;
    }

    public void setColors(int[][] colors) {
        this.colors = colors;
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

    public double getBlock_aspect_ratio() {
        return block_aspect_ratio;
    }

    public void setBlock_aspect_ratio(double block_aspect_ratio) {
        this.block_aspect_ratio = block_aspect_ratio;
    }

    public MainActivity.Type getItem() {
        return item;
    }

    public void setItem(MainActivity.Type item) {
        this.item = item;
    }

    public Rect getSrc() {
        return src;
    }

    public void setSrc(Rect src) {
        this.src = src;
    }

    public Rect getDst() {
        return dst;
    }

    public void setDst(Rect dst) {
        this.dst = dst;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public int getLife() {
        return this.life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public double getE() {
        return e;
    }

    public void setE(double e) {
        this.e = e;
    }
}
