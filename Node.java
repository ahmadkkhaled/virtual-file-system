public abstract class Node {
    protected String _path;
    protected String _name;
    protected boolean _isDeleted; // might not need it, can directly delete node from tree

    public abstract void print();
}
