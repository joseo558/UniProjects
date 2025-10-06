# Control LEDs green, yellow and red from ESTS/IPS Shield so that they reflect (in binary) the value received from the connected PC keyboard.

Tools:
- Microchip Studio
- Arduido Uno
- Armega328P
- Shield ESTS/IPS
- Avrdude 6.3
- CoolTerm
- USB to UART converter

Utilization of comparison and jump instructions.
Control the ESTS/IPS Shield LEDs so that they reflect (in binary) the value received from the connected PC keyboard (serial port).
Creation and use of routines.
- For numeric values 0 to 7 show in binary the number visualization (red LED the most significant bit, green the least). Extra LED is off.
- For other values all LEDs off except the extra LED.
The serial port input reception and the LED visualization repeats in loop.
The routine developed should control the LEDs based on the value received.

Note: routines for initialization and control of serial port are given.
