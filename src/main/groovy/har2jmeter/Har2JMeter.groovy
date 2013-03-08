package har2jmeter

import groovy.json.*
import groovy.xml.*

class Har2JMeter {
	
	def withHttpHeaders = true
	
	def convert(File harFile, File jmxFile) {
		
		if(!harFile.exists()) {
			println "The har input file \"${harFile}\" does not exist!"
			return
		}
		
		def jmeterSamplers = []
		def jsonSlurper = new JsonSlurper()
		def har = jsonSlurper.parse(new FileReader(harFile))
		har.log.entries.each { entry ->
			try {
				def request = entry.request
				if(request) {
					URL url = new URL(request.url)
					JMeterHttpSampler sampler = new JMeterHttpSampler(url: url, method: request.method)
					request.headers.each { header ->
						sampler.headers[header.name] = header.value
					}
					jmeterSamplers.add(sampler)
				}
			} catch(MalformedURLException exp){
				println "The HAR file contains entry with a not valid URL: ${entry.request.url}. This entry will be ignored"
			}
		}
		
		
		jmxFile.text = toJmx(jmeterSamplers)
		println "The JMX file \"${jmxFile}\" was successful created."
	}
	
	
	def toJmx(jmeterSamplers) {
		def writer = new StringWriter()
		def xml = new MarkupBuilder(writer)
		xml.jmeterTestPlan(version: "1.2", properties:"2.4", jmeter:"2.9")  {
			hashTree(){
				TestPlan(guiclass:"TestPlanGui", testclass:"TestPlan", testname:"Testplan", enabled:"true") {
					stringProp(name:"TestPlan.comments", "")
					boolProp(name: "TestPlan.functional_mode", false)
					boolProp(name: "TestPlan.serialize_threadgroups", false)
					elementProp(name: "TestPlan.user_defined_variables", elementType: "Arguments", guiclass: "ArgumentsPanel", testclass:"Arguments", testname: "Benutzer definierte Variablen", enabled: true){
						collectionProp(name: "Arguments.arguments")
					}
					stringProp(name: "TestPlan.user_define_classpath")
				}
				hashTree(){
					ThreadGroup(guiclass:"ThreadGroupGui", testclass:"ThreadGroup", testname:"Thread-Gruppe", enabled:"true") {
						stringProp(name:"ThreadGroup.on_sample_error", "continue")
						elementProp(name:"ThreadGroup.main_controller", elementType:"LoopController", guiclass:"LoopControlPanel", testclass:"LoopController", testname:"Schleifen-Controller (Loop Controller)", enabled:"true"){
							boolProp(name:"LoopController.continue_forever", "false")
							stringProp(name:"LoopController.loops", "1")
						}
						stringProp(name:"ThreadGroup.num_threads", "1")
						stringProp(name:"ThreadGroup.ramp_time", "1")
						longProp(name:"ThreadGroup.start_time", "1362062247000")
						longProp(name:"ThreadGroup.end_time", "1362062247000")
						boolProp(name:"ThreadGroup.scheduler", "false")
						stringProp(name:"ThreadGroup.duration", "")
						stringProp(name:"ThreadGroup.delay", "")
					}
					hashTree() {
						jmeterSamplers.each { sampler ->
							HTTPSamplerProxy(guiclass:"HttpTestSampleGui", testclass:"HTTPSamplerProxy", testname:"${sampler.path}", enabled:"true"){
							  elementProp(name:"HTTPsampler.Arguments", elementType:"Arguments", guiclass:"HTTPArgumentsPanel", testclass:"Arguments", testname:"User Defined Variables", enabled:"true") {
								collectionProp(name:"Arguments.arguments")
							  }
							  stringProp(name:"HTTPSampler.domain", sampler.domain)
							  stringProp(name:"HTTPSampler.port", 443)
							  stringProp(name:"HTTPSampler.connect_timeout", "")
							  stringProp(name:"HTTPSampler.response_timeout", "")
							  stringProp(name:"HTTPSampler.protocol", "https")
							  stringProp(name:"HTTPSampler.contentEncoding", "")
							  stringProp(name:"HTTPSampler.path", sampler.path)
							  stringProp(name:"HTTPSampler.method", "GET")
							  boolProp(name:"HTTPSampler.follow_redirects", "true")
							  boolProp(name:"HTTPSampler.auto_redirects", "false")
							  boolProp(name:"HTTPSampler.use_keepalive", "true")
							  boolProp(name:"HTTPSampler.DO_MULTIPART_POST", "false")
							  boolProp(name:"HTTPSampler.monitor", "false")
							  stringProp(name:"HTTPSampler.embedded_url_re", "")
							  stringProp(name:"HTTPSampler.implementation", "Java")
							  
							}
							hashTree(){
								if(withHttpHeaders && sampler.headers) {
									HeaderManager(guiclass:"HeaderPanel", testclass:"HeaderManager", testname:"HTTP Header Manager", enabled:"true"){
										collectionProp(name:"HeaderManager.headers") {
											sampler.headers.each { header ->
												elementProp(name:"", elementType:"Header") {
													stringProp( name:"Header.name", header.key)
													stringProp( name:"Header.value", header.value)
												}
											}
										}
									}
									hashTree()
								}
							}
						}
					}
				}
			}
		}
		return writer.toString()
	}

}
