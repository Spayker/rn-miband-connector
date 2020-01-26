[![GitHub license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/Spayker/rn-miband-connector/blob/master/LICENSE) [![Build Status](https://travis-ci.org/Spayker/rn-miband-connector.svg?branch=master)](https://travis-ci.org/Spayker/rn-miband-connector) [![codecov.io](https://codecov.io/github/Spayker/rn-miband-connector/coverage.svg?branch=master)](https://codecov.io/github/Spayker/rn-miband-connector?branch=master) [![Gitter](https://badges.gitter.im/rn-miband-connector/community.svg)](https://gitter.im/rn-miband-connector/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

# rn-miband-connector

MiBand Connector represents POC of communication between miBand devices and Android app. Latest improvements added some API to transfer already gathered
data toa dedicated server (more info about server side can be found [here](https://github.com/Spayker/sbp_server)).
Was tested on miBand 3 and React-Native (v0.61.2)

Tech stack:
- JDK: 1.8 (latest inner build) or higher
- NodeJs: 10.x.x or above
- Android SDK: 21 or above
- Gradle: v3.3.1
- React-Native: v0.61.2
- Android Debug Bridge or adb: v1.0.39

##### Notes

Solution partially uses sources taken from:
1) https://github.com/creotiv/MiBand2
2) https://github.com/vshymanskyy/miband2-python-test

Thanks guys for your great work.