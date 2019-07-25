package com.orange.note.h5cache.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author maomao
 * @date 2019-07-12
 */
public class FileUtil {

    public static File saveFile(InputStream is, String path) {
        if (is != null) {
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            byte[] buf = new byte[2048];
            int len;
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
                fos.flush();
                return file;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        return null;
    }

}
