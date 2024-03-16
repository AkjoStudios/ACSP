FROM prom/prometheus:latest
USER root

ARG GRAFANA_AGENT_TOKEN
RUN echo ${GRAFANA_AGENT_TOKEN} > /etc/prometheus/agent_token

ADD prometheus.yml /etc/prometheus/prometheus.yml

EXPOSE 9090

CMD [ "--config.file=/etc/prometheus/prometheus.yml", "--storage.tsdb.path=/prometheus", "--storage.tsdb.retention.time=30d", "--web.console.libraries=/usr/share/prometheus/console_libraries", "--web.console.templates=/usr/share/prometheus/consoles", "--web.enable-remote-write-receiver", "--log.level=info"]