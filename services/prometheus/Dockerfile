FROM prom/prometheus:latest
USER root

ARG GRAFANA_METRICS_TOKEN
ARG PROMETHEUS_PASSWORD

ADD prometheus.yml /etc/prometheus/prometheus.yml

EXPOSE 9090

CMD [ "--config.file=/etc/prometheus/prometheus.yml", "--storage.tsdb.path=/prometheus", "--storage.tsdb.retention.time=30d", "--web.console.libraries=/usr/share/prometheus/console_libraries", "--web.console.templates=/usr/share/prometheus/consoles", "--log.level=info"]