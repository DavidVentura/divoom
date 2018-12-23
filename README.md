An attempt to consolidate some bits of information about divoom into a clean python3.6 package

1- https://github.com/MarcG046/timebox/blob/master/doc/protocol.md

Issues and things I do not understand

# Replies to changing views
Smashing the SWITCH button outputs:
```
command 0x46 data [0, 0, 6, 255, 0, 0, 0, 0, 100, 1, 75, 0, 0, 75, 0, 0, 255, 0, 0, 169, 132, 0]
command 0x46 data [1, 0, 6, 255, 0, 0, 0, 0, 100, 1, 75, 0, 0, 75, 0, 0, 255, 0, 0, 169, 132, 0]
command 0x46 data [2, 0, 6, 255, 0, 0, 0, 0, 100, 1, 75, 0, 0, 75, 0, 0, 255, 0, 0, 169, 132, 0]
command 0x46 data [3, 0, 6, 255, 0, 0, 0, 0, 100, 1, 75, 0, 0, 75, 0, 0, 255, 0, 0, 169, 132, 0]
command 0x46 data [4, 0, 6, 255, 0, 0, 0, 0, 100, 1, 75, 0, 0, 75, 0, 0, 255, 0, 0, 169, 132, 0]
command 0x46 data [5, 0, 6, 255, 0, 0, 0, 0, 100, 1, 75, 0, 0, 75, 0, 0, 255, 0, 0, 169, 132, 0]
command 0x46 data [5, 0, 6, 255, 0, 0, 0, 0, 100, 1, 75, 0, 0, 75, 0, 0, 255, 0, 0, 169, 132, 0]
command 0x46 data [0, 0, 6, 255, 0, 0, 0, 0, 100, 1, 75, 0, 0, 75, 0, 0, 255, 0, 0, 169, 132, 0]
```

1. what are these values?
2. Why is 5 repeated?


```
Command(Commands.SWITCH_VIEW, Views.CLOCK_24, extra_bytes=[11, 22, 33]),
Command(Commands.SWITCH_VIEW, Views.STOPWATCH, extra_bytes=[44, 55, 66]),
Command(Commands.SWITCH_VIEW, Views.SCORE, extra_bytes=[77, 88, 99]),
Command(Commands.SWITCH_VIEW, Views.TEMP_C, extra_bytes=[100, 111, 122]),

command 0x46 data [0, 0, 6, 255, 0, 0, 0, 0, 100, 1, 11, 22, 33, 100, 111, 122, 255, 0, 0, 169, 132, 0]
```

len == 22

indexes:
0 = view ? why is 5 repeated?
1.. 10 == ?
11 = brightness
12 = ?
10:13 == CLOCK rgb
13:16 == TEMP_C rgb
16 == intensity?


```
{'TEMP_COLOR': [100, 111, 122],
 'CLOCK_COLOR': [11, 22, 33],
 'VIEW_ID': 4,
 'FIRST_PART': [0, 6, 255, 0, 0, 0, 0, 100, 1],
 'LAST_PART': [255, 0, 0, 169, 132, 0],
}
```
# 
