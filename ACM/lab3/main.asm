;
; lab3.asm
;
; Created: 08/11/2023 09:51:56
; Author : Jose
;


; Replace with your application code
.EQU maximo=7 ; 7 máximo contagem binária (3bits=1+2+4)
inicio:
	LDI R16,0b00000000 ; final
	LDI R17,0b00000000 ; bit a mover
	LDI R18,0b00000000 ; contagem
	LDI R19,0b01110100 ; registo pin extra e pins dos leds
	OUT DDRD,R19 ; tranfere valor de r19 para ddrd (porta i/o), configura pins para out
	LDI R19,0b00000100 ; registo pin extra
	OUT PORTD,R19 ; activar pin extra
ciclo:
	LSL R18 ; mover bit de contagem para os bits associados a pins dos leds (posições 6, 5 e 4)
	LSL R18
	LSL R18
	LSL R18
	; reordenar devido à diferente ordem dos pins no hardware vs. contagem
	LSL R18
	MOV R17, R18
	ANDI R17, 0b10000000 ; máscara para reposicionar o bit mais significativo no menos significativo dos leds (100 na contagem passa a 001)
	LSR R17
	LSR R17
	LSR R17
	OR R18, R17

	; add pin extra
	MOV R16, R19
	OR R16, R18
	; out
	OUT PORTD, R16
	; reposicionar para continuar a contagem
	LSR R18
	LSR R18
	LSR R18
	LSR R18
	LSR R18
	ANDI R18, maximo ; se >7 volta a 0
	INC R18 ; increment
	;CALL delay
	JMP ciclo ; reninicia o ciclo
.INCLUDE "delay.asm"
