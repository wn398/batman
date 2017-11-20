package com.rayleigh.core.QRcode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtil {
    private static Logger logger = LoggerFactory.getLogger(ImageUtil.class);
    public static BufferedImage imageAddQRCode(BufferedImage backgroundImage,BufferedImage addedImage,Location location)throws IOException{
        Graphics2D g2 = backgroundImage.createGraphics();;
        int matrixWidth = backgroundImage.getWidth();
        int matrixHeight = backgroundImage.getHeight();

        int logoWidth = addedImage.getWidth();
        int logoHeight = addedImage.getHeight();
        int x=0;
        int y=0;
        if(location==Location.Center){
            x= (matrixWidth-logoWidth)/2;
            y= (matrixHeight-logoHeight)/2;
        }else if(location==Location.LeftDown){
            y=matrixHeight-logoHeight;
        }else if(location==Location.RightUp){
            x=matrixWidth-logoWidth;
        }else if(location==Location.RightDown){
            x=matrixWidth-logoWidth;
            y=matrixHeight-logoHeight;
        }
        //开始绘制图片
        g2.drawImage(addedImage,x,y, logoWidth, logoHeight, null);//绘制
//        BasicStroke stroke = new BasicStroke(5,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
//        g2.setStroke(stroke);// 设置笔画对象
//        //指定弧度的圆角矩形
//        RoundRectangle2D.Float round = new RoundRectangle2D.Float(matrixWidth-logoWidth, matrixHeight-logoHeight, logoWidth-4, logoHeight-4,20,20);
//        g2.setColor(Color.white);
//        g2.draw(round);// 绘制圆弧矩形

        //设置logo 有一道灰色边框
//        BasicStroke stroke2 = new BasicStroke(5,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
//        g2.setStroke(stroke2);// 设置笔画对象
//        RoundRectangle2D.Float round2 = new RoundRectangle2D.Float(x, y, logoWidth, logoHeight,20,20);
//        g2.setColor(new Color(128,128,128));
//        //g2.setColor(Color.red);
//        g2.draw(round2);// 绘制圆弧矩形

        g2.dispose();
        backgroundImage.flush() ;
//        if(ImageIO.write(backgroundImage,"jpg",image)){
//            logger.info("图片生成成功!");
//        }
        return backgroundImage;
    }

    public static void main(String[] args)throws Exception {
        File file = new File("D:"+File.separator+"test2.jpg");
        File logoFile = new File("d:"+File.separator+"new-1.gif");
        RenderedImage bufferedImage =imageAddQRCode(ImageIO.read(file),ImageIO.read(logoFile),Location.RightUp);
        ImageIO.write(bufferedImage,"jpg",file);
    }
}
