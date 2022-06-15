package simsim.ipfilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IpFilter {
    private final IpSet allowIpSet;
    private final IpSet denyIpSet;
    private boolean defaultAllow;
    private boolean allowFirst;

    private IpFilter(List<String> allowIps, List<String> denyIps, boolean defaultAllow, boolean allowFirst, IpSetType allowIpSetType, IpSetType denyIpSetType) {
        this.defaultAllow = defaultAllow;
        this.allowFirst = allowFirst;
        allowIpSet = createIpSet(allowIpSetType, allowIps);
        denyIpSet = IpSet.createTreeIpSet(denyIps);
    }

    private IpSet createIpSet(IpSetType ipSetType, List<String> ips) {
        switch (ipSetType) {
            case TREE: return IpSet.createTreeIpSet(ips);
            case HASH: return IpSet.createHashIpSet(ips);
            case SIMPLE: return IpSet.createSimpleIpSet(ips);
        }
        throw new IllegalArgumentException("bad IpSetType: " + ipSetType);
    }

    public boolean allow(String ip) {
        if (allowFirst) {
            if (allowIpSet.contains(ip)) return true;
            if (denyIpSet.contains(ip)) return false;
            return defaultAllow;
        }
        if (denyIpSet.contains(ip)) return false;
        return defaultAllow || allowIpSet.contains(ip);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<String> allowIps = new ArrayList<>();
        private List<String> denyIps = new ArrayList<>();
        private boolean defaultAllow = false;
        private boolean allowFirst = false;
        private IpSetType allowIpSetType = IpSetType.TREE;
        private IpSetType denyIpSetType = IpSetType.TREE;

        public Builder allowIp(String ip) {
            allowIps.add(ip);
            return this;
        }

        public Builder allowIps(Collection ips) {
            allowIps.addAll(ips);
            return this;
        }

        public Builder denyIp(String ip) {
            denyIps.add(ip);
            return this;
        }

        public Builder denyIps(Collection ips) {
            denyIps.addAll(ips);
            return this;
        }

        public Builder allowByDefault() {
            defaultAllow = true;
            return this;
        }

        public Builder denyByDefault() {
            defaultAllow = false;
            return this;
        }

        public Builder allowFirst() {
            allowFirst = true;
            return this;
        }

        public Builder denyFirst() {
            allowFirst = false;
            return this;
        }

        public Builder allowIpSetType(IpSetType ipSetType) {
            allowIpSetType = ipSetType;
            return this;
        }

        public Builder denyIpSetType(IpSetType ipSetType) {
            allowIpSetType = ipSetType;
            return this;
        }

        public IpFilter build() {
            return new IpFilter(allowIps, denyIps, defaultAllow, allowFirst, allowIpSetType, denyIpSetType);
        }
    }

    public enum IpSetType {
        TREE, HASH, SIMPLE
    }
}
