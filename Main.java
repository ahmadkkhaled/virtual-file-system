import java.util.*;

public class Main {
    public static void main(String[] args) {

        int system_size = 10; // Default system size
        if(args.length > 0){
            system_size = Integer.parseInt(args[0]);
        }

        VFileSystem vfs = new VFileSystem(system_size);

        Scanner reader = new Scanner(System.in);
        String userInput = "";
        boolean quit = false;
        while(!quit){
            System.out.print("Enter a command <enter 'quit' to SAVE and exit>: ");
            userInput = reader.nextLine();
            String[] parsed = userInput.split(" ");
            if(parsed.length > 3){
                System.out.println("Error: A command can have a maximum of 2 parameters");
                continue;
            }
            switch(parsed[0])
            {
                case "CreateFile": /// CreateFile root/dir1/dir2/filename int_size
                {
                    if(parsed.length < 3){
                        System.out.println("Please provide 2 parameters for 'CreateFile'");
                        break;
                    }
                    int fileSize = Integer.parseInt(parsed[2]);
                    List<String> directories = new LinkedList<String>(Arrays.asList(parsed[1].split("/")));
                    if(!directories.get(0).equals("root")){
                        System.out.println("The given path must start with 'root'");
                        break;
                    }
                    String fileName = directories.get(directories.size() - 1);
                    directories.remove(directories.size() - 1);
                    try{    /// i made it use indexed allocation for now only
                        vfs.CreateFile(fileName, fileSize,1, directories, vfs.getRoot());
                    }catch (Exception e){e.printStackTrace();}
                    break;
                }
                case "CreateDirectory":
                {
                    List<String> directories = new LinkedList<String>(Arrays.asList(parsed[1].split("/")));
                    if(!directories.get(0).equals("root")){
                        System.out.println("The given path must start with 'root'");
                        break;
                    }
                    String directoryName = directories.get(directories.size() - 1);
                    directories.remove(directories.size() - 1);
                    try{
                        vfs.CreateDirectory(directoryName, 1, directories, vfs.getRoot());
                    }catch (Exception e){e.printStackTrace();}
                    break;
                }

                case "DisplayDiskStructure":
                {
                    System.out.println("======================== DISK STRUCTURE ========================");
                    vfs.DisplayDiskStructure();
                    break;
                }
                case "DisplayDiskStatus":
                {
                    vfs.DisplayDiskStatus();
                    break;
                }
                case "deleteFile":{ ///done
                    List<String> directories = new LinkedList<String>(Arrays.asList(parsed[1].split("/")));
                    if(!directories.get(0).equals("root")){
                        System.out.println("The given path must start with 'root'");
                        break;
                    }
                    String dfname = directories.get(directories.size() - 1);
                    directories.remove(directories.size() - 1);
                    try{

                        vfs.getFileOrDirectorytoBeDeleted(dfname, 1, directories, vfs.getRoot() ,false );
                    }catch (Exception e){e.printStackTrace();}
                    break;
                }
                case  "deleteDirectory":{
                    List<String> directories = new LinkedList<String>(Arrays.asList(parsed[1].split("/")));
                    if(!directories.get(0).equals("root")){
                        System.out.println("The given path must start with 'root'");
                        break;
                    }
                    String dfname = directories.get(directories.size() - 1);
                    directories.remove(directories.size() - 1);
                    try {
                        vfs.getFileOrDirectorytoBeDeleted(dfname,1,directories,vfs.getRoot(),true);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;

                }
                case "quit":
                {
                    quit = true;
                    try{
                        vfs.SaveState();
                    }catch (Exception e){e.printStackTrace();}
                    break;
                }

                default:
                {
                    System.out.println("Command not supported");
                }
            }
        }
        reader.close();
    }
}
