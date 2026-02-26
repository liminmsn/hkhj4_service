package com.hkhj4.utily;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class ImageCodeUtils {

    private static final String CODE_CHARS = "0123456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final int WIDTH = 100;
    private static final int HEIGHT = 40;
    private static final int CODE_LENGTH = 4;

    // 生成验证码图片 & 验证码文本
    public static BufferedImage generateImage(String code) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        Random random = new Random();

        // 背景
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 字体
        g.setFont(new Font("Arial", Font.BOLD, 28));

        // 干扰线
        for (int i = 0; i < 6; i++) {
            g.setColor(getRandomColor(150, 250));
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        // 画文字
        for (int i = 0; i < CODE_LENGTH; i++) {
            char c = code.charAt(i);
            g.setColor(getRandomColor(20, 130));
            g.drawString(String.valueOf(c), 20 + i * 20, 30);
        }

        g.dispose();
        return image;
    }

    // 生成随机验证码文本
    public static String generateCode() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CODE_CHARS.charAt(random.nextInt(CODE_CHARS.length())));
        }
        return sb.toString();
    }

    // 随机颜色
    private static Color getRandomColor(int min, int max) {
        Random r = new Random();
        int red = min + r.nextInt(max - min);
        int green = min + r.nextInt(max - min);
        int blue = min + r.nextInt(max - min);
        return new Color(red, green, blue);
    }
}