FROM ls/stores_app
WORKDIR /usr/src/app

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.7.3/wait /wait
RUN chmod +x /wait
CMD /wait && java -cp store-1.0-SNAPSHOT-all.jar store.cloudtolocal.ImportDataKt