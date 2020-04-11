import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class VFile {
    private String _name;
    private List<Integer> _allocatedBlocks; // the indices of the blocks allocated to this file in the secondary storage.

    VFile(String name){
        _name = name;
        _allocatedBlocks = new ArrayList<>();
    }

    public String getName(){
        return _name;
    }

    @Override
    public String toString(){
        return _name;
    }

    public List<Integer> getAllocatedBlocks(){
        return _allocatedBlocks;
    }

    public int getSize(){
        return _allocatedBlocks.size();
    }
}
