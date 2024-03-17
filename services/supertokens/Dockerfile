FROM registry.supertokens.io/supertokens/supertokens-postgresql:8.0
USER root

ARG AUTH_DB_URL
ENV POSTGRESQL_CONNECTION_URI=${AUTH_DB_URL}

EXPOSE 3567

CMD [ "supertokens", "start" ]