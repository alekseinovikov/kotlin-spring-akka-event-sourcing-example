import json

import pika

config = {'host': 'localhost',
          'port': 5672,
          'exchange': 'test_exchange',
          'queue': 'test_queue',
          'routing_key': 'test_queue_routing_key'}


class Publisher:
    def __init__(self, config):
        self.config = config
        self.connection: pika.BlockingConnection = self.__create_connection()
        self.__init_connection_and_declare()

    def __create_connection(self):
        param = pika.ConnectionParameters(host=self.config['host'], port=self.config['port'])
        return pika.BlockingConnection(param)

    def __get_channel(self):
        return self.__create_connection().channel()

    def __init_connection_and_declare(self):
        channel = self.__get_channel()
        channel.exchange_declare(exchange=self.config['exchange'], exchange_type='direct')
        channel.queue_declare(queue=self.config['queue'], durable=True)
        channel.queue_bind(queue=self.config['queue'], exchange=self.config['exchange'], routing_key=self.config['routing_key'])

    @staticmethod
    def __serialize_if_needed(message):
        if isinstance(message, str):
            return message

        return json.dumps(message)

    def publish(self, message):
        payload = self.__serialize_if_needed(message)
        channel = self.__get_channel()
        channel.basic_publish(exchange=self.config['exchange'], routing_key=self.config['routing_key'], body=payload)


def main():
    publisher = Publisher(config)

    classes = ['class-' + str(i) for i in range(100)]
    student = 'student'
    action_add = 'ADD'
    action_delete = 'DELETE'

    for clazz in classes:
        for i in range(10):  # The last i is 9
            if i % 2 == 0:
                action = action_add
            else:
                action = action_delete  # As last i is 9, this action must be the last one (DELETE). So the state must be empty!

            message = {
                'className': clazz,
                'actionType': action,
                'studentName': student
            }
            publisher.publish(message)


if __name__ == "__main__":
    main()
