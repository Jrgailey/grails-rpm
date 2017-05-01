grails-rpm
==========

Create a configurable rpm from your grails artifacts.
This a fork from the [wotifgroup grails-rpm repo](https://github.com/wotifgroup/grails-rpm).

## Quick start
Here's a sample rpm configuration that you can drop into BuildConfig.groovy:
```
rpm = [
    appUser: "testApp",
    appGroup: "sg_testApp",
    metaData: [
        vendor: "You",
        group: "Applications/Internet",
        description: "$appName RPM",
        packager: "Rpm Script",
        license: "Company. All rights reserved",
        summary: "$appName",
        url: "http://github.com/project?id=${argsMap.git_hash}",
        distribution: "(none)",
        buildHost: System.getProperty("HOSTNAME") ?: 'localhost',
        type: "BINARY",
        prefixes: "/apps/test"
    ],
    preRemove: "rpm/scripts/preremove.sh",
    postInstall: "rpm/scripts/postinstall.sh",
    packageInfo: [name: appName, version: appVersion],
    platform: [arch: "NOARCH", osName: "LINUX"],
    dependencies: [
        jdk: "1.7",
        curl: "7.0.0"
    ],
    structure: [
        apps: [
            test: [
                permissions: 775,
                user: rpm.appUser,
                group: rpm.appGroup,
                directive: "CONFIG",
                bin: [
                    permissions: 775,
                    files: [
                        "target/$appName*.jar": [
                            permissions: 744,
                            dirPermissions: 644
                            links: [
                                "$appName.jar": [
                                    to: "/apps/test/other.txt",
                                    permissions: 0755
                                ]
                            ]
                        ]
                    ]
                ],
                etc: [
                    permissions: 775,
                    user: rpm.appUser,
                    group: rpm.appGroup,
                    files: [
                        "rpm/apps/test/etc/.keystorepassword": [
                            permissions: 644,
                            directive: "CONFIG"
                        ]
                    ]
                ]
            ]
        ],
        etc: [
            logrotate: [
                files: [
                    "rpm/etc/logrotate.d/test": [
                        permissions: 644,
                        directive: "CONFIG"
                    ]
                ]
            ]
        ]
    ]
]
```
Tweak it as needed. Then run:
```
grails rpm
```
to produce your rpm.

## Configuration
The rpm plugin works by reading from your grails build configuration the property "rpm", which is an object defining the structure of the rpm you
wish to build. The rpm property is broken into the following sections:

### metaData
The metaData section is a map, allowing you to set any of the bean properties on [redline's Builder class](http://redline-rpm.org/apidocs/org/freecompany/redline/Builder.html).

type is one of the enum values from [redline's RpmType](http://redline-rpm.org/usage.html).

### Pre/Post Scripts
You can specify shell scripts inside your grails project to run on post-install or pre-uninstall of the rpm:
```
preRemove = "rpm/scripts/preremove.sh"
postInstall = "rpm/scripts/postinstall.sh"
```

### packageInfo
packageInfo is a double which lets you specify the name and version of the installed package. See "Command-Line Arguments" below for how to set the release.

### platform
platform lets you specify the intended architecture and OS for your rpm. The values here should be one of enum values from redline's [Architecture](http://redline-rpm.org/apidocs/org/freecompany/redline/header/Architecture.html)
or [Os](http://redline-rpm.org/usage.html)

### dependencies
dependencies is map of the installed packages (and their versions) that your rpm depends on.

### structure
structure is the actual layout of the content of the rpm. It specifies the files (and associated metadata) that the rpm will install onto the box as
a tree structure. At each node (i.e. directory) in the tree, you can specify the permissions, user and group and [RPM directive](http://www.rpm.org/max-rpm/s1-rpm-inside-files-list-directives.html) for that
node. If you don't specify values for each node then it will assume the defaults:
- permissions: 775
- user: "root"
- group: "root"
- RPM directive: none

Each node may also then specify a set of files to install at that directory, via the "files" map. Each file node can specify, as with a directory, the
permissions, user, group and directive. The name of the file may also be wildcarded, according to commons-io [WildcardFileFilter](http://commons.apache.org/proper/commons-io/apidocs/org/apache/commons/io/filefilter/WildcardFileFilter.html)
to match multiple files or to match a file with a variable name (e.g. jar file whose name includes a version or date). It can also specify a map of "links", which will be symlinks created to this file.

Each node may also specify a set of symlinks to install at that directory, via the "links" map. Each entry should specify the real file the link points to, as well as optionally
the permissions for that symlink

## Command-Line Arguments
By default, the rpm will be named with the following form: appName-appVersion.noarch.rpm. 

###Commands:
The file generated will contain the following order of information:
`appName-appVersion-appRelease-appDate-appBuildNumber`

Example: `myApp-1.2.3-hotfix-2017.05.01-1`
```
grails rpm
grails rpm --release=XXX
grails rpm --build=777
grails rpm --useDate=true

```

#### release (appRelease)
Adds a custom release value to the end of the rpm name.
```
grails rpm --release=abc
```
would produce an rpm something like "testapp-1.0.0-abc.noarch.rpm".

#### useDate (appDate)
Flag used to determine if the date should be added to the generated filename. By default the date is omitted, unless you
 pass in `true`.
```
grails rpm --useDate=true
grails rpm --useDate=false
```
would produce an rpm something like "testapp-1.0.0-2017.05.01.noarch.rpm".

#### build (appBuildNumber)
```
grails rpm --build=999
```
would produce an rpm something like "testapp-1.0.0-999.noarch.rpm".

#### name (appName)
Furthermore, you can completely override the name of the rpm like this:
```
grails rpm --name=hello
```
would produce the rpm: hello.noarch.rpm
