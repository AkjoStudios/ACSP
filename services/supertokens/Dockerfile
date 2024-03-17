FROM registry.supertokens.io/supertokens/supertokens-postgresql:8.0
USER root

ARG AUTH_DB_URL
ENV POSTGRESQL_CONNECTION_URI=${AUTH_DB_URL}

ARG AUTH_API_KEY
ENV API_KEYS=${AUTH_API_KEY}

EXPOSE 3567

CMD [ "supertokens", "start" ]