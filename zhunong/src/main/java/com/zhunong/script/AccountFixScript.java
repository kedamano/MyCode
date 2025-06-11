package com.zhunong.script;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhunong.entity.Account;
import com.zhunong.entity.User;
import com.zhunong.mapper.AccountMapper;
import com.zhunong.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 账户修复脚本 - 为所有现有且已激活的农户创建账户
 */
@Component
public class AccountFixScript implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AccountFixScript.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public void run(String... args) throws Exception {
        logger.info("开始执行账户修复脚本...");
        
        try {
            // 获取所有已激活的农户
            List<User> farmers = userService.list(new LambdaQueryWrapper<User>()
                    .eq(User::getRole, "FARMER")
                    .eq(User::getStatus, 1));
            
            logger.info("找到 {} 个已激活农户", farmers.size());
            
            int created = 0;
            
            // 为每个农户检查并创建账户
            for (User farmer : farmers) {
                // 检查是否已有账户
                Account existingAccount = accountMapper.selectOne(
                        new LambdaQueryWrapper<Account>().eq(Account::getUserId, farmer.getId())
                );
                
                if (existingAccount == null) {
                    // 创建新账户
                    Account account = new Account();
                    account.setUserId(farmer.getId());
                    account.setUsername(farmer.getUsername());
                    account.setBalance(BigDecimal.ZERO);
                    accountMapper.insert(account);
                    
                    created++;
                    logger.info("为农户 {} (ID: {}) 创建了账户", farmer.getUsername(), farmer.getId());
                }
            }
            
            logger.info("账户修复完成: 总共为 {} 个农户创建了账户", created);
        } catch (Exception e) {
            logger.error("账户修复脚本执行失败", e);
        }
    }
} 