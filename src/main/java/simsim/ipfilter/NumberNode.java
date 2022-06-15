package simsim.ipfilter;

class NumberNode extends Node {
    private int number;
    private NumberNode[] childs; // 0~255 (1~255)
    private RangeNode rangeChild;

    public NumberNode(int number) {
        if (number < 0 || number > 255) {
            throw new IllegalArgumentException("ip part number must be in 0-255, but " + number);
        }
        this.number = number;
    }

    public NumberNode getOrAddChild(int num) {
        NumberNode child = childs == null ? null : childs[num];
        if (child == null) {
            child = new NumberNode(num);
            addChild(child);
        }
        return child;
    }

    private void addChild(NumberNode child) {
        if (childs == null) {
            childs = new NumberNode[256];
        }
        childs[child.number] = child;
    }

    public void addRangeChild(int from, int to) {
        rangeChild = new RangeNode(from, to);
    }

    @Override
    public Node getMatchingChild(int num) {
        if (rangeChild != null && rangeChild.contains(num)) {
            return rangeChild;
        }
        return childs == null ? null : childs[num];
    }
}
