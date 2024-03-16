FROM grafana/grafana-enterprise:latest-ubuntu
USER root

ARG GRAFANA_AGENT_TOKEN
ENV GRAFANA_AGENT_TOKEN=${GRAFANA_AGENT_TOKEN}

ARG ACSP_DB_HOST
ENV ACSP_DB_HOST=${ACSP_DB_HOST}
ARG ACSP_DB_PORT
ENV ACSP_DB_PORT=${ACSP_DB_PORT}
ARG ACSP_DB_USER
ENV ACSP_DB_USER=${ACSP_DB_USER}
ARG ACSP_DB_PASSWORD
ENV ACSP_DB_PASSWORD=${ACSP_DB_PASSWORD}
ARG ACSP_DB_NAME
ENV ACSP_DB_NAME=${ACSP_DB_NAME}

ADD ./grafana.ini /etc/grafana/grafana.ini
ADD ./datasources /etc/grafana/provisioning/datasources

EXPOSE 3000

CMD [ "grafana-server", "--config=/etc/grafana/grafana.ini" ]