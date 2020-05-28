package com.cgcg.context.util;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 文件上传model
 * @author xujinbang
 * @date 2019-10-10
 */
@Setter
@Getter
public class UploadFileModel {

    /**
     * 处理人
     */
    private String sysUserName;

    /**
     * 处理时间
     */
    private Date createTime;

    /**
     * 文件路径
     */
    private String resPath;

    /**
     * 文件原名称
     */
    private String oldName;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件格式
     */
    private String fileFormat;

    /**
     * 文件大小
     */
    private BigDecimal fileSize;

    /**
     * 错误信息
     */
    private String errorMsg;

}
