FROM mysql:8.0.26
COPY init /docker-entrypoint-initdb.d
EXPOSE 3306
ENV MYSQL_RANDOM_ROOT_PASSWORD=1