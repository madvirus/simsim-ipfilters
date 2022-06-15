package simsim.ipfilter;

import java.util.List;
import java.util.stream.Collectors;

public class SimpleIpSet implements IpSet {

    private final List<IpPattern> patterns;

    public SimpleIpSet(List<String> ipPatterns) {
        patterns = ipPatterns.stream().map(IpPattern::parse).collect(Collectors.toList());
    }

    @Override
    public boolean contains(String ip) {
        for (IpPattern pattern : patterns) {
            if (match(pattern, ip)) {
                return true;
            }
        }
        return false;
    }

    private boolean match(IpPattern pattern, String ip) {
        if (pattern.isSingleIp()) {
            return pattern.getValue().equals(ip);
        }
        long ipNum = Util.ipToNumber(ip);
        return pattern.getFrom() <= ipNum && ipNum <= pattern.getTo();
    }
}
