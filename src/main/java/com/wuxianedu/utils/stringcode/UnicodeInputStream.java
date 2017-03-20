package com.wuxianedu.utils.stringcode;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;


public class UnicodeInputStream extends InputStream {
    PushbackInputStream internalIn;    
    boolean isInited = false;    
    String defaultEnc;    
    String encoding;   
    private byte[]inputStreamBomBytes;
    
    private static final int BOM_SIZE = 4;    
    
    public UnicodeInputStream(InputStream in) {
        internalIn = new PushbackInputStream(in, BOM_SIZE);    
        this.defaultEnc = "GBK";//这里假如默认字符集是GBK 
        try {    
                init();    
            } catch (IOException ex) {    
                IllegalStateException ise = new IllegalStateException(    
                        "Init method failed.");    
                ise.initCause(ise);    
                throw ise;    
            }    
    }
    
    public UnicodeInputStream(InputStream in, String defaultEnc) {    
        internalIn = new PushbackInputStream(in, BOM_SIZE);    
        this.defaultEnc = defaultEnc;    
    }    
    
    public String getDefaultEncoding() {    
        return defaultEnc;    
    }    
    
    public String getEncoding() {  
        return encoding;    
    }    
    
    /**  
     * Read-ahead four bytes and check for BOM marks. Extra bytes are unread  
     * back to the stream, only BOM bytes are skipped.  
     */    
    protected void init() throws IOException {    
        if (isInited)    
            return;    
    
        byte bom[] = new byte[BOM_SIZE];    
        int n, unread;    
        n = internalIn.read(bom, 0, bom.length);
        inputStreamBomBytes = bom;
    
        if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00)    
                && (bom[2] == (byte) 0xFE) && (bom[3] == (byte) 0xFF)) {    
            encoding = "UTF-32BE";    
            unread = n - 4;    
        } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)    
                && (bom[2] == (byte) 0x00) && (bom[3] == (byte) 0x00)) {    
            encoding = "UTF-32LE";    
            unread = n - 4;    
        } else if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB)    
                && (bom[2] == (byte) 0xBF)) {    
            encoding = "UTF-8";    
            unread = n - 3;    
        } else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {    
            encoding = "UTF-16BE";    
            unread = n - 2;    
        } else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {    
            encoding = "UTF-16LE";    
            unread = n - 2;    
        } else {//没有捕获到的字符集
            //encoding = defaultEnc; //这里暂时不用默认字符集   
            unread = n;    
            //inputStreamBomBytes = new byte[0];
        }    
        // System.out.println("read=" + n + ", unread=" + unread);    
    
        if (unread > 0)    
            internalIn.unread(bom, (n - unread), unread);    
    
        isInited = true;    
    }    
    
    public byte[] getInputStreamBomBytes() {
        return inputStreamBomBytes;
    }


    public void close() throws IOException {    
        isInited = true;    
        internalIn.close();    
    }    
    
    public int read() throws IOException {    
        isInited = true;    
        return internalIn.read();    
    }
}