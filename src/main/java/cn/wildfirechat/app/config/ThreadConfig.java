package cn.wildfirechat.app.config;

import cn.wildfirechat.app.tools.ThreadPoolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 线程池工具实例
 */
@Configuration
public class ThreadConfig {

	private static final Logger LOG = LoggerFactory.getLogger(ThreadConfig.class);

	@Bean
	public ThreadPoolUtils getThreadPoolUtils() {
		int processors = Runtime.getRuntime().availableProcessors();
		LOG.info("系统JVM可用核心数：" + processors);
		return new ThreadPoolUtils(ThreadPoolUtils.ThreadEnum.MAXED, processors * 2, processors * 4);
	}

}
