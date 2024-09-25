import json
import socket
import random
import hashlib
from Crypto.Cipher import AES
from Crypto.Random import get_random_bytes

class LonaDB:
    def __init__(self, host, port, name, password):
        #Import all connection details
        self.host = host
        self.port = port
        self.name = name
        self.password = password

    def make_id(self, length):
        characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz'
        #Generate random string of desired lenght
        return ''.join(random.choice(characters) for _ in range(length))

    async def send_request(self, action, data):
        #Generate ProcessID
        process_id = self.make_id(5)
        #Generate encryption key for passwords
        encryption_key = hashlib.sha256(process_id.encode()).digest().hex()
        #Check if we have to encrypt something else
        if action == "create_user":
            data["user"]["password"] = await self.encrypt_password(data["user"]["password"], encryption_key)
        elif action == "check_password":
            data["checkPass"]["pass"] = await self.encrypt_password(data["checkPass"]["pass"], encryption_key)
        #Encrypt password
        encrypted_password = await self.encrypt_password(self.password, encryption_key)
        #Generate request
        request = json.dumps({
            "action": action,
            "login": {
                "name": self.name,
                "password": encrypted_password
            },
            "process": process_id,
            **data
        })
        #Send request
        with socket.create_connection((self.host, self.port)) as s:
            s.sendall(request.encode())
            response = s.recv(1024).decode()
            #Return response
            return json.loads(response)

    async def encrypt_password(self, password, key):
        #Generate IV and cipher
        iv = get_random_bytes(16)
        cipher = AES.new(key.encode(), AES.MODE_CBC, iv)
        #Encrypt
        encrypted = cipher.encrypt(password.encode())
        #Return IV and encrypted value
        return iv.hex() + ':' + encrypted.hex()

    #All other functions work the same
    def create_function(self, name, content):
        #Generate request to send to the database
        data = {
            'function': {
                'name': name,
                'content': content
            }
        }
        #Send request to the database and return the response
        return self.send_request('add_function', data)

    def execute_function(self, name):
        data = {'name': name}
        return self.send_request('execute_function', data)

    def get_tables(self, user):
        data = {'user': user}
        return self.send_request('get_tables', data)

    def get_table_data(self, table):
        data = {'table': table}
        return self.send_request('get_table_data', data)

    def delete_table(self, table):
        data = {'table': {'name': table}}
        return self.send_request('delete_table', data)

    def create_table(self, table):
        data = {'table': {'name': table}}
        return self.send_request('create_table', data)

    def set_variable(self, table, name, value):
        data = {
            'table': {'name': table},
            'variable': {
                'name': name,
                'value': value
            }
        }
        return self.send_request('set_variable', data)

    def remove_variable(self, table, name):
        data = {
            'table': {'name': table},
            'variable': {'name': name}
        }
        return self.send_request('remove_variable', data)

    def get_variable(self, table, name):
        data = {
            'table': {'name': table},
            'variable': {'name': name}
        }
        return self.send_request('get_variable', data)

    def get_users(self):
        data = {}
        return self.send_request('get_users', data)

    def create_user(self, name, password):
        data = {
            'user': {
                'name': name,
                'password': password
            }
        }
        return self.send_request('create_user', data)

    def delete_user(self, name):
        data = {'user': {'name': name}}
        return self.send_request('delete_user', data)

    def check_password(self, name, password):
        data = {
            'checkPass': {
                'name': name,
                'pass': password
            }
        }
        return self.send_request('check_password', data)

    def check_permission(self, name, permission):
        data = {
            'permission': {
                'user': name,
                'name': permission
            }
        }
        return self.send_request('check_permission', data)

    def remove_permission(self, name, permission):
        data = {
            'permission': {
                'user': name,
                'name': permission
            }
        }
        return self.send_request('remove_permission', data)

    def get_permissions_raw(self, name):
        data = {'user': name}
        return self.send_request('get_permissions_raw', data)

    def add_permission(self, name, permission):
        data = {
            'permission': {
                'user': name,
                'name': permission
            }
        }
        return self.send_request('add_permission', data)

    def eval(self, func):
        data = {'function': func}
        return self.send_request('eval', data)
