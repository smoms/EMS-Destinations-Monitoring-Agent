# EMS-Destinations-Monitoring-Agent
This Java project implements a simple TIBCO EMS monitoring agent that periodically sends notification alerts (for example emails) whenever any queue and/or topic destination has a pending backlog of messages greater than a configurable threashold.

It is a Maven project. You can build it in Eclipse or run the jar (with dependencies) in the target folder. Main class is <b>com.digitalstrom.dshub.esb.logic.EMSMonitoringAgent</b>


NB <br/>
you need to add libs folder including EMS client <i>tib*.jar</i> files
