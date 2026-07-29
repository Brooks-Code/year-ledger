package com.brooks.mall.user.service;

import com.brooks.mall.user.dto.ExpenseRecordDTO;
import com.brooks.mall.user.entity.ExpenseRecord;
import com.brooks.mall.user.mapper.ExpenseMapper;
import com.brooks.mall.user.service.impl.ExpenseService;
import com.brooks.mall.user.util.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TODO
 *
 * @Author mr.yang
 * @Date 2026/7/29 13:25
 */
@Service
public class ExpenseServiceImpl implements ExpenseService {
    @Autowired
    private ExpenseMapper expenseMapper;
    /**
     * 新增账单记录
     *
     * @param record
     * @return
     */
    @Override
    public boolean save(ExpenseRecordDTO record) {
        // 1. 实例化数据库实体对象
        ExpenseRecord entity = new ExpenseRecord();
        long id = new SnowflakeIdGenerator(1, 1).nextId();
        entity.setId(id);
        //entity.setCreatedAt(LocalDateTime.now());
        //TODO 先写死，后面再改
        entity.setCreatedBy("Brooks Cole");
        // 2. 将请求参数赋值给实体 (这里演示手动赋值，也可以用 BeanUtils)
        entity.setAmount(record.getAmount());
        entity.setCategory(record.getCategory());
        entity.setExpenseDate(record.getExpenseDate());
        entity.setPaymentMethod(record.getPaymentMethod());
        entity.setRemark(record.getRemark());
        // 调用 DAO 层保存到数据库
        int rows = expenseMapper.insert(entity);
        // 根据受影响行数判断是否成功
        return rows > 0;
    }
}
