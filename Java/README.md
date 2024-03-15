## Installation

You can use the LonaDB Java Client by adding it as a dependency in your Maven project. Follow these steps to include LonaDB in your project:

1. Add the GitHub Packages repository to your `pom.xml` file:

```xml
<repositories>
    <repository>
        <id>github</id>
        <name>GitHub Packages</name>
        <url>https://maven.pkg.github.com/LonaDB/Clients</url>
    </repository>
</repositories>
```

2. Add the LonaDB dependency to your `pom.xml` file:

```xml
<dependencies>
    <dependency>
        <groupId>org.lona-development.java-client</groupId>
        <artifactId>lonadb</artifactId>
        <version>2.1.3</version>
    </dependency>
</dependencies>
```

## Usage

To use the `LonaDB` Java Client, follow these steps:

1. Import the `LonaDB` class in your Java file:

```java
import org.lona_development.java_client.lonadb;
```

2. Create an instance of the `lonadb` class by providing the required connection details:

```java
lonadb client = new lonadb(host, port, name, password);
```

Replace `host`, `port`, `name`, and `password` with the appropriate values for your LonaDB Server.

3. Use the provided methods to interact with the server:

```java
// Example: Get a list of tables
String tables = client.getTables("username");

// Display the list of tables
System.out.println(tables);
```

## Available Methods

The following methods are available in the `LonaDB` Java Client:

- `getTables(username)`: Retrieves a list of tables available in the database.
- `getTableData(table)`: Retrieves data from a specified table.
- `deleteTable(name)`: Deletes a table by its name.
- `createTable(name)`: Creates a new table with the given name.
- `set(table, name, value)`: Sets a variable within a table to the specified value.
- `delete(table, name)`: Deletes a variable from a table.
- `get(table, name)`: Retrieves the value of a variable from a table.
- `getUsers()`: Retrieves a list of users in the database.
- `createUser(name, password)`: Creates a new user with the given name and password.
- `deleteUser(name)`: Deletes a user by their name.
- `checkPassword(name, password)`: Checks if the provided password is correct for a given user.
- `checkPermission(user, permission)`: Checks if a user has a specific permission.
- `removePermission(user, permission)`: Removes a permission from a user.
- `getPermissionsRaw(name)`: Retrieves the raw permission data for a user.
- `addPermission(user, permission)`: Adds a permission to a user.
- `createFunction(name, content)`: Create a function which can be executed whenever you want. Just like eval. Content = string of Java code.
- `executeFunction(name)`: Executes the function.
- `eval(function)`: Runs the function (must be a string of Java code).

## License

This project is licensed under the GNU Affero General Public License version 3 (GNU AGPL-3.0).