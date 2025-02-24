# write  script to ask for userinput and execute command base on 3 choices

import os
import subprocess
import sys
import time

choice = input("Please enter your choice: ")
if  choice == "1":
    print("You have selected option 1")
    subprocess.call("ls -l", shell=True)
    time.sleep(2)   
    os.system("clear")  
    sys.exit()


if  choice == "2":
    print("You have selected option 2")
    subprocess.call("pwd", shell=True)
    time.sleep(2)   
    os.system("clear")  
    sys.exit() 




