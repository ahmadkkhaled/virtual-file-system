import java.util.ArrayList;
import java.util.List;

public class VFileSystem {
    private VDirectory _root;
    private boolean[] _storageBlocks;

    // TODO loadSystemFromStorage()

    public VFileSystem(int storageSize){
        _root = new VDirectory("root");
        _storageBlocks = new boolean[storageSize];
        _storageBlocks[0] = _storageBlocks[1] = true;
        _storageBlocks[2] = _storageBlocks[3] = _storageBlocks[4] = _storageBlocks[5] = false;
        _storageBlocks[6] = true;
        _storageBlocks[7] = _storageBlocks[8] = _storageBlocks[9] = false;
    }


    private void CAllocate(VFile file, int fileSize) throws Exception{ // contiguous allocation.
        int fileLeft, fileRight, windowLeft, windowRight;
        fileLeft = fileRight = windowLeft = windowRight = -1;

        for(int i = 0; i<_storageBlocks.length; i++){
            if(!_storageBlocks[i]){
                if(windowLeft == -1)
                    windowLeft = i;
                windowRight = i;
            }
            if(_storageBlocks[i] || i == _storageBlocks.length - 1){
                if(windowLeft != -1 && (windowRight - windowLeft + 1 >= fileSize)){
                    if(fileLeft == -1){ // first time assigning the file window.
                        fileLeft = windowLeft;
                        fileRight = windowRight;
                    }
                    else if(windowRight - windowLeft < fileRight - fileLeft){
                        fileLeft = windowLeft;
                        fileRight = windowRight;
                    }
                }
                windowLeft = windowRight = -1;
            }
        }

        if(fileLeft == -1){
            throw new Exception("Couldn't allocate a contiguous set of blocks of size " + fileSize);
        }
        else{
            List<Integer> allocatedBlocks = file.getAllocatedBlocks();
            for(int i = fileLeft; i < fileLeft + fileSize; i++){
                _storageBlocks[i] = true;
                allocatedBlocks.add(i);
            }
        }
    }
    /**
     *
     * @param fileName Name of the file to be created
     * @param currentDirectory The directory the current recursive call is pointing to
     * @param directories A list of the directory names (in the order they were given by the user e.g. x/y/z/file.txt -> directories = {x, y, z}
     * @param nextDirectoryIndex the index of the directory name proceeding 'currentDirectory' in the given directories list.
     *                           e.g. if the directories list is {x, y, z, i} and currentDirectory is z
     *                           then nextDirectoryIndex is 3 (0-based indexing)
     * @param fileSize The size of the file in KB.
     */
    public void CreateFile(String fileName, int fileSize, int nextDirectoryIndex, List<String> directories, VDirectory currentDirectory) throws Exception {


        if(nextDirectoryIndex == directories.size()){ // BASE CASE: the traversal has reached the directory in which a file should be created
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
                CAllocate(file, fileSize);
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
                CreateFile(fileName, fileSize, nextDirectoryIndex + 1, directories, subDirectory);
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

    public void DisplayDiskStructure(){
        print(_root, "");
    }

    private void print(VDirectory currentDirectory, String indentation){
        System.out.println(indentation + currentDirectory);

        indentation += "      ";
        for(VFile file : currentDirectory.getFiles()){
            System.out.println(indentation + file);
        }
        for(VDirectory subDirectory : currentDirectory.getSubDirectories()){
            print(subDirectory, indentation);
        }
    }
}
