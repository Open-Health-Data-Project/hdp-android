As part of the data collection issues of the project, I thought it would be a good idea to write an application for Android (because collecting data on the phone is the most comfortable) allowing to collect various user data. The short-term plan is to save the time of various activities (similarly as in this application - https://play.google.com/store/apps/details?id=com.jee.timer) with saving the data to the database locally and exporting the data to a file (in the form of daily summaries and record history).
Now I would like to describe in more detail what functionalities of the application I mean at the beginning:
- adding multiple stopwatches with names (which define the activities they register)
- adding own categories - each stopwatch must have a category assigned to it
- more than one stopwatch can work simultaneously at a given moment (however, only if they come from different categories that allow simultaneous recording - this is determined by the parameter of that category)
- registered time periods should be saved in a database in the form of time record history and should include the start and end time of a given recording period
- it should be possible to view the history and correct incorrectly recorded time (for example, if someone forgets to turn off the stopwatch)
- every day at a certain time all stopwatches should be reset (without breaking the history, of course)
 - possibility to export registration history and daily summaries to file

The long-term plan for the Android application also assumes:
 - automatic saving of data, such as time of using the phone (as here - https://play.google.com/store/apps/details?id=com.burockgames.timeclocker)
 - recording physical activity time
 - recording of nutrition data
 - recording of the number of floors travelled with a barometer - if the device has one

It is therefore an application that allows people to register different types of data. Of course, all with the permission of the user, who can choose which data is collected, which data he wants to store only offline in order to export it to a file or sync them with a desktop app for further analysis, and which he wants to share for comparison with other users. The application would be part of a larger ecosystem composed of a website and a desktop application.
