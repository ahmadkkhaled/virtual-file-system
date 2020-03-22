import java.util.*;

public class Main {
    public static void main(String[] args) {
        VFileSystem vfs = new VFileSystem();

        Scanner reader = new Scanner(System.in);
        String userInput = "";
        boolean quit = false;
        while(!quit){
            System.out.print("Enter a command <enter 'quit' to exit>: ");
            userInput = reader.nextLine();
            String parsed[] = userInput.split(" ");
            if(parsed.length > 2){
                System.out.println("Please omit whitespaces from the given command string");
                continue;
            }
            switch(parsed[0])
            {
                case "CreateFile":
                {
                    List<String> directories = new LinkedList<String>(Arrays.asList(parsed[1].split("/")));
                    if(!directories.get(0).equals("root")){
                        System.out.println("The given path must start with 'root'");
                        break;
                    }
                    String fileName = directories.get(directories.size() - 1);
                    directories.remove(directories.size() - 1);
                    try{
                        vfs.CreateFile(fileName, 1, directories, vfs.getRoot());
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
                case "quit":
                {
                    quit = true;
                    break;
                }

                default:
                {
                    System.out.println("No valid command was given");
                }
            }
        }
    }
}
