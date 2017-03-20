package com.wuxianedu.utils.stringcode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;

public class ShowCode {


	/**
     * 判断文件的编码格式
     * @param fileName :file
     * @return 文件编码格式
     * @throws Exception
     */
    public static String codeString(String fileName) throws Exception{
        BufferedInputStream bin = new BufferedInputStream(
        new FileInputStream(fileName));
        int p = (bin.read() << 8) + bin.read();
        String code = null;
        System.out.println("P:"+p);
        //其中的 0xefbb、0xfffe、0xfeff、0x5c75这些都是这个文件的前面两个字节的16进制数
        switch (p) {
            case 0xefbb:
                code = "UTF-8";
                break;
            case -257:
            	 code = "UTF-8";
                 break;
            case 0xfffe:
                code = "Unicode";
                break;
            case 0xfeff:
                code = "UTF-16BE";
                break;
            case 0x5c75:
            	code = "ANSI|ASCII" ;
            	break ;
            default:
                code = "GBK";
        }
        bin.close();
        return code;
    }


    /** 
     * 利用第三方开源包cpdetector获取文件编码格式 
     *  
     * @param path 
     *            要判断文件编码格式的源文件的路径 
     * @author huanglei 
     * @version 2012-7-12 14:05 
     */  
    public static String getFileEncode(String path) {  
        /* 
         * detector是探测器，它把探测任务交给具体的探测实现类的实例完成。 
         * cpDetector内置了一些常用的探测实现类，这些探测实现类的实例可以通过add方法 加进来，如ParsingDetector、 
         * JChardetFacade、ASCIIDetector、UnicodeDetector。 
         * detector按照“谁最先返回非空的探测结果，就以该结果为准”的原则返回探测到的 
         * 字符集编码。使用需要用到三个第三方JAR包：antlr.jar、chardet.jar和cpdetector.jar 
         * cpDetector是基于统计学原理的，不保证完全正确。 
         */  
        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();  
        /* 
         * ParsingDetector可用于检查HTML、XML等文件或字符流的编码,构造方法中的参数用于 
         * 指示是否显示探测过程的详细信息，为false不显示。 
         */  
        detector.add(new ParsingDetector(false));  
        /* 
         * JChardetFacade封装了由Mozilla组织提供的JChardet，它可以完成大多数文件的编码 
         * 测定。所以，一般有了这个探测器就可满足大多数项目的要求，如果你还不放心，可以 
         * 再多加几个探测器，比如下面的ASCIIDetector、UnicodeDetector等。 
         */  
        detector.add(JChardetFacade.getInstance());// 用到antlr.jar、chardet.jar  
        // ASCIIDetector用于ASCII编码测定  
        detector.add(ASCIIDetector.getInstance());  
        // UnicodeDetector用于Unicode家族编码的测定  
        detector.add(UnicodeDetector.getInstance());  
        java.nio.charset.Charset charset = null;  
        File f = new File(path);  
        try {  
            charset = detector.detectCodepage(f.toURI().toURL());  
        } catch (Exception ex) {  
            ex.printStackTrace();  
        }  
        if (charset != null) { 
        	System.out.println("------------------------->");
        	System.out.println(charset.name());
        	System.out.println("------------------------->");
            return charset.name();  
        }
        else  
            return null;  
    }  
    
    @SuppressWarnings("resource")
	public static String getCharset(File file) {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(
                  new FileInputStream(file));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1)
                return charset;
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1]
                == (byte) 0xFF) {
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1]
                    == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8";
                checked = true;
            }
            bis.reset();
            if (!checked) {
                int loc = 0;
                while ((read = bis.read()) != -1) {
                    loc++;
                    if (read >= 0xF0)
                        break;
                    //单独出现BF以下的，也算是GBK
                    if (0x80 <= read && read <= 0xBF)
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF)// 双字节 (0xC0 - 0xDF)
                            // (0x80 -
                            // 0xBF),也可能在GB编码内
                            continue;
                        else
                            break;
                     // 也有可能出错，但是几率较小
                    } else if (0xE0 <= read && read <= 0xEF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
                System.out.println(loc + " " + Integer.toHexString(read));
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return charset;
    }
    
    public static String getFileStringByInputStream2(InputStream inputStream , boolean isCloseInputStream) 
            throws IOException {
   if(inputStream.available() < 2) return "";
   try {
        UnicodeInputStream in = new UnicodeInputStream(inputStream);
        String encoding = in.getEncoding();
        int available = inputStream.available();
        byte []bomBytes = in.getInputStreamBomBytes();
        int bomLength = bomBytes.length;
        byte []last = new byte[available + bomLength];
        System.arraycopy(bomBytes , 0 , last , 0 , bomLength);//将头部拷贝进去
        inputStream.read(last , bomBytes.length , available);//抛开头部位置开始读取
        String result = new String(last , encoding);
        if(encoding != null && encoding.startsWith("GB")) {
           return result;
        }else {
           return result.substring(1);
        }
    }finally {
        if(isCloseInputStream) inputStream.close();
    }
}
}
