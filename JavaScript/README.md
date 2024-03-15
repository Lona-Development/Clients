## Installation

You can install the JavaScript-Client library using npm:

```bash
npm install lonadb-client
```

## Usage

To use the LonaDB Client library, follow these steps:

1. Import the required modules and classes:

```javascript
const LonaClient = require("lonadb-client");
```

2. Create an instance of the `LonaDB-Client` class:

```javascript
const client = new LonaClient(host, port, name, password);
```

Replace `host`, `port`, `name`, and `password` with the appropriate values for your LonaDB Server.

3. Use the provided methods to interact with the server:

```javascript
// Example: Get a list of tables
client.getTables()
    .then(tables => {
        console.log("List of tables:");
        console.log(tables);
    })
    .catch(error => {
        console.error("Error:", error);
    });
```

## Available Methods

### `getTables(username)`

Retrieves a list of tables available in the database.

### `getTableData(table)`

Retrieves data from a specified table.

### `deleteTable(name)`

Deletes a table by its name.

### `createTable(name)`

Creates a new table with the given name.

### `set(table, name, value)`

Sets a variable within a table to the specified value.

### `delete(table, name)`

Deletes a variable from a table.

### `get(table, name)`

Retrieves the value of a variable from a table.

### `getUsers()`

Retrieves a list of users in the database.

### `createUser(name, password)`

Creates a new user with the given name and password.

### `deleteUser(name)`

Deletes a user by their name.

### `checkPassword(name, password)`

Checks if the provided password is correct for a given user.

### `checkPermission(user, permission)`

Checks if a user has a specific permission.

### `removePermission(user, permission)`

Removes a permission from a user.

### `getPermissionsRaw(name)`

Retrieves the raw permission data for a user.

### `addPermission(user, permission)`

Adds a permission to a user.

### `createFunction(name, content)`

Create a function which can be executed whenever you want. Just like eval.
Content = string of PHP code

### `executeFunction(name)`

Executes the function

### `eval(function)`

Runs the function (must be a string of PHP code) </br>
Example: "if($abc === 1234) return 'wtf';"
Response: {"success": true, "response": "wtf", "process": processID}

## License

This project is licensed under the GNU Affero General Public License version 3 (GNU AGPL-3.0)