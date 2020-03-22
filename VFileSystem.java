import java.util.List;

public class VFileSystem {
    private VDirectory _root;
    private List<Boolean> _storageBlocks; // TODO fill accordingly

    // TODO loadSystemFromStorage()

    public VFileSystem(){
        _root = new VDirectory("root");
    }

    public void CreateFile(String fileName, int nextDirectoryIndex, List<String> directories, VDirectory currentDirectory) throws Exception {
        if(nextDirectoryIndex == directories.size()){ // the traversal has reached the directory in which a file should be created
            List<VFile> files = currentDirectory.getFiles();
            Boolean matchingFileNameFound = false;
            for(VFile file : files){
                if(file.getName().equals(fileName)){
                    matchingFileNameFound = true;
                    break;
                }
            }
            if(matchingFileNameFound){
                throw new Exception("A file with the same name already exists under [" + currentDirectory.getName() + "]");
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
                if( directory.getName().equals(directories.get(nextDirectoryIndex))){
                    subDirectoryFound = true;
                    subDirectory = directory;
                    break;
                }
            }
            if(subDirectoryFound){
                CreateFile(fileName, nextDirectoryIndex + 1, directories, subDirectory);
            }
            else{
                throw new Exception("Couldn't find [" +
                                    directories.get(nextDirectoryIndex) +
                                    "] under [" +
                                    currentDirectory.getName() + "]");
            }
        }
    }

    private VDirectory getDirectory(String directoryName, List<VDirectory> directories){
        for(VDirectory vd : directories){
            if(vd.getName().equals(directoryName))
                return vd;
        }
        return null;
    }
    public void CreateDirectory(String directoryName, int nextDirectoryIndex, List<String> directories, VDirectory currentDirectory) throws Exception{
        List<VDirectory> subDirectories = currentDirectory.getSubDirectories(); // directories under currentDirectory

        if(nextDirectoryIndex == directories.size()){
            VDirectory directory = getDirectory(directoryName, subDirectories);
            if(directory == null){
                directory = new VDirectory(directoryName);
                subDirectories.add(directory);
            }else{
                throw new Exception("A directory with the same name already exists under [" + currentDirectory.getName() + "]");
            }
        }else{
            /*
             *  Check if the directory following currentDirectory in the directories list exists under currentDirectory
             *  e.g. folder1/folder2/folder3
             *  let currentDirectory = folder2
             *  check if folder3 exists under folder2
             */
            VDirectory directory = getDirectory(directories.get(nextDirectoryIndex), subDirectories);
            if(directory == null){
                throw new Exception("Couldn't find [" +
                        directories.get(nextDirectoryIndex) +
                        "] under [" +
                        currentDirectory.getName() + "]");
            }else{
                CreateDirectory(directoryName, nextDirectoryIndex + 1, directories, directory);
            }
        }
    }
    public VDirectory getRoot(){
        return _root;
    }
}
