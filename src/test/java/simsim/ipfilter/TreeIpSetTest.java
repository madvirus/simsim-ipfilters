package simsim.ipfilter;

import java.util.List;

public class TreeIpSetTest extends BaseIpSetTest {

    @Override
    protected IpSet createIpSet(List<String> ipPatterns) {
        return IpSet.createTreeIpSet(ipPatterns);
    }
}
