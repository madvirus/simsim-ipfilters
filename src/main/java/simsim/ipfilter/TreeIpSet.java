package simsim.ipfilter;

import java.util.List;

public class TreeIpSet implements IpSet {
    private NumberNode root;

    public TreeIpSet(List<String> ipPatterns) {
        NumberNode root = new NumberNode(0);
        ipPatterns.stream()
                .map(str -> IpPattern.parse(str))
                .forEach(value -> {
                    int cidrValue = value.getCidr();
                    String[] parts = value.getParts();
                    NumberNode cur = root;
                    for (String part : parts) {
                        if ("*".equals(part)) {
                            cur.addRangeChild(0, 255);
                            break;
                        } else {
                            int num = Integer.parseInt(part);
                            if (cidrValue > -1) {
                                // cidrValue exists
                                if (cidrValue < 8) {
                                    // get range from cidrValue
                                    int to = getTo(num, cidrValue);
                                    cur.addRangeChild(num, to);
                                    break;
                                } else {
                                    cidrValue = cidrValue - 8;
                                }
                            }
                            NumberNode child = cur.getOrAddChild(num);
                            cur = child;
                        }
                    }
                });
        this.root = root;
    }

    private int getTo(int num, int cidrValue) {
        switch (cidrValue) {
            case 0:
                return num + 0b1111_1111;
            case 1:
                return num + 0b0111_1111;
            case 2:
                return num + 0b0011_1111;
            case 3:
                return num + 0b0001_1111;
            case 4:
                return num + 0b0000_1111;
            case 5:
                return num + 0b0000_0111;
            case 6:
                return num + 0b0000_0011;
            case 7:
                return num + 0b0000_0001;
        }
        throw new IllegalArgumentException("bad cidr value: " + cidrValue);
    }

    @Override
    public boolean contains(String ip) {
        String[] parts = ip.split("\\.");
        int[] ipNums = new int[4];
        ipNums[0] = Integer.parseInt(parts[0]);
        ipNums[1] = Integer.parseInt(parts[1]);
        ipNums[2] = Integer.parseInt(parts[2]);
        ipNums[3] = Integer.parseInt(parts[3]);
        Node cur = root;
        for (int num : ipNums) {
            Node child = cur.getMatchingChild(num);
            if (child == null) {
                return false;
            }
            if (child.isRangeNode()) {
                return true;
            }
            cur = child;
        }
        return true;
    }

}
