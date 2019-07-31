package com.orange.note.h5cache.exception

/**
 * 如果某条静态资源文件下载失败，可能会报出这个异常
 * @author maomao
 * @date 2019-07-30
 */
class DownloadErrorException(errorMessage: String?) : RuntimeException(errorMessage)