FROM ls/core:latest
WORKDIR /usr/src/app
CMD [ "java", "-cp", "store-1.0-SNAPSHOT-all.jar", "store.cloudtolocal.ImportDataKt" ]
