package tesseract.core.executor;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import tesseract.Constant;
import tesseract.config.TesseractConfiguration;
import tesseract.core.annotation.ClientJobDetail;
import tesseract.core.context.ExecutorContext;
import tesseract.core.dto.TesseractAdminJobNotify;
import tesseract.core.dto.TesseractExecutorRequest;
import tesseract.core.dto.TesseractExecutorResponse;
import tesseract.core.dto.TesseractStopTaskRequest;
import tesseract.core.executor.netty.server.NettyClientCommandDispatcher;
import tesseract.core.executor.service.IClientService;
import tesseract.core.executor.thread.HeartbeatThread;
import tesseract.core.executor.thread.RegistryThread;
import tesseract.core.handler.JobHandler;
import tesseract.core.netty.NettyHttpClient;
import tesseract.core.netty.NettyHttpServer;
import tesseract.core.serializer.ISerializerService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static tesseract.core.constant.CommonConstant.NOTIFY_MAPPING;

/**
 * 任务执行器，采用线程池执行
 *
 * @author nickle
 */
@Slf4j
public class TesseractExecutor {
    private IClientService clientFeignService;
    private String adminServerAddress;
    private List<ClientJobDetail> clientJobDetailList;
    private ISerializerService serializerService;
    private Integer nettyServerPort;
    private Integer corePoolSize;
    private Integer maxPoolSize;
    private Integer queueSize;

