# 3 bit mux
# v1,v0 data v2 address

variable v0
variable v1
variable v2

f0 = NOT v2
f1 = AND f0 v1
f2 = AND v2 v0
f3 = OR f1 f2

output f3
