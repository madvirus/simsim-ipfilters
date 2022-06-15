package simsim.ipfilter;

import org.junit.jupiter.api.Test;
import simsim.ipfilter.IpFilter;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class IpFilterTest {
    @Test
    void noAllowIp() {
        IpFilter ipFilter = IpFilter.builder().build();
        assertThat(ipFilter.allow("1.2.3.4")).isFalse();
    }

    @Test
    void allowIp() {
        IpFilter ipFilter = IpFilter.builder()
                .allowIp("1.2.3.4")
                .allowIp("1.2.3.5")
                .allowIps(Arrays.asList("1.2.3.6"))
                .build();

        assertThat(ipFilter.allow("1.2.3.4")).isTrue();
        assertThat(ipFilter.allow("1.2.3.5")).isTrue();
        assertThat(ipFilter.allow("1.2.3.6")).isTrue();

        assertThat(ipFilter.allow("1.2.3.1")).isFalse();
    }

    @Test
    void denyIp() {
        IpFilter ipFilter = IpFilter.builder()
                .denyIp("1.2.3.4")
                .denyIps(Arrays.asList("1.2.3.5"))
                .build();

        assertThat(ipFilter.allow("1.2.3.4")).isFalse();
        assertThat(ipFilter.allow("1.2.3.5")).isFalse();
    }

    @Test
    void denyFirst_Implicitly() {
        IpFilter ipFilter = IpFilter.builder()
                .allowIp("1.2.3.4")
                .denyIp("1.2.3.4")
                .build();

        assertThat(ipFilter.allow("1.2.3.4")).isFalse();
    }

    @Test
    void denyFirst() {
        IpFilter ipFilter = IpFilter.builder()
                .allowIp("1.2.3.4")
                .denyIp("1.2.3.4")
                .denyFirst()
                .build();

        assertThat(ipFilter.allow("1.2.3.4")).isFalse();
    }

    @Test
    void allowFirst() {
        IpFilter ipFilter = IpFilter.builder()
                .allowIp("1.2.3.4")
                .denyIp("1.2.3.4")
                .allowFirst()
                .build();

        assertThat(ipFilter.allow("1.2.3.4")).isTrue();
    }

    @Test
    void allowByDefault() {
        IpFilter ipFilter = IpFilter.builder()
                .allowIp("1.2.3.4")
                .denyIp("1.2.3.5")
                .allowByDefault()
                .build();
        assertThat(ipFilter.allow("1.2.3.4")).isTrue();
        assertThat(ipFilter.allow("1.2.3.5")).isFalse();
        assertThat(ipFilter.allow("1.2.3.6")).isTrue();
    }

    @Test
    void denyByDefault() {
        IpFilter ipFilter = IpFilter.builder()
                .allowIp("1.2.3.4")
                .denyIp("1.2.3.5")
                .denyByDefault()
                .build();
        assertThat(ipFilter.allow("1.2.3.4")).isTrue();
        assertThat(ipFilter.allow("1.2.3.5")).isFalse();
        assertThat(ipFilter.allow("1.2.3.6")).isFalse();
    }

    @Test
    void denyByDefault_Implicitly() {
        IpFilter ipFilter = IpFilter.builder()
                .allowIp("1.2.3.4")
                .denyIp("1.2.3.5")
                .build();
        assertThat(ipFilter.allow("1.2.3.4")).isTrue();
        assertThat(ipFilter.allow("1.2.3.5")).isFalse();
        assertThat(ipFilter.allow("1.2.3.6")).isFalse();
    }
}
