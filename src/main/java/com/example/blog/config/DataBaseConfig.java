package com.example.blog.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import lombok.val;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.io.IOException;

/**
 * @author 言曌
 * @date 2020/4/11 5:41 下午
 */

@Configuration
public class DataBaseConfig {

    // 创建全局配置
    @Bean
    public GlobalConfig globalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
        // 默认为自增
        dbConfig.setIdType(IdType.AUTO);
        // 全局的表前缀策略配置
        dbConfig.setLogicDeleteValue("1");
        dbConfig.setLogicNotDeleteValue("0");
        globalConfig.setDbConfig(dbConfig);
        return globalConfig;
    }

    @Bean
    public MybatisSqlSessionFactoryBean sqlSessionFactory(DruidDataSource dataSource, GlobalConfig globalConfig) throws IOException {
        MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        //加载数据源
        sqlSessionFactoryBean.setDataSource(dataSource);
        //加载 mybatis-plus 全局属性
        sqlSessionFactoryBean.setGlobalConfig(globalConfig);
        //指定 pojo 目录
        sqlSessionFactoryBean.setTypeAliasesPackage("com.example.blog.entity");
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath*:/mapper/**Mapper.xml"));
        //mybatis-plus 插件(分页插件)
        Interceptor[] interceptors = {new PaginationInterceptor()};
        sqlSessionFactoryBean.setPlugins(interceptors);
        return sqlSessionFactoryBean;
    }

    @Bean
    public static MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setBasePackage("com.example.blog.mapper");
        // 设置为上面的 factory name
        configurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        return configurer;
    }

    //事务管理器
    @Bean
    public DataSourceTransactionManager transactionManager(DruidDataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
