package com.example.sdk.hikvision.bo;

import lombok.Data;

/**
 * SDK 执行结果
 */
@Data
public class SdkActionResult {


    private static final int ERROR = -1;

    /**
     * 执行结果：true-成功
     */
    private boolean success;

    /**
     * 返回值
     */
    private int retValue;

    /**
     * 错误码
     */
    private int errorCode;


}
