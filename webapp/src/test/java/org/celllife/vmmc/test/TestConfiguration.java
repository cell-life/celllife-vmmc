package org.celllife.vmmc.test;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration({
        "classpath:/META-INF/spring/spring-aop.xml",
        "classpath:/META-INF/spring/spring-application-test.xml",
        "classpath:/META-INF/spring/spring-cache.xml",
        "classpath:/META-INF/spring/spring-config-test.xml",
        "classpath:/META-INF/spring/spring-domain-test.xml",
        "classpath:/META-INF/spring/spring-dozer.xml",
        "classpath:/META-INF/spring/spring-jdbc-test.xml",
        "classpath:/META-INF/spring/spring-json.xml",
        "classpath:/META-INF/spring/spring-orm-test.xml",
        "classpath:/META-INF/spring/spring-smooks.xml",
        "classpath:/META-INF/spring/spring-task.xml",
        "classpath:/META-INF/spring-data/spring-data-jpa-test.xml",
        "classpath:/META-INF/spring-integration/spring-integration-core.xml"
})
//@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class TestConfiguration {

}
