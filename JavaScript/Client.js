const crypto = require('crypto');

class LonaDB {
    constructor(host, port, name, password) {
        this.host = host;
        this.port = port;
        this.name = name;
        this.password = password;
    }

    makeid(length) {
        let result = '';
        const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz';
        let counter = 0;

        while (counter < length) {
            result += characters[Math.floor(Math.random() * characters.length)];
            counter += 1;
        }

        return result;
    }

    sendRequest = async function(action, data) {
        let net = require('net');
        let processID = this.makeid(5);

        let encryptionKey = crypto.createHash('sha256').update(processID).digest('base64');

        let encryptedPassword = await this.encryptPassword(this.password, encryptionKey);

        let request = JSON.stringify({
            action,
            login: {
                name: this.name,
                password: encryptedPassword
            },
            process: processID,
            ...data
        });

        return new Promise((resolve, reject) => {
            let socket = net.createConnection({
                host: this.host,
                port: this.port
            }, () => {
                socket.write(request);
            });

            let response = '';

            socket.on('data', (chunk) => {
                response += chunk;
            });

            socket.on('end', () => {
                resolve(JSON.parse(response));
            });

            socket.on('error', (err) => {
                reject({ err: err.message });
            });
        });
    }


    encryptPassword(password, key) {
        const iv = crypto.randomBytes(16);
        const cipher = crypto.createCipheriv('aes-256-cbc', Buffer.from(key, 'base64'), iv);
    
        let encrypted = cipher.update(password);
        encrypted = Buffer.concat([encrypted, cipher.final()]);
    
        return iv.toString('hex') + ':' + encrypted.toString('hex');
    }
    
    createFunction(name, content) {
        const data = {
          "function": {
            "name": name,
            "content": content
          }
        };
    
        return this.sendRequest("add_function", data);
    }
        
    executeFunction(name) {
        const data = {
          "name": name
        };
    
        return this.sendRequest("execute_function", data);
    }

    getTables(user) {
        const data = {
            user
        };

        return this.sendRequest('get_tables', data);
    }

    getTableData(table) {
        const data = {
            table
        };

        return this.sendRequest('get_table_data', data);
    }

    deleteTable(table) {
        const data = {
            table: { name: table }
        };

        return this.sendRequest('delete_table', data);
    }

    createTable(table) {
        const data = {
            table: { name: table }
        };

        return this.sendRequest('create_table', data);
    }

    set(table, name, value) {
        const data = {
            table: { name: table },
            variable: {
                name: name,
                value: value
            }
        };

        return this.sendRequest('set_variable', data);
    }

    delete(table, name) {
        const data = {
            table: { name: table },
            variable: { name: name }
        };

        return this.sendRequest('remove_variable', data);
    }

    get(table, name) {
        const data = {
            table: { name: table },
            variable: { name: name }
        };

        return this.sendRequest('get_variable', data);
    }

    getUsers() {
        const data = {};
        return this.sendRequest('get_users', data);
    }

    createUser(name, pass) {
        const data = {
            user: {
                name: name,
                password: pass
            }
        };

        return this.sendRequest('create_user', data);
    }

    deleteUser(name) {
        const data = {
            user: {
                name: name
            }
        };

        return this.sendRequest('delete_user', data);
    }

    checkPassword(name, pass) {
        const data = {
            checkPass: {
                name: name,
                pass: pass
            }
        };

        return this.sendRequest('check_password', data);
    }

    checkPermission(name, permission) {
        const data = {
            permission: {
                user: name,
                name: permission
            }
        };

        return this.sendRequest('check_permission', data);
    }

    removePermission(name, permission) {
        const data = {
            permission: {
                user: name,
                name: permission
            }
        };

        return this.sendRequest('remove_permission', data);
    }

    getPermissionsRaw(name) {
        const data = {
            user: name
        };

        return this.sendRequest('get_permissions_raw', data);
    }

    addPermission(name, permission) {
        const data = {
            permission: {
                user: name,
                name: permission
            }
        };

        return this.sendRequest('add_permission', data);
    }

    eval(func) {
        const data = {
            function: func
        };

        return this.sendRequest('eval', data);
    }
}

module.exports = LonaDB;
