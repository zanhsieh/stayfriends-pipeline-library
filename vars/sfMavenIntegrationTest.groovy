#!/usr/bin/groovy

def call(body) {

    // evaluate the body block, and collect configuration into the object
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()


    stage 'Integration Test' {
		mavenIntegrationTest {
			environment = 'Testing'

			def failIfNoTests = ""
			try {
			  failIfNoTests = ITEST_FAIL_IF_NO_TEST
			} catch (Throwable e) {
			  failIfNoTests = "false"
			}

			def localItestPattern = ""
			try {
			  localItestPattern = ITEST_PATTERN
			} catch (Throwable e) {
			  localItestPattern = "*KT"
			}


			failIfNoTests = localFailIfNoTests
			itestPattern = localItestPattern
		}
	}

}
