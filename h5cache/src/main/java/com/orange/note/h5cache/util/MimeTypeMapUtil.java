package com.orange.note.h5cache.util;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.util.HashMap;
import java.util.Map;

/**
 * @author maomao
 * @date 2019-07-12
 */
public class MimeTypeMapUtil {

    private static Map<String, String> internalMimeMap = new HashMap<String, String>() {
        {
            put("js", "application/javascript");
        }
    };

    private static String getFileExtensionFromUrl(String url) {
        url = url.toLowerCase();
        if (!TextUtils.isEmpty(url)) {
            int fragment = url.lastIndexOf('#');
            if (fragment > 0) {
                url = url.substring(0, fragment);
            }

            int query = url.lastIndexOf('?');
            if (query > 0) {
                url = url.substring(0, query);
            }

            int filenamePos = url.lastIndexOf('/');
            String filename =
                    0 <= filenamePos ? url.substring(filenamePos + 1) : url;

            // if the filename contains special characters, we don't
            // consider it valid for our matching purposes:
            if (!filename.isEmpty()) {
                int dotPos = filename.lastIndexOf('.');
                if (0 <= dotPos) {
                    return filename.substring(dotPos + 1);
                }
            }
        }

        return "";
    }

    public static String getMimeTypeFromUrl(String url) {
        return getMimeTypeFromExtension(getFileExtensionFromUrl(url));
    }

    private static String getMimeTypeFromExtension(String extension) {
        if (internalMimeMap.containsKey(extension)) {
            return internalMimeMap.get(extension);
        }
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
}
