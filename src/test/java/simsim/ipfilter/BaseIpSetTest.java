package simsim.ipfilter;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BaseIpSetTest {
    protected abstract IpSet createIpSet(List<String> ipPatterns);

    @Test
    void exactIp() {
        IpSet ipSet = createIpSet(Arrays.asList("1.2.3.4"));
        assertThat(ipSet.contains("1.2.3.4")).isTrue();
        TestHelper.assertIpSetNotContainsIp(ipSet, ip -> !ip.equals("1.2.3.4"));
    }

    @Test
    void lastStar() {
        IpSet ipSet = createIpSet(Arrays.asList("1.2.3.*"));
        // in 1.2.3.0 ~ 1.2.3.255
        TestHelper.assertIpSetContainsIp(ipSet, () -> "1.2.3." + TestHelper.randomIpPart());
        // not in 1.2.3.0 ~ 1.2.3.255
        TestHelper.assertIpSetNotContainsIp(ipSet, ip -> !ip.equals("1.2.3."));
    }

    @Test
    void thirdStar() {
        IpSet ipSet = createIpSet(Arrays.asList("1.2.*"));
        TestHelper.assertIpSetContainsIp(ipSet, () -> "1.2." + TestHelper.randomIpPart() + "." + TestHelper.randomIpPart());
        TestHelper.assertIpSetNotContainsIp(ipSet, ip -> !ip.startsWith("1.2."));
    }

    @Test
    void secondStar() {
        IpSet ipSet = createIpSet(Arrays.asList("1.*"));
        TestHelper.assertIpSetContainsIp(ipSet, () -> "1." + TestHelper.randomIpPart() + "." + TestHelper.randomIpPart() + "." + TestHelper.randomIpPart());
        TestHelper.assertIpSetNotContainsIp(ipSet, ip -> !ip.startsWith("1."));
    }

    @Test
    void cidr6() {
        IpSet ipSet = createIpSet(Arrays.asList("128.0.0.0/6"));
        TestHelper.assertIpSetContainsIp(ipSet, () -> TestHelper.random(128, 128 + 3) + "." + TestHelper.randomIpPart() + "." + TestHelper.randomIpPart() + "." + TestHelper.randomIpPart());
        TestHelper.assertIpSetNotContainsIp(ipSet, ip -> !ip.startsWith("128.") && !ip.startsWith("129.") &&
                !ip.startsWith("130.") && !ip.startsWith("131."));
    }

    @Test
    void cidr8() {
        IpSet ipSet = createIpSet(Arrays.asList("1.0.0.0/8"));
        TestHelper.assertIpSetContainsIp(ipSet, () -> "1." + TestHelper.randomIpPart() + "." + TestHelper.randomIpPart() + "." + TestHelper.randomIpPart());
        TestHelper.assertIpSetNotContainsIp(ipSet, ip -> !ip.startsWith("1."));
    }

    @Test
    void cidr23() {
        IpSet ipSet = createIpSet(Arrays.asList("1.0.0.0/23"));
        TestHelper.assertIpSetContainsIp(ipSet, () -> "1.0.0." + TestHelper.randomIpPart());
        TestHelper.assertIpSetContainsIp(ipSet, () -> "1.0.1." + TestHelper.randomIpPart());
        TestHelper.assertIpSetNotContainsIp(ipSet, ip -> !ip.startsWith("1.0.0.") && !ip.startsWith("1.0.1."));
    }

    @Test
    void cidr24() {
        IpSet ipSet = createIpSet(Arrays.asList("1.0.0.0/24"));
        TestHelper.assertIpSetContainsIp(ipSet, () -> "1.0.0." + TestHelper.randomIpPart());
        TestHelper.assertIpSetNotContainsIp(ipSet, ip -> !ip.startsWith("1.0.0."));
    }

    @Test
    void cidr26_last_128() {
        IpSet ipSet = createIpSet(Arrays.asList("1.0.0.128/26"));
        TestHelper.assertIpSetContainsIp(ipSet, () -> "1.0.0." + TestHelper.random(128, 128 + 0b111111));
        TestHelper.assertIpSetNotContainsIp(ipSet, ip -> {
            if (!ip.startsWith("1.0.0.")) return true;
            int ip4 = Integer.parseInt(ip.split(".")[3]);
            return ip4 < 128;
        });
    }

    @Test
    void cidr26_last_0() {
        IpSet ipSet = createIpSet(Arrays.asList("1.0.0.0/26"));
        TestHelper.assertIpSetContainsIp(ipSet, () -> "1.0.0." + TestHelper.random(0, 0b111111));
        TestHelper.assertIpSetNotContainsIp(ipSet, ip -> {
            if (!ip.startsWith("1.0.0.")) return true;
            int ip4 = Integer.parseInt(ip.split(".")[3]);
            return ip4 > 0b111111;
        });
    }

    @Test
    void composite() {
        IpSet ipSet = createIpSet(Arrays.asList(
                "1.2.3.4",
                "10.*",
                "20.0.*",
                "30.0.0.0/24",
                "40.50.60.70"
        ));
        assertThat(ipSet.contains("1.2.3.4")).isTrue();
        assertThat(ipSet.contains("40.50.60.70")).isTrue();
        TestHelper.assertIpSetContainsIp(ipSet, () ->
                String.format("10.%d.%d.%d", TestHelper.randomIpPart(), TestHelper.randomIpPart(), TestHelper.randomIpPart()));
        TestHelper.assertIpSetContainsIp(ipSet, () ->
                String.format("20.0.%d.%d", TestHelper.randomIpPart(), TestHelper.randomIpPart()));
        TestHelper.assertIpSetContainsIp(ipSet, () ->
                String.format("30.0.0.%d", TestHelper.randomIpPart()));

        TestHelper.assertIpSetNotContainsIp(ipSet, ip -> !ip.equals("1.2.3.4") &&
                !ip.equals("40.50.60.70") &&
                !ip.startsWith("10.") &&
                !ip.startsWith("20.0.") &&
                !ip.startsWith("30.0.0."));
    }

}
