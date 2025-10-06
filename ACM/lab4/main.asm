;
; lab42.asm
;
; Created: 29/11/2023 10:35:28
; Author : Jose
;


; Replace with your application code
start:
    call USART_Init
	.EQU maximo=7 ; 7 máximo contagem binária (3bits=1+2+4)
	LDI R18,0b00000100 ; valor led extra
	LDI R19,0b00000000 ; bit mais sig a mover
	LDI R20,0b00000000 ; bit menos sig a mover
	LDI R21,0x30 ; 0 hex
	LDI R22,0x38 ; 8 hex
	LDI R23,0b01110100 ; pins dos leds
	OUT DDRD, R23
	OUT PORTD, R19
	; R17 contém caracter recebido
check:
	call USART_Receive
	CP R17, R21 ; >=0?
	BRGE maior
	OUT PORTD, R18
	jmp check
maior:
	CP R17, R22 ; <8?
	BRLT isnumber
	OUT PORTD, R18
	jmp check
isnumber:
	SUB R17, R21 ; convert hex to binary by subtrating hex value of 0
	LSL R17 ; mover bit de contagem para os bits associados a pins dos leds (posições 6, 5 e 4)
	LSL R17
	LSL R17
	LSL R17
	MOV R19, R17
	MOV R20, R17
	; reordenar devido à diferente ordem dos pins no hardware vs. contagem
	ANDI R19, 0b01100000 ; máscara para reposicionar o bit mais significativo no meio dos leds
	LSR R19
	ANDI R20, 0b00010000 ; máscara para reposicionar o bit menos significativo no mais significativo dos leds
	LSL R20
	LSL R20
	; ors
	OR R19, R20
	OUT PORTD, R19
	LDI R17, 0x00
	jmp check
.INCLUDE "rotinas.asm"

