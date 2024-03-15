FROM grafana/agent:latest
USER root

ENV AGENT_MODE=flow

ADD ./config.river /etc/agent/config.river

EXPOSE 12345

CMD ["run", "--server.http.listen-addr=0.0.0.0:12345", "--metrics.wal-directory=/var/agent/data", "/etc/agent/config.river"]