package com.orange.note.h5cache.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * @author maomao
 * @date 2019-07-12
 */
public class MD5Util {

    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F'};


    private static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte b1 : b) {
            sb.append(HEX_DIGITS[(b1 & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b1 & 0x0f]);
        }
        return sb.toString();
    }

    public static String md5(File file) {
        if (file == null) {
            return null;
        }
        if (!file.isFile() || !file.exists()) {
            return null;
        }
        InputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            int numRead;
            while ((numRead = fis.read(buffer)) > 0) {
                md5.update(buffer, 0, numRead);
            }

            return toHexString(md5.digest());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
