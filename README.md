# Health Data Tracker
### Subproject within the organisation ![Open Health Data Project](https://github.com/Open-Health-Data-Project)
<!--- These are examples. See https://shields.io for others or to customize this set of shields. You might want to include dependencies, project status and licence info here --->
![GitHub issues](https://img.shields.io/github/issues/Open-Health-Data-Project/hdp-android)
![Project license](https://img.shields.io/github/license/Open-Health-Data-Project/hdp-android)
![GitHub build](https://img.shields.io/circleci/build/github/Open-Health-Data-Project/hdp-android/master)
![GitHub release](https://img.shields.io/github/v/release/Open-Health-Data-Project/hdp-android)

As part of the data collection issues of the project, I thought it would be a good idea to write an application for Android (because collecting data on the phone is the most comfortable) allowing to collect various user data. Health Data Tracker is an Android app that allows users to track their data mostly related to (but not limited to) health. That includes actions such as:
* measuring the time of different activities 
* automatic data logging in the background (using smartphone sensors) 
* tracking numeric or categorical data

All of this with the possibility to use only trackers that you want. **Currently app is under development.**

![Main_screen](http://foto.hostuje.org/x/ee114f874ca173b59e1aad90a52c75c2.jpg)
![Settings screen](http://foto.hostuje.org/x/a6ce3915fb9dfea2630ab9f9f17167ed.jpg)

## Plans for future development

The short-term plan is to save the time of various activities (similarly as in this application - https://play.google.com/store/apps/details?id=com.jee.timer) with saving the data to the database locally and exporting the data to a file (in the form of daily summaries and record history).
Now I would like to describe in more detail what functionalities of the application I mean at the beginning:
* adding multiple stopwatches with names (which define the activities they register)
* adding own categories - each stopwatch must have a category assigned to it
* more than one stopwatch can work simultaneously at a given moment (however, only if they come from different categories that allow simultaneous recording - this is determined by the parameter of that category)
* registered time periods should be saved in a database in the form of time record history and should include the start and end time of a given recording period
* it should be possible to view the history and correct incorrectly recorded time (for example, if someone forgets to turn off the stopwatch)
* every day at a certain time all stopwatches should be reset (without breaking the history, of course)
* possibility to export registration history and daily summaries to file

The long-term plan for the Android application also assumes:
* automatic saving of data, such as time of using the phone (as here - https://play.google.com/store/apps/details?id=com.burockgames.timeclocker)
* recording physical activity time
* recording of nutrition data
* recording of the number of floors travelled with a barometer - if the device has one

It is therefore an application that allows people to register different types of data. Of course, all with the permission of the user, who can choose which data is collected, which data he wants to store only offline in order to export it to a file or sync them with a desktop app for further analysis, and which he wants to share for comparison with other users. The application would be part of a larger ecosystem composed of a website and a desktop application.

## Prerequisites

Before you begin, ensure you have met the following requirements:
* You have installed the latest version of `Android Studio`

## Installing Health Data Tracker on AVD

To install Health Data Tracker, follow these steps:

* Click on `File > New > Project from Version Control`
* Paste link to the repository (https://github.com/Open-Health-Data-Project/hdp-android.git) to URL field and click `Clone`.
* Ensure that you are on the `master` branch if you want to use stable version of the app.
* Build project and run it on AVD.

<!-- Add this after release -->
<!-- ## Using Health Data Tracker -->
 
<!--- To use <project_name>, follow these steps: --->
## Contributing to Health Data Tracker

You can found these informations in our ![CONTRIBUTING file](https://github.com/Open-Health-Data-Project/hdp-android/blob/develop/CONTRIBUTING.md)

## Contributors

Thanks to the following people who have contributed to this project:

* [@Marchuck](https://github.com/Marchuck) 💻 

<!--- You might want to consider using something like the [All Contributors](https://github.com/all-contributors/all-contributors) specification and its [emoji key](https://allcontributors.org/docs/en/emoji-key). --->

## Contact

If you want to contact me you can reach me at <openhdp@gmail.com>.
