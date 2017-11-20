package com.rayleigh.core.QRcode;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;  
import java.io.IOException;  
import java.io.OutputStream;
import javax.imageio.ImageIO;
import com.google.zxing.common.BitMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * 二维码的生成需要借助MatrixToImageWriter类，该类是由Google提供的，可以将该类直接拷贝到源码中使用，当然你也可以自己写个 
 * 生产条形码的基类 
 */  
public class MatrixToImageWriter {  
    private static final int BLACK = 0xFF000000;//用于设置图案的颜色  
    private static final int WHITE = 0xFFFFFFFF; //用于背景色  
    private static Logger logger = LoggerFactory.getLogger(MatrixToImageWriter.class);
    private MatrixToImageWriter() {  
    }  
    //绘制二维码图片
    public static BufferedImage toBufferedImage(BitMatrix matrix) {  
        int width = matrix.getWidth();  
        int height = matrix.getHeight();  
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);  
        for (int x = 0; x < width; x++) {  
            for (int y = 0; y < height; y++) {  
               image.setRGB(x, y,  (matrix.get(x, y) ? BLACK : WHITE));
            }  
        }  
        return image;  
    }  
  
    public static void writeToFile(BitMatrix matrix, String format, File file) throws IOException {  
       writeToFile(matrix,format,file,null);
    }

    public static void writeToFile(BitMatrix matrix, String format, File file,File logoFile) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if(null!=logoFile) {
            //设置logo图标
            image = bufferedImageAddLogo(image, logoFile);
        }
        if (!ImageIO.write(image, format, file)) {
            throw new IOException("Could not write an image of format " + format + " to " + file);
        }else{
            logger.info("图片生成成功！");
        }
    }

    public static void writeToStream(BitMatrix matrix, String format, OutputStream stream) throws IOException{
        writeToStream(matrix,format,stream,null);
    }

    public static void writeToStream(BitMatrix matrix, String format, OutputStream stream,File logoFile) throws IOException {
        BufferedImage image = toBufferedImage(matrix);  
        if(null!=logoFile) {
            //设置logo图标
            image = bufferedImageAddLogo(image, logoFile);
        }
        if (!ImageIO.write(image, format, stream)) {  
            throw new IOException("Could not write an image of format " + format);  
        }  
    }

    /**
     *
     * @param matrixImage
     * @param logoFile
     * @return  向bufferImage正中添加logo图片
     * @throws IOException
     */
    private static BufferedImage bufferedImageAddLogo(BufferedImage matrixImage, File logoFile) throws IOException{
        /**
         * 读取二源维码图片，并构建绘图对象
         */
        Graphics2D g2 = matrixImage.createGraphics();

        int matrixWidth = matrixImage.getWidth();
        int matrixHeigh = matrixImage.getHeight();
        /**
         * 读取Logo图片
         */
        BufferedImage logo = ImageIO.read(logoFile);

        //开始绘制图片
        g2.drawImage(logo,matrixWidth/5*2,matrixHeigh/5*2, matrixWidth/5, matrixHeigh/5, null);//绘制
        BasicStroke stroke = new BasicStroke(5,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke);// 设置笔画对象
        //指定弧度的圆角矩形
        RoundRectangle2D.Float round = new RoundRectangle2D.Float(matrixWidth/5f*2, matrixHeigh/5f*2, matrixWidth/5f, matrixHeigh/5f,20f,20f);
        g2.setColor(Color.white);
        g2.draw(round);// 绘制圆弧矩形

        //设置logo 有一道灰色边框
        BasicStroke stroke2 = new BasicStroke(1,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke2);// 设置笔画对象
        RoundRectangle2D.Float round2 = new RoundRectangle2D.Float(matrixWidth/5*2+2, matrixHeigh/5*2+2, matrixWidth/5-4, matrixHeigh/5-4,20,20);
        g2.setColor(new Color(128,128,128));
        g2.draw(round2);// 绘制圆弧矩形

        g2.dispose();
        matrixImage.flush() ;
        return matrixImage ;
    }
}  