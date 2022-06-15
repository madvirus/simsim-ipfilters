package simsim.ipfilter;

class IpPattern {
    private String value;
    private int cidr;
    private String[] parts;

    private long from;
    private long to;
    private boolean exactIp;

    private IpPattern(String value, String[] parts, int cidr) {
        this.value = value;
        this.cidr = cidr;
        this.parts = parts;
        setRange();
    }

    private void setRange() {
        if (cidr > 0) {
            long base = 0xFF_FF_FF_FF;
            long ipNum = Util.ipToNumber(parts);
            long sub = ~(base & (base << (32 - cidr)));
            from = ipNum;
            to = ipNum + sub;
        } else if (value.contains("*")) {
            long from = 0;
            long to = 0;
            boolean starRange = false;
            for (int i = 0 ; i < parts.length || i < 4 ; i++) {
                from = from << 8;
                to = to << 8;
                if (!starRange && parts[i].equals("*")) {
                    starRange = true;
                }
                if (starRange) {
                    to += 255;
                } else {
                    long num = Long.parseLong(parts[i]);
                    from += num;
                    to += num;
                }
            }
            this.from = from;
            this.to = to;
        } else {
            exactIp = true;
        }
    }

    public String getValue() {
        return value;
    }

    public int getCidr() {
        return cidr;
    }

    public String[] getParts() {
        return parts;
    }

    public static IpPattern parse(String ipPattern) {
        int cidrValue = -1;
        int cidrPartIdx = ipPattern.indexOf("/");
        if (cidrPartIdx > 0) {
            cidrValue = Integer.parseInt(ipPattern.substring(cidrPartIdx + 1));
        }
        String ipPart = cidrPartIdx > 0 ? ipPattern.substring(0, cidrPartIdx) : ipPattern;
        String[] parts = ipPart.split("\\.");
        return new IpPattern(ipPattern, parts, cidrValue);
    }

    public boolean isSingleIp() {
        return exactIp;
    }

    public long getFrom() {
        return from;
    }

    public long getTo() {
        return to;
    }
}
