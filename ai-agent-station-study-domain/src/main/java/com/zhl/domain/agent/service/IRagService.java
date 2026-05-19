package com.zhl.domain.agent.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *  知识库接口服务
 * @author zhl
 * @since 2026-05-20 1:04
 */
public interface IRagService {

    void storeRagFile(String name, String tag, List<MultipartFile> files);

}
