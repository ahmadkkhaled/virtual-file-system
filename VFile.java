import java.util.List;

public class VFile {
    private String _name;

    private List<Integer> _allocatedBlocks; // the indices of the blocks allocated to this file in the secondary storage.

    VFile(String name){
        _name = name;
    }

    public String getName(){
        return _name;
    }

    public void print() {
        throw new UnsupportedOperationException(); // not yet implemented
    }
}