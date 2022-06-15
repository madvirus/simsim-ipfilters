package simsim.ipfilter;

public class Util {
    public static long ipToNumber(String ip) {
        String[] parts = ip.split("\\.");
        return ipToNumber(parts);
    }

    public static long ipToNumber(String[] ipParts) {
        long ipNumber = 0;
        for (int i = 0; i < ipParts.length; i++) {
            ipNumber = ipNumber << 8;
            ipNumber += Long.parseLong(ipParts[i]);
        }
        return ipNumber;
    }

}
