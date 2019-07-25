package com.orange.note.h5cache.util;

import android.util.Log;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author maomao
 * @date 2019/7/10
 */
public class ZipUtils {

    private static final String TAG = "ZipUtils";
    private static final String UTF_8 = "UTF-8";

    private ZipUtils() {
        //no instance
    }

    /**
     * 解压缩
     * 将zipFile文件解压到folderPath目录下.
     *
     * @param file       zip文件
     * @param folderPath 解压到的地址
     * @throws IOException
     */
    public static void upZipFile(File file, String folderPath) throws IOException {
        ZipFile zipFile = new ZipFile(file);
        Enumeration zList = zipFile.entries();
        ZipEntry ze;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                String dirStr = folderPath + ze.getName();
                dirStr = new String(dirStr.getBytes(UTF_8));
                File f = new File(dirStr);
                if (!f.exists()) {
                    f.mkdirs();
                }
                continue;
            }
            OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
            InputStream is = new BufferedInputStream(zipFile.getInputStream(ze));
            int readLen;
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zipFile.close();
    }

    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     *
     * @param baseDir     指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    private static File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                try {
                    substr = new String(substr.getBytes(UTF_8));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ret = new File(ret, substr);

            }
            Log.d(TAG, "1ret = " + ret);
            if (!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length - 1];
            try {
                substr = new String(substr.getBytes(UTF_8));
                Log.d(TAG, "substr = " + substr);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ret = new File(ret, substr);
            Log.d(TAG, "2ret = " + ret);
            return ret;
        }
        return ret;
    }

}
