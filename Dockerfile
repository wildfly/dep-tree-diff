FROM maven:3.6.2-ibmjava-8-alpine

RUN apk update && apk upgrade && \
    apk add --no-cache bash git openssh jq && \
    mkdir /tool



COPY entry-point.sh /entry-point.sh
COPY reporters/github-labels/target/dep-tree-reporter-github-labels.jar tool/

ENTRYPOINT ["/entry-point.sh"]
