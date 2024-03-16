FROM grafana/grafana-enterprise:latest-ubuntu
USER root

ARG GRAFANA_AGENT_TOKEN
ENV GRAFANA_AGENT_TOKEN=${GRAFANA_AGENT_TOKEN}

ADD ./grafana.ini /etc/grafana/grafana.ini
ADD ./datasources /etc/grafana/provisioning/datasources

EXPOSE 3000

CMD [ "grafana-server", "--config=/etc/grafana/grafana.ini" ]