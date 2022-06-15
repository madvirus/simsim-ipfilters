package simsim.ipfilter;

abstract class Node {
    Node() {
    }

    public abstract Node getMatchingChild(int num);

    public boolean isRangeNode() {
        return false;
    }
}
