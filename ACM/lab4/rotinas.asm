;ROTINAS
;configuração da porta série
; Deve ser chamada uma vez no início do programa.
; Não tem parâmetros de entrada nem de saída.
USART_Init:
	; Set baud rate
	ldi r17, 0
	ldi r16, 103
	sts UBRR0H, r17
	sts UBRR0L, r16
	; Set frame format: 8data, 1stop bit
	ldi r16, 0x06
	sts UCSR0C,r16
	; Enable receiver and transmitter
	ldi r16, 0x18
	sts UCSR0B,r16
	ret
;envio de um carácter através da porta série
; Envia pela porta série o valor colocado no registo r17.
; Parâmetro de entrada: carácter a enviar que deve ser colocado no registo r17
; antes da chamada desta rotina.
; Não tem parâmetros de saída.
USART_Transmit:
	; Wait for empty transmit buffer
	lds r16, UCSR0A
	sbrs r16, UDRE0
	rjmp USART_Transmit
	; Put data (r17) into buffer, sends the data
	sts UDR0,r17
	ret
;recepção de um carácter através da porta série
; Recebe o carácter vindo através da porta série e coloca-o no registo r17.
; Não tem parâmetros de entrada.
; Parâmetro de saída: o carácter recebido é colocado no registo r17 para ser utilizado
; depois da chamada desta rotina.
USART_Receive:
	; Wait for data to be received
	lds r16, UCSR0A
	sbrs r16, RXC0
	rjmp USART_Receive
	; Get and return received data from buffer
	lds r17, UDR0
	ret

