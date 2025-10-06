;
; lab2.asm
;
; Created: 11/10/2023 09:53:20
; Author : Jose
;


; Replace with your application code
start:
 LDI R16, 0xFF
OUT DDRB, R16
L1: OUT PORTB, R16
CALL delay
LDI R20, 0
OUT PORTB, R20
CALL delay
RJMP L1
.include "delay.asm"

; colocar a LINHA EM BRANCO anterior a este comentário!!!
