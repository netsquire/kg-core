FROM ubuntu:latest
LABEL authors="netsquire"

ENTRYPOINT ["top", "-b"]