package com.xkcoding.upload.service.impl;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.xkcoding.upload.service.IQiNiuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * <p>
 * Seven Cow Cloud Upload Service
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2018-11-06 17:22
 */
@Service
@Slf4j
public class QiNiuServiceImpl implements IQiNiuService, InitializingBean {
    private final UploadManager uploadManager;

    private final Auth auth;

    @Value("${qiniu.bucket}")
    private String bucket;

    private StringMap putPolicy;

    @Autowired
    public QiNiuServiceImpl(UploadManager uploadManager, Auth auth) {
        this.uploadManager = uploadManager;
        this.auth = auth;
    }

    /**
     * Seven Cow Cloud upload files
     *
     * @param file file
     * @return Seven Bulls upload Response
     * @throws QiniuException Seven Bulls Exception
     */
    @Override
    public Response uploadFile(File file) throws QiniuException {
        Response response = this.uploadManager.put(file, file.getName(), getUploadToken());
        int retry = 0;
        while (response.needRetry() && retry < 3) {
            response = this.uploadManager.put(file, file.getName(), getUploadToken());
            retry++;
        }
        return response;
    }

    @Override
    public void afterPropertiesSet() {
        this.putPolicy = new StringMap();
        putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"width\":$(imageInfo.width), \"height\":${imageInfo.height}}");
    }

    /**
     * Get upload credentials
     *
     * @return Upload credentials
     */
    private String getUploadToken() {
        return this.auth.uploadToken(bucket, null, 3600, putPolicy);
    }
}
