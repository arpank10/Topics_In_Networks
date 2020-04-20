# CS 544: Topics in Networks, IIT Guwahati

 Assignment 2

 Steps to Run

1. compile all the java files in the PacketSwitch folder by running the command 'javac *.java'
2. run the command 'java PacketSwitchController switchportcount buffersize packetgenprob queueType knockout outputfile maxtimeslots' according to the parameter list given in the question
3. Example 'java PacketSwitchController 8 4 0.5 INQ 4 output.txt 10000'. Copying from the report won't work, if you want to copy, copy from here.                
4. The output will be displayed on the terminal. The output file will be created, if it doesn't exist else output will be appended to it.