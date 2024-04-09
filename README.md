# Using Kotlinx-datetime library to build a simple time and date picker 

#### Ultimately inspired by Uber's home screen here. The use of the word "Now" just makes sense. You either need to do something `"Now"` or some time after.


![image](https://github.com/spectechular/Kotlinx-Datetime-Picker/assets/11188935/8b7c30f7-a078-43a2-ac96-da612e90cdb4)

So I wondered if I could build something similar using Jetpack Compose that is responsive and clear when you want to do something immediately or in the future. Like schedule a meeting.

I use compose remember to do conditional checks when the time selected changes in relation to our time when we started the picking / selection.
If the user selects a time in the future we show that time.

```
 val isTimeInFuture by remember {
        derivedStateOf {
            currentSelectedDate.time > staticNowDateTime.time
        }
    }

 Text(if (!isTimeInFuture && !isDayInFuture) "Now" else formatTimeJava(currentSelectedDate.toJavaLocalDateTime()))
```
![image](https://github.com/spectechular/Kotlinx-Datetime-Picker/assets/11188935/0a09ef3f-a43a-44ca-b4d9-043191000935)

![image](https://github.com/spectechular/Kotlinx-Datetime-Picker/assets/11188935/6cfac27a-8103-42bc-b085-33ed0e8b2ee1)




#### We do the same for the date.

![image](https://github.com/spectechular/Kotlinx-Datetime-Picker/assets/11188935/a4539e5d-9651-4fa1-bb4c-c414304fadfe)

#### If the date selected is not Today specifically we just show the date

![image](https://github.com/spectechular/Kotlinx-Datetime-Picker/assets/11188935/42cc202e-b31f-4f40-822b-fb01ff5828a2)

```
val isDayInFuture by remember {
        derivedStateOf {
            currentSelectedDate.date > staticNowDateTime.date
        }
    }
Text(if (!isDayInFuture) "Today" else formatDateJava(currentSelectedDate.toJavaLocalDateTime()))

```
#### We could even do something like say `"Tomorrow"` 

```
val isTomorrow by remember {
        derivedStateOf {
  currentSelectedDate.date == staticNowDateTime.date.plus(1, kotlinx.datetime.DateTimeUnit.DAY)
        }
    }
```



https://github.com/spectechular/Kotlinx-Datetime-Picker/assets/11188935/b1febafb-a76b-434e-83eb-41eb4ba26f3b


https://github.com/spectechular/Kotlinx-Datetime-Picker/assets/11188935/35a31280-3138-4507-978c-54574d62e117

You can read this that goes into detail regarding UTC and decisions and tradeoffs to be made when designing systems with time:

https://codeblog.jonskeet.uk/2019/03/27/storing-utc-is-not-a-silver-bullet/
