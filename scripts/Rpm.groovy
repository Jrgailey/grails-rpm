includeTargets << grailsScript('_GrailsBootstrap')

target(rpmMain: "Build the application RPM") {
    depends(configureProxy, classpath, loadApp)

    //have to invoke this via reflection to work around gant classpathing issues
    def rpmBuilder = classLoader.loadClass('grails.plugin.rpm.RpmBuilder').newInstance(buildSettings.config.rpm, rpmName, rpmRelease)
    rpmBuilder.build()

    println "Complete"
}
def getRpmName() {
    def rpmName = argsMap.name
    def buildNum = argsMap.build
    def release = argsMap.release
    def useDate = argsMap.useDate

    if (!rpmName) {
        String appName = metadata['app.name']
        String appVersion = metadata['app.version']
        rpmName = "$appName-$appVersion"

        if (release) {
            String appRelease = rpmRelease
            rpmName += "-$appRelease"
        }
        if (useDate == "true") {
            System.out.println(useDate)
            def appDate = new Date().format("yyyy.MM.dd")
            rpmName += "-$appDate"
        }
        if (buildNum) {
            rpmName += "-$buildNum"
        }
    }
    rpmName
}

def getRpmRelease() {
    String appRelease = ""
    String appBuildNumber = argsMap.release
    if (appBuildNumber) {
        appRelease = appBuildNumber
    }
    appRelease
}

setDefaultTarget(rpmMain)
