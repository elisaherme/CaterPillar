# CaterPillar
Smart Personalised Pill Organiser

## `app` folder
For the tablet app.

## `pillbox` folder
The folder contains the pillbox_code.py which detects opening of lids and presence of pills, and the client_py which configures the WebSocket. 

The following libraries will need be installed before running the code. 
1. sudo apt-get install python3-spidev python-spidev

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

# Relevant 
* Server - [Github](https://github.com/jovanhan2/MobileHealthCare-Server)
* Face Recognition - [Github](https://github.com/zyl115/MHML)
* Adaptive System (Alert Level & Notification Time) - [GitHub](https://github.com/xxyypp/EE4-67-Machine-Learning-Code)
