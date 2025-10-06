/*
 * delay.asm
 *
 *  Created: 11/10/2023 10:18:17
 *   Author: Jose
 */ 

 ;Atraso de 1 s
delay:
 PUSH R22
 PUSH R21
 PUSH R20
 LDI R20, 0x40
del1: NOP
 LDI R21, 0xFF
del2: NOP
 LDI R22, 0xFF
del3: NOP
 DEC R22
 BRNE del3
 DEC R21
 BRNE del2
 DEC R20
 BRNE del1
 POP R20
 POP R21
 POP R22
 RET