# CaterPillar
Smart Personalised Pill Organiser

The source code for the major components of this project has been compiled and stored in this GitHub repository. Future work and enhancement of CaterPillar proposed can be carried out by forking the repository. 

# Relevant Components
As each components are seperate, each of the corresponding README and Github Repositories are linked below
* Server - [Github](https://github.com/jovanhan2/MobileHealthCare-Server)
* Face Recognition - [Github](https://github.com/zyl115/MHML)
* Adaptive System (Alert Level & Notification Time) - [GitHub](https://github.com/xxyypp/EE4-67-Machine-Learning-Code)

## `app` folder
This folder contains the implementation of the CaterPillar Android tablet application.
The overall activity layout and translation are illustrated in the following diagram:

![Alt text](/app_flow.png?raw=true "Optional Title")

## `pillbox` folder
The folder contains the pillbox_code.py which detects opening of lids and presence of pills, and the client_py which configures the WebSocket. 

The following libraries will need be installed before running the code. 
```
sudo apt-get install python3-spidev python-spidev
```

The pillbox_code.py receives topic `slot_to_open` from the server and turns on the LED assigned to the topic. It also continuously detects the opening of the lid and presence of the pills and sends topics `topic_lid`, `topic_pills` and `topic_correct_lid` to the server.

## `wear` folder
For the Android Wear app.  

`MainActivity.java` sets up the app and starts the service in `SensorDataSender.java` when the user clicks on the `Sleep` button, and stops it when the user clicks on `Stop`. It also has a Data Listener to wait for the next dosage time from the tablet.


`SensorDataSender` starts the service in `SensorReader.java`. These readings are then sent to the tablet application using the Data Layer API. 


`SensorReader` is a `SensorEventListener` set up for listening to the accelerometer sensors in the watch. 

Notes for debugging via Bluetooth:
- Enable Developer Mode in both the phone/tablet and the watch
- On the watch: enable Debugging via Bluetooth and ADB
- On phone/tablet: enable Debugging via Bluetooth in the WearOS app
- Run the following commands on the command line to enable Android Studio to build on the watch
```
adb forward tcp:4444 localabstract:/adb-hub
adb connect 127.0.0.1:4444
adb devices
```


