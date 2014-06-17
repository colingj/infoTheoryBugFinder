# 11-bit multiplexer
# solution from Koza I p 186

variable a0
variable a1
variable a2
variable d0
variable d1
variable d2
variable d3
variable d4
variable d5
variable d6
variable d7

f0 = IF a1 d2 d0
f1 = IF a1 d6 d4
f2 = IF a2 f1 f0

f3 = IF a1 d3 d1
f4 = IF a1 d7 d5
f5 = IF a2 f4 f3

f6 = IF a0 f5 f3

output f6
