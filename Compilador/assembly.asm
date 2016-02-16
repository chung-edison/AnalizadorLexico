addI fp, 104 => tp
addI fp, 312 => hp
addI tp, 0 => r1
main_entry:
subI hp, 2 => r2
subI hp, 3 => r3
load 23 => a0
store a0 => r2
l1:
load r2 => t1
load r1 => t2
cmp_EQ t1, t2 => a0
store a0 => r3
load r2 => t1
loadI 50 => t2
cmp_LT t1, t2 => a0
cbr a0 => l1, l2
l2:
load r3 => a0
store a0 => sp
subi sp, 4 => sp
load r1 => a0
loadAI sp, 4 => t1
sub a0, t1 => a0
addi sp, 4 => sp
store a0 => r2
