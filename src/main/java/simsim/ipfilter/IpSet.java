package simsim.ipfilter;

import java.util.List;

public interface IpSet {
    boolean contains(String ip);

    static IpSet createTreeIpSet(List<String> ipPatterns) {
        return new TreeIpSet(ipPatterns);
    }

    static IpSet createHashIpSet(List<String> ipPatterns) {
        return new HashIpSet(ipPatterns);
    }

    static IpSet createSimpleIpSet(List<String> ipPatterns) {
        return new SimpleIpSet(ipPatterns);
    }

}
