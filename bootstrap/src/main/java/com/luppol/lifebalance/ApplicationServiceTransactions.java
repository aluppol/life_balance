package com.luppol.lifebalance;

import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

public class ApplicationServiceTransactions implements TransactionAttributeSource {
    private static final String APPLICATION_PACKAGE = "com.luppol.lifebalance.application";
    private static final TransactionAttribute READ_WRITE = new RuleBasedTransactionAttribute();
    private static final TransactionAttribute READ_ONLY = readOnly();

    @Override
    public TransactionAttribute getTransactionAttribute(Method method, Class<?> targetClass) {
        if (targetClass == null) {
            return null;
        }
        Class<?> serviceClass = ClassUtils.getUserClass(targetClass);
        if (!serviceClass.getPackageName().startsWith(APPLICATION_PACKAGE)) {
            return null;
        }
        return attributeFor(serviceClass.getSimpleName());
    }

    private static TransactionAttribute attributeFor(String serviceName) {
        if (serviceName.endsWith("CommandService")) {
            return READ_WRITE;
        }
        return serviceName.endsWith("QueryService") ? READ_ONLY : null;
    }

    private static TransactionAttribute readOnly() {
        RuleBasedTransactionAttribute attribute = new RuleBasedTransactionAttribute();
        attribute.setReadOnly(true);
        return attribute;
    }
}
