@echo off
del \\parwinproto\d$\proto\tioga-notify-server-grizzly\lib\*.jar
copy C:\dvlp\3rd-party\tioga-solutions\Notify\tioga-notify-server-grizzly\build\install\tioga-notify-server-grizzly\lib\*.jar \\parwinproto\d$\proto\tioga-notify-server-grizzly\lib\
copy C:\dvlp\3rd-party\tioga-solutions\Notify\tioga-notify-server-grizzly\build\install\tioga-notify-server-grizzly\server-start.bat \\parwinproto\d$\proto\tioga-notify-server-grizzly\
