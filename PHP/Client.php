<?php

class LonaDB {
  //Create all needed variables
  private $host;
  private $port;
  private $name;
  private $password;

  public function __construct($host, $port, $name, $password) {
    //Import all connection details
    $this->host = $host;
    $this->port = $port;
    $this->name = $name;
    $this->password = $password;
  }

  private function makeid($length) {
    $result = "";
    $characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz";
    $counter = 0;
    //Add random characters from $characters to $result until it has reached the dired lenght
    while ($counter < $length) {
      $result .= $characters[mt_rand(0, strlen($characters) - 1)];
      $counter += 1;
    }
    return $result;
  }
	
	private function encryptPassword($password, $key) {
    //Hash our encryption key (ProcessID)
    $keyBuffer = hash('sha256', $key, true);
    //Generate IV
    $iv = openssl_random_pseudo_bytes(16);
    //Encrypt
    $encrypted = openssl_encrypt($password, 'aes-256-cbc', $keyBuffer, OPENSSL_RAW_DATA, $iv);
    //Add the IV to the encrypted value
    $encryptedString = bin2hex($iv) . ':' . bin2hex($encrypted);
    return $encryptedString;
  }

  private function sendRequest($action, $data) {
    //Create socket
    $socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
    //Check if socket was created successfully
    if (!$socket) return ["err" => socket_strerror(socket_last_error())];
    //Try connecting to the database
    $result = socket_connect($socket, $this->host, $this->port);
    //Check if connection was successfull
    if (!$result) return ["err" => socket_strerror(socket_last_error($socket))];
    //Generate ProcessID
    $processID = $this->makeid(5);
    //Check if we need to encrypt something other than our own password
    switch($action){
      case "create_user":
        $data['user']['password'] = $this->encryptPassword($data['user']['password'], $processID);
        break;
      case "check_password":
        $data['checkPass']['pass'] = $this->encryptPassword($data['checkPass']['pass'], $processID);
        break;
    }
		//Encrypt password
		$encryptedPassword = $this->encryptPassword($this->password, $processID);
    //Generate request array
    $request = json_encode([
      "action" => $action,
      "login" => [
        "name" => $this->name,
        "password" => $encryptedPassword
      ],
      "process" => $processID
    ] + $data);
    //Send request to database
    socket_write($socket, $request, strlen($request));
    //Read response form database
    $response = socket_read($socket, 2048);
    //Close socket
    socket_close($socket);
    //Give back the response
    return json_decode($response, true);
  }

	//All other functions work the same
	public function createFunction($name, $content) {
    //Generate request to send to the database
    $data = [
      "function" => [
				"name" => $name,
				"content" => $content
      ]
    ];
    //Send request to the database and return the response
    return $this->sendRequest("add_function", $data);
  }
	
  public function executeFunction($name) {
    $data = [
      "name" => $name
    ];

    return $this->sendRequest("execute_function", $data);
  }

  public function getTables($user) {
    $data = [
      "user" => $user
    ];

    return $this->sendRequest("get_tables", $data);
  }

  public function getTableData($table) {
    $data = [
      "table" => $table
    ];

    return $this->sendRequest("get_table_data", $data);
  }
  
  public function deleteTable($table) {
    $data = [
      "table" => ["name" => $table]
    ];
          
    return $this->sendRequest("delete_table", $data);
  }
  
  public function createTable($table) {
    $data = [
      "table" => ["name" => $table]
    ];
        
    return $this->sendRequest("create_table", $data);
  }
  
  public function set($table, $name, $value) {
    $data = [
      "table" => ["name" => $table],
      "variable" => [
        "name" => $name,
        "value" => $value
      ]
    ];
      
    return $this->sendRequest("set_variable", $data);
  }
  
  public function delete($table, $name){
    $data = [
      "table" => ["name" => $table],
      "variable" => ["name" => $name]
    ];
        
    return $this->sendRequest("remove_variable", $data);
  }
  
  public function get($table, $name){
    $data = [
      "table" => ["name" => $table],
      "variable" => [
        "name" => $name
      ]
    ];
      
    return $this->sendRequest("get_variable", $data);
  }
  
  public function getUsers(){
    $data = [];
      
    return $this->sendRequest("get_users", $data);
  }
  
  public function createUser($name, $pass){
    $data = [
      "user" => [
        "name" => $name,
        "password" => $pass
      ]
    ];
      
    return $this->sendRequest("create_user", $data);
  }
  
  public function deleteUser($name){
    $data = [
      "user" => [
        "name" => $name
      ]
    ];
      
    return $this->sendRequest("delete_user", $data);
  }
  
  public function checkPassword($name, $pass){
    $data = [
      "checkPass" => [
        "name" => $name,
        "pass" => $pass
      ]
    ];
      
    return $this->sendRequest("check_password", $data);
  }
  
  public function checkPermission($name, $permission){
    $data = [
      "permission" => [
        "user" => $name,
        "name" => $permission
      ]
    ];
    
    return $this->sendRequest("check_permission", $data);
  }
  
	public function removePermission($name, $permission){
    $data = [
      "permission" => [
        "user" => $name,
        "name" => $permission
      ]
    ];
      
    return $this->sendRequest("remove_permission", $data);
  }

  public function getPermissionsRaw($name){
    $data = [
      "user" => $name
    ];
      
    return $this->sendRequest("get_permissions_raw", $data);
  }

  public function addPermission($name){
    $data = [
      "permission" => [
        "user" => $name,
        "name" => $permission
      ]
    ];
      
    return $this->sendRequest("add_permission", $data);
  }

  public function eval($func) {
    $data = [
      "function" => $func
    ];

    return $this->sendRequest("eval", $data);
  }
}

?>