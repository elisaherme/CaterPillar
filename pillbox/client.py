import socketio
import sys
from os import environ
slot_to_open = "100"

class socket_sender:
    # init
    sio = socketio.Client()
    # init server ip
    #ip = 'http://35.246.29.217:65080/' 
    ip='http://35.246.29.217:65080/'
    #slot_to_open = "0" 
    
    
    def __init__(self):
        #sio = socketio.Client()
        self.sio.connect(self.ip)
        
    def get_slot(self):
        global slot_to_open
        return slot_to_open
    
    def set_slot(self, data):
        self.slot_to_open = data
        
    # connect to server    
    @sio.on('connect')
    def on_connect():
        print('connection established')
        #sio.emit(getTopic(),getMsg())

    # client receiver
    @sio.on('slot_to_open')
    def on_message(data):
        global slot_to_open
        slot_to_open = data
        print('message received with ', slot_to_open)
        #sio.emit('my response', {'response': 'my response'})

    @sio.on('disconnect')
    def on_disconnect():
        print('disconnected from server')

    # not used...
    def connect_to_server(self):
        print('connect function...')
        print(self.ip)
        self.sio.connect(self.ip)
        self.sio.wait()    

    def getMsg():
        return msg

    def getTopic():
        return topic
    
    # function to send msg to server
    def sendMsg(self, topic, msg):
        self.sio.emit(topic, msg)
        self.sio.sleep(0.5)
        #sio.disconnect()
        
   