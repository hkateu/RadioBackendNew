FROM mysql
MAINTAINER hkateu@gmail.com

ENV MYSQL_ROOT_PASSWORD password
ENV MYSQL_USER  herb
ENV MYSQL_PASSWORD password1
ENV MYSQL_DATABASE radio
ADD Aradio.sql /docker-entrypoint-initdb.d
ADD Bradio.sql /docker-entrypoint-initdb.d

EXPOSE 3306