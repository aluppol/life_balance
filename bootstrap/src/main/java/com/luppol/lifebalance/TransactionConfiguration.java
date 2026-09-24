package com.luppol.lifebalance;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

@Configuration(proxyBeanMethods = false)
public class TransactionConfiguration {
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    static TransactionAttributeSource applicationServiceTransactionAttributes() {
        return new ApplicationServiceTransactions();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    static TransactionInterceptor applicationServiceTransactionInterceptor(
            @Qualifier("applicationServiceTransactionAttributes") TransactionAttributeSource attributes) {
        TransactionInterceptor interceptor = new TransactionInterceptor();
        interceptor.setTransactionAttributeSource(attributes);
        return interceptor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    static BeanFactoryTransactionAttributeSourceAdvisor applicationServiceTransactionAdvisor(
            @Qualifier("applicationServiceTransactionAttributes") TransactionAttributeSource attributes,
            @Qualifier("applicationServiceTransactionInterceptor") TransactionInterceptor interceptor) {
        BeanFactoryTransactionAttributeSourceAdvisor advisor = new BeanFactoryTransactionAttributeSourceAdvisor();
        advisor.setTransactionAttributeSource(attributes);
        advisor.setAdvice(interceptor);
        return advisor;
    }
}
