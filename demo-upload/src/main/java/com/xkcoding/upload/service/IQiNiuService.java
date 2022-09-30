package com.xkcoding.upload.service;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;

import java.io.File;

/**
 * <p>
 * Seven Cow Cloud Upload Service
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-06 17:21
 */
public interface IQiNiuService {
    /**
     * Seven Cow Cloud upload files
     *
     * @param file file
     * @return Seven Bulls upload Response
     * @throws QiniuException Seven Bulls Exception
     */
    Response uploadFile(File file) throws QiniuException;
}
