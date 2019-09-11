package tesseract.core.executor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import tesseract.core.annotation.ClientJobDetail;
import tesseract.core.context.ExecutorContext;
import tesseract.core.dto.TesseractAdminJobNotify;
import tesseract.core.dto.TesseractExecutorRequest;
import tesseract.core.dto.TesseractExecutorResponse;
import tesseract.core.executor.thread.HangThread;
import tesseract.core.executor.thread.HeartbeatThread;
import tesseract.core.executor.thread.RegistryThread;
import tesseract.core.handler.JobHandler;
import tesseract.core.serializer.ISerializerService;
import tesseract.service.IClientService;
import tesseract.service.netty.NettyHttpClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static tesseract.core.constant.CommonConstant.NOTIFY_MAPPING;


@Slf4j
public class TesseractExecutor {
    @Autowired
    private IClientService clientFeignService;
    @Value("${tesseract.admin.address}")
    private String adminServerAddress;
    @Autowired(required = false)
    private List<ClientJobDetail> clientJobDetailList;
    @Autowired
    private ISerializerService serializerService;
    /**
     * 线程池
     */
    private final String THREAD_NAME_FORMATTER = "TesseractExecutorThread-%d";
    private final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);
    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10,
            800, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000)
            , r -> new Thread(r, String.format(THREAD_NAME_FORMATTER, ATOMIC_INTEGER.getAndIncrement())));

    /***********************************************threads*****************************************/
    //注册线程
    public static RegistryThread registryThread;
    /**
     * 心跳线程
     */
    public static HeartbeatThread heartbeatThread;
    /**
     * 使用netty 需要一个线程来hold住jvm
     */
    public static HangThread hangThread;

    /**
     * 开始执行任务，扔到线程池后发送成功执行通知，执行完毕后发送异步执行成功通知
     *
     * @param tesseractExecutorRequest
     * @return
     */
    public TesseractExecutorResponse execute(TesseractExecutorRequest tesseractExecutorRequest) {
        threadPoolExecutor.execute(new WorkRunnable(tesseractExecutorRequest, clientFeignService, this.adminServerAddress));
        return TesseractExecutorResponse.builder().status(TesseractExecutorResponse.SUCCESS_STATUS).body("成功进入队列").build();
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
        if (!CollectionUtils.isEmpty(clientJobDetailList)) {
            heartbeatThread = new HeartbeatThread(clientFeignService, adminServerAddress);
            registryThread = new RegistryThread(clientFeignService, clientJobDetailList, adminServerAddress);
            hangThread = new HangThread();
            heartbeatThread.setDaemon(true);
            registryThread.setDaemon(true);
            heartbeatThread.setTesseractExecutor(this);
            heartbeatThread.setRegistryThread(registryThread);
            registryThread.setHeartbeatThread(heartbeatThread);
            registryThread.startThread();
            heartbeatThread.startThread();
            hangThread.startThread();
        }
        //初始化序列化
        NettyHttpClient.serializerService = this.serializerService;
    }

    public void destroy() {
        if (registryThread != null) {
            registryThread.stopThread();
        }
        if (heartbeatThread != null) {
            heartbeatThread.stopThread();
        }
        if (hangThread != null) {
            hangThread.stopThread();
        }
        NettyHttpClient.close();
    }

//    /**
//     * 默认取本地回环地址
//     *
//     * @return
//     */
//    private String getValidIP() {
//        if (!StringUtils.isEmpty(ip)) {
//            return this.ip;
//        }
//        Enumeration<NetworkInterface> netInterfaces;
//        try {
//            // 拿到所有网卡
//            netInterfaces = NetworkInterface.getNetworkInterfaces();
//            InetAddress ip;
//            // 遍历每个网卡，拿到ip
//            while (netInterfaces.hasMoreElements()) {
//                NetworkInterface ni = netInterfaces.nextElement();
//                Enumeration<InetAddress> addresses = ni.getInetAddresses();
//                while (addresses.hasMoreElements()) {
//                    ip = addresses.nextElement();
//                    if (ip.isLoopbackAddress() && ip.getHostAddress().indexOf(':') == -1) {
//                        return ip.getHostAddress();
//                    }
//                }
//            }
//        } catch (Exception e) {
//        }
//        throw new TesseractException("找不到网卡");
//    }


    @Data
    @AllArgsConstructor
    private class WorkRunnable implements Runnable {
        private TesseractExecutorRequest tesseractExecutorRequest;
        private IClientService clientFeignService;
        private String adminServerAddress;

        @Override
        public void run() {
            String className = tesseractExecutorRequest.getClassName();
            TesseractAdminJobNotify tesseractAdminJobNotify = new TesseractAdminJobNotify();
            tesseractAdminJobNotify.setLogId(tesseractExecutorRequest.getLogId());
            tesseractAdminJobNotify.setExecutorDetailId(tesseractExecutorRequest.getExecutorDetailId());
            try {
                Class<?> aClass = Class.forName(className);
                JobHandler jobHandler = (JobHandler) aClass.newInstance();
                ExecutorContext executorContext = new ExecutorContext();
                executorContext.setShardingIndex(tesseractExecutorRequest.getShardingIndex());
                jobHandler.execute(executorContext);
                clientFeignService.notify(new URI(adminServerAddress + NOTIFY_MAPPING), tesseractAdminJobNotify);
            } catch (Exception e) {
                log.error("执行异常:{}", e.getMessage());
                tesseractAdminJobNotify.setException(e.getMessage());
                try {
                    clientFeignService.notify(new URI(adminServerAddress + NOTIFY_MAPPING), tesseractAdminJobNotify);
                } catch (URISyntaxException ex) {
                    log.error("执行异常URI异常:{}", e.getMessage());
                } catch (InterruptedException ex) {
                    log.error("中断异常");
                }
            }
        }
    }

}
