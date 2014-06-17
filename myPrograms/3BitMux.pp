# 3 bit mux

variable a0
variable d0
variable d1

f0 = NOT a0
f1 = AND f0 d0
f2 = AND a0 d1
f3 = OR f1 f2

output f3
