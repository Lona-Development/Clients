import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    private String makeid(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }
        return result.toString();
    }

    private String encryptPassword(String password, String key) throws Exception {
        // Hash the key
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBuffer = sha.digest(key.getBytes());

        // Generate IV
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);

        // Encrypt
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBuffer, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(password.getBytes());

        // Convert to hex and concatenate IV and encrypted data
        return bytesToHex(iv) + ":" + bytesToHex(encrypted);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private Map<String, Object> sendRequest(String action, Map<String, Object> data) throws Exception {
        // Create socket
        Socket socket = new Socket(host, port);

        // Generate ProcessID
        String processID = makeid(5);

        // Encrypt password
        String encryptedPassword = encryptPassword(this.password, processID);

        // Build request data
        Map<String, Object> request = new HashMap<>();
        request.put("action", action);
        Map<String, String> login = new HashMap<>();
        login.put("name", this.name);
        login.put("password", encryptedPassword);
        request.put("login", login);
        request.put("process", processID);
        request.putAll(data);

        // Send request to database
        OutputStream outputStream = socket.getOutputStream();
        PrintWriter writer = new PrintWriter(outputStream, true);
        writer.println(new ObjectMapper().writeValueAsString(request));

        // Read response
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String response = reader.readLine();

        // Close socket
        socket.close();

        return new ObjectMapper().readValue(response, Map.class);
    }

    public Map<String, Object> createFunction(String name, String content) throws Exception {
        Map<String, Object> data = new HashMap<>();
        Map<String, String> functionData = new HashMap<>();
        functionData.put("name", name);
        functionData.put("content", content);
        data.put("function", functionData);

        return sendRequest("add_function", data);
    }

    public Map<String, Object> executeFunction(String name) throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        return sendRequest("execute_function", data);
    }

    public Map<String, Object> getTables(String user) throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        return sendRequest("get_tables", data);
    }

    public Map<String, Object> getTableData(String table) throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("table", table);
        return sendRequest("get_table_data", data);
    }

    public Map<String, Object> deleteTable(String table) throws Exception {
        Map<String, Object> data = new HashMap<>();
        Map<String, String> tableData = new HashMap<>();
        tableData.put("name", table);
        data.put("table", tableData);

        return sendRequest("delete_table", data);
    }

    public Map<String, Object> createTable(String table) throws Exception {
        Map<String, Object> data = new HashMap<>();
        Map<String, String> tableData = new HashMap<>();
        tableData.put("name", table);
        data.put("table", tableData);

        return sendRequest("create_table", data);
    }

    public Map<String, Object> set(String table, String name, String value) throws Exception {
        Map<String, Object> data = new HashMap<>();
        Map<String, String> tableData = new HashMap<>();
        tableData.put("name", table);
        data.put("table", tableData);
        Map<String, String> variable = new HashMap<>();
        variable.put("name", name);
        variable.put("value", value);
        data.put("variable", variable);

        return sendRequest("set_variable", data);
    }

    public Map<String, Object> delete(String table, String name) throws Exception {
        Map<String, Object> data = new HashMap<>();
        Map<String, String> tableData = new HashMap<>();
        tableData.put("name", table);
        data.put("table", tableData);
        Map<String, String> variable = new HashMap<>();
        variable.put("name", name);
        data.put("variable", variable);

        return sendRequest("remove_variable", data);
    }

    public Map<String, Object> get(String table, String name) throws Exception {
        Map<String, Object> data = new HashMap<>();
        Map<String, String> tableData = new HashMap<>();
        tableData.put("name", table);
        data.put("table", tableData);
        Map<String, String> variable = new HashMap<>();
        variable.put("name", name);
        data.put("variable", variable);

        return sendRequest("get_variable", data);
    }

    public Map<String, Object> createUser(String name, String pass) throws Exception {
        Map<String, Object> data = new HashMap<>();
        Map<String, String> user = new HashMap<>();
        user.put("name", name);
        user.put("password", pass);
        data.put("user", user);

        return sendRequest("create_user", data);
    }

    public Map<String, Object> deleteUser(String name) throws Exception {
        Map<String, Object> data = new HashMap<>();
        Map<String, String> user = new HashMap<>();
        user.put("name", name);
        data.put("user", user);

        return sendRequest("delete_user", data);
    }

    public Map<String, Object> checkPassword(String name, String pass) throws Exception {
        Map<String, Object> data = new HashMap<>();
        Map<String, String> checkPass = new HashMap<>();
        checkPass.put("name", name);
        checkPass.put("pass", pass);
        data.put("checkPass", checkPass);

        return sendRequest("check_password", data);
    }

    public Map<String, Object> getUsers() throws Exception {
        Map<String, Object> data = new HashMap<>();
        return sendRequest("get_users", data);
    }

    public Map<String, Object> addPermission(String name, String permission) throws Exception {
        Map<String, Object> data = new HashMap<>();
        Map<String, String> permissionData = new HashMap<>();
        permissionData.put("user", name);
        permissionData.put("name", permission);
        data.put("permission", permissionData);

        return sendRequest("add_permission", data);
    }

    public Map<String, Object> removePermission(String name, String permission) throws Exception {
        Map<String, Object> data = new HashMap<>();
        Map<String, String> permissionData = new HashMap<>();
        permissionData.put("user", name);
        permissionData.put("name", permission);
        data.put("permission", permissionData);

        return sendRequest("remove_permission", data);
    }

    public Map<String, Object> checkPermission(String name, String permission) throws Exception {
        Map<String, Object> data = new HashMap<>();
        Map<String, String> permissionData = new HashMap<>();
        permissionData.put("user", name);
        permissionData.put("name", permission);
        data.put("permission", permissionData);

        return sendRequest("check_permission", data);
    }

    public Map<String, Object> getPermissionsRaw(String name) throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("user", name);

        return sendRequest("get_permissions_raw", data);
    }

    public Map<String, Object> eval(String func) throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("function", func);

        return sendRequest("eval", data);
    }
}
