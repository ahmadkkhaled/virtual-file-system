import java.util.ArrayList;
import java.util.List;

public class VDirectory {
    private String _name;
    private List<VDirectory> _subDirectories;
    private List<VFile> _files;

    VDirectory(String name){
        _name = name;
        _subDirectories = new ArrayList<>();
        _files = new ArrayList<>();
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

    @Override
    public String toString(){
        return "<" + _name + ">";
    }
}
