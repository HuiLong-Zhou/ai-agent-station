package com.zhl.types.exception;

import java.io.Serial;

/**
 * MCP 身份验证或授权失败异常。
 */
public class McpSecurityException extends RuntimeException {

    /**
     * 序列化版本号。
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 安全异常错误码。
     */
    private final String code;

    /**
     * 创建 MCP 安全异常。
     *
     * @param code    错误码
     * @param message 错误说明
     */
    public McpSecurityException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 获取安全异常错误码。
     *
     * @return 错误码
     */
    public String getCode() {
        return code;
    }
}
