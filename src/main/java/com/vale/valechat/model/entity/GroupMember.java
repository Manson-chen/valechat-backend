package com.vale.valechat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName(value ="group_member")
@Data
public class GroupMember {
    @TableId(type = IdType.AUTO)
    private Long Id;

    private Long userId;

    private Long groupId;

    private Long joinTime;

    private Integer status;
}