    public TesseractExecutor(TesseractConfiguration tesseractConfiguration) {
        log.info("使用configuration: {}", tesseractConfiguration);
        this.clientFeignService = tesseractConfiguration.getClientFeignService();
        this.adminServerAddress = tesseractConfiguration.getAdminServerAddress();
        this.clientJobDetailList = tesseractConfiguration.getClientJobDetailList();
        this.serializerService = tesseractConfiguration.getSerializerService();
        this.nettyServerPort = tesseractConfiguration.getNettyServerPort();

        this.corePoolSize = tesseractConfiguration.getCorePoolSize();
        this.maxPoolSize = tesseractConfiguration.getMaxPoolSize();
        this.queueSize = tesseractConfiguration.getQueueSize();

        threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize
                , 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(queueSize)
                , r -> new Thread(r, String.format(THREAD_NAME_FORMATTER, ATOMIC_INTEGER.getAndIncrement())));
    }

    /**
     * 线程池
     */
    private final String THREAD_NAME_FORMATTER = "TesseractExecutorThread-%d";
    private final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);
    private final ThreadPoolExecutor threadPoolExecutor;

    /**
     * 注册线程
     */
    public static RegistryThread registryThread;

    /**
     * 心跳线程
     */
    public static HeartbeatThread heartbeatThread;

    public static TesseractExecutor tesseractExecutor;

    /**
     * 保存任务和线程关联，用于停止任务
     */
    private final Map<Integer, Thread> TASK_THREAD_MAP = Maps.newConcurrentMap();

    /**
     * 开始执行任务，扔到线程池后发送成功执行通知，执行完毕后发送异步执行成功通知
     *
     * @param tesseractExecutorRequest
     * @return
     */
    public TesseractExecutorResponse execute(TesseractExecutorRequest tesseractExecutorRequest) {
        TesseractExecutorResponse executorResponse = TesseractExecutorResponse.builder().status(TesseractExecutorResponse.SUCCESS_STATUS)
                .msg("成功进入队列").body(tesseractExecutorRequest.getFireJobId()).build();
        try {
            threadPoolExecutor.execute(new WorkRunnable(tesseractExecutorRequest, clientFeignService, this.adminServerAddress));
        } catch (RejectedExecutionException e) {
            String msg = "执行队列已满";
            log.error(msg);
            executorResponse.setMsg(msg);
            executorResponse.setStatus(TesseractExecutorResponse.FAIL_STATUS);
        }
        return executorResponse;
    }

    /**
     * 停止任务
     *
     * @return
     */
    public void stopTask(TesseractStopTaskRequest tesseractStopTaskRequest) {
        Thread thread = TASK_THREAD_MAP.get(tesseractStopTaskRequest);
        if (thread == null) {
            log.error("任务已结束");
            return;
        }
        thread.interrupt();
    }


    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    /**
     * 属性初始化完毕后开始注册
     *
     * @throws Exception
     */
    @SuppressWarnings("AlibabaAvoidManuallyCreateThread")
    public void init() {
        tesseractExecutor = this;
        initServiceDelegator();
        initNettyServer();
        initThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("调用shutdown 钩子");
            tesseractExecutor.destroy();
        }));
    }

    /**
     * 初始化Netty Sever
     */
    private void initNettyServer() {
        NettyHttpServer nettyHttpServer = new NettyHttpServer(nettyServerPort, new NettyClientCommandDispatcher());
        Thread thread = new Thread(() -> nettyHttpServer.startServer());
        thread.start();
    }

    /**
     * 初始化 注册线程和心跳线程
     */
    public void initThread() {
        if (!CollectionUtils.isEmpty(clientJobDetailList)) {
            heartbeatThread = new HeartbeatThread();
            registryThread = new RegistryThread();
            heartbeatThread.setDaemon(true);
            registryThread.setDaemon(true);
            heartbeatThread.setTesseractExecutor(this);
            heartbeatThread.setRegistryThread(registryThread);
            registryThread.setHeartbeatThread(heartbeatThread);
            registryThread.startThread();
            heartbeatThread.startThread();
        }
    }


    /**
     * 初始化服务代理
     */
    private void initServiceDelegator() {
        ClientServiceDelegator.adminServerAddress = adminServerAddress;
        ClientServiceDelegator.clientFeignService = clientFeignService;
        ClientServiceDelegator.clientJobDetailList = clientJobDetailList;
        ClientServiceDelegator.nettyServerPort = nettyServerPort;
        ClientServiceDelegator.serializerService = serializerService;
        ClientServiceDelegator.tesseractExecutor = this;
    }

    public void destroy() {
        if (registryThread != null) {
            registryThread.stopThread();
        }
        if (heartbeatThread != null) {
            heartbeatThread.stopThread();
        }
        NettyHttpClient nettyHttpClient = ClientServiceDelegator.getNettyHttpClient();
        if (nettyHttpClient != null) {
            nettyHttpClient.close();
        }
    }

    /**
     * 任务执行体
     *
     * @author nickle
     */
    @Data
    @AllArgsConstructor
    private class WorkRunnable implements Runnable {
        private TesseractExecutorRequest tesseractExecutorRequest;
        private IClientService clientFeignService;
        private String adminServerAddress;

        @Override
        public void run() {
            Integer fireJobId = tesseractExecutorRequest.getFireJobId();
            Long logId = tesseractExecutorRequest.getLogId();
            String className = tesseractExecutorRequest.getClassName();
            Integer shardingIndex = tesseractExecutorRequest.getShardingIndex();
            Object param = tesseractExecutorRequest.getParam();

            TASK_THREAD_MAP.put(fireJobId, Thread.currentThread());
            TesseractAdminJobNotify tesseractAdminJobNotify = new TesseractAdminJobNotify();
            tesseractAdminJobNotify.setLogId(logId);
            tesseractAdminJobNotify.setFireJobId(fireJobId);
            try {
                Class<?> aClass = Class.forName(className);
                JobHandler jobHandler = (JobHandler) aClass.newInstance();
                //构建执行上下文
                ExecutorContext executorContext = new ExecutorContext(shardingIndex, param);
                //开始执行任务
                jobHandler.execute(executorContext);
                //执行成功后通知调度端
                clientFeignService.notify(new URI(adminServerAddress + NOTIFY_MAPPING), tesseractAdminJobNotify);
            } catch (Exception e) {
                log.error("执行异常:{}", e.toString());
                tesseractAdminJobNotify.setException(e.toString());
                try {
                    clientFeignService.notify(new URI(adminServerAddress + NOTIFY_MAPPING), tesseractAdminJobNotify);
                } catch (URISyntaxException ex) {
                    log.error("执行异常URI异常:{}", e.getMessage());
                } catch (InterruptedException ex) {
                    log.error("中断异常");
                }
            } finally {
                TASK_THREAD_MAP.remove(fireJobId);
            }
        }
    }

}
