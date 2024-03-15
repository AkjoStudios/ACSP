FROM grafana/agent:latest
USER root

ENV AGENT_MODE=flow

ADD ./config.river /etc/agent/config.river

EXPOSE 12345

CMD [ "grafana-agent", "/etc/agent/config.river" ]