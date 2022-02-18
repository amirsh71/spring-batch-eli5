# Getting Started

### Reference Documentation

A simple Example on using Spring batch.

In this example, we read a csv input file that contains each student and their scores.

then we calculate each student grade and finally write each student grade in another csv file.

to run the project you should create input and output file and provide their path in run command:

```
java -jar project.jar inputFile=student-scores.csv  outputFile=student-grades.csv
```