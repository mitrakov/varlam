FROM tomcat:8.5

ENV destination /usr/local/tomcat/webapps/
COPY target/varlam.war ${destination}
COPY web/ ${destination}/guap/

EXPOSE 8080

CMD ["catalina.sh", "run"]
