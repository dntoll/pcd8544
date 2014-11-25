#pcd8544 Java library for Raspberry Pi
Forked from the python library by [XavierBerger/pcd8544](https://github.com/XavierBerger/pcd8544) and ported to Java

**This repository contains a Java library to drive PCD8544 LCD (Nokia 5110)**

PCD8544 LCD screen is a small cheap screen originally used into Nokia 3110/5110 handset. This screen is still sold nowaday and can be easily purchased online. It fits very well with Raspberry Pi and opens the world of user interface.

![pcd8544](https://raw.github.com/XavierBerger/pcd8544/master/doc/PCD8544.png)

## Installing the dependencies

This project is dependend on [pi4j](http://pi4j.com)
Install pi4j http://pi4j.com/install.html

    curl -s get.pi4j.com | sudo bash
    TODO: Write and test installation instructions on blank Raspian


The program we will use require `spidev` to be activated. The kernel module should then be activated.
To do so, comment the line `blacklist spi-bcm2708` by adding a heading `#` in the file `/etc/modprobe.d/raspi-blacklist.conf` then reboot the Raspberry Pi to activate this module.

Finally install spidev python library:

    sudo apt-get install python-pip python-dev build-essential 
    sudo pip install spidev

## Building and installing the library

To install the library, execute the following commands:

    git clone https://github.com/dntoll/pcd8544.git
    cd pcd8544


## Wiring the LCD to the Raspberry Pi

The following schema represent how to connect the LCD screen to the Raspberry Pi

![Wiring Schematic](https://raw.github.com/XavierBerger/pcd8544/master/doc/PCD8544wiring.png)

**Note: Check carefully the pin order of your LCD screen, it may be different.**

*Schema made with Fritring (http://fritzing.org)*

## Examples

    TODO: The examples have not yet been ported

The library comes with [examples](https://github.com/XavierBerger/pcd8544/tree/master/examples) showing different feature and library usage.

[![pi_logo](https://raw.github.com/XavierBerger/pcd8544/master/doc/pi_logo.png)](https://github.com/XavierBerger/pcd8544/tree/master/examples)

## Special thanks and references

Special thanks goes to Raspberry Pi community:
 * http://www.raspberrypi.org/phpBB3/viewtopic.php?p=301522#p301522
 * https://github.com/rm-hull/pcd8544 

