package com.ljc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljc.entity.Ticket;
import com.ljc.mapper.TicketMapper;
import com.ljc.service.TicketService;
import org.springframework.stereotype.Service;

@Service
public class TicketServiceImpl extends ServiceImpl<TicketMapper, Ticket> implements TicketService {
}
