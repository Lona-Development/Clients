import java.io.*;
import java.net.*;
import java.util.Base64;
import java.util.Random;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;

public class LonaDB {
    private String host;
    private int port;
    private String name;
    private String password;

    public LonaDB(String host, int port, String name, String password) {
        this.host = host;
        this.port = port;
        this.name = name;
        this.password = password;
    }

    private String makeId(int length) {
        StringBuilder result = new StringBuilder();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz";
        Random rand = new Random();
        int counter = 0;

        while (counter < length) {
            result.append(characters.charAt(rand.nextInt(characters.length())));
            counter++;
        }

        return result.toString();
    }

    private String encryptPassword(String password, String key) throws Exception {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(Base64.getDecoder().decode(key), "AES"), new IvParameterSpec(iv));
        byte[] encrypted = cipher.doFinal(password.getBytes());
        return bytesToHex(iv) + ":" + bytesToHex(encrypted);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    public String sendRequest(String action, String data) throws Exception {
        String processID = makeId(5);
        String encryptionKey = Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(processID.getBytes()));

        String encryptedPassword = encryptPassword(password, encryptionKey);

        String request = String.format("{\"action\":\"%s\",\"login\":{\"name\":\"%s\",\"password\":\"%s\"},\"process\":\"%s\",%s}", action, name, encryptedPassword, processID, data);

        Socket socket = new Socket(host, port);
        OutputStream out = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(out, true);
        writer.println(request);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }

        socket.close();

        return response.toString();
    }

    public String createFunction(String name, String content) throws Exception {
        String data = String.format("{\"function\":{\"name\":\"%s\",\"content\":\"%s\"}}", name, content);
        return sendRequest("add_function", data);
    }

    public String executeFunction(String name) throws Exception {
        String data = String.format("{\"name\":\"%s\"}", name);
        return sendRequest("execute_function", data);
    }

    public String getTables(String user) throws Exception {
        String data = String.format("{\"user\":\"%s\"}", user);
        return sendRequest("get_tables", data);
    }

    public String getTableData(String table) throws Exception {
        String data = String.format("{\"table\":\"%s\"}", table);
        return sendRequest("get_table_data", data);
    }

    public String deleteTable(String table) throws Exception {
        String data = String.format("{\"table\":{\"name\":\"%s\"}}", table);
        return sendRequest("delete_table", data);
    }

    public String createTable(String table) throws Exception {
        String data = String.format("{\"table\":{\"name\":\"%s\"}}", table);
        return sendRequest("create_table", data);
    }

    public String set(String table, String name, String value) throws Exception {
        String data = String.format("{\"table\":{\"name\":\"%s\"},\"variable\":{\"name\":\"%s\",\"value\":\"%s\"}}", table, name, value);
        return sendRequest("set_variable", data);
    }

    public String delete(String table, String name) throws Exception {
        String data = String.format("{\"table\":{\"name\":\"%s\"},\"variable\":{\"name\":\"%s\"}}", table, name);
        return sendRequest("remove_variable", data);
    }

    public String get(String table, String name) throws Exception {
        String data = String.format("{\"table\":{\"name\":\"%s\"},\"variable\":{\"name\":\"%s\"}}", table, name);
        return sendRequest("get_variable", data);
    }

    public String getUsers() throws Exception {
        return sendRequest("get_users", "{}");
    }

    public String createUser(String name, String pass) throws Exception {
        String data = String.format("{\"user\":{\"name\":\"%s\",\"password\":\"%s\"}}", name, pass);
        return sendRequest("create_user", data);
    }

    public String deleteUser(String name) throws Exception {
        String data = String.format("{\"user\":{\"name\":\"%s\"}}", name);
        return sendRequest("delete_user", data);
    }

    public String checkPassword(String name, String pass) throws Exception {
        String data = String.format("{\"checkPass\":{\"name\":\"%s\",\"pass\":\"%s\"}}", name, pass);
        return sendRequest("check_password", data);
    }

    public String checkPermission(String name, String permission) throws Exception {
        String data = String.format("{\"permission\":{\"user\":\"%s\",\"name\":\"%s\"}}", name, permission);
        return sendRequest("check_permission", data);
    }

    public String removePermission(String name, String permission) throws Exception {
        String data = String.format("{\"permission\":{\"user\":\"%s\",\"name\":\"%s\"}}", name, permission);
        return sendRequest("remove_permission", data);
    }

    public String getPermissionsRaw(String name) throws Exception {
        String data = String.format("{\"user\":\"%s\"}", name);
        return sendRequest("get_permissions_raw", data);
    }

    public String addPermission(String name, String permission) throws Exception {
        String data = String.format("{\"permission\":{\"user\":\"%s\",\"name\":\"%s\"}}", name, permission);
        return sendRequest("add_permission", data);
    }

    public String eval(String func) throws Exception {
        String data = String.format("{\"function\":\"%s\"}", func);
        return sendRequest("eval", data);
    }
}
