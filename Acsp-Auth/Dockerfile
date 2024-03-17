FROM node:21-alpine3.19
WORKDIR /usr/src/app

COPY package*.json ./
RUN npm ci --only=production
COPY . .

EXPOSE 3001

ENV NODE_ENV=production

ARG APP_API_URL
ENV APP_API_URL=${APP_API_URL}

ARG APP_WEBSITE_URL
ENV APP_WEBSITE_URL=${APP_WEBSITE_URL}

CMD ["npm", "start"]