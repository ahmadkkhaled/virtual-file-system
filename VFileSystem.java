import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VFileSystem {
    private VDirectory _root;
    private boolean[] _storageBlocks;

    // TODO loadSystemFromStorage()

    public VFileSystem(int storageSize){
        _root = new VDirectory("root");
        _storageBlocks = new boolean[storageSize];
    }


    private void C_Allocate(VFile file, int fileSize) throws Exception{ // contiguous allocation.
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
                C_Allocate(file, fileSize);
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

    public void SaveState() throws IOException {
        File stateFile = new File("state_file.txt");
        FileWriter writer = new FileWriter("state_file.txt");

        writer.write(_storageBlocks.length + "\n");
        for(int i = 0; i<_storageBlocks.length; i++){
            writer.write( Integer.toString(_storageBlocks[i] ? 1 : 0) );

            if(i == _storageBlocks.length - 1)
                writer.write("\n");
        }
        DFS_Save(_root, stateFile, writer);

        writer.close();
    }

    public void DFS_Save(VDirectory currentDirectory, File stateFile, FileWriter writer) throws IOException {
        List<VFile> subFiles = currentDirectory.getFiles();
        for(VFile file : subFiles){
            writer.write(file + "\n");

            writer.write(file.getAllocatedBlocks().size() + "\n");
            for(int i = 0; i<file.getAllocatedBlocks().size(); i++){
                writer.write(file.getAllocatedBlocks().get(i) + "\n");
            }
        }

        List<VDirectory> subDirectories = currentDirectory.getSubDirectories();
        for(VDirectory subDirectory : subDirectories){
            writer.write(subDirectory + "\n");
            DFS_Save(subDirectory, stateFile, writer);
        }

        writer.write("#\n");
    }
}
