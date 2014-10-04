M2E Connector for maven-remote-resources
=============================================

This m2e connector for the Maven Remote Resources Plugin is designed to handle the bundle and process goals of the [maven-remote-resources-plugin](http://maven.apache.org/plugins/maven-remote-resources-plugin/)

[![Build Status](https://buildhive.cloudbees.com/job/coderplus/job/m2e-connector-for-maven-remote-resources-plugin/badge/icon)](https://buildhive.cloudbees.com/job/coderplus/job/m2e-connector-for-maven-remote-resources-plugin/)

## FAQ ##

### How do I use it? ###

First off, note that this is currently Beta code.  It has been minimally tested, and all the usual early adopter
warnings apply.  That said if you're willing to help test the connector all you have to do is:

1. Add the below
[update site](http://help.eclipse.org/juno/topic/org.eclipse.platform.doc.user/tasks/tasks-127.htm?cp=0_3_15_5):

   http://coderplus.com/m2e-update-sites/maven-remote-resources-plugin/
1. Install it into Eclipse like any other
[new feature](http://help.eclipse.org/juno/topic/org.eclipse.platform.doc.user/tasks/tasks-124.htm?cp=0_3_15_1).
1. Remove any [lifecycle mapping metadata](http://wiki.eclipse.org/M2E_plugin_execution_not_covered#ignore_plugin_goal)
you might have had in your POMs for the remote-resource:bundle or  remote-resource:process goals.

That's it!  The connector will run on full builds. It will be executed on incremental builds only if something interesting to the plugin has changed.

### How can I help the project? ###

Thanks for asking...

* If you're a remote-resource:bundle or  remote-resource:process user:
	* Test this out.  [File an issue](https://github.com/coderplus/m2e-connector-for-maven-remote-resources-plugin/issues) if it doesn't
	work for you.  File an issue if you think it should do something more, or something different.
* If you're a Tycho/Eclipse Plugin/m2e  expert:
	* File an issue or submit a pull request if there is something that could be done better.
	* Contribute test cases.
* If you're a representative of the Eclipse Foundation or Apache Software Foundation or similar:
	* I'd be happy to consider donating this plugin&mdash;do get in touch.


## Thanks ##

Many thanks to the folks from the m2e-dev mailing list 
