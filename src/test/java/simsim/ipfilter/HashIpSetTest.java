package simsim.ipfilter;

import java.util.List;

public class HashIpSetTest extends BaseIpSetTest {

    @Override
    protected IpSet createIpSet(List<String> ipPatterns) {
        return IpSet.createHashIpSet(ipPatterns);
    }
}
