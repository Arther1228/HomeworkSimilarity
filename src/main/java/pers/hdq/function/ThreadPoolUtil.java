package pers.hdq.function;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yangliangchuang 2024-01-11 13:13
 */
public class ThreadPoolUtil {

    /**
     * 本机CPU核数
     **/
    final static int CORE_NUM = Runtime.getRuntime().availableProcessors();

    /**
     * 文件读取线程池，核心线程数=2CPU核数，最大线程数2Cpu核数
     **/
    final static ExecutorService fileThreadPool = new ThreadPoolExecutor(2 * CORE_NUM, 2 * CORE_NUM, 10L,
            TimeUnit.SECONDS,
            new LinkedTransferQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("doc-ini-pool-%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 单线程线程池
     **/
    final static ExecutorService singleThreadPool = new ThreadPoolExecutor(1, 1, 10L, TimeUnit.SECONDS,
            new LinkedTransferQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("single-pool-%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 文档比较线程池，核心线程数=CPU核数，最大线程数Cpu核数
     **/
    final static ExecutorService compareThreadPool = new ThreadPoolExecutor(CORE_NUM, CORE_NUM, 10L, TimeUnit.SECONDS,
            new LinkedTransferQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("compare-pool-%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy());

}
