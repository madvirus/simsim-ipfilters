package simsim.ipfilter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class TestHelper {

    public static List<String> createIps(int range1, int range2, int range3, int range4) {
        List<String> ips = new ArrayList<>();
        for (int i1 = 1; i1 <= range1; i1++) {
            for (int i2 = 1; i2 <= range2; i2++) {
                for (int i3 = 1; i3 <= range3; i3++) {
                    for (int i4 = 1; i4 <= range4; i4++) {
                        ips.add(String.format("%d.%d.%d.%d", i1, i2, i3, i4));
                    }
                }
            }
        }
        return ips;
    }

    public static int random(int from, int to) {
        return ThreadLocalRandom.current().nextInt(from, to + 1);
    }

    public static String randomIp() {
        return randomFirstIpPart() + "." + randomIpPart() + "." + randomIpPart() + "." + randomIpPart();
    }

    public static int randomFirstIpPart() {
        return random(1, 255);
    }

    public static int randomIpPart() {
        return random(0, 255);
    }

    public static void assertIpSetContainsIp(IpSet ipSet, Supplier<String> gen) {
        assertIpSetContainsIp(100_000, ipSet, gen);
    }

    public static void assertIpSetContainsIp(long count, IpSet ipSet, Supplier<String> gen) {
        Stream.generate(gen)
                .limit(count)
                .forEach(ip -> assertThat(ipSet.contains(ip)).describedAs(ip).isTrue());
    }

    public static void runIpSetContainsIp(long count, IpSet ipSet, Supplier<String> gen) {
        Stream.generate(gen)
                .limit(count)
                .forEach(ip -> ipSet.contains(ip));
    }

    public static void assertIpSetNotContainsIp(IpSet ipSet, Predicate<String> filter) {
        assertIpSetNotContainsIp(100_000, ipSet, filter);
    }

    public static void assertIpSetNotContainsIp(long count, IpSet ipSet, Predicate<String> filter) {
        Stream.generate(() -> TestHelper.randomIp())
                .filter(filter)
                .limit(count)
                .forEach(ip -> assertThat(ipSet.contains(ip)).describedAs(ip).isFalse());
    }

    public static void runIpSetNotContainsIp(long count, IpSet ipSet, Predicate<String> filter) {
        Stream.generate(() -> TestHelper.randomIp())
                .filter(filter)
                .limit(count)
                .forEach(ip -> ipSet.contains(ip));
    }

}
