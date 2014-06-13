# 8 bit odd parity program

variable v0
variable v1
variable v2
variable v3
variable v4
variable v5
variable v6
variable v7

#first level
f0 = XOR v0 v1
f1 = XOR v2 v3
f2 = XOR v4 v5
f3 = XOR v6 v7

#second level
f4 = XOR f0 f1
f5 = XOR f2 f3

#third and final level
f6 = XOR f4 f5

output f6
