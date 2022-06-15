package simsim.ipfilter;

class RangeNode extends Node {
    private final int from;
    private final int to;

    public RangeNode(int from, int to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public Node getMatchingChild(int num) {
        return null;
    }

    @Override
    public boolean isRangeNode() {
        return true;
    }

    public boolean contains(int num) {
        return from <= num && num <= to;
    }
}
