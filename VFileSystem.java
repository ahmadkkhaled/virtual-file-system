import java.util.List;

public class VFileSystem {
    private VDirectory _root;
    private List<Boolean> _storageBlocks;

    // TODO loadSystemFromStorage()

    public VFileSystem(){
        _root = new VDirectory("root");
    }

    public void CreateFile(String fileName, int currentDirectoryIndex, List<String> directories, VDirectory currentDirectory) throws Exception {
        if(currentDirectoryIndex == directories.size() - 1){ // the traversal has reached the directory in which a file should be created
            List<VFile> files = currentDirectory.getFiles();
            Boolean matchingFileNameFound = false;
            for(VFile file : files){
                if(file.getName().equals(fileName)){
                    matchingFileNameFound = true;
                    break;
                }
            }
            if(matchingFileNameFound){
                throw new Exception("A file with the same name already exists in this directory");
            }
            else{
                VFile file = new VFile(fileName);
                files.add(file);
            }
        }
        else{
            List<VDirectory> subDirectories = currentDirectory.getSubDirectories();
            Boolean subDirectoryFound = false;
            VDirectory subDirectory = null;
            for(VDirectory directory : subDirectories){
                if( directory.getName().equals(directories.get(currentDirectoryIndex))){
                    subDirectoryFound = true;
                    subDirectory = directory;
                    break;
                }
            }
            if(subDirectoryFound){
                CreateFile(fileName, currentDirectoryIndex + 1, directories, subDirectory);
            }
            else{
                throw new Exception("Couldn't find [" +
                                    directories.get(currentDirectoryIndex) +
                                    "] under [" +
                                    currentDirectory.getName() + "]"); // TODO create custom exception
            }
        }
    }

    public VDirectory getRoot(){
        return _root;
    }
}
