package com.vale.valechat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName(value ="chat_group")      // Has changed to Channel
@Data
public class ChatGroup implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long groupId;

    private String groupName;

    private Long masterId;

    private Date createTime;

    private Integer status;
}