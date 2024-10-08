import socket
import json
import hashlib
import os
import asyncio
from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
from cryptography.hazmat.backends import default_backend

class LonaDB:
    def __init__(self, host, port, name, password):
        # Import connection details
        self.host = host
        self.port = port
        self.name = name
        self.password = password

    def makeid(self, length):
        # Generate a random string of specified length
        characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz'
        return ''.join(os.urandom(1).decode(errors='ignore') for _ in range(length))

    async def send_request(self, action, data):
        # Generate ProcessID
        process_id = self.makeid(5)
        # Generate encryptionKey for encrypting passwords
        encryption_key = hashlib.sha256(process_id.encode()).digest()

        # Encrypt password if needed
        if action == "create_user":
            data['user']['password'] = self.encrypt_password(data['user']['password'], encryption_key)
        elif action == "check_password":
            data['checkPass']['pass'] = self.encrypt_password(data['checkPass']['pass'], encryption_key)
        
        # Encrypt the login password
        encrypted_password = self.encrypt_password(self.password, encryption_key)
        
        # Create the request data
        request_data = {
            "action": action,
            "login": {
                "name": self.name,
                "password": encrypted_password
            },
            "process": process_id
        }
        request_data.update(data)

        # Convert request data to JSON
        request_json = json.dumps(request_data)

        # Send request via socket
        reader, writer = await asyncio.open_connection(self.host, self.port)

        writer.write(request_json.encode())
        await writer.drain()

        # Read the response from the server
        response_data = await reader.read(2048)
        writer.close()
        await writer.wait_closed()

        # Parse the response and return it
        return json.loads(response_data.decode())

    def encrypt_password(self, password, key):
        # Generate IV and create cipher
        iv = os.urandom(16)
        cipher = Cipher(algorithms.AES(key), modes.CBC(iv), backend=default_backend())
        encryptor = cipher.encryptor()

        # Pad password to block size (16 bytes)
        padding_length = 16 - len(password) % 16
        password_padded = password + chr(padding_length) * padding_length

        # Encrypt the password
        encrypted = encryptor.update(password_padded.encode()) + encryptor.finalize()

        # Return IV and encrypted password
        return iv.hex() + ':' + encrypted.hex()

    # All other functions work similarly
    async def create_function(self, name, content):
        data = {
            "function": {
                "name": name,
                "content": content
            }
        }
        return await self.send_request("add_function", data)

    async def execute_function(self, name):
        data = {
            "name": name
        }
        return await self.send_request("execute_function", data)

    async def delete_function(self, name):
        data = {
            "function": {
                "name": name
            }
        }
        return await self.send_request("delete_function", data)

    async def get_tables(self, user):
        data = {
            "user": user
        }
        return await self.send_request("get_tables", data)

    async def get_table_data(self, table):
        data = {
            "table": table
        }
        return await self.send_request("get_table_data", data)

    async def delete_table(self, table):
        data = {
            "table": {"name": table}
        }
        return await self.send_request("delete_table", data)

    async def create_table(self, table):
        data = {
            "table": {"name": table}
        }
        return await self.send_request("create_table", data)

    async def set(self, table, name, value):
        data = {
            "table": {"name": table},
            "variable": {
                "name": name,
                "value": value
            }
        }
        return await self.send_request("set_variable", data)

    async def delete(self, table, name):
        data = {
            "table": {"name": table},
            "variable": {"name": name}
        }
        return await self.send_request("remove_variable", data)

    async def get(self, table, name):
        data = {
            "table": {"name": table},
            "variable": {"name": name}
        }
        return await self.send_request("get_variable", data)

    async def get_users(self):
        data = {}
        return await self.send_request("get_users", data)

    async def create_user(self, name, passw):
        data = {
            "user": {
                "name": name,
                "password": passw
            }
        }
        return await self.send_request("create_user", data)

    async def delete_user(self, name):
        data = {
            "user": {
                "name": name
            }
        }
        return await self.send_request("delete_user", data)

    async def check_password(self, name, passw):
        data = {
            "checkPass": {
                "name": name,
                "pass": passw
            }
        }
        return await self.send_request("check_password", data)

    async def check_permission(self, name, permission):
        data = {
            "permission": {
                "user": name,
                "name": permission
            }
        }
        return await self.send_request("check_permission", data)

    async def remove_permission(self, name, permission):
        data = {
            "permission": {
                "user": name,
                "name": permission
            }
        }
        return await self.send_request("remove_permission", data)

    async def get_permissions_raw(self, name):
        data = {
            "user": name
        }
        return await self.send_request("get_permissions_raw", data)

    async def add_permission(self, name, permission):
        data = {
            "permission": {
                "user": name,
                "name": permission
            }
        }
        return await self.send_request("add_permission", data)

    async def eval(self, func):
        data = {
            "function": func
        }
        return await self.send_request("eval", data)