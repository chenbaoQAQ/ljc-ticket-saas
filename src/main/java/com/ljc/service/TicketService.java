package com.ljc.service;

import com.ljc.entity.Ticket;
import com.ljc.mapper.TicketMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    private TicketMapper ticketMapper; // 注入你的数据库“操作员” 💉

    public List<Ticket> getAllTickets() {
        // 使用 MyBatis-Plus 提供的查询方法，查询所有记录
        return ticketMapper.selectList(null);
    }
}