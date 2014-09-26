# 11-bit multiplexer
# solution modified from Koza I p 186

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

f0 = IF a0 d1 d0
f1 = IF a0 d3 d2
f2 = IF a1 f1 f0

f3 = IF a0 d5 d4
f4 = IF a0 d7 d6
f5 = IF a1 f4 f3

f6 = IF a2 f5 f2

output f6
