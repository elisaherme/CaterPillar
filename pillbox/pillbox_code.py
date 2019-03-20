# for this prototype, 2 boxes are used 
# each box has an LED on the lid and arrays of IR 'tripwires' in the compartments
# server will send a state --> which box to open and which LED to light up

import RPi.GPIO as GPIO
import time
from gpiozero import MCP3008
import socketio
import sys
import client
from os import environ

# initial configurations
GPIO.setmode(GPIO.BCM)

# configuration for box 0
IR_0 = MCP3008(0) # analog pin for IR_0
LED_0 = 15 # digital pin of LED_0 
GPIO.setup(LED_0, GPIO.OUT) # set as output
GPIO.output(LED_0, GPIO.LOW) # set LOW
conductor_0 = 14 # digital pin of conductive element 0
GPIO.setup(conductor_0, GPIO.IN, pull_up_down = GPIO.PUD_DOWN) # set as input

# configuration for box 1
IR_1 = MCP3008(1) # analog pin for IR_1
LED_1 = 3 # digital pin for LED_1
GPIO.setup(LED_1, GPIO.OUT) # set as output
GPIO.output(LED_1, GPIO.LOW) # set LOW
conductor_1 = 4 # digital pin of conductive element 1
GPIO.setup(conductor_1, GPIO.IN, pull_up_down = GPIO.PUD_DOWN) # set as input

# configuration for box 2
IR_2 = MCP3008(3) # analog pin for IR_2
LED_2 = 18 # digital pin for LED_2 
GPIO.setup(LED_2, GPIO.OUT) # set as output
GPIO.output(LED_2, GPIO.LOW) # set LOW
conductor_2 = 27 # digital pin of conductive element 2
GPIO.setup(conductor_2, GPIO.IN, pull_up_down = GPIO.PUD_DOWN) # set as input

# configuration for box 3
IR_3 = MCP3008(4) # analog pin for IR_3
LED_3 = 24 # digital pin for LED_3
GPIO.setup(LED_3, GPIO.OUT) # set as output
GPIO.output(LED_3, GPIO.LOW) # set LOW
conductor_3 = 23 # digital pin of conductive element 3
GPIO.setup(conductor_3, GPIO.IN, pull_up_down = GPIO.PUD_DOWN) # set as input

#threshold_0 = 300
threshold = 795 # threshold value for IR phototransistor reading 

msg_sender = client.socket_sender() # initialise the socket object

open_slot = "1"

topic_lid = 'slot_lid' # open: 1, closed: 0
topic_pills = 'pill_presence' # present: 1, absent: 0
topic_correct_lid = "wrong_slot" # default: 0, wrong: 1

LED_power = 0

print("sending data...")

while True:
    # turn on LED depending on which slot set by the UI
    if msg_sender.get_slot() == "1":
    #if open_slot == "1":
        GPIO.output(LED_0, GPIO.HIGH)
        LED_power = 1
    elif msg_sender.get_slot()== "2":
    #elif open_slot == "2":
        GPIO.output(LED_1, GPIO.HIGH)
        LED_power = 1
    elif msg_sender.get_slot()== "3":
    #elif open_slot == "3":
        GPIO.output(LED_2, GPIO.HIGH)
        LED_power = 1
    elif msg_sender.get_slot()== "4":
    #if open_slot == "4":
        GPIO.output(LED_3, GPIO.HIGH)
        LED_power = 1
    else:
        GPIO.output(LED_0, GPIO.LOW)
        GPIO.output(LED_1, GPIO.LOW)
        GPIO.output(LED_2, GPIO.LOW)
        GPIO.output(LED_3, GPIO.LOW)
        LED_power = 0
    #print(msg_sender.slot_to_open)`
    
    lid_char = [0,0,0,0]
    pill_char = [0,0,0,0]
    wrong_slot = 2
    
    if GPIO.input(conductor_0) == 0: # no voltage across conductor indicates box is opened
        lid_char[0] = 1
        if msg_sender.get_slot() != "1" and LED_power == 1: 
        #if open_slot == "1":
            wrong_slot = 1
        elif msg_sender.get_slot == "1" and LED_power == 1:
            wrong_slot = 0
        else:
            wrong_slot = 2
            
    else:
        lid_char[0] = 0
        
    if GPIO.input(conductor_1) == 0: # no voltage across conductor indicates box is opened
        lid_char[1] = 1
        if msg_sender.get_slot() != "2" and LED_power == 1:
        #if open_slot == "2":
            wrong_slot = 1
        elif msg_sender.get_slot == "2" and LED_power == 1:
            wrong_slot = 0
        else:
            wrong_slot = 2
    else:
        lid_char[1] = 0
    
    if GPIO.input(conductor_2) == 0: # no voltage across conductor indicates box is opened
        lid_char[2] = 1
        if msg_sender.get_slot() != "3" and LED_power == 1:
        #if open_slot == "3":
            wrong_slot = 1
        elif msg_sender.get_slot == "3" and LED_power == 1:
            wrong_slot = 0
        else:
            wrong_slot = 2
    else:
        lid_char[2] = 0
    
    if GPIO.input(conductor_3) == 0: # no voltage across conductor indicates box is opened
        lid_char[3] = 1
        if msg_sender.get_slot() != "4" and LED_power == 1:
        #if open_slot == "4":
            wrong_slot = 1
        elif msg_sender.get_slot == "4" and LED_power == 1:
            wrong_slot = 0
        else:
            wrong_slot = 2
    else:
        lid_char[3] = 0

    sensor_value_0 = (IR_0.value*1000)
    sensor_value_1 = (IR_1.value*1000)
    sensor_value_2 = (IR_2.value*1000)
    sensor_value_3 = (IR_3.value*1000)
    print(sensor_value_0)
    print(sensor_value_1)
    print(sensor_value_2)
    print(sensor_value_3)
    
    if sensor_value_0 < threshold:
        pill_char[0] = 1
    else:
        pill_char[0] = 0
    
    if sensor_value_1 < threshold:
        pill_char[1] = 1
    else:
        pill_char[1] = 0
        
    if sensor_value_2 < threshold:
        pill_char[2] = 1
    else:
        pill_char[2] = 0
        
    if sensor_value_3 < threshold:
        pill_char[3] = 1
    else:
        pill_char[3] = 0

    print("lid = ", lid_char)
    print("pill = ", pill_char)
    print("opened slot = ", wrong_slot, '\n')
    msg_sender.sendMsg(topic_lid, lid_char);
    msg_sender.sendMsg(topic_pills, pill_char);
    msg_sender.sendMsg(topic_correct_lid , wrong_slot);
    

	
    #time.sleep(0.5)
			


		
	
	
	
