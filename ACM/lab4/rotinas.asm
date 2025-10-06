;ROTINAS
;configura��o da porta s�rie
; Deve ser chamada uma vez no in�cio do programa.
; N�o tem par�metros de entrada nem de sa�da.
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
;envio de um car�cter atrav�s da porta s�rie
; Envia pela porta s�rie o valor colocado no registo r17.
; Par�metro de entrada: car�cter a enviar que deve ser colocado no registo r17
; antes da chamada desta rotina.
; N�o tem par�metros de sa�da.
USART_Transmit:
	; Wait for empty transmit buffer
	lds r16, UCSR0A
	sbrs r16, UDRE0
	rjmp USART_Transmit
	; Put data (r17) into buffer, sends the data
	sts UDR0,r17
	ret
;recep��o de um car�cter atrav�s da porta s�rie
; Recebe o car�cter vindo atrav�s da porta s�rie e coloca-o no registo r17.
; N�o tem par�metros de entrada.
; Par�metro de sa�da: o car�cter recebido � colocado no registo r17 para ser utilizado
; depois da chamada desta rotina.
USART_Receive:
	; Wait for data to be received
	lds r16, UCSR0A
	sbrs r16, RXC0
	rjmp USART_Receive
	; Get and return received data from buffer
	lds r17, UDR0
	ret

