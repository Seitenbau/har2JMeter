# HAR to Apache JMeter Convertor

[![Build Status](https://buildhive.cloudbees.com/job/Seitenbau/job/har2JMeter/badge/icon)](https://buildhive.cloudbees.com/job/Seitenbau/job/har2JMeter/)

The project contains a simple Java/Groovy based command line tool 
to convert a HAR file into a JMeter test scripts.

	usage: har2JMeter -har [*.har] -jmx [*.jmx]
	Options:
 		-har <arg>   The har input file which could be converted to a JMeter JMX file.
 		-jmx <arg>   The jmx output file.
 		-help	     Print this message.

## Requirements

 - Java VM >= 1.6.0

## Build

	./gradlew distZip
	
or
	
	./gradlew distTar

The packages can be found in the directory:

 - build/distributions/har2JMeter-*.zip 
 - build/distributions/har2JMeter-*.tar
 
## Download

Download the har2JMeter from the project web site: 
http://seitenbau.github.com/har2JMeter/

## License

Apache License, Version 2.0 (current)
http://www.apache.org/licenses/LICENSE-2.0