import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserManagerBooter{

    UserManagerBooter(){ }

    // public static UserManager readManager(String file){}

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
                        os.writeUTF(per);
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
