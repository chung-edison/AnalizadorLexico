addI fp, 108 => tp
addI fp, 324 => hp
addI tp, 0 => r1
subI hp, 4 => r2
main_entry:
subI hp, 6 => r3
subI hp, 7 => r4
load 23 => a0
store a0 => r3
l1:
load r3 => t1
loadAI r1, 1 => a0
cmp_EQ t1, t2 => a0
store a0 => r4
load r3 => t1
loadI 50 => t2
cmp_LT t1, t2 => a0
cbr a0 => l1, l2
l2:
load r4 => a0
store a0 => sp
subi sp, 4 => sp
loadAI r1, 2 => a0
loadAI sp, 4 => t1
sub a0, t1 => a0
addi sp, 4 => sp
store a0 => r3
