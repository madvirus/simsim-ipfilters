package simsim.ipfilter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;

public class IpSetBenchmarkIteration {

    public static void main(String[] args) {
        preload();
        List<BenchResult> results = new ArrayList<>();
        Consumer<BenchResult> collector = result -> {
            results.add(result);
            System.out.println(LocalDateTime.now() + " - collected " + result.getName());
        };
        run100(100_000, collector);
        run100(100_000, collector);
        run1K(100_000, collector);
        run10K(100_000, collector);
        run100K(100_000, collector);
        run1M(100_000, collector);

        System.out.printf("| %15s | %10s | %8s | %8s | %8s | %8s |", "name", "ip count", "iter", "job1", "job2", "job3");
        System.out.println();
        results.forEach(r -> {
            System.out.printf("| %15s | %,10d | %,8d | %,8d | %,8d | %,8d |\n",
                    r.getName(), r.get("ipCount"), r.get("iter"),
                    r.get("job1"), r.get("job2"), r.get("job3"));
        });
    }

    private static void preload() {
        runIps("pre", ipPatterns -> IpSet.createHashIpSet(ipPatterns), 5, 2, 5, 2, 5);
        runIps("pre", ipPatterns -> IpSet.createTreeIpSet(ipPatterns), 5, 2, 5, 2, 5);
        runIps("pre", ipPatterns -> IpSet.createSimpleIpSet(ipPatterns), 5, 2, 5, 2, 5);
    }

    private static void run100(int iter, Consumer<BenchResult> collector) {
        collector.accept(run100("TreeIpSet", ips -> IpSet.createTreeIpSet(ips), iter));
        collector.accept(run100("HashIpSet", ips -> IpSet.createHashIpSet(ips), iter));
        collector.accept(run100("SimpleIpSet", ips -> IpSet.createSimpleIpSet(ips), iter));
    }

    private static void run1K(int iter, Consumer<BenchResult> collector) {
        collector.accept(run1K("TreeIpSet", ips -> IpSet.createTreeIpSet(ips), iter));
        collector.accept(run1K("HashIpSet", ips -> IpSet.createHashIpSet(ips), iter));
        collector.accept(run1K("SimpleIpSet", ips -> IpSet.createSimpleIpSet(ips), iter));
    }

    private static void run10K(int iter, Consumer<BenchResult> collector) {
        collector.accept(run10K("TreeIpSet", ips -> IpSet.createTreeIpSet(ips), iter));
        collector.accept(run10K("HashIpSet", ips -> IpSet.createHashIpSet(ips), iter));
        collector.accept(run10K("SimpleIpSet", ips -> IpSet.createSimpleIpSet(ips), iter));
    }

    private static void run100K(int iter, Consumer<BenchResult> collector) {
        collector.accept(run100K("TreeIpSet", ips -> IpSet.createTreeIpSet(ips), iter));
        collector.accept(run100K("HashIpSet", ips -> IpSet.createHashIpSet(ips), iter));
    }

    private static void run1M(int iter, Consumer<BenchResult> collector) {
        collector.accept(run1M("TreeIpSet", ips -> IpSet.createTreeIpSet(ips), iter));
        collector.accept(run1M("HashIpSet", ips -> IpSet.createHashIpSet(ips), iter));
    }

    private static BenchResult run100(String name, Function<List<String>, IpSet> createSet, int iter) {
        return runIps(name, createSet, iter, 2, 5, 2, 5);
    }

    private static BenchResult run1K(String name, Function<List<String>, IpSet> createSet, int iter) {
        return runIps(name, createSet, iter, 2, 5, 10, 10);
    }

    private static BenchResult run10K(String name, Function<List<String>, IpSet> createSet, int iter) {
        return runIps(name, createSet, iter, 10, 10, 10, 10);
    }

    private static BenchResult run100K(String name, Function<List<String>, IpSet> createSet, int iter) {
        return runIps(name, createSet, iter, 10, 10, 10, 100);
    }

    private static BenchResult run1M(String name, Function<List<String>, IpSet> createSet, int iter) {
        return runIps(name, createSet, iter, 10, 10, 100, 100);
    }

    private static BenchResult runIps(String name, Function<List<String>, IpSet> createSet, int iter, int range1, int range2, int range3, int range4) {
        List<String> ips = TestHelper.createIps(range1, range2, range3, range4);
        // some pattern
        ips.add("100.100.100.*");
        ips.add("101.101.*");
        ips.add("102.*");

        Collections.shuffle(ips);

        IpSet ipSet = createSet.apply(ips);

        long start = System.currentTimeMillis();
        TestHelper.runIpSetContainsIp(
                iter,
                ipSet,
                () -> String.format("%d.%d.%d.%d",
                        TestHelper.random(1, range1),
                        TestHelper.random(1, range2),
                        TestHelper.random(1, range3),
                        TestHelper.random(1, range4)
                ));

        long step1 = System.currentTimeMillis();

        TestHelper.runIpSetContainsIp(
                iter,
                ipSet,
                () -> {
                    switch (ThreadLocalRandom.current().nextInt(2)) {
                        case 0:
                            return String.format("100.100.100.%d",
                                    TestHelper.randomIpPart()
                            );
                        case 1:
                            return String.format("101.101.%d.%d",
                                    TestHelper.randomIpPart(),
                                    TestHelper.randomIpPart()
                            );
                        default:
                            return String.format("102.%d.%d.%d",
                                    TestHelper.randomIpPart(),
                                    TestHelper.randomIpPart(),
                                    TestHelper.randomIpPart()
                            );
                    }
                });

        long step2 = System.currentTimeMillis();

        TestHelper.runIpSetNotContainsIp(
                iter,
                ipSet,
                ip -> {
                    String[] parts = ip.split("\\.");
                    int i1 = Integer.parseInt(parts[0]);
                    int i2 = Integer.parseInt(parts[1]);
                    int i3 = Integer.parseInt(parts[2]);
                    int i4 = Integer.parseInt(parts[3]);
                    return (i1 > range1 && (i2 < 1 || i2 > range2)
                            && (i3 < 1 || i3 > range3)
                            && (i4 < 1 || i4 > range4))
                            && (i1 != 100 && i2 != 100 & i3 != -100)
                            && (i1 != 101 && i2 != 101)
                            && (i1 != 102)
                            ;
                });

        long step3 = System.currentTimeMillis();
        BenchResult result = new BenchResult(name);
        result.put("ipCount", ips.size());
        result.put("iter", iter);
        result.put("job1", step1 - start);
        result.put("job2", step2 - step1);
        result.put("job3", step3 - step2);
        return result;
    }

}
