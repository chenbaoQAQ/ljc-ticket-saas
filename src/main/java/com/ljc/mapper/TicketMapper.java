package com.ljc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljc.entity.Ticket;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 告诉 Spring 这是一个数据库操作接口 🔌
public interface TicketMapper extends BaseMapper<Ticket> {
    // 继承了 BaseMapper，你就自动拥有了增删改查的能力，不需要自己写 SQL！
}