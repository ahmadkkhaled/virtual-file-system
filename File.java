import java.util.List;

public class File extends Node {
    private List<Integer> _allocatedBlocks; // the indices of the blocks allocated to this file in the secondary storage.

    @Override
    public void print() {
        throw new UnsupportedOperationException(); // not yet implemented
    }
}
