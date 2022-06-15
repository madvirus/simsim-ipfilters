package simsim.ipfilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HashIpSet implements IpSet {

    private final Set<String> singleIpSet;
    private final List<IpPattern> rangePatterns;

    public HashIpSet(List<String> ipPatterns) {
        List<IpPattern> patterns = ipPatterns.stream().map(ip -> IpPattern.parse(ip)).collect(Collectors.toList());

        List<IpPattern> exactIpPatterns = patterns.stream().filter(p -> p.isSingleIp()).collect(Collectors.toList());
        Set<String> exactIpSet = new HashSet<>();
        exactIpPatterns.forEach(p -> exactIpSet.add(p.getValue()));

        List<IpPattern> rangePatterns = patterns.stream().filter(p -> !p.isSingleIp()).collect(Collectors.toList());

        this.singleIpSet = exactIpSet;
        this.rangePatterns = rangePatterns;
    }

    @Override
    public boolean contains(String ip) {
        if (singleIpSet.contains(ip)) return true;

        for (IpPattern pattern : rangePatterns) {
            long ipNum = Util.ipToNumber(ip);
            if (pattern.getFrom() <= ipNum && ipNum <= pattern.getTo()) {
                return true;
            }
        }
        return false;
    }
}
