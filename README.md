# Cell-Life VMMC

VMMC is an app designed on the [Cell-life IVR](https://github.com/cell-life/celllife-ivr) to
send post operative messages to men who have received a medical circumcision.

It integrates with a [Verboice](https://verboice.instedd.org/) server to provide the IVR
functionality.

## Instructions on how to set up a VMMC server

1. Install [Verboice server](https://verboice.instedd.org/)
2. Setup the campaign on Verboice
3. Install an LDAP server e.g [OpenLDAP](https://www.openldap.org/) and configure with users
4. Install Tomcat 7
5. Add the [MySql Java Connector Jar](https://www.mysql.com/products/connector/) to a Tomcat lib directory
6. Create a "vmmc" database in mysql - this database will be initialised when the war application is deployed
7. Set up properties files and environment variables
 - `VMMC_HOME` - a folder where reports can be compiled and stored
 - `VMMC_APPLICATION_PROPERTIES` - link to the [application properties](etc/application.properties) for VMMC
 - `CELLLIFE_HOME` - a folder where there is a file called [celllife-cas.properties](https://github.com/cell-life/celllife-cas/blob/master/etc/celllife-cas.properties) containing the CAS application properties
8. Install the [Cell-Life CAS web application](https://github.com/cell-life/celllife-cas) in Tomcat (CAS server is used for single sign-on)
9. Install VMMC web application in Tomcat. You will also need to manually copy the mysql library to one of the Tomcat lib folders (or the webapp lib folder)
10. Set up VMMC server so that it has a campaign. VMMC unfortunately doesn't have a user interface and uses a REST api. And unfortunately I don't have a copy of the REST API documentation. All I can suggest is that you use a REST sniffer app once you get the VMMC server running.

## Troubleshooting:

### Tomcat Permgen
If you get a Tomcat permgen error, then follow the instructions here to increase Tomcat memory: http://stackoverflow.com/questions/12688778/increase-tomcat-memory-settings

## More information

See: [IVR-090218-1942-28.pdf](docs/IVR-090218-1942-28.pdf)
