package har2jmeter

import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import spock.*
import spock.lang.*

import static org.custommonkey.xmlunit.XMLAssert.*

class Har2JMeterSpec extends Specification {

	@Rule
	TemporaryFolder folder= new TemporaryFolder()
	
	static googleHarFile
	
	def setupSpec() {
		def googleHar = getClass().getResource("www.google.de.har")
		googleHarFile = new File(googleHar.file)
	}
	
	def cleanup() {
		assert folder.folder.deleteDir() : "Output folder should be clean up."
	}
	
	def createGoogleJmx() {
		given:
			def outputFile = folder.newFile()
			Har2JMeter har2jMeter = new Har2JMeter()
		when:
			har2jMeter.convert(googleHarFile, outputFile)
		then:
			assertXMLEqual(outputFile.text,
			har2jMeter.toJmx([new JMeterHttpSampler(
				url: 		new URL("https://www.google.de/"),
				method: 	"GET", 
				headers: [
					host: "www.google.de"
				]  
			)]))
	}
	
	def createGoogleJmxNoHeaders() {
		given:
			Har2JMeter har2jMeter = new Har2JMeter(withHttpHeaders: false)
			def outputFile = folder.newFile()
		when:
			har2jMeter.convert(googleHarFile, outputFile)
		then:
			assertXMLEqual(outputFile.text,
			har2jMeter.toJmx([new JMeterHttpSampler(
				url: 		new URL("https://www.google.de/"),
				method: 	"GET",
			)]))
	}
	
}
