package com.ljc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljc.entity.Company;
import com.ljc.mapper.CompanyMapper;
import com.ljc.service.CompanyService;
import org.springframework.stereotype.Service;

@Service
public class CompanyServiceImpl
        extends ServiceImpl<CompanyMapper, Company>
        implements CompanyService {
}
