import json
import socket

class LonaDB:
    def __init__(self, host, port, name, password):
        self.host = host
        self.port = port
        self.name = name
        self.password = password

    def make_id(self, length):
        import random
        import string
        return ''.join(random.choice(string.ascii_letters + string.digits) for _ in range(length))

    def send_request(self, action, data):
        process_id = self.make_id(5)
        request = json.dumps({
            'action': action,
            'login': {
                'name': self.name,
                'password': self.password
            },
            'process': process_id,
            **data
        })

        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.connect((self.host, self.port))
            s.sendall(request.encode())
            response = s.recv(4096).decode()
            return json.loads(response)

    def create_function(self, name, content):
        data = {
            'function': {
                'name': name,
                'content': content
            }
        }
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