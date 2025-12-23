package com.ljc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljc.dto.TicketCreateReq;
import com.ljc.entity.Ticket;
import com.ljc.mapper.TicketMapper;
import com.ljc.service.TicketService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class TicketServiceImpl extends ServiceImpl<TicketMapper, Ticket> implements TicketService {

    @Override
    public Long createTicket(TicketCreateReq req) {
        if (req == null) throw new IllegalArgumentException("请求体不能为空");
        if (req.getWorkOrderId() == null) throw new IllegalArgumentException("workOrderId 不能为空");
        if (!StringUtils.hasText(req.getDescription())) throw new IllegalArgumentException("description 不能为空");

        Ticket t = new Ticket();
        t.setWorkOrderId(req.getWorkOrderId());
        t.setDescription(req.getDescription().trim());
        t.setCreateTime(LocalDateTime.now());

        this.save(t);
        return t.getId();
    }
}
