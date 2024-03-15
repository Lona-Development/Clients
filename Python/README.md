## Installation

You can install the LonaDB Python Client via pip:

```bash
pip install lonadb-client
```

## Usage

To use the `LonaDB` Python Client, follow these steps:

1. Import the `LonaDB` class into your Python script:

```python
from lonadb_client import LonaDB
```

2. Create an instance of the `LonaDB` class by providing the required connection details:

```python
client = LonaDB(host, port, name, password)
```

Replace `host`, `port`, `name`, and `password` with the appropriate values for your LonaDB server.

3. Use the provided methods to interact with the server:

```python
# Example: Get a list of tables
tables = client.get_tables("username");

# Display the list of tables
print(tables)
```

## Available Methods

### `get_tables(user)`

Retrieves a list of tables available in the database.

### `get_table_data(table)`

Retrieves data from a specified table.

### `delete_table(table)`

Deletes a table by its name.

### `create_table(table)`

Creates a new table with the given name.

### `set_variable(table, name, value)`

Sets a variable within a table to the specified value.

### `remove_variable(table, name)`

Deletes a variable from a table.

### `get_variable(table, name)`

Retrieves the value of a variable from a table.

### `get_users()`

Retrieves a list of users in the database.

### `create_user(name, password)`

Creates a new user with the given name and password.

### `delete_user(name)`

Deletes a user by their name.

### `check_password(name, password)`

Checks if the provided password is correct for a given user.

### `check_permission(name, permission)`

Checks if a user has a specific permission.

### `remove_permission(name, permission)`

Removes a permission from a user.

### `get_permissions_raw(name)`

Retrieves the raw permission data for a user.

### `add_permission(name, permission)`

Adds a permission to a user.

### `create_function(name, content)`

Create a function which can be executed whenever you want. Just like eval.
Content = string of PHP code

### `execute_function(name)`

Executes the function

### `eval(func)`

Runs the function (must be a string of PHP code)
Example: "if ($abc == 1234) return 'wtf';"
Response: {"success": True, "response": "wtf", "process": processID}

## License

This project is licensed under the GNU Affero General Public License version 3 (GNU AGPL-3.0).