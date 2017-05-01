class RpmGrailsPlugin {
    def version = "0.10.3"
    def grailsVersion = "2.0 > *"

    def title = "Rpm Plugin"
    def description = '''\
Create an rpm from your grails artifacts, based configuration in Config.groovy.
'''

    def documentation = "https://github.com/Jrgailey/grails-rpm"


    def license = "APACHE"

    def developers = [ [ name: "James Gailey", email: "jrgailey@gmail.com" ]]

    def issueManagement = [ system: "github", url: "https://github.com/Jrgailey/grails-rpm" ]

    def scm = [ url: "https://github.com/Jrgailey/grails-rpm" ]
}
