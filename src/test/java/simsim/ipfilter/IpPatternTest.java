package simsim.ipfilter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IpPatternTest {

    @Test
    void cidrFromTo() {
        assertIpPatternRange("1.0.0.0/23", 16777216L, 16777727L);
        assertIpPatternRange("128.0.0.0/6", 2147483648L, 2214592511L);
        assertIpPatternRange("1.0.0.128/26", 16777344L, 16777407L);
    }

    private void assertIpPatternRange(String ipPattern, long from, long to) {
        IpPattern pattern = IpPattern.parse(ipPattern);
        assertThat(pattern.getFrom()).isEqualTo(from);
        assertThat(pattern.getTo()).isEqualTo(to);
    }
}