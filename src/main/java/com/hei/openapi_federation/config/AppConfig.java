package com.hei.openapi_federation.config;

import com.hei.openapi_federation.repository.CollectivityRepository;
import com.hei.openapi_federation.repository.MemberRepository;
import com.hei.openapi_federation.service.CollectivityService;
import com.hei.openapi_federation.service.CollectivityServiceImpl;
import com.hei.openapi_federation.service.MemberService;
import com.hei.openapi_federation.service.MemberServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class AppConfig {

    @Bean
    public Connection connection(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password
    ) throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    @Bean
    public MemberRepository memberRepository(Connection connection) {
        return new MemberRepository(connection);
    }

    @Bean
    public CollectivityRepository collectivityRepository(Connection connection) {
        return new CollectivityRepository(connection);
    }

    @Bean
    public MemberService memberService(MemberRepository memberRepository,
                                       CollectivityRepository collectivityRepository) {
        return new MemberServiceImpl(memberRepository, collectivityRepository);
    }

    @Bean
    public CollectivityService collectivityService(CollectivityRepository collectivityRepository,
                                                   MemberRepository memberRepository) {
        return new CollectivityServiceImpl(collectivityRepository, memberRepository);
    }
}