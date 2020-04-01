import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VFileSystem {
    private VDirectory _root;
    private boolean[] _storageBlocks;

    private void DFS_Load(VDirectory currentDirectory, Scanner reader){
        String line = reader.nextLine();

        while(!line.equals("#")){
            if(line.charAt(0) == '<'){ // the line is a directory name
                StringBuilder directoryName = new StringBuilder();

                for(int i = 0; i<line.length(); i++){
                    if(line.charAt(i) != '<' && line.charAt(i) != '>')
                        directoryName.append(line.charAt(i));
                }

                VDirectory subDirectory = new VDirectory(directoryName.toString());
                currentDirectory.getSubDirectories().add(subDirectory);
                DFS_Load(subDirectory, reader);
            }
            else{ // the line is a file name
                VFile file = new VFile(line);

                int fileSize = Integer.parseInt(reader.nextLine()); // the line following the file name is the size of the file
                for(int i = 0; i < fileSize; i++){ // 'fileSize' lines follow, each contains the index of the block allocated to that file
                    int allocatedBlock = Integer.parseInt(reader.nextLine());
                    file.getAllocatedBlocks().add(allocatedBlock);
                }

                currentDirectory.addFile(file);
            }
            line = reader.nextLine();
        }
    }

    private void LoadState() throws Exception {
        Scanner reader = new Scanner(new File("state_file.txt"));

        Integer storageSize = Integer.parseInt(reader.nextLine());
        if(storageSize != _storageBlocks.length){
            throw new Exception("Saved storage size is different from storage size passed to VFileSystem constructor");
        }

        String blocks = reader.nextLine();
        for(int i = 0; i<_storageBlocks.length; i++){
            if(blocks.charAt(i) == '1'){
                _storageBlocks[i] = true;
            }else{
                _storageBlocks[i] = false;
            }
        }

        DFS_Load(_root, reader);

        reader.close();
    }

    public VFileSystem(int storageSize){
        _root = new VDirectory("root");
        _storageBlocks = new boolean[storageSize];

        try{ LoadState(); }
        catch (Exception e){e.printStackTrace();}
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
    private void indexedAllocation(VFile file , int fileSize) throws Exception {
        List <Integer> tobeallocated  = new ArrayList<>();
        for (int i = 0 ; i < _storageBlocks.length && tobeallocated.size()<fileSize ; ++i){ /// to break when the filesize is satisfied
            if(!_storageBlocks[i]){ /// not allocated
                tobeallocated.add(i);
            }
        }
        if(tobeallocated.size() >= fileSize){
            List<Integer> allocatedBlocks = file.getAllocatedBlocks();
            for (int i = 0 ; i < fileSize ; ++i){
                allocatedBlocks.add(tobeallocated.get(i));
                _storageBlocks[tobeallocated.get(i)] = true;
            }
        }
        else {
            throw new Exception("Couldn't allocate an indexed set of blocks of size " + fileSize);
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
//                C_Allocate(file, fileSize);/// i commented this to make it create with indexed allocation
                indexedAllocation(file, fileSize);
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
    private int getFileindex(String fName, List<VFile>files){
        for(int i = 0 ; i < files.size() ; ++i){
            VFile vd = files.get(i);
            if(vd.getName().equals(fName))
                return i;
        }
        return -1;
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
    void deleteDirectory(VDirectory currentDirectory ){
        ///base case this is file or directory with no directories
        List<VDirectory> subDirectories = currentDirectory.getSubDirectories(); // directories under currentDirectory
        List<VFile> subfiles = currentDirectory.getFiles();
        for(VFile f : subfiles){
            deallocateFile(f);
        }
        subfiles.clear(); /// delete all files
        if(subDirectories.size() == 0){
            /// if there's no subfiles then remove
            return;
        }
        for(int i = 0 ; i < subDirectories.size() ; ++i){
            deleteDirectory(subDirectories.get(i) );
        }
        subDirectories.clear();
        return  ;



    }
    void deallocateFile(VFile file){
        List<Integer> ls =    file.getAllocatedBlocks();
        for(Integer i : ls){
            this._storageBlocks[i] = false;
        }
    }
    public  void getFileOrDirectorytoBeDeleted(String dfname , int nextDirectoryIndex ,List<String> directories
            , VDirectory currentDirectory, boolean isDirectory ) throws Exception {
        List<VDirectory> subDirectories = currentDirectory.getSubDirectories(); // directories under currentDirectory

        if (nextDirectoryIndex == directories.size()) {
            if(isDirectory){
                VDirectory directory = getDirectory(dfname , subDirectories);
                if(directory!=null) {
                    deleteDirectory(directory);
                    subDirectories.remove(directory);
                }
                else {
                    throw new Exception("directory not found");
                }
            }
            else {
                List<VFile> subfiles = currentDirectory.getFiles();
                int indx=  getFileindex(dfname , subfiles);
                if(indx != -1){
                    VFile file = subfiles.get(indx);
                    deallocateFile(file);
                    subfiles.remove(indx);
                }
                else {
                    throw new Exception("The file doesn't exist");
                }
                return;

            }
        }
        else {
            /// we need to check directories only because the file is the last in the path and checked above
            VDirectory directory = getDirectory(directories.get(nextDirectoryIndex), subDirectories);
            if(directory == null){
                throw new Exception("Couldn't find [" +
                        directories.get(nextDirectoryIndex) +
                        "] under [" +
                        currentDirectory.getName() + "]");
            }else{
                CreateDirectory(dfname, nextDirectoryIndex + 1, directories, directory);
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

    private void DFS_Save(VDirectory currentDirectory, File stateFile, FileWriter writer) throws IOException {
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
