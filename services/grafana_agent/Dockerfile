FROM grafana/agent:latest
USER root

ENV AGENT_MODE=flow

ADD ./config.river /etc/agent/config.river

EXPOSE 12345

CMD [ "grafana-agent", "run", "--server.http.listen-addr=0.0.0.0:12345", "/etc/agent/config.river" ]