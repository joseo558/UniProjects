# Control LEDs green, yellow and red from ESTS/IPS Shield so that they reflect (in binary) the count from 0 to 7.

Tools:
- Microchip Studio
- Arduido Uno
- Armega328P
- Shield ESTS/IPS
- Avrdude 6.3

Utilization of I/O pins.
Application of masks.
Use of logic instructions (AND, OR, EXOR, shifts...) to control the output value in the I/O pins.
Connect an extra LED that will always be lighted during the program execution.
Count must be automatically incremented every second.

Note: CBI and SBI not allowed.
