import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class UserManagerBooter{

    UserManagerBooter(){ }

    public static UserManager readManager(VFileSystem vfs, String file){
        UserManager manager = new UserManager(vfs);
        try {
            manager.setLoggedIn("admin");
            DataInputStream is = new DataInputStream(new FileInputStream(file));
            int nUsers = is.readInt();
            for(int i = 0; i < nUsers; ++i){
                String line = is.readLine().trim();
                String username = line.split(" ", 2)[0].trim();
                String password = line.split(" ", 2)[1].trim();
                manager.CreateUser(username, password);
            }
            int nPermission = is.readInt();
            for(int i = 0; i < nPermission; ++i){
                String line = is.readLine().trim();
                String[] segments = line.split(":", 2);
                String pathStr = segments[0];
                List<String> path = Arrays.asList(pathStr.split("/"));
                String[] permissions = segments[1].split("\\|");
                for(String permission : permissions){
                    String[] p = permission.split(",");
                    String username = p[0].trim();
                    String access = p[1].trim();
                    manager.grant(username, path, access);
                }
            }
            is.close();
            manager.setLoggedIn("");
        } catch (FileNotFoundException e) {
            // No state, automatic admin
        } catch (IOException e) { }
        return manager;
    }

    public static void saveManager(UserManager manager, String filename){
        // Get all users
        HashMap<String, String> users = manager.getUsers();
        HashMap<List<String>, ArrayList<HashMap<String, String>>> permissions = manager.getPermissions();
        File file = new File(filename);
        try {
            file.createNewFile();
            DataOutputStream os = new DataOutputStream(new FileOutputStream(file));
            // Write number of users
            os.writeInt(users.size());
            // Write users and passwords
            for(String user : users.keySet()){
                os.writeUTF(user + " ");
                os.writeUTF(users.get(user) + "\n");
            }
            // Write permissions size
            os.writeInt(permissions.size());
            // Write permissions one by one
            for(List<String> path : permissions.keySet()){
                String key = pathToString(path);
                os.writeUTF(key + ":");
                for(HashMap<String, String> permission : permissions.get(path)){
                    for(String user : permission.keySet()){
                        String per = permission.get(user);
                        os.writeUTF(user + ",");
                        os.writeUTF(per + "|");
                    }
                }
            }
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String pathToString(List<String> path){
        return String.join("/", path);
    }
};
