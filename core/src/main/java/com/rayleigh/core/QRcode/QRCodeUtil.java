package com.rayleigh.core.QRcode;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import com.google.zxing.BarcodeFormat;  
import com.google.zxing.EncodeHintType;  
import com.google.zxing.MultiFormatWriter;  
import com.google.zxing.WriterException;  
import com.google.zxing.common.BitMatrix;  
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;  

public class QRCodeUtil {

    public static void generateQRCodeImageFile(String contents,Integer width,Integer height,String imageType,File destFile,File logoFile) throws IOException, WriterException{
        if(null==width||null==height){
            width = 300;
            height = 300;
        }
        if(null==imageType){
            imageType = "gif";//二维码的图片格式
        }
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        // 指定纠错等级,纠错级别（L 7%、M 15%、Q 25%、H 30%）
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 内容所使用字符集编码
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
//      hints.put(EncodeHintType.MAX_SIZE, 350);//设置图片的最大值
//      hints.put(EncodeHintType.MIN_SIZE, 100);//设置图片的最小值
        hints.put(EncodeHintType.MARGIN, 1);//设置二维码边的边距，非负数
        BitMatrix bitMatrix = new MultiFormatWriter().encode(contents,BarcodeFormat.QR_CODE,width,height,hints);//生成条形码时的一些配置,此项可选
        MatrixToImageWriter.writeToFile(bitMatrix, imageType, destFile,logoFile);
    }

    public static void generateQRCodeImageStream(String contents, Integer width, Integer height, String imageType, OutputStream outputStream, File logoFile) throws IOException, WriterException{
        if(null==width||null==height){
            width = 300;
            height = 300;
        }
        if(null==imageType){
            imageType = "gif";//二维码的图片格式
        }
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(contents,BarcodeFormat.QR_CODE,width,height,hints);
        MatrixToImageWriter.writeToStream(bitMatrix, imageType, outputStream,logoFile);
    }

    private void testEncodeQRCode() throws IOException, WriterException{
        String contents = "http://www.baidu.com"; // 二维码内容
        int width = 400; // 二维码图片宽度 300
        int height = 400; // 二维码图片高度300

        String format = "gif";// 二维码的图片格式 gif

        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        // 指定纠错等级,纠错级别（L 7%、M 15%、Q 25%、H 30%）
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 内容所使用字符集编码
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
//      hints.put(EncodeHintType.MAX_SIZE, 350);//设置图片的最大值
//      hints.put(EncodeHintType.MIN_SIZE, 100);//设置图片的最小值
        hints.put(EncodeHintType.MARGIN, 1);//设置二维码边的边距，非负数

        BitMatrix bitMatrix = new MultiFormatWriter().encode(contents,//要编码的内容
                //编码类型，目前zxing支持：Aztec 2D,CODABAR 1D format,Code 39 1D,Code 93 1D ,Code 128 1D,
                //Data Matrix 2D , EAN-8 1D,EAN-13 1D,ITF (Interleaved Two of Five) 1D,
                //MaxiCode 2D barcode,PDF417,QR Code 2D,RSS 14,RSS EXPANDED,UPC-A 1D,UPC-E 1D,UPC/EAN extension,UPC_EAN_EXTENSION
                BarcodeFormat.QR_CODE,
                width, //条形码的宽度
                height, //条形码的高度
                hints);//生成条形码时的一些配置,此项可选
        // 生成二维码
        File outputFile = new File("d:" + File.separator + "new-1.gif");//指定输出路径

        MatrixToImageWriter.writeToFile(bitMatrix, format, outputFile,new File("d:"+File.separator+"logo.jpg"));
    }

    public static void main(String[] args) throws Exception {  
        try {
            QRCodeUtil qrCodeUtil = new QRCodeUtil();
            qrCodeUtil.testEncodeQRCode();
        } catch (Exception e) {
            e.printStackTrace();  
        }  
    }  
      
}  