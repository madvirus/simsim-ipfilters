package simsim.ipfilter;

import java.util.List;

public class SimpleIpSetTest extends BaseIpSetTest {

    @Override
    protected IpSet createIpSet(List<String> ipPatterns) {
        return IpSet.createSimpleIpSet(ipPatterns);
    }
}
