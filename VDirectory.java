import java.util.List;

public class VDirectory {
    private String _name;
    private List<VDirectory> _subDirectories;
    private List<VFile> _files;

    VDirectory(String name){
        _name = name;
    }

    public List<VDirectory> getSubDirectories(){
        return _subDirectories;
    }

    public List<VFile> getFiles(){
        return _files;
    }

    public String getName(){
        return _name;
    }

    public void print() {
        throw new UnsupportedOperationException(); // not yet implemented
    }
}