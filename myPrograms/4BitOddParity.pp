# 4 bit even parity program

variable v0
variable v1
variable v2
variable v3

f0 = XOR v0 v1
f1 = XOR v2 v3
f2 = XOR f0 f1

output f2
