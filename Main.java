import java.util.*;

public class Main {
    public static void main(String[] args) {

        int system_size = 10; // Default system size
        if(args.length > 0){
            system_size = Integer.parseInt(args[0]);
        }

        final String permissionsFile = "permissions.dat";

        Scanner reader = new Scanner(System.in);

        VFileSystem vfs = new VFileSystem(system_size);
        UserManager userManager = UserManagerBooter.readManager(vfs, permissionsFile);
        while(userManager.getUser().equals("")){
            System.out.print("Enter username: ");
            String username = reader.nextLine().trim();
            System.out.print("Enter password: ");
            String password = reader.nextLine().trim();
            System.out.println(userManager.login(username, password));
        }

        String userInput = "";
        boolean quit = false;
        while(!quit){
            System.out.print("Enter a command <enter 'quit' to SAVE and exit>: ");
            userInput = reader.nextLine();
            String[] parsed = userInput.split(" ");
            switch(parsed[0])
            {
                case "TellUser":
                {
                    System.out.println(userManager.getActiveUser());
                    break;
                }
                case "CreateUser":
                {
                    System.out.println(userManager.CreateUser(parsed[1], parsed[2]));
                    break;
                }
                case "Login":
                {
                    System.out.println(userManager.login(parsed[1], parsed[2]));
                    break;
                }
                case "Grant":
                {
                    List<String> path = new LinkedList<String>(Arrays.asList(parsed[2].split("/")));
                    System.out.println(userManager.grant(parsed[1], path, parsed[3]));
                    break;
                }
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
                        UserManagerBooter.saveManager(userManager, permissionsFile);
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
