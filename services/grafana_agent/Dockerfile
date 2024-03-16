FROM grafana/agent:latest
USER root

ARG GRAFANA_AGENT_TOKEN
RUN echo ${GRAFANA_AGENT_TOKEN} > /etc/agent/agent_token

ARG ACSP_DB_URL
ENV ACSP_DB_URL=${ACSP_DB_URL}

ENV AGENT_MODE=flow

ADD ./config.river /etc/agent/config.river

EXPOSE 9090 12345

CMD ["run", "--server.http.listen-addr=0.0.0.0:12345", "--storage.path=/var/agent/data", "/etc/agent/config.river"]