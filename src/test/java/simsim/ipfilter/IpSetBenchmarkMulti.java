package simsim.ipfilter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class IpSetBenchmarkMulti {
    public static void main(String[] args) throws InterruptedException {
        List<BenchResult> results = new ArrayList<>();
        List<String> ips = TestHelper.createIps(20, 20, 20, 20);
        int threadCount = 500;
        int iteration = 500;
        int sleepMin = 5;
        int sleepMax = 20;
        IpSet treeIpSet = IpSet.createTreeIpSet(ips);
        IpSet hashIpSet = IpSet.createHashIpSet(ips);
        IpSet simpleIpSet = IpSet.createSimpleIpSet(ips);

        Thread.sleep(10000);
        log("start run TreeIpSet");
        BenchResult result1 = run("TreeIpSet", treeIpSet, threadCount, iteration, sleepMin, sleepMax);
        log("finish run TreeIpSet");
        result1.put("ipCount", ips.size());
        results.add(result1);

        Thread.sleep(1000);

        log("start run HashIpSet");
        BenchResult result2 = run("HashIpSet", hashIpSet, threadCount, iteration, sleepMin, sleepMax);
        log("finish run HashIpSet");
        result2.put("ipCount", ips.size());
        results.add(result2);

        Thread.sleep(1000);

        log("start run SimpleIpSet");
        BenchResult result3 = run("SimpleIpSet", simpleIpSet, threadCount, iteration, sleepMin, sleepMax);
        log("finish run SimpleIpSet");
        result3.put("ipCount", ips.size());
        results.add(result3);

        Thread.sleep(1000);

        System.out.printf("| %15s | %10s | %8s | %8s | %8s | %8s |", "name", "ip count", "thread", "iter", "elapsed", "tps");
        System.out.println();
        results.forEach(r -> {
            System.out.printf("| %15s | %,10d | %,8d | %,8d | %,8d | %,8d |\n",
                    r.getName(), r.get("ipCount"),
                    r.get("thread"), r.get("iter"),
                    r.get("elapsed"), r.get("tps"));
        });
    }

    private static void log(String message) {
        System.out.println(LocalDateTime.now() + " : " + message);
    }

    private static BenchResult run(String name, IpSet ipSet,
                                   int threadCount, int iteration, int sleepMin, int sleepMax) {
        CountDownLatch latch = new CountDownLatch(threadCount);
        long start = System.currentTimeMillis();
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                sleep(sleepMin, sleepMax);
                try {
                    for (int iter = 0; iter < iteration; iter++) {
                        String ip = TestHelper.randomIp();
                        ipSet.contains(ip);
                        sleep(sleepMin, sleepMax);
                    }
                } finally {
                    latch.countDown();
                }
            }, "name" + i).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        long finish = System.currentTimeMillis();
        BenchResult result = new BenchResult(name);
        result.put("thread", threadCount);
        result.put("iter", iteration);
        long elapsed = finish - start;
        result.put("elapsed", elapsed);
        long tps = BigDecimal.valueOf(threadCount * iteration)
                .divide(BigDecimal.valueOf(elapsed), 20, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(1000L))
                .longValue();
        result.put("tps", tps);
        return result;
    }

    private static void sleep(int sleepMin, int sleepMax) {
        try {
            Thread.sleep(TestHelper.random(sleepMin, sleepMax));
        } catch (InterruptedException e) {
        }
    }
}
