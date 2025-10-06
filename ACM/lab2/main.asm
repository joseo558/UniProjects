;
; lab2.asm
;
; Created: 11/10/2023 10:12:01
; Author : Jose
;


; Replace with your application code
;programa principal
.EQU pinoLED=4
.EQU pinoLEDD=5
inicio:
	LDI R16,0b00110000 ; atribui valor a R16
	OUT DDRD,R16 ; tranfere valor de r16 para ddrd (porta i/o)
ciclo:
	CBI PORTD,pinoLED ; limpar bit
	CBI PORTD,pinoLEDD ; limpar bit
	CALL delay
	SBI PORTD,pinoLED ; set portd
	SBI PORTD,pinoLEDD ; set portd
	CALL delay
	JMP ciclo
.INCLUDE "delay.asm"

; incluir a linha em branco anterior a este comentário!!!