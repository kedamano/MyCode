package com.zhunong.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.MessageDigest;
import java.math.BigInteger;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 使用自定义的MD5密码编码器
        return new CustomMd5PasswordEncoder();
    }
    
    // 自定义MD5密码编码器，使用原生MD5实现
    public static class CustomMd5PasswordEncoder implements PasswordEncoder {
        private static final Logger logger = LoggerFactory.getLogger(CustomMd5PasswordEncoder.class);
        
        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            if (rawPassword == null) {
                logger.warn("原始密码为空");
                return false;
            }
            
            String encoded = encode(rawPassword);
            boolean matches = encoded.equals(encodedPassword);
            
            logger.debug("密码匹配: 原始密码={}, 编码后={}, 存储密码={}, 匹配结果={}", 
                    rawPassword, encoded, encodedPassword, matches);
            
            return matches;
        }
        
        @Override
        public String encode(CharSequence rawPassword) {
            if (rawPassword == null) {
                return null;
            }
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(rawPassword.toString().getBytes());
                byte[] digest = md.digest();
                BigInteger no = new BigInteger(1, digest);
                String hashtext = no.toString(16);
                while (hashtext.length() < 32) {
                    hashtext = "0" + hashtext;
                }
                return hashtext;
            } catch (Exception e) {
                logger.error("MD5加密失败", e);
                return null;
            }
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/css/**", "/js/**", "/images/**", "/fonts/**", "/favicon.ico").permitAll()
                .antMatchers("/user/register", "/user/register/**").permitAll()
                .antMatchers("/debug/**").permitAll()
                .antMatchers("/cart/debug/**").permitAll()
                .antMatchers("/test-login/**").permitAll()
                .antMatchers("/direct-login/**").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/employee/**").hasRole("EMPLOYEE")
                .antMatchers("/farmer/**").hasRole("FARMER")
                .anyRequest().authenticated()
            .and()
            .formLogin()
                .loginPage("/user/login")
                .loginProcessingUrl("/user/login")
                .defaultSuccessUrl("/")
                .failureUrl("/user/login?error=true")
                .permitAll()
            .and()
            .logout()
                .logoutSuccessUrl("/user/login?logout=true")
                .permitAll()
            .and()
            .csrf().disable();
            
        logger.info("Spring Security配置已加载");
    }
} 